package net.ooder.skill.common.spi.audit;

import java.util.Map;

public class AuditEvent {
    
    private String eventId;
    private String eventType;
    private String sceneId;
    private String userId;
    private String action;
    private String resource;
    private String message;
    private Map<String, Object> details;
    private long timestamp;
    private boolean success;
    
    public AuditEvent() {}
    
    public AuditEvent(String eventType, String userId, String action, String message) {
        this.eventType = eventType;
        this.userId = userId;
        this.action = action;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public static class Builder {
        private AuditEvent event = new AuditEvent();
        
        public Builder eventId(String eventId) { event.eventId = eventId; return this; }
        public Builder eventType(String eventType) { event.eventType = eventType; return this; }
        public Builder sceneId(String sceneId) { event.sceneId = sceneId; return this; }
        public Builder userId(String userId) { event.userId = userId; return this; }
        public Builder action(String action) { event.action = action; return this; }
        public Builder resource(String resource) { event.resource = resource; return this; }
        public Builder message(String message) { event.message = message; return this; }
        public Builder details(Map<String, Object> details) { event.details = details; return this; }
        public Builder timestamp(long timestamp) { event.timestamp = timestamp; return this; }
        public Builder success(boolean success) { event.success = success; return this; }
        
        public AuditEvent build() {
            if (event.timestamp == 0) {
                event.timestamp = System.currentTimeMillis();
            }
            return event;
        }
    }
}
