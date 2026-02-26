package net.ooder.sdk.orchestration;

import net.ooder.sdk.story.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface StoryOrchestrator {
    
    CompletableFuture<OrchestrationResult> orchestrate(UserStory story);
    
    CompletableFuture<OrchestrationResult> orchestrate(WillTransformer.WillExpression will);
    
    CompletableFuture<OrchestrationResult> resume(String storyId);
    
    void pause(String storyId);
    
    void cancel(String storyId);
    
    Optional<OrchestrationStatus> getStatus(String storyId);
    
    List<OrchestrationStatus> getActiveOrchestrations();
    
    void addOrchestrationListener(OrchestrationListener listener);
    
    void removeOrchestrationListener(OrchestrationListener listener);
    
    void setCapabilityRouter(CapabilityRouter router);
    
    void setContextProvider(ContextProvider provider);
    
    class OrchestrationResult {
        private String storyId;
        private boolean success;
        private String message;
        private Object finalResult;
        private long totalExecutionTime;
        private List<StepExecutionRecord> stepRecords;
        
        public String getStoryId() { return storyId; }
        public void setStoryId(String storyId) { this.storyId = storyId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getFinalResult() { return finalResult; }
        public void setFinalResult(Object finalResult) { this.finalResult = finalResult; }
        
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public void setTotalExecutionTime(long totalExecutionTime) { this.totalExecutionTime = totalExecutionTime; }
        
        public List<StepExecutionRecord> getStepRecords() { return stepRecords; }
        public void setStepRecords(List<StepExecutionRecord> stepRecords) { this.stepRecords = stepRecords; }
        
        public static OrchestrationResult success(String storyId, Object result) {
            OrchestrationResult r = new OrchestrationResult();
            r.setStoryId(storyId);
            r.setSuccess(true);
            r.setFinalResult(result);
            return r;
        }
        
        public static OrchestrationResult failure(String storyId, String message) {
            OrchestrationResult r = new OrchestrationResult();
            r.setStoryId(storyId);
            r.setSuccess(false);
            r.setMessage(message);
            return r;
        }
    }
    
    class OrchestrationStatus {
        private String storyId;
        private String status;
        private double progress;
        private String currentStep;
        private int completedSteps;
        private int totalSteps;
        
        public String getStoryId() { return storyId; }
        public void setStoryId(String storyId) { this.storyId = storyId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    }
    
    class StepExecutionRecord {
        private String stepId;
        private String capabilityId;
        private boolean success;
        private Object result;
        private String message;
        private long executionTime;
        private long timestamp;
        
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    interface OrchestrationListener {
        
        void onStoryStarted(UserStory story);
        
        void onStepStarted(String storyId, StoryStep step);
        
        void onStepCompleted(String storyId, StoryStep step, StepExecutionRecord record);
        
        void onStoryCompleted(String storyId, OrchestrationResult result);
        
        void onStoryFailed(String storyId, Throwable error);
        
        void onStoryPaused(String storyId);
        
        void onStoryCancelled(String storyId);
    }
    
    interface ContextProvider {
        
        StoryContext provideContext(UserStory story);
        
        void enrichContext(StoryContext context, String capabilityId);
    }
}
