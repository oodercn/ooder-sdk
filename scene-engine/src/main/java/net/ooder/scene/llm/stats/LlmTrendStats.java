package net.ooder.scene.llm.stats;

import java.util.Map;

/**
 * LLM 趋势统计
 * 
 * <p>包含相比上一周期的趋势变化百分比。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmTrendStats {
    
    private String companyId;
    private long startTime;
    private long endTime;
    private long statsTime;
    
    private long totalCalls;
    private long previousCalls;
    private double callsTrend;
    
    private long totalTokens;
    private long previousTokens;
    private double tokensTrend;
    
    private double totalCost;
    private double previousCost;
    private double costTrend;
    
    private double avgLatency;
    private double previousAvgLatency;
    private double latencyTrend;
    
    private double successRate;
    private double previousSuccessRate;
    private double successRateTrend;
    
    private Map<String, Long> providerDistribution;
    private Map<String, Long> modelDistribution;
    
    public LlmTrendStats() {}
    
    public String getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public long getStatsTime() {
        return statsTime;
    }
    
    public void setStatsTime(long statsTime) {
        this.statsTime = statsTime;
    }
    
    public long getTotalCalls() {
        return totalCalls;
    }
    
    public void setTotalCalls(long totalCalls) {
        this.totalCalls = totalCalls;
    }
    
    public long getPreviousCalls() {
        return previousCalls;
    }
    
    public void setPreviousCalls(long previousCalls) {
        this.previousCalls = previousCalls;
    }
    
    public double getCallsTrend() {
        return callsTrend;
    }
    
    public void setCallsTrend(double callsTrend) {
        this.callsTrend = callsTrend;
    }
    
    public long getTotalTokens() {
        return totalTokens;
    }
    
    public void setTotalTokens(long totalTokens) {
        this.totalTokens = totalTokens;
    }
    
    public long getPreviousTokens() {
        return previousTokens;
    }
    
    public void setPreviousTokens(long previousTokens) {
        this.previousTokens = previousTokens;
    }
    
    public double getTokensTrend() {
        return tokensTrend;
    }
    
    public void setTokensTrend(double tokensTrend) {
        this.tokensTrend = tokensTrend;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public double getPreviousCost() {
        return previousCost;
    }
    
    public void setPreviousCost(double previousCost) {
        this.previousCost = previousCost;
    }
    
    public double getCostTrend() {
        return costTrend;
    }
    
    public void setCostTrend(double costTrend) {
        this.costTrend = costTrend;
    }
    
    public double getAvgLatency() {
        return avgLatency;
    }
    
    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }
    
    public double getPreviousAvgLatency() {
        return previousAvgLatency;
    }
    
    public void setPreviousAvgLatency(double previousAvgLatency) {
        this.previousAvgLatency = previousAvgLatency;
    }
    
    public double getLatencyTrend() {
        return latencyTrend;
    }
    
    public void setLatencyTrend(double latencyTrend) {
        this.latencyTrend = latencyTrend;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
    
    public double getPreviousSuccessRate() {
        return previousSuccessRate;
    }
    
    public void setPreviousSuccessRate(double previousSuccessRate) {
        this.previousSuccessRate = previousSuccessRate;
    }
    
    public double getSuccessRateTrend() {
        return successRateTrend;
    }
    
    public void setSuccessRateTrend(double successRateTrend) {
        this.successRateTrend = successRateTrend;
    }
    
    public Map<String, Long> getProviderDistribution() {
        return providerDistribution;
    }
    
    public void setProviderDistribution(Map<String, Long> providerDistribution) {
        this.providerDistribution = providerDistribution;
    }
    
    public Map<String, Long> getModelDistribution() {
        return modelDistribution;
    }
    
    public void setModelDistribution(Map<String, Long> modelDistribution) {
        this.modelDistribution = modelDistribution;
    }
    
    public static double calculateTrend(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100.0;
    }
}
