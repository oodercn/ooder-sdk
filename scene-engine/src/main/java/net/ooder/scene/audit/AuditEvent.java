package net.ooder.scene.audit;

import java.util.Map;

/**
 * 审计事件
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class AuditEvent {
    
    private String eventType;
    private String result;
    private String userId;
    private String agentId;
    private String resourceType;
    private String resourceId;
    private String action;
    private String detail;
    private String ipAddress;
    private Map<String, Object> metadata;
    
    public AuditEvent() {
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public static class Builder {
        private final AuditEvent event = new AuditEvent();
        
        public Builder eventType(String eventType) {
            event.setEventType(eventType);
            return this;
        }
        
        public Builder result(String result) {
            event.setResult(result);
            return this;
        }
        
        public Builder userId(String userId) {
            event.setUserId(userId);
            return this;
        }
        
        public Builder agentId(String agentId) {
            event.setAgentId(agentId);
            return this;
        }
        
        public Builder resourceType(String resourceType) {
            event.setResourceType(resourceType);
            return this;
        }
        
        public Builder resourceId(String resourceId) {
            event.setResourceId(resourceId);
            return this;
        }
        
        public Builder action(String action) {
            event.setAction(action);
            return this;
        }
        
        public Builder detail(String detail) {
            event.setDetail(detail);
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            event.setIpAddress(ipAddress);
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            event.setMetadata(metadata);
            return this;
        }
        
        public AuditEvent build() {
            return event;
        }
    }
}
