package net.ooder.skill.common.spi.calendar;

public class TimeSlot {
    
    private long startTime;
    private long endTime;
    private boolean available;
    
    public TimeSlot() {}
    
    public TimeSlot(long startTime, long endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }
    
    public static TimeSlot available(long startTime, long endTime) {
        return new TimeSlot(startTime, endTime, true);
    }
    
    public static TimeSlot busy(long startTime, long endTime) {
        return new TimeSlot(startTime, endTime, false);
    }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
