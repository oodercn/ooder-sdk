package net.ooder.sdk.story;

import java.util.List;
import java.util.Map;

public interface StoryStep {
    
    String getStepId();
    
    String getName();
    
    String getDescription();
    
    StepType getType();
    
    String getCapabilityId();
    
    Map<String, Object> getParams();
    
    List<String> getDependencies();
    
    StepStatus getStatus();
    
    void setStatus(StepStatus status);
    
    StepResult getResult();
    
    void setResult(StepResult result);
    
    enum StepType {
        ACTION,
        QUERY,
        DECISION,
        WAIT,
        PARALLEL,
        LOOP
    }
    
    enum StepStatus {
        PENDING,
        READY,
        RUNNING,
        COMPLETED,
        FAILED,
        SKIPPED
    }
    
    class StepResult {
        private boolean success;
        private Object data;
        private String message;
        private long executionTime;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public static StepResult success(Object data) {
            StepResult result = new StepResult();
            result.setSuccess(true);
            result.setData(data);
            return result;
        }
        
        public static StepResult failure(String message) {
            StepResult result = new StepResult();
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
    
    static StoryStepBuilder builder() {
        return new StoryStepBuilder();
    }
}
