package net.ooder.scene.group.config;

import java.util.HashMap;
import java.util.Map;

public class SceneGroupConfig {
    
    private final Map<String, Object> config = new HashMap<>();
    private LlmConfigProperties llmConfig;
    
    public void set(String key, Object value) {
        config.put(key, value);
    }
    
    public Object get(String key) {
        return config.get(key);
    }
    
    public Map<String, Object> getAllConfig() {
        return new HashMap<>(config);
    }
    
    public void merge(Map<String, Object> other) {
        if (other != null) {
            config.putAll(other);
        }
    }
    
    public LlmConfigProperties getLlmConfig() { return llmConfig; }
    public void setLlmConfig(LlmConfigProperties llmConfig) { this.llmConfig = llmConfig; }
}
