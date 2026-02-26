package net.ooder.sdk.api.lifecycle;

import java.util.Map;

/**
 * Skill 生命周期事件
 * 
 * 包含事件完整信息：时间戳、状态、元数据
 */
public class SkillLifecycleEvent {
    
    private String eventId;
    private String skillId;
    private String skillName;
    private SkillLifecycleEventType eventType;
    private long timestamp;
    private String source;
    private Map<String, Object> metadata;
    private Throwable error;
    
    public SkillLifecycleEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public SkillLifecycleEvent(String skillId, SkillLifecycleEventType eventType) {
        this();
        this.skillId = skillId;
        this.eventType = eventType;
    }
    
    // Getters and Setters
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    
    public SkillLifecycleEventType getEventType() {
        return eventType;
    }
    
    public void setEventType(SkillLifecycleEventType eventType) {
        this.eventType = eventType;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public Throwable getError() {
        return error;
    }
    
    public void setError(Throwable error) {
        this.error = error;
    }
    
    @Override
    public String toString() {
        return "SkillLifecycleEvent{" +
                "eventId='" + eventId + '\'' +
                ", skillId='" + skillId + '\'' +
                ", eventType=" + eventType +
                ", timestamp=" + timestamp +
                '}';
    }
}
