package net.ooder.scene.llm.stats;

/**
 * 统计时间范围
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class StatsTimeRange {
    
    private long startTime;
    private long endTime;
    private StatsGranularity granularity;
    
    public StatsTimeRange() {}
    
    public StatsTimeRange(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.granularity = StatsGranularity.DAY;
    }
    
    public StatsTimeRange(long startTime, long endTime, StatsGranularity granularity) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.granularity = granularity;
    }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public StatsGranularity getGranularity() { return granularity; }
    public void setGranularity(StatsGranularity granularity) { this.granularity = granularity; }
    
    /**
     * 统计粒度
     */
    public enum StatsGranularity {
        HOUR,
        DAY,
        WEEK,
        MONTH
    }
}
