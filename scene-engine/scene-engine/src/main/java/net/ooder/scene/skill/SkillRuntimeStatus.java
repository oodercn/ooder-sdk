package net.ooder.scene.skill;

public class SkillRuntimeStatus {
    private String skillId;
    private String status;
    private long startTime;
    private long uptime;
    private long requestCount;
    private long errorCount;
    private double cpuUsage;
    private double memoryUsage;
    private String lastError;

    public SkillRuntimeStatus() {}

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
    public long getRequestCount() { return requestCount; }
    public void setRequestCount(long requestCount) { this.requestCount = requestCount; }
    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }

    public boolean isRunning() {
        return "RUNNING".equals(status);
    }

    public double getErrorRate() {
        if (requestCount == 0) return 0.0;
        return (double) errorCount / requestCount * 100;
    }
}
