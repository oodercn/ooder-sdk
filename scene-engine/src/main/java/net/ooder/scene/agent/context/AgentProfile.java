package net.ooder.scene.agent.context;

import java.util.Map;

/**
 * Agent 档案信息
 *
 * <p>表示 Agent 的完整档案信息，包含注册信息、状态等。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class AgentProfile {
    
    private String agentId;
    private String name;
    private AgentType type;
    private String sceneGroupId;
    private AgentStatus status;
    private boolean isVirtual;
    private String role;
    private String description;
    
    private long registeredAt;
    private long lastActiveAt;
    private long lastHeartbeatAt;
    
    private int heartbeatInterval;
    private int heartbeatTimeout;
    
    private Map<String, Object> capabilities;
    private Map<String, Object> metadata;
    
    public AgentProfile() {
        this.status = AgentStatus.OFFLINE;
        this.registeredAt = System.currentTimeMillis();
        this.lastActiveAt = this.registeredAt;
    }
    
    public AgentProfile(String agentId, String name, AgentType type) {
        this();
        this.agentId = agentId;
        this.name = name;
        this.type = type;
        this.isVirtual = (type == AgentType.VIRTUAL);
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public AgentType getType() {
        return type;
    }
    
    public void setType(AgentType type) {
        this.type = type;
        this.isVirtual = (type == AgentType.VIRTUAL);
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public AgentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgentStatus status) {
        this.status = status;
    }
    
    public boolean isVirtual() {
        return isVirtual;
    }
    
    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getRegisteredAt() {
        return registeredAt;
    }
    
    public void setRegisteredAt(long registeredAt) {
        this.registeredAt = registeredAt;
    }
    
    public long getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
    
    public long getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }
    
    public void setLastHeartbeatAt(long lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }
    
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }
    
    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
    
    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }
    
    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
    
    public Map<String, Object> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public boolean isOnline() {
        if (isVirtual) {
            return status != AgentStatus.OFFLINE && status != AgentStatus.ERROR;
        }
        
        if (status == AgentStatus.OFFLINE) {
            return false;
        }
        
        if (heartbeatTimeout > 0 && lastHeartbeatAt > 0) {
            return (System.currentTimeMillis() - lastHeartbeatAt) < heartbeatTimeout;
        }
        
        return true;
    }
    
    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }
    
    public void heartbeat() {
        this.lastHeartbeatAt = System.currentTimeMillis();
        this.lastActiveAt = this.lastHeartbeatAt;
        if (status == AgentStatus.OFFLINE) {
            status = AgentStatus.ONLINE;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final AgentProfile profile = new AgentProfile();
        
        public Builder agentId(String agentId) {
            profile.setAgentId(agentId);
            return this;
        }
        
        public Builder name(String name) {
            profile.setName(name);
            return this;
        }
        
        public Builder type(AgentType type) {
            profile.setType(type);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            profile.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder status(AgentStatus status) {
            profile.setStatus(status);
            return this;
        }
        
        public Builder role(String role) {
            profile.setRole(role);
            return this;
        }
        
        public Builder description(String description) {
            profile.setDescription(description);
            return this;
        }
        
        public Builder heartbeatInterval(int interval) {
            profile.setHeartbeatInterval(interval);
            return this;
        }
        
        public Builder heartbeatTimeout(int timeout) {
            profile.setHeartbeatTimeout(timeout);
            return this;
        }
        
        public Builder capabilities(Map<String, Object> capabilities) {
            profile.setCapabilities(capabilities);
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            profile.setMetadata(metadata);
            return this;
        }
        
        public AgentProfile build() {
            return profile;
        }
    }
    
    @Override
    public String toString() {
        return "AgentProfile{" +
                "agentId='" + agentId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", isVirtual=" + isVirtual +
                '}';
    }
}
