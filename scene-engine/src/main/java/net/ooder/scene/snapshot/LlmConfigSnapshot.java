package net.ooder.scene.snapshot;

import java.util.Map;

/**
 * LLM配置快照
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class LlmConfigSnapshot {

    private String provider;
    private String model;
    private double temperature;
    private int maxTokens;
    private long timeout;
    private Map<String, Object> extensions;

    public LlmConfigSnapshot() {}

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
}
