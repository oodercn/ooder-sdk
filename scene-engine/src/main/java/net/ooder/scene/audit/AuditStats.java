package net.ooder.scene.audit;

import java.util.Map;

/**
 * 审计统计信息
 *
 * <p>统一审计统计类，合并了 skill/audit 和 audit 包的字段</p>
 *
 * @author Ooder Team
 * @since 2.3
 */
public class AuditStats {

    // 来自 audit 包的字段
    private long totalCount;
    private long successCount;
    private long failureCount;
    private Map<String, Long> eventTypeCounts;
    private Map<String, Long> actionCounts;
    private Map<String, Long> severityCounts;

    // 来自 skill/audit 包的字段
    private String userId;
    private long totalOperations;
    private Map<String, Long> operationCounts;
    private Map<String, Long> resourceTypeCounts;

    // 共同字段
    private long startTime;
    private long endTime;

    public AuditStats() {}

    // Getters and Setters for audit 包字段
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }

    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }

    public Map<String, Long> getEventTypeCounts() { return eventTypeCounts; }
    public void setEventTypeCounts(Map<String, Long> eventTypeCounts) { this.eventTypeCounts = eventTypeCounts; }

    public Map<String, Long> getActionCounts() { return actionCounts; }
    public void setActionCounts(Map<String, Long> actionCounts) { this.actionCounts = actionCounts; }

    public Map<String, Long> getSeverityCounts() { return severityCounts; }
    public void setSeverityCounts(Map<String, Long> severityCounts) { this.severityCounts = severityCounts; }

    // Getters and Setters for skill/audit 包字段
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTotalOperations() { return totalOperations; }
    public void setTotalOperations(long totalOperations) { this.totalOperations = totalOperations; }

    public Map<String, Long> getOperationCounts() { return operationCounts; }
    public void setOperationCounts(Map<String, Long> operationCounts) { this.operationCounts = operationCounts; }

    public Map<String, Long> getResourceTypeCounts() { return resourceTypeCounts; }
    public void setResourceTypeCounts(Map<String, Long> resourceTypeCounts) { this.resourceTypeCounts = resourceTypeCounts; }

    // Getters and Setters for 共同字段
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    // 计算方法
    public double getSuccessRate() {
        long total = totalCount > 0 ? totalCount : totalOperations;
        if (total == 0) return 0.0;
        return (double) successCount / total * 100;
    }
}
