package net.ooder.scene.provider.model.network;

public class SystemStatus {
    
    private double cpuUsage;
    private long memoryUsed;
    private long memoryTotal;
    private double memoryUsage;
    private long uptime;
    private String hostname;
    private String osVersion;
    private String kernelVersion;
    private double loadAverage1;
    private double loadAverage5;
    private double loadAverage15;
    
    public double getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public long getMemoryUsed() {
        return memoryUsed;
    }
    
    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }
    
    public long getMemoryTotal() {
        return memoryTotal;
    }
    
    public void setMemoryTotal(long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }
    
    public double getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public long getUptime() {
        return uptime;
    }
    
    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public String getOsVersion() {
        return osVersion;
    }
    
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getKernelVersion() {
        return kernelVersion;
    }
    
    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }
    
    public double getLoadAverage1() {
        return loadAverage1;
    }
    
    public void setLoadAverage1(double loadAverage1) {
        this.loadAverage1 = loadAverage1;
    }
    
    public double getLoadAverage5() {
        return loadAverage5;
    }
    
    public void setLoadAverage5(double loadAverage5) {
        this.loadAverage5 = loadAverage5;
    }
    
    public double getLoadAverage15() {
        return loadAverage15;
    }
    
    public void setLoadAverage15(double loadAverage15) {
        this.loadAverage15 = loadAverage15;
    }
}
