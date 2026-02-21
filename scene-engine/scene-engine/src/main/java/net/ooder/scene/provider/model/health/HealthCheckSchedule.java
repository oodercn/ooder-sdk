package net.ooder.scene.provider.model.health;

public class HealthCheckSchedule {
    
    private String scheduleId;
    private String cron;
    private boolean enabled;
    private String checkType;
    private long nextRunAt;
    private long lastRunAt;
    
    public String getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getCron() {
        return cron;
    }
    
    public void setCron(String cron) {
        this.cron = cron;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getCheckType() {
        return checkType;
    }
    
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
    
    public long getNextRunAt() {
        return nextRunAt;
    }
    
    public void setNextRunAt(long nextRunAt) {
        this.nextRunAt = nextRunAt;
    }
    
    public long getLastRunAt() {
        return lastRunAt;
    }
    
    public void setLastRunAt(long lastRunAt) {
        this.lastRunAt = lastRunAt;
    }
}
