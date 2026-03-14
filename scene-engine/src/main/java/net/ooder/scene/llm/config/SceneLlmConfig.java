package net.ooder.scene.llm.config;

/**
 * LLM 配置类
 * <p>SE 业务层配置类，区别于 SDK 的 LlmDriver.LlmConfig</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class SceneLlmConfig {

    private String endpoint = "https://api.openai.com/v1";
    private String apiKey;
    private String model = "gpt-4";
    private int maxTokens = 2048;
    private double temperature = 0.7;
    private long timeout = 60000;

    // 新增字段，兼容 LlmDriver.LlmConfig
    private String provider = "openai";
    private String defaultModel = "gpt-4";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }
}
