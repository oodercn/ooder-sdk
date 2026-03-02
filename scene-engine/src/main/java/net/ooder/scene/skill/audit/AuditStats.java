package net.ooder.scene.skill.audit;

import java.util.Map;

/**
 * 审计统计信息
 *
 * @author ooder
 * @since 2.3
 */
public class AuditStats {
    
    /** 用户ID */
    private String userId;
    
    /** 总操作次数 */
    private long totalOperations;
    
    /** 成功次数 */
    private long successCount;
    
    /** 失败次数 */
    private long failureCount;
    
    /** 按操作类型统计 */
    private Map<String, Long> operationCounts;
    
    /** 按资源类型统计 */
    private Map<String, Long> resourceTypeCounts;
    
    /** 统计开始时间 */
    private long startTime;
    
    /** 统计结束时间 */
    private long endTime;
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public long getTotalOperations() { return totalOperations; }
    public void setTotalOperations(long totalOperations) { this.totalOperations = totalOperations; }
    
    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }
    
    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
    
    public Map<String, Long> getOperationCounts() { return operationCounts; }
    public void setOperationCounts(Map<String, Long> operationCounts) { this.operationCounts = operationCounts; }
    
    public Map<String, Long> getResourceTypeCounts() { return resourceTypeCounts; }
    public void setResourceTypeCounts(Map<String, Long> resourceTypeCounts) { this.resourceTypeCounts = resourceTypeCounts; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
}
