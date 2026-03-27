package net.ooder.scene.agent.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 虚拟 Agent 配置
 *
 * <p>LLM 驱动的虚拟 Agent 配置。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class VirtualAgentConfig extends AgentConfig {
    
    private String llmProvider;
    private String llmModel;
    private Map<String, Object> llmParams = new HashMap<>();
    private String systemPrompt;
    private List<String> capabilities = new ArrayList<>();
    private List<String> knowledgeBaseIds = new ArrayList<>();
    private int maxHistoryLength = 20;
    private double temperature = 0.7;
    
    public VirtualAgentConfig() {
    }
    
    public VirtualAgentConfig(String agentId, String name) {
        this.agentId = agentId;
        this.name = name;
    }
    
    @Override
    public AgentType getType() {
        return AgentType.VIRTUAL;
    }
    
    public String getLlmProvider() {
        return llmProvider;
    }
    
    public void setLlmProvider(String llmProvider) {
        this.llmProvider = llmProvider;
    }
    
    public String getLlmModel() {
        return llmModel;
    }
    
    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }
    
    public Map<String, Object> getLlmParams() {
        return llmParams;
    }
    
    public void setLlmParams(Map<String, Object> llmParams) {
        this.llmParams = llmParams;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public List<String> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }
    
    public void addCapability(String capability) {
        this.capabilities.add(capability);
    }
    
    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }
    
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }
    
    public void addKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseIds.add(knowledgeBaseId);
    }
    
    public int getMaxHistoryLength() {
        return maxHistoryLength;
    }
    
    public void setMaxHistoryLength(int maxHistoryLength) {
        this.maxHistoryLength = maxHistoryLength;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final VirtualAgentConfig config = new VirtualAgentConfig();
        
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
        
        public Builder llmProvider(String llmProvider) {
            config.setLlmProvider(llmProvider);
            return this;
        }
        
        public Builder llmModel(String llmModel) {
            config.setLlmModel(llmModel);
            return this;
        }
        
        public Builder systemPrompt(String systemPrompt) {
            config.setSystemPrompt(systemPrompt);
            return this;
        }
        
        public Builder capability(String capability) {
            config.addCapability(capability);
            return this;
        }
        
        public Builder knowledgeBaseId(String knowledgeBaseId) {
            config.addKnowledgeBaseId(knowledgeBaseId);
            return this;
        }
        
        public Builder maxHistoryLength(int maxHistoryLength) {
            config.setMaxHistoryLength(maxHistoryLength);
            return this;
        }
        
        public Builder temperature(double temperature) {
            config.setTemperature(temperature);
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            config.getMetadata().put(key, value);
            return this;
        }
        
        public VirtualAgentConfig build() {
            return config;
        }
    }
}
