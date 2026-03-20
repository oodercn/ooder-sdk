package net.ooder.skills.sync.model;

import java.util.ArrayList;
import java.util.List;

public class CommunicationLinkInfo {
    
    private String sceneGroupId;
    private String primaryAgentId;
    private String primaryEndpoint;
    private List<AgentInfo> backupAgents;
    private long lastUpdate;
    
    public CommunicationLinkInfo() {
        this.backupAgents = new ArrayList<>();
        this.lastUpdate = System.currentTimeMillis();
    }
    
    private CommunicationLinkInfo(Builder builder) {
        this.sceneGroupId = builder.sceneGroupId;
        this.primaryAgentId = builder.primaryAgentId;
        this.primaryEndpoint = builder.primaryEndpoint;
        this.backupAgents = builder.backupAgents != null ? new ArrayList<>(builder.backupAgents) : new ArrayList<>();
        this.lastUpdate = builder.lastUpdate;
    }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getPrimaryAgentId() { return primaryAgentId; }
    public void setPrimaryAgentId(String primaryAgentId) { this.primaryAgentId = primaryAgentId; }
    
    public String getPrimaryEndpoint() { return primaryEndpoint; }
    public void setPrimaryEndpoint(String primaryEndpoint) { this.primaryEndpoint = primaryEndpoint; }
    
    public List<AgentInfo> getBackupAgents() { return backupAgents; }
    public void setBackupAgents(List<AgentInfo> backupAgents) { this.backupAgents = backupAgents; }
    
    public long getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(long lastUpdate) { this.lastUpdate = lastUpdate; }
    
    public void addBackupAgent(AgentInfo agent) {
        if (backupAgents == null) {
            backupAgents = new ArrayList<>();
        }
        backupAgents.add(agent);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class AgentInfo {
        private String agentId;
        private String endpoint;
        
        public AgentInfo() {}
        
        private AgentInfo(Builder builder) {
            this.agentId = builder.agentId;
            this.endpoint = builder.endpoint;
        }
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private String agentId;
            private String endpoint;
            
            public Builder agentId(String agentId) {
                this.agentId = agentId;
                return this;
            }
            
            public Builder endpoint(String endpoint) {
                this.endpoint = endpoint;
                return this;
            }
            
            public AgentInfo build() {
                return new AgentInfo(this);
            }
        }
    }
    
    public static class Builder {
        private String sceneGroupId;
        private String primaryAgentId;
        private String primaryEndpoint;
        private List<AgentInfo> backupAgents;
        private long lastUpdate = System.currentTimeMillis();
        
        public Builder sceneGroupId(String sceneGroupId) {
            this.sceneGroupId = sceneGroupId;
            return this;
        }
        
        public Builder primaryAgentId(String primaryAgentId) {
            this.primaryAgentId = primaryAgentId;
            return this;
        }
        
        public Builder primaryEndpoint(String primaryEndpoint) {
            this.primaryEndpoint = primaryEndpoint;
            return this;
        }
        
        public Builder backupAgents(List<AgentInfo> backupAgents) {
            this.backupAgents = backupAgents;
            return this;
        }
        
        public Builder lastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }
        
        public CommunicationLinkInfo build() {
            return new CommunicationLinkInfo(this);
        }
    }
}
