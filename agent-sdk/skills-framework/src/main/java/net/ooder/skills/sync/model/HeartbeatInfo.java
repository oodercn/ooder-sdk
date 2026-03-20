package net.ooder.skills.sync.model;

public class HeartbeatInfo {
    
    private String agentId;
    private long lastHeartbeat;
    private int missedCount;
    private long expectedInterval;
    private long nextExpectedTime;
    private String status;
    
    public static final String STATUS_HEALTHY = "healthy";
    public static final String STATUS_WARNING = "warning";
    public static final String STATUS_CRITICAL = "critical";
    
    public HeartbeatInfo() {}
    
    private HeartbeatInfo(Builder builder) {
        this.agentId = builder.agentId;
        this.lastHeartbeat = builder.lastHeartbeat;
        this.missedCount = builder.missedCount;
        this.expectedInterval = builder.expectedInterval;
        this.nextExpectedTime = builder.nextExpectedTime;
        this.status = builder.status;
    }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    
    public int getMissedCount() { return missedCount; }
    public void setMissedCount(int missedCount) { this.missedCount = missedCount; }
    
    public long getExpectedInterval() { return expectedInterval; }
    public void setExpectedInterval(long expectedInterval) { this.expectedInterval = expectedInterval; }
    
    public long getNextExpectedTime() { return nextExpectedTime; }
    public void setNextExpectedTime(long nextExpectedTime) { this.nextExpectedTime = nextExpectedTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public boolean isHealthy() {
        return missedCount < 2;
    }
    
    public boolean isCritical() {
        return missedCount >= 3;
    }
    
    public long getTimeSinceLastHeartbeat() {
        return System.currentTimeMillis() - lastHeartbeat;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String agentId;
        private long lastHeartbeat;
        private int missedCount;
        private long expectedInterval;
        private long nextExpectedTime;
        private String status;
        
        public Builder agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }
        
        public Builder lastHeartbeat(long lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
            return this;
        }
        
        public Builder missedCount(int missedCount) {
            this.missedCount = missedCount;
            return this;
        }
        
        public Builder expectedInterval(long expectedInterval) {
            this.expectedInterval = expectedInterval;
            return this;
        }
        
        public Builder nextExpectedTime(long nextExpectedTime) {
            this.nextExpectedTime = nextExpectedTime;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public HeartbeatInfo build() {
            return new HeartbeatInfo(this);
        }
    }
}
