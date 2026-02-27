package net.ooder.sdk.orchestration;

// Story 和 Will 类已从外部 llm-sdk 导入
import net.ooder.sdk.story.UserStory;
import net.ooder.sdk.story.StoryStep;
import net.ooder.sdk.story.StoryContext;
import net.ooder.sdk.will.WillExpression;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface StoryOrchestrator {
    
    <T> CompletableFuture<OrchestrationResult<T>> orchestrate(UserStory story);
    
    <T> CompletableFuture<OrchestrationResult<T>> orchestrate(WillExpression will);
    
    <T> CompletableFuture<OrchestrationResult<T>> resume(String storyId);
    
    void pause(String storyId);
    
    void cancel(String storyId);
    
    Optional<OrchestrationStatus> getStatus(String storyId);
    
    List<OrchestrationStatus> getActiveOrchestrations();
    
    void addOrchestrationListener(OrchestrationListener listener);
    
    void removeOrchestrationListener(OrchestrationListener listener);
    
    void setCapabilityRouter(CapabilityRouter router);
    
    void setContextProvider(ContextProvider provider);
    
    /**
     * 编排结果（泛型版本）
     * @param <T> 结果类型
     */
    class OrchestrationResult<T> {
        private String storyId;
        private boolean success;
        private String message;
        private T finalResult;
        private long totalExecutionTime;
        private List<StepExecutionRecord<?>> stepRecords;
        
        public String getStoryId() { return storyId; }
        public void setStoryId(String storyId) { this.storyId = storyId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public T getFinalResult() { return finalResult; }
        public void setFinalResult(T finalResult) { this.finalResult = finalResult; }
        
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public void setTotalExecutionTime(long totalExecutionTime) { this.totalExecutionTime = totalExecutionTime; }
        
        public List<StepExecutionRecord<?>> getStepRecords() { return stepRecords; }
        public void setStepRecords(List<StepExecutionRecord<?>> stepRecords) { this.stepRecords = stepRecords; }
        
        public static <T> OrchestrationResult<T> success(String storyId, T result) {
            OrchestrationResult<T> r = new OrchestrationResult<>();
            r.setStoryId(storyId);
            r.setSuccess(true);
            r.setFinalResult(result);
            return r;
        }
        
        public static <T> OrchestrationResult<T> failure(String storyId, String message) {
            OrchestrationResult<T> r = new OrchestrationResult<>();
            r.setStoryId(storyId);
            r.setSuccess(false);
            r.setMessage(message);
            return r;
        }
    }
    
    /**
     * 非泛型 OrchestrationResult（向后兼容）
     */
    @Deprecated
    default OrchestrationResult<Object> orchestrateLegacy(UserStory story) {
        throw new UnsupportedOperationException("Use orchestrate() instead");
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
    
    /**
     * 步骤执行记录（泛型版本）
     * @param <R> 结果类型
     */
    class StepExecutionRecord<R> {
        private String stepId;
        private String capabilityId;
        private boolean success;
        private R result;
        private String message;
        private long executionTime;
        private long timestamp;
        
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public R getResult() { return result; }
        public void setResult(R result) { this.result = result; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public static <R> StepExecutionRecord<R> success(String stepId, R result) {
            StepExecutionRecord<R> record = new StepExecutionRecord<>();
            record.setStepId(stepId);
            record.setSuccess(true);
            record.setResult(result);
            record.setTimestamp(System.currentTimeMillis());
            return record;
        }
        
        public static <R> StepExecutionRecord<R> failure(String stepId, String message) {
            StepExecutionRecord<R> record = new StepExecutionRecord<>();
            record.setStepId(stepId);
            record.setSuccess(false);
            record.setMessage(message);
            record.setTimestamp(System.currentTimeMillis());
            return record;
        }
    }
    
    interface OrchestrationListener {
        
        void onStoryStarted(UserStory story);
        
        void onStepStarted(String storyId, StoryStep step);
        
        void onStepCompleted(String storyId, StoryStep step, StepExecutionRecord<?> record);
        
        void onStoryCompleted(String storyId, OrchestrationResult<?> result);
        
        void onStoryFailed(String storyId, Throwable error);
        
        void onStoryPaused(String storyId);
        
        void onStoryCancelled(String storyId);
    }
    
    interface ContextProvider {
        
        StoryContext provideContext(UserStory story);
        
        void enrichContext(StoryContext context, String capabilityId);
    }
}
