package net.ooder.sdk.will;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class WillManagerImpl implements WillManager {
    
    private static final Logger log = LoggerFactory.getLogger(WillManagerImpl.class);
    
    private final Map<String, WillExpression> wills = new ConcurrentHashMap<>();
    private final Map<String, WillTransformer.WillTransformResult> transformResults = new ConcurrentHashMap<>();
    private final List<WillListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicInteger willCounter = new AtomicInteger(0);
    
    private final WillParser parser;
    private final WillTransformer transformer;
    private final WillExecutor executor;
    
    public WillManagerImpl() {
        this.parser = new WillParserImpl();
        this.transformer = new WillTransformerImpl();
        this.executor = new WillExecutorImpl();
    }
    
    public WillManagerImpl(WillParser parser, WillTransformer transformer, WillExecutor executor) {
        this.parser = parser;
        this.transformer = transformer;
        this.executor = executor;
    }
    
    @Override
    public WillExpression expressWill(String willText, WillExpression.WillType type, int priority) {
        if (willText == null || willText.trim().isEmpty()) {
            throw new IllegalArgumentException("Will text cannot be empty");
        }
        
        WillParser.WillParseResult parseResult = parser.parse(willText);
        if (!parseResult.isSuccess()) {
            throw new RuntimeException("Failed to parse will: " + parseResult.getErrorMessage());
        }
        
        WillExpression will = parseResult.toWillExpression();
        if (type != null) {
            will = WillExpression.builder()
                .willId(will.getWillId())
                .type(type)
                .goal(will.getGoal())
                .action(will.getAction())
                .object(will.getObject())
                .timeline(will.getTimeline())
                .deadline(will.getDeadline())
                .priority(priority > 0 ? priority : will.getPriority())
                .constraints(will.getConstraints())
                .originalText(willText)
                .build();
        }
        
        wills.put(will.getWillId(), will);
        willCounter.incrementAndGet();
        
        notifyWillExpressed(will);
        
        log.info("Will expressed: {} type={}, priority={}", 
            will.getWillId(), will.getType(), will.getPriority());
        
        return will;
    }
    
    @Override
    public WillExpression expressWill(String willText) {
        return expressWill(willText, null, 0);
    }
    
    @Override
    public CompletableFuture<WillExpression> expressAndTransform(String willText) {
        return CompletableFuture.supplyAsync(() -> {
            WillExpression will = expressWill(willText);
            
            WillTransformer.WillTransformResult result = transformer.transform(will);
            if (result.isSuccess()) {
                transformResults.put(will.getWillId(), result);
                will.setStatus(WillExpression.WillStatus.TRANSFORMED);
                notifyWillTransformed(will, result);
            }
            
            return will;
        });
    }
    
    @Override
    public Optional<WillExpression> getWill(String willId) {
        return Optional.ofNullable(wills.get(willId));
    }
    
    @Override
    public List<WillExpression> getWillsByType(WillExpression.WillType type) {
        return wills.values().stream()
            .filter(w -> w.getType() == type)
            .sorted(Comparator.comparingInt(WillExpression::getPriority).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<WillExpression> getWillsByStatus(WillExpression.WillStatus status) {
        return wills.values().stream()
            .filter(w -> w.getStatus() == status)
            .sorted(Comparator.comparingInt(WillExpression::getPriority).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<WillExpression> getActiveWills() {
        return wills.values().stream()
            .filter(w -> w.getStatus() == WillExpression.WillStatus.EXECUTING ||
                        w.getStatus() == WillExpression.WillStatus.TRANSFORMED ||
                        w.getStatus() == WillExpression.WillStatus.UNDERSTOOD)
            .sorted(Comparator.comparingInt(WillExpression::getPriority).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<WillExpression> getAllWills() {
        return new ArrayList<>(wills.values());
    }
    
    @Override
    public void cancelWill(String willId) {
        WillExpression will = wills.get(willId);
        if (will != null) {
            will.setStatus(WillExpression.WillStatus.CANCELLED);
            executor.cancel(willId);
            notifyWillCancelled(will);
            log.info("Will cancelled: {}", willId);
        }
    }
    
    @Override
    public void updateWill(String willId, Map<String, Object> updates) {
        WillExpression existing = wills.get(willId);
        if (existing == null) {
            throw new IllegalArgumentException("Will not found: " + willId);
        }
        
        WillExpressionBuilder builder = WillExpression.builder()
            .willId(existing.getWillId())
            .type(existing.getType())
            .status(existing.getStatus())
            .goal(existing.getGoal())
            .timeline(existing.getTimeline())
            .priority(existing.getPriority())
            .constraints(existing.getConstraints())
            .action(existing.getAction())
            .object(existing.getObject())
            .resourceRequirements(existing.getResourceRequirements())
            .deadline(existing.getDeadline())
            .responsible(existing.getResponsible())
            .originalText(existing.getOriginalText())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .metadata(existing.getMetadata());
        
        if (updates.containsKey("goal")) {
            builder.goal((String) updates.get("goal"));
        }
        if (updates.containsKey("priority")) {
            builder.priority((Integer) updates.get("priority"));
        }
        if (updates.containsKey("deadline")) {
            builder.deadline((String) updates.get("deadline"));
        }
        if (updates.containsKey("responsible")) {
            builder.responsible((String) updates.get("responsible"));
        }
        
        WillExpression updated = builder.build();
        wills.put(willId, updated);
        
        log.info("Will updated: {}", willId);
    }
    
    @Override
    public CompletableFuture<WillExecutor.WillExecutionResult> executeWill(String willId) {
        WillExpression will = wills.get(willId);
        if (will == null) {
            return CompletableFuture.completedFuture(
                WillExecutor.WillExecutionResult.failure(willId, "Will not found"));
        }
        return executeWill(will);
    }
    
    @Override
    public CompletableFuture<WillExecutor.WillExecutionResult> executeWill(WillExpression will) {
        if (will == null) {
            return CompletableFuture.completedFuture(
                WillExecutor.WillExecutionResult.failure("unknown", "Will cannot be null"));
        }
        
        WillTransformer.WillTransformResult transformResult = transformResults.get(will.getWillId());
        if (transformResult == null || !transformResult.isSuccess()) {
            transformResult = transformer.transform(will);
            if (!transformResult.isSuccess()) {
                return CompletableFuture.completedFuture(
                    WillExecutor.WillExecutionResult.failure(will.getWillId(), 
                        "Transform failed: " + transformResult.getErrorMessage()));
            }
            transformResults.put(will.getWillId(), transformResult);
        }
        
        List<WillTransformer.ExecutionPlan> plans = transformResult.getPlans();
        if (plans == null || plans.isEmpty()) {
            return CompletableFuture.completedFuture(
                WillExecutor.WillExecutionResult.failure(will.getWillId(), "No execution plans available"));
        }
        
        will.setStatus(WillExpression.WillStatus.EXECUTING);
        notifyWillExecuting(will);
        
        return executor.execute(will, plans.get(0))
            .thenApply(result -> {
                if (result.isSuccess()) {
                    will.setStatus(WillExpression.WillStatus.COMPLETED);
                    notifyWillCompleted(will, result);
                } else {
                    will.setStatus(WillExpression.WillStatus.FAILED);
                    notifyWillFailed(will, new RuntimeException(result.getMessage()));
                }
                return result;
            });
    }
    
    @Override
    public CompletableFuture<WillExecutor.ExecutionStatus> getExecutionStatus(String willId) {
        return executor.monitorExecution(willId);
    }
    
    @Override
    public CompletableFuture<WillExecutor.EffectEvaluation> evaluateWill(String willId) {
        return executor.evaluateEffect(willId);
    }
    
    @Override
    public void addWillListener(WillListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeWillListener(WillListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public int getWillCount() {
        return wills.size();
    }
    
    @Override
    public void clear() {
        wills.clear();
        transformResults.clear();
        log.info("All wills cleared");
    }
    
    private void notifyWillExpressed(WillExpression will) {
        for (WillListener listener : listeners) {
            try {
                listener.onWillExpressed(will);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyWillTransformed(WillExpression will, WillTransformer.WillTransformResult result) {
        for (WillListener listener : listeners) {
            try {
                listener.onWillTransformed(will, result);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyWillExecuting(WillExpression will) {
        for (WillListener listener : listeners) {
            try {
                listener.onWillExecuting(will);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyWillCompleted(WillExpression will, WillExecutor.WillExecutionResult result) {
        for (WillListener listener : listeners) {
            try {
                listener.onWillCompleted(will, result);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyWillFailed(WillExpression will, Throwable error) {
        for (WillListener listener : listeners) {
            try {
                listener.onWillFailed(will, error);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyWillCancelled(WillExpression will) {
        for (WillListener listener : listeners) {
            try {
                listener.onWillCancelled(will);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
}
