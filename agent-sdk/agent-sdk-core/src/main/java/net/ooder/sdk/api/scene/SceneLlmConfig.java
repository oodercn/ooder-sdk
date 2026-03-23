package net.ooder.sdk.api.scene;

import java.util.HashMap;
import java.util.Map;

public class SceneLlmConfig {

    private String configId;
    private String configName;
    private String provider;
    private String model;
    private String sceneId;
    private String sceneGroupId;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Integer topK;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private Map<String, Object> providerParams;
    private Map<String, Object> customParams;
    private boolean enabled;
    private long createTime;
    private long updateTime;

    public SceneLlmConfig() {
        this.providerParams = new HashMap<>();
        this.customParams = new HashMap<>();
        this.enabled = true;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public SceneLlmConfig(String configId, String provider, String model) {
        this();
        this.configId = configId;
        this.provider = provider;
        this.model = model;
    }

    public String getConfigId() { return configId; }
    public void setConfigId(String configId) { this.configId = configId; }

    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }

    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }

    public Double getFrequencyPenalty() { return frequencyPenalty; }
    public void setFrequencyPenalty(Double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }

    public Double getPresencePenalty() { return presencePenalty; }
    public void setPresencePenalty(Double presencePenalty) { this.presencePenalty = presencePenalty; }

    public Map<String, Object> getProviderParams() { return providerParams; }
    public void setProviderParams(Map<String, Object> providerParams) { this.providerParams = providerParams != null ? providerParams : new HashMap<>(); }

    public Map<String, Object> getCustomParams() { return customParams; }
    public void setCustomParams(Map<String, Object> customParams) { this.customParams = customParams != null ? customParams : new HashMap<>(); }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

    public void addProviderParam(String key, Object value) {
        this.providerParams.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProviderParam(String key) {
        return (T) this.providerParams.get(key);
    }

    public void addCustomParam(String key, Object value) {
        this.customParams.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCustomParam(String key) {
        return (T) this.customParams.get(key);
    }

    public SceneLlmConfig copy() {
        SceneLlmConfig copy = new SceneLlmConfig();
        copy.setConfigId(this.configId);
        copy.setConfigName(this.configName);
        copy.setProvider(this.provider);
        copy.setModel(this.model);
        copy.setSceneId(this.sceneId);
        copy.setSceneGroupId(this.sceneGroupId);
        copy.setTemperature(this.temperature);
        copy.setMaxTokens(this.maxTokens);
        copy.setTopP(this.topP);
        copy.setTopK(this.topK);
        copy.setFrequencyPenalty(this.frequencyPenalty);
        copy.setPresencePenalty(this.presencePenalty);
        copy.setProviderParams(new HashMap<>(this.providerParams));
        copy.setCustomParams(new HashMap<>(this.customParams));
        copy.setEnabled(this.enabled);
        return copy;
    }

    public static SceneLlmConfigBuilder builder() {
        return new SceneLlmConfigBuilder();
    }

    public static class SceneLlmConfigBuilder {
        private SceneLlmConfig config = new SceneLlmConfig();

        public SceneLlmConfigBuilder configId(String configId) {
            config.setConfigId(configId);
            return this;
        }

        public SceneLlmConfigBuilder configName(String configName) {
            config.setConfigName(configName);
            return this;
        }

        public SceneLlmConfigBuilder provider(String provider) {
            config.setProvider(provider);
            return this;
        }

        public SceneLlmConfigBuilder model(String model) {
            config.setModel(model);
            return this;
        }

        public SceneLlmConfigBuilder sceneId(String sceneId) {
            config.setSceneId(sceneId);
            return this;
        }

        public SceneLlmConfigBuilder sceneGroupId(String sceneGroupId) {
            config.setSceneGroupId(sceneGroupId);
            return this;
        }

        public SceneLlmConfigBuilder temperature(Double temperature) {
            config.setTemperature(temperature);
            return this;
        }

        public SceneLlmConfigBuilder maxTokens(Integer maxTokens) {
            config.setMaxTokens(maxTokens);
            return this;
        }

        public SceneLlmConfigBuilder topP(Double topP) {
            config.setTopP(topP);
            return this;
        }

        public SceneLlmConfigBuilder topK(Integer topK) {
            config.setTopK(topK);
            return this;
        }

        public SceneLlmConfigBuilder frequencyPenalty(Double frequencyPenalty) {
            config.setFrequencyPenalty(frequencyPenalty);
            return this;
        }

        public SceneLlmConfigBuilder presencePenalty(Double presencePenalty) {
            config.setPresencePenalty(presencePenalty);
            return this;
        }

        public SceneLlmConfigBuilder providerParam(String key, Object value) {
            config.addProviderParam(key, value);
            return this;
        }

        public SceneLlmConfigBuilder customParam(String key, Object value) {
            config.addCustomParam(key, value);
            return this;
        }

        public SceneLlmConfigBuilder enabled(boolean enabled) {
            config.setEnabled(enabled);
            return this;
        }

        public SceneLlmConfig build() {
            return config;
        }
    }
}
