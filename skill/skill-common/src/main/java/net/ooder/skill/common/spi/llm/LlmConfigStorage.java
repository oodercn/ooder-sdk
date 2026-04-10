package net.ooder.skill.common.spi.llm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LlmConfigStorage {
    
    LlmConfigData save(LlmConfigData config);
    
    Optional<LlmConfigData> findById(String id);
    
    Optional<LlmConfigData> findByName(String name);
    
    List<LlmConfigData> findByProvider(String provider);
    
    List<LlmConfigData> findAll();
    
    void deleteById(String id);
    
    boolean existsByName(String name);
    
    class LlmConfigData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String name;
        private String provider;
        private String model;
        private String apiKey;
        private String baseUrl;
        private Integer maxTokens;
        private Double temperature;
        private Map<String, Object> extraConfig;
        private Long createdAt;
        private Long updatedAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        public Map<String, Object> getExtraConfig() { return extraConfig; }
        public void setExtraConfig(Map<String, Object> extraConfig) { this.extraConfig = extraConfig; }
        public Long getCreatedAt() { return createdAt; }
        public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
        public Long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    }
}
