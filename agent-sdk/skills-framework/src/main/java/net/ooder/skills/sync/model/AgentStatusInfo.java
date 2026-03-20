package net.ooder.skills.sync.model;

public class AgentStatusInfo {
    
    private String agentId;
    private String agentName;
    private String status;
    private String role;
    private long lastHeartbeat;
    private String endpoint;
    private int heartbeatMissed;
    
    public AgentStatusInfo() {}
    
    private AgentStatusInfo(Builder builder) {
        this.agentId = builder.agentId;
        this.agentName = builder.agentName;
        this.status = builder.status;
        this.role = builder.role;
        this.lastHeartbeat = builder.lastHeartbeat;
        this.endpoint = builder.endpoint;
        this.heartbeatMissed = builder.heartbeatMissed;
    }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public int getHeartbeatMissed() { return heartbeatMissed; }
    public void setHeartbeatMissed(int heartbeatMissed) { this.heartbeatMissed = heartbeatMissed; }
    
    public boolean isOnline() {
        return "online".equals(status);
    }
    
    public boolean isHealthy() {
        return isOnline() && heartbeatMissed < 3;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String agentId;
        private String agentName;
        private String status;
        private String role;
        private long lastHeartbeat;
        private String endpoint;
        private int heartbeatMissed;
        
        public Builder agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }
        
        public Builder agentName(String agentName) {
            this.agentName = agentName;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public Builder lastHeartbeat(long lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
            return this;
        }
        
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }
        
        public Builder heartbeatMissed(int heartbeatMissed) {
            this.heartbeatMissed = heartbeatMissed;
            return this;
        }
        
        public AgentStatusInfo build() {
            return new AgentStatusInfo(this);
        }
    }
}
