package net.ooder.scene.provider;

/**
 * 系统状态
 */
public class SystemStatus {
    private String status;
    private String message;
    private long timestamp;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;
    private int threadCount;
    private int activeThreads;
    private long totalMemory;
    private long freeMemory;
    private long maxMemory;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
    public int getActiveThreads() { return activeThreads; }
    public void setActiveThreads(int activeThreads) { this.activeThreads = activeThreads; }
    public long getTotalMemory() { return totalMemory; }
    public void setTotalMemory(long totalMemory) { this.totalMemory = totalMemory; }
    public long getFreeMemory() { return freeMemory; }
    public void setFreeMemory(long freeMemory) { this.freeMemory = freeMemory; }
    public long getMaxMemory() { return maxMemory; }
    public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }
}
