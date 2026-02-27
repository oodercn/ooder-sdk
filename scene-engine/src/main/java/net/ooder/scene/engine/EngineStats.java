package net.ooder.scene.engine;

/**
 * 引擎统计信息
 */
public class EngineStats {
    private String engineType;
    private long requestCount;
    private long successCount;
    private long failureCount;
    private long avgResponseTime;
    private long maxResponseTime;
    private long minResponseTime;
    private long lastRequestTime;
    private long uptime;

    public EngineStats() {}

    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }
    public long getRequestCount() { return requestCount; }
    public void setRequestCount(long requestCount) { this.requestCount = requestCount; }
    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }
    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
    public long getAvgResponseTime() { return avgResponseTime; }
    public void setAvgResponseTime(long avgResponseTime) { this.avgResponseTime = avgResponseTime; }
    public long getMaxResponseTime() { return maxResponseTime; }
    public void setMaxResponseTime(long maxResponseTime) { this.maxResponseTime = maxResponseTime; }
    public long getMinResponseTime() { return minResponseTime; }
    public void setMinResponseTime(long minResponseTime) { this.minResponseTime = minResponseTime; }
    public long getLastRequestTime() { return lastRequestTime; }
    public void setLastRequestTime(long lastRequestTime) { this.lastRequestTime = lastRequestTime; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }

    public double getSuccessRate() {
        if (requestCount == 0) return 0.0;
        return (double) successCount / requestCount * 100;
    }

    public double getFailureRate() {
        if (requestCount == 0) return 0.0;
        return (double) failureCount / requestCount * 100;
    }
}
