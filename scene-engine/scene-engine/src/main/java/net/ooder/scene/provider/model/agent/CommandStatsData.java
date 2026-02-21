package net.ooder.scene.provider.model.agent;

public class CommandStatsData {
    
    private long totalCommands;
    private double successRate;
    private long avgLatency;
    private long maxLatency;
    private long minLatency;
    private long failedCount;
    private long timeoutCount;
    
    public long getTotalCommands() {
        return totalCommands;
    }
    
    public void setTotalCommands(long totalCommands) {
        this.totalCommands = totalCommands;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
    
    public long getAvgLatency() {
        return avgLatency;
    }
    
    public void setAvgLatency(long avgLatency) {
        this.avgLatency = avgLatency;
    }
    
    public long getMaxLatency() {
        return maxLatency;
    }
    
    public void setMaxLatency(long maxLatency) {
        this.maxLatency = maxLatency;
    }
    
    public long getMinLatency() {
        return minLatency;
    }
    
    public void setMinLatency(long minLatency) {
        this.minLatency = minLatency;
    }
    
    public long getFailedCount() {
        return failedCount;
    }
    
    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }
    
    public long getTimeoutCount() {
        return timeoutCount;
    }
    
    public void setTimeoutCount(long timeoutCount) {
        this.timeoutCount = timeoutCount;
    }
}
