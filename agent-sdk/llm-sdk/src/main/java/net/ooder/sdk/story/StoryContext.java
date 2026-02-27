package net.ooder.sdk.story;

import java.util.List;
import java.util.Map;

public interface StoryContext {
    
    String getContextId();
    
    String getStoryId();
    
    Map<String, Object> getVariables();
    
    void setVariable(String key, Object value);
    
    Object getVariable(String key);
    
    Map<String, Object> getEnvironment();
    
    List<String> getAvailableCapabilities();
    
    Map<String, Object> getAssetSummary();
    
    List<ExecutionRecord> getExecutionHistory();
    
    void addExecutionRecord(ExecutionRecord record);
    
    String getCurrentStepId();
    
    void setCurrentStepId(String stepId);
    
    class ExecutionRecord {
        private String stepId;
        private String capabilityId;
        private boolean success;
        private Object result;
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
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    static StoryContext create(String storyId) {
        return new StoryContextImpl(storyId);
    }
}
