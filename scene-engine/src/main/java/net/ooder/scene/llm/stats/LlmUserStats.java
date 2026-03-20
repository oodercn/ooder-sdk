package net.ooder.scene.llm.stats;

import java.util.List;

/**
 * 用户级 LLM 统计
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmUserStats {
    
    private String userId;
    private String userName;
    private String departmentId;
    private String departmentName;
    
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    private double totalCost;
    private double quotaLimit;
    private double quotaUsed;
    private double quotaUsedPercent;
    
    private double avgLatency;
    
    private List<LlmModuleStats> moduleStats;
    
    private long statsTime;
    
    public LlmUserStats() {}
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(long totalCalls) { this.totalCalls = totalCalls; }
    public long getSuccessCalls() { return successCalls; }
    public void setSuccessCalls(long successCalls) { this.successCalls = successCalls; }
    public long getFailedCalls() { return failedCalls; }
    public void setFailedCalls(long failedCalls) { this.failedCalls = failedCalls; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
    public long getTotalInputTokens() { return totalInputTokens; }
    public void setTotalInputTokens(long totalInputTokens) { this.totalInputTokens = totalInputTokens; }
    public long getTotalOutputTokens() { return totalOutputTokens; }
    public void setTotalOutputTokens(long totalOutputTokens) { this.totalOutputTokens = totalOutputTokens; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getQuotaLimit() { return quotaLimit; }
    public void setQuotaLimit(double quotaLimit) { this.quotaLimit = quotaLimit; }
    public double getQuotaUsed() { return quotaUsed; }
    public void setQuotaUsed(double quotaUsed) { this.quotaUsed = quotaUsed; }
    public double getQuotaUsedPercent() { return quotaUsedPercent; }
    public void setQuotaUsedPercent(double quotaUsedPercent) { this.quotaUsedPercent = quotaUsedPercent; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
    public List<LlmModuleStats> getModuleStats() { return moduleStats; }
    public void setModuleStats(List<LlmModuleStats> moduleStats) { this.moduleStats = moduleStats; }
    public long getStatsTime() { return statsTime; }
    public void setStatsTime(long statsTime) { this.statsTime = statsTime; }
}
