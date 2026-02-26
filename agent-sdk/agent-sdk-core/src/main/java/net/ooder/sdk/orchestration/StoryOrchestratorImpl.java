package net.ooder.sdk.orchestration;

import net.ooder.sdk.story.*;
import net.ooder.sdk.orchestration.CapabilityRouter.RouteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class StoryOrchestratorImpl implements StoryOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(StoryOrchestratorImpl.class);
    
    private final Map<String, OrchestrationStatus> statuses = new ConcurrentHashMap<>();
    private final Map<String, StoryContext> contexts = new ConcurrentHashMap<>();
    private final Map<String, UserStory> stories = new ConcurrentHashMap<>();
    private final List<OrchestrationListener> listeners = new CopyOnWriteArrayList<>();
    
    private CapabilityRouter capabilityRouter;
    private ContextProvider contextProvider;
    private WillTransformer willTransformer;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public StoryOrchestratorImpl() {
        this.willTransformer = new WillTransformerImpl();
    }
    
    @Override
    public CompletableFuture<OrchestrationResult> orchestrate(UserStory story) {
        if (story == null) {
            return CompletableFuture.completedFuture(
                OrchestrationResult.failure("unknown", "Story cannot be null"));
        }
        
        String storyId = story.getStoryId();
        stories.put(storyId, story);
        
        OrchestrationStatus status = new OrchestrationStatus();
        status.setStoryId(storyId);
        status.setStatus("RUNNING");
        status.setProgress(0.0);
        status.setCompletedSteps(0);
        status.setTotalSteps(story.getSteps().size());
        statuses.put(storyId, status);
        
        StoryContext context = contextProvider != null 
            ? contextProvider.provideContext(story) 
            : StoryContext.create(storyId);
        contexts.put(storyId, context);
        
        story.setStatus(UserStory.StoryStatus.IN_PROGRESS);
        notifyStoryStarted(story);
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            OrchestrationResult result = new OrchestrationResult();
            result.setStoryId(storyId);
            List<StepExecutionRecord> records = new ArrayList<>();
            
            try {
                for (StoryStep step : story.getSteps()) {
                    if (!canExecuteStep(step, records)) {
                        continue;
                    }
                    
                    status.setCurrentStep(step.getStepId());
                    context.setCurrentStepId(step.getStepId());
                    step.setStatus(StoryStep.StepStatus.RUNNING);
                    
                    notifyStepStarted(storyId, step);
                    
                    StepExecutionRecord record = executeStep(storyId, step, context);
                    records.add(record);
                    
                    step.setResult(record.isSuccess() 
                        ? StoryStep.StepResult.success(record.getResult())
                        : StoryStep.StepResult.failure(record.getMessage()));
                    step.setStatus(record.isSuccess() 
                        ? StoryStep.StepStatus.COMPLETED 
                        : StoryStep.StepStatus.FAILED);
                    
                    notifyStepCompleted(storyId, step, record);
                    
                    status.setCompletedSteps(status.getCompletedSteps() + 1);
                    status.setProgress((double) status.getCompletedSteps() / status.getTotalSteps());
                    
                    if (!record.isSuccess() && isCriticalStep(step)) {
                        throw new RuntimeException("Critical step failed: " + step.getName());
                    }
                }
                
                status.setStatus("COMPLETED");
                status.setProgress(1.0);
                story.setStatus(UserStory.StoryStatus.COMPLETED);
                
                result.setSuccess(true);
                result.setMessage("Story completed successfully");
                result.setTotalExecutionTime(System.currentTimeMillis() - startTime);
                result.setStepRecords(records);
                
                notifyStoryCompleted(storyId, result);
                
            } catch (Exception e) {
                log.error("Story orchestration failed: {}", storyId, e);
                
                status.setStatus("FAILED");
                story.setStatus(UserStory.StoryStatus.FAILED);
                
                result.setSuccess(false);
                result.setMessage("Story failed: " + e.getMessage());
                result.setTotalExecutionTime(System.currentTimeMillis() - startTime);
                result.setStepRecords(records);
                
                notifyStoryFailed(storyId, e);
            }
            
            return result;
        }, executor);
    }
    
    @Override
    public CompletableFuture<OrchestrationResult> orchestrate(WillTransformer.WillExpression will) {
        UserStory story = willTransformer.transform(will);
        return orchestrate(story);
    }
    
    private boolean canExecuteStep(StoryStep step, List<StepExecutionRecord> records) {
        List<String> dependencies = step.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }
        
        Set<String> completedSteps = new HashSet<>();
        for (StepExecutionRecord record : records) {
            if (record.isSuccess()) {
                completedSteps.add(record.getStepId());
            }
        }
        
        return completedSteps.containsAll(dependencies);
    }
    
    private StepExecutionRecord executeStep(String storyId, StoryStep step, StoryContext context) {
        StepExecutionRecord record = new StepExecutionRecord();
        record.setStepId(step.getStepId());
        record.setCapabilityId(step.getCapabilityId());
        record.setTimestamp(System.currentTimeMillis());
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (capabilityRouter != null && step.getCapabilityId() != null) {
                Map<String, Object> params = new HashMap<>(step.getParams());
                params.put("_context", context);
                
                RouteResult routeResult = capabilityRouter.route(step.getCapabilityId(), params).join();
                
                record.setSuccess(routeResult.isSuccess());
                record.setResult(routeResult.getData());
                record.setMessage(routeResult.getMessage());
                
                context.addExecutionRecord(toExecutionRecord(record));
            } else {
                record.setSuccess(true);
                record.setResult(null);
                record.setMessage("No capability router configured");
            }
        } catch (Exception e) {
            record.setSuccess(false);
            record.setMessage("Execution failed: " + e.getMessage());
        }
        
        record.setExecutionTime(System.currentTimeMillis() - startTime);
        return record;
    }
    
    private StoryContext.ExecutionRecord toExecutionRecord(StepExecutionRecord record) {
        StoryContext.ExecutionRecord er = new StoryContext.ExecutionRecord();
        er.setStepId(record.getStepId());
        er.setCapabilityId(record.getCapabilityId());
        er.setSuccess(record.isSuccess());
        er.setResult(record.getResult());
        er.setExecutionTime(record.getExecutionTime());
        er.setTimestamp(record.getTimestamp());
        return er;
    }
    
    private boolean isCriticalStep(StoryStep step) {
        return step.getType() == StoryStep.StepType.ACTION;
    }
    
    @Override
    public CompletableFuture<OrchestrationResult> resume(String storyId) {
        UserStory story = stories.get(storyId);
        if (story == null) {
            return CompletableFuture.completedFuture(
                OrchestrationResult.failure(storyId, "Story not found"));
        }
        
        OrchestrationStatus status = statuses.get(storyId);
        if (status != null) {
            status.setStatus("RUNNING");
        }
        
        return orchestrate(story);
    }
    
    @Override
    public void pause(String storyId) {
        OrchestrationStatus status = statuses.get(storyId);
        if (status != null && "RUNNING".equals(status.getStatus())) {
            status.setStatus("PAUSED");
            notifyStoryPaused(storyId);
        }
    }
    
    @Override
    public void cancel(String storyId) {
        OrchestrationStatus status = statuses.get(storyId);
        if (status != null) {
            status.setStatus("CANCELLED");
            UserStory story = stories.get(storyId);
            if (story != null) {
                story.setStatus(UserStory.StoryStatus.CANCELLED);
            }
            notifyStoryCancelled(storyId);
        }
    }
    
    @Override
    public Optional<OrchestrationStatus> getStatus(String storyId) {
        return Optional.ofNullable(statuses.get(storyId));
    }
    
    @Override
    public List<OrchestrationStatus> getActiveOrchestrations() {
        return statuses.values().stream()
            .filter(s -> "RUNNING".equals(s.getStatus()) || "PAUSED".equals(s.getStatus()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public void addOrchestrationListener(OrchestrationListener listener) {
        if (listener != null) listeners.add(listener);
    }
    
    @Override
    public void removeOrchestrationListener(OrchestrationListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void setCapabilityRouter(CapabilityRouter router) {
        this.capabilityRouter = router;
    }
    
    @Override
    public void setContextProvider(ContextProvider provider) {
        this.contextProvider = provider;
    }
    
    private void notifyStoryStarted(UserStory story) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStoryStarted(story); } catch (Exception ignored) {}
        }
    }
    
    private void notifyStepStarted(String storyId, StoryStep step) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStepStarted(storyId, step); } catch (Exception ignored) {}
        }
    }
    
    private void notifyStepCompleted(String storyId, StoryStep step, StepExecutionRecord record) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStepCompleted(storyId, step, record); } catch (Exception ignored) {}
        }
    }
    
    private void notifyStoryCompleted(String storyId, OrchestrationResult result) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStoryCompleted(storyId, result); } catch (Exception ignored) {}
        }
    }
    
    private void notifyStoryFailed(String storyId, Throwable error) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStoryFailed(storyId, error); } catch (Exception ignored) {}
        }
    }
    
    private void notifyStoryPaused(String storyId) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStoryPaused(storyId); } catch (Exception ignored) {}
        }
    }
    
    private void notifyStoryCancelled(String storyId) {
        for (OrchestrationListener listener : listeners) {
            try { listener.onStoryCancelled(storyId); } catch (Exception ignored) {}
        }
    }
}
