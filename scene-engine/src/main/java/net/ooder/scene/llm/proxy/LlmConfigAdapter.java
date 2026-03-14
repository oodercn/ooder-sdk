package net.ooder.scene.llm.proxy;

import net.ooder.sdk.drivers.llm.LlmDriver;

/**
 * LlmConfig 适配器
 * 将 scene-engine 的 SceneLlmConfig 适配到 net.ooder.sdk.drivers.llm.LlmDriver.LlmConfig
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class LlmConfigAdapter {

    private String endpoint = "https://api.openai.com/v1";
    private String apiKey;
    private String model = "gpt-4";
    private int maxTokens = 2048;
    private double temperature = 0.7;
    private long timeout = 60000;

    public static LlmConfigAdapter fromDriverConfig(LlmDriver.LlmConfig config) {
        LlmConfigAdapter adapter = new LlmConfigAdapter();
        if (config != null) {
            adapter.endpoint = config.getEndpoint();
            adapter.apiKey = config.getApiKey();
            adapter.model = config.getDefaultModel() != null ? config.getDefaultModel() : config.getModel();
            adapter.maxTokens = config.getMaxTokens();
            adapter.temperature = config.getTemperature();
            adapter.timeout = config.getTimeout();
        }
        return adapter;
    }

    public static LlmConfigAdapter fromSceneConfig(net.ooder.scene.llm.config.SceneLlmConfig sceneConfig) {
        LlmConfigAdapter adapter = new LlmConfigAdapter();
        if (sceneConfig != null) {
            adapter.endpoint = sceneConfig.getEndpoint();
            adapter.apiKey = sceneConfig.getApiKey();
            adapter.model = sceneConfig.getModel();
            adapter.maxTokens = sceneConfig.getMaxTokens();
            adapter.temperature = sceneConfig.getTemperature();
            adapter.timeout = sceneConfig.getTimeout();
        }
        return adapter;
    }

    public LlmDriver.LlmConfig toDriverConfig() {
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setEndpoint(endpoint);
        config.setApiKey(apiKey);
        config.setModel(model);
        config.setDefaultModel(model);
        config.setMaxTokens(maxTokens);
        config.setTemperature(temperature);
        config.setTimeout((int) timeout);
        config.setMaxRetries(3);
        return config;
    }

    // Getters and Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
}
