package net.ooder.scene.llm.command;

import java.io.Serializable;

/**
 * Agent 信息
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class AgentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentId;
    private String agentType;
    private String sceneId;
    private String endpoint;

    public AgentInfo() {}
    
    public AgentInfo(String agentId, String agentType) {
        this.agentId = agentId;
        this.agentType = agentType;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public static class Builder {
        private AgentInfo info = new AgentInfo();
        
        public Builder agentId(String agentId) {
            info.setAgentId(agentId);
            return this;
        }
        
        public Builder agentType(String agentType) {
            info.setAgentType(agentType);
            return this;
        }
        
        public Builder sceneId(String sceneId) {
            info.setSceneId(sceneId);
            return this;
        }
        
        public Builder endpoint(String endpoint) {
            info.setEndpoint(endpoint);
            return this;
        }
        
        public AgentInfo build() {
            return info;
        }
    }
}
