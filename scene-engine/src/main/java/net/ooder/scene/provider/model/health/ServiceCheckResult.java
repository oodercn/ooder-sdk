package net.ooder.scene.provider.model.health;

public class ServiceCheckResult {
    
    private String serviceName;
    private String status;
    private long latency;
    private long timestamp;
    private String message;
    private boolean reachable;
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getLatency() {
        return latency;
    }
    
    public void setLatency(long latency) {
        this.latency = latency;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isReachable() {
        return reachable;
    }
    
    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
