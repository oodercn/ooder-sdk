package net.ooder.scene.agent.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 物理 Agent 配置
 *
 * <p>外部服务驱动的物理 Agent 配置。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class PhysicalAgentConfig extends AgentConfig {
    
    private String endpoint;
    private String secretKey;
    private int heartbeatInterval = 30000;
    private int heartbeatTimeout = 60000;
    private int maxRetries = 3;
    private long requestTimeout = 30000;
    
    public PhysicalAgentConfig() {
    }
    
    public PhysicalAgentConfig(String agentId, String name) {
        this.agentId = agentId;
        this.name = name;
    }
    
    @Override
    public AgentType getType() {
        return AgentType.PHYSICAL;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public long getRequestTimeout() {
        return requestTimeout;
    }
    
    public void setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final PhysicalAgentConfig config = new PhysicalAgentConfig();
        
        public Builder agentId(String agentId) {
            config.setAgentId(agentId);
            return this;
        }
        
        public Builder name(String name) {
            config.setName(name);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            config.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder role(String role) {
            config.setRole(role);
            return this;
        }
        
        public Builder description(String description) {
            config.setDescription(description);
            return this;
        }
        
        public Builder endpoint(String endpoint) {
            config.setEndpoint(endpoint);
            return this;
        }
        
        public Builder secretKey(String secretKey) {
            config.setSecretKey(secretKey);
            return this;
        }
        
        public Builder heartbeatInterval(int heartbeatInterval) {
            config.setHeartbeatInterval(heartbeatInterval);
            return this;
        }
        
        public Builder heartbeatTimeout(int heartbeatTimeout) {
            config.setHeartbeatTimeout(heartbeatTimeout);
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            config.setMaxRetries(maxRetries);
            return this;
        }
        
        public Builder requestTimeout(long requestTimeout) {
            config.setRequestTimeout(requestTimeout);
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            config.getMetadata().put(key, value);
            return this;
        }
        
        public PhysicalAgentConfig build() {
            return config;
        }
    }
}
