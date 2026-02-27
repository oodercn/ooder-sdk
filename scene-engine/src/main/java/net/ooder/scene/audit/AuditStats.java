package net.ooder.scene.audit;

import java.util.Map;

/**
 * 审计统计信息
 */
public class AuditStats {
    private long totalCount;
    private long successCount;
    private long failureCount;
    private Map<String, Long> eventTypeCounts;
    private Map<String, Long> actionCounts;
    private Map<String, Long> severityCounts;
    private long startTime;
    private long endTime;

    public AuditStats() {}

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
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public double getSuccessRate() {
        if (totalCount == 0) return 0.0;
        return (double) successCount / totalCount * 100;
    }
}
