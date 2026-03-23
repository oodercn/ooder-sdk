package net.ooder.sdk.api.security;

import java.util.List;

public interface KeyUsageLogService {
    
    KeyUsageLog recordLog(KeyUsageLog log);
    
    KeyUsageLog getLog(String logId);
    
    List<KeyUsageLog> getLogsByKey(String keyId);
    
    List<KeyUsageLog> getLogsByScene(String sceneGroupId);
    
    List<KeyUsageLog> getLogsByOperator(String operatorId);
    
    List<KeyUsageLog> getRecentLogs(int limit);
    
    List<KeyUsageLog> queryLogs(LogQueryRequest request);
    
    void clearOldLogs(long beforeTimestamp);
    
    KeyUsageStats getStatsByKey(String keyId);
    
    KeyUsageStats getOverallStats();
    
    class LogQueryRequest {
        private String keyId;
        private String operatorId;
        private String sceneGroupId;
        private Long startTime;
        private Long endTime;
        private Boolean success;
        private String operation;
        private int pageNum;
        private int pageSize;
        
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        
        public String getOperatorId() { return operatorId; }
        public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
        
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        
        public Long getStartTime() { return startTime; }
        public void setStartTime(Long startTime) { this.startTime = startTime; }
        
        public Long getEndTime() { return endTime; }
        public void setEndTime(Long endTime) { this.endTime = endTime; }
        
        public Boolean getSuccess() { return success; }
        public void setSuccess(Boolean success) { this.success = success; }
        
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
        
        public int getPageNum() { return pageNum; }
        public void setPageNum(int pageNum) { this.pageNum = pageNum; }
        
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    }
    
    class KeyUsageStats {
        private String keyId;
        private long totalUsage;
        private long successCount;
        private long failureCount;
        private long todayUsage;
        private long avgDuration;
        private String lastUsedAt;
        
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        
        public long getTotalUsage() { return totalUsage; }
        public void setTotalUsage(long totalUsage) { this.totalUsage = totalUsage; }
        
        public long getSuccessCount() { return successCount; }
        public void setSuccessCount(long successCount) { this.successCount = successCount; }
        
        public long getFailureCount() { return failureCount; }
        public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
        
        public long getTodayUsage() { return todayUsage; }
        public void setTodayUsage(long todayUsage) { this.todayUsage = todayUsage; }
        
        public long getAvgDuration() { return avgDuration; }
        public void setAvgDuration(long avgDuration) { this.avgDuration = avgDuration; }
        
        public String getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(String lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    }
}
