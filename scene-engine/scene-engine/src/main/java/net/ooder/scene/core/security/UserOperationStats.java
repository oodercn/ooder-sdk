package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 用户操作统计
 */
public class UserOperationStats {
    private String userId;
    private long totalOperations;
    private long successCount;
    private long failureCount;
    private long deniedCount;
    private Map<String, Long> operationCounts;
    private Map<String, Long> resourceCounts;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getTotalOperations() { return totalOperations; }
    public void setTotalOperations(long totalOperations) { this.totalOperations = totalOperations; }
    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }
    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
    public long getDeniedCount() { return deniedCount; }
    public void setDeniedCount(long deniedCount) { this.deniedCount = deniedCount; }
    public Map<String, Long> getOperationCounts() { return operationCounts; }
    public void setOperationCounts(Map<String, Long> operationCounts) { this.operationCounts = operationCounts; }
    public Map<String, Long> getResourceCounts() { return resourceCounts; }
    public void setResourceCounts(Map<String, Long> resourceCounts) { this.resourceCounts = resourceCounts; }
}
