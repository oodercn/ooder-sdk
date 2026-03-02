package net.ooder.scene.monitor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 性能监控接口
 * 提供场景性能指标监控功能
 *
 * @author ooder
 * @since 2.3
 */
public interface PerformanceMonitor {
    
    /**
     * 获取当前性能指标
     * @param sceneId 场景ID
     * @return 当前性能指标
     */
    CompletableFuture<CurrentMetrics> getCurrentMetrics(String sceneId);
    
    /**
     * 获取性能历史数据
     * @param sceneId 场景ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔(秒)
     * @return 性能历史数据
     */
    CompletableFuture<PerformanceHistory> getPerformanceHistory(
            String sceneId, 
            long startTime, 
            long endTime, 
            int interval
    );
}

/**
 * 当前性能指标
 */
class CurrentMetrics {
    private String sceneId;
    private double cpuUsage;
    private double memoryUsage;
    private long memoryUsed;
    private long memoryTotal;
    private double diskUsage;
    private double networkInRate;
    private double networkOutRate;
    private int threadCount;
    private int connectionCount;
    private long totalRequests;
    private double qps;
    private double avgLatency;
    private long errorCount;
    private long timestamp;
    
    // Getters and Setters
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public long getMemoryUsed() { return memoryUsed; }
    public void setMemoryUsed(long memoryUsed) { this.memoryUsed = memoryUsed; }
    public long getMemoryTotal() { return memoryTotal; }
    public void setMemoryTotal(long memoryTotal) { this.memoryTotal = memoryTotal; }
    public double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
    public double getNetworkInRate() { return networkInRate; }
    public void setNetworkInRate(double networkInRate) { this.networkInRate = networkInRate; }
    public double getNetworkOutRate() { return networkOutRate; }
    public void setNetworkOutRate(double networkOutRate) { this.networkOutRate = networkOutRate; }
    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
    public int getConnectionCount() { return connectionCount; }
    public void setConnectionCount(int connectionCount) { this.connectionCount = connectionCount; }
    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
    public double getQps() { return qps; }
    public void setQps(double qps) { this.qps = qps; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * 性能历史数据
 */
class PerformanceHistory {
    private String sceneId;
    private List<MetricPoint> cpuHistory;
    private List<MetricPoint> memoryHistory;
    private List<MetricPoint> diskHistory;
    private List<MetricPoint> networkInHistory;
    private List<MetricPoint> networkOutHistory;
    
    // Getters and Setters
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public List<MetricPoint> getCpuHistory() { return cpuHistory; }
    public void setCpuHistory(List<MetricPoint> cpuHistory) { this.cpuHistory = cpuHistory; }
    public List<MetricPoint> getMemoryHistory() { return memoryHistory; }
    public void setMemoryHistory(List<MetricPoint> memoryHistory) { this.memoryHistory = memoryHistory; }
    public List<MetricPoint> getDiskHistory() { return diskHistory; }
    public void setDiskHistory(List<MetricPoint> diskHistory) { this.diskHistory = diskHistory; }
    public List<MetricPoint> getNetworkInHistory() { return networkInHistory; }
    public void setNetworkInHistory(List<MetricPoint> networkInHistory) { this.networkInHistory = networkInHistory; }
    public List<MetricPoint> getNetworkOutHistory() { return networkOutHistory; }
    public void setNetworkOutHistory(List<MetricPoint> networkOutHistory) { this.networkOutHistory = networkOutHistory; }
}

/**
 * 指标数据点
 */
class MetricPoint {
    private long timestamp;
    private double value;
    
    public MetricPoint() {}
    
    public MetricPoint(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
    
    // Getters and Setters
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
