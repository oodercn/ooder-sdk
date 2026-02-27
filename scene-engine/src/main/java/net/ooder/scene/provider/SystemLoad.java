package net.ooder.scene.provider;

/**
 * 系统负载
 */
public class SystemLoad {
    private double cpuLoad;
    private double memoryUsage;
    private double diskUsage;
    private double networkUsage;
    private int processCount;
    private int threadCount;
    private long timestamp;

    public double getCpuLoad() { return cpuLoad; }
    public void setCpuLoad(double cpuLoad) { this.cpuLoad = cpuLoad; }
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
    public double getNetworkUsage() { return networkUsage; }
    public void setNetworkUsage(double networkUsage) { this.networkUsage = networkUsage; }
    public int getProcessCount() { return processCount; }
    public void setProcessCount(int processCount) { this.processCount = processCount; }
    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
