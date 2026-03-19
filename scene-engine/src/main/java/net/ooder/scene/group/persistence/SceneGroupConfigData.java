package net.ooder.scene.group.persistence;

import java.util.Map;

public class SceneGroupConfigData {
    
    private Map<String, Object> config;
    private Map<String, Object> llmConfig;
    
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public Map<String, Object> getLlmConfig() { return llmConfig; }
    public void setLlmConfig(Map<String, Object> llmConfig) { this.llmConfig = llmConfig; }
}
