package net.ooder.scene.provider.model.health;

import java.util.List;
import java.util.Map;

public class HealthCheckResult {
    
    private String checkId;
    private boolean healthy;
    private String status;
    private long timestamp;
    private List<ComponentHealth> components;
    private List<String> issues;
    private Map<String, Object> details;
    
    public String getCheckId() {
        return checkId;
    }
    
    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }
    
    public boolean isHealthy() {
        return healthy;
    }
    
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<ComponentHealth> getComponents() {
        return components;
    }
    
    public void setComponents(List<ComponentHealth> components) {
        this.components = components;
    }
    
    public List<String> getIssues() {
        return issues;
    }
    
    public void setIssues(List<String> issues) {
        this.issues = issues;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
    
    public static class ComponentHealth {
        private String name;
        private String status;
        private String message;
        private Map<String, Object> details;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Map<String, Object> getDetails() {
            return details;
        }
        
        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }
    }
}
