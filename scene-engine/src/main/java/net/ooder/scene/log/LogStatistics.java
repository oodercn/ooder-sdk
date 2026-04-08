package net.ooder.scene.log;

import java.util.Map;

public class LogStatistics {

    private long totalCount;
    private long successCount;
    private long errorCount;
    private long warnCount;
    private long infoCount;
    private long debugCount;
    private long fatalCount;
    private long startTime;
    private long endTime;
    private Map<String, Long> categoryCounts;
    private Map<String, Long> sourceCounts;

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }

    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }

    public long getWarnCount() { return warnCount; }
    public void setWarnCount(long warnCount) { this.warnCount = warnCount; }

    public long getInfoCount() { return infoCount; }
    public void setInfoCount(long infoCount) { this.infoCount = infoCount; }

    public long getDebugCount() { return debugCount; }
    public void setDebugCount(long debugCount) { this.debugCount = debugCount; }

    public long getFatalCount() { return fatalCount; }
    public void setFatalCount(long fatalCount) { this.fatalCount = fatalCount; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public Map<String, Long> getCategoryCounts() { return categoryCounts; }
    public void setCategoryCounts(Map<String, Long> categoryCounts) { this.categoryCounts = categoryCounts; }

    public Map<String, Long> getSourceCounts() { return sourceCounts; }
    public void setSourceCounts(Map<String, Long> sourceCounts) { this.sourceCounts = sourceCounts; }
}
