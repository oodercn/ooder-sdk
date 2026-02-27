package net.ooder.scene.provider;

/**
 * 日志统计
 */
public class LogStatistics {
    private long totalCount;
    private long errorCount;
    private long warnCount;
    private long infoCount;
    private long debugCount;
    private long traceCount;
    private long startTime;
    private long endTime;

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
    public long getWarnCount() { return warnCount; }
    public void setWarnCount(long warnCount) { this.warnCount = warnCount; }
    public long getInfoCount() { return infoCount; }
    public void setInfoCount(long infoCount) { this.infoCount = infoCount; }
    public long getDebugCount() { return debugCount; }
    public void setDebugCount(long debugCount) { this.debugCount = debugCount; }
    public long getTraceCount() { return traceCount; }
    public void setTraceCount(long traceCount) { this.traceCount = traceCount; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
}
