package net.ooder.scene.llm.config;

import java.util.HashMap;
import java.util.Map;

public class SceneLlmConfigInfo {

    private String configId;
    private String sceneGroupId;
    private String provider;
    private String model;
    private double temperature;
    private int maxTokens;
    private long timeout;
    private Map<String, Object> extensions;

    public SceneLlmConfigInfo() {
        this.temperature = 0.7;
        this.maxTokens = 2048;
        this.timeout = 60000;
        this.extensions = new HashMap<>();
    }

    public SceneLlmConfigInfo(String sceneGroupId) {
        this();
        this.sceneGroupId = sceneGroupId;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    public void addExtension(String key, Object value) {
        this.extensions.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(String key) {
        return (T) this.extensions.get(key);
    }

    public SceneLlmConfig toSceneLlmConfig() {
        SceneLlmConfig config = new SceneLlmConfig();
        config.setProvider(this.provider);
        config.setModel(this.model);
        config.setTemperature(this.temperature);
        config.setMaxTokens(this.maxTokens);
        config.setTimeout(this.timeout);
        return config;
    }

    public static SceneLlmConfigInfo fromSceneLlmConfig(String sceneGroupId, SceneLlmConfig config) {
        SceneLlmConfigInfo info = new SceneLlmConfigInfo(sceneGroupId);
        info.setProvider(config.getProvider());
        info.setModel(config.getModel());
        info.setTemperature(config.getTemperature());
        info.setMaxTokens(config.getMaxTokens());
        info.setTimeout(config.getTimeout());
        return info;
    }

    @Override
    public String toString() {
        return "SceneLlmConfigInfo{" +
                "configId='" + configId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", provider='" + provider + '\'' +
                ", model='" + model + '\'' +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                '}';
    }
}
