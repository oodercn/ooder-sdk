package net.ooder.skills.md;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SkillExecutionEngine {
    
    CompletableFuture<SkillExecutionResult> execute(String skillId, Map<String, Object> params);
    
    CompletableFuture<SkillExecutionResult> execute(SkillMdDocument skill, Map<String, Object> params);
    
    void registerExecutor(String skillId, SkillExecutor executor);
    
    void unregisterExecutor(String skillId);
    
    boolean hasExecutor(String skillId);
    
    void setDefaultTimeout(long timeoutMillis);
    
    long getDefaultTimeout();
    
    void addExecutionListener(SkillExecutionListener listener);
    
    void removeExecutionListener(SkillExecutionListener listener);
    
    SkillExecutionStats getStats(String skillId);
    
    List<SkillExecutionRecord> getRecentExecutions(int limit);
    
    class SkillExecutionResult {
        private String executionId;
        private String skillId;
        private boolean success;
        private Object data;
        private String message;
        private long executionTime;
        private Map<String, Object> metadata;
        
        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public static SkillExecutionResult success(String skillId, Object data) {
            SkillExecutionResult result = new SkillExecutionResult();
            result.setSkillId(skillId);
            result.setSuccess(true);
            result.setData(data);
            result.setMessage("Success");
            return result;
        }
        
        public static SkillExecutionResult failure(String skillId, String message) {
            SkillExecutionResult result = new SkillExecutionResult();
            result.setSkillId(skillId);
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
    
    class SkillExecutionStats {
        private String skillId;
        private long totalExecutions;
        private long successCount;
        private long failureCount;
        private double successRate;
        private long avgExecutionTime;
        private long maxExecutionTime;
        private long minExecutionTime;
        private long lastExecutionTime;
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        
        public long getTotalExecutions() { return totalExecutions; }
        public void setTotalExecutions(long totalExecutions) { this.totalExecutions = totalExecutions; }
        
        public long getSuccessCount() { return successCount; }
        public void setSuccessCount(long successCount) { this.successCount = successCount; }
        
        public long getFailureCount() { return failureCount; }
        public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
        
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        
        public long getAvgExecutionTime() { return avgExecutionTime; }
        public void setAvgExecutionTime(long avgExecutionTime) { this.avgExecutionTime = avgExecutionTime; }
        
        public long getMaxExecutionTime() { return maxExecutionTime; }
        public void setMaxExecutionTime(long maxExecutionTime) { this.maxExecutionTime = maxExecutionTime; }
        
        public long getMinExecutionTime() { return minExecutionTime; }
        public void setMinExecutionTime(long minExecutionTime) { this.minExecutionTime = minExecutionTime; }
        
        public long getLastExecutionTime() { return lastExecutionTime; }
        public void setLastExecutionTime(long lastExecutionTime) { this.lastExecutionTime = lastExecutionTime; }
    }
    
    class SkillExecutionRecord {
        private String executionId;
        private String skillId;
        private boolean success;
        private long executionTime;
        private long timestamp;
        private Map<String, Object> params;
        private Object result;
        
        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
        
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
    }
    
    interface SkillExecutor {
        
        CompletableFuture<SkillExecutionResult> execute(Map<String, Object> params);
        
        String getExecutorId();
        
        boolean isAvailable();
    }
    
    interface SkillExecutionListener {
        
        void onExecutionStarted(String skillId, Map<String, Object> params);
        
        void onExecutionCompleted(String skillId, SkillExecutionResult result);
        
        void onExecutionFailed(String skillId, Throwable error);
    }
}
