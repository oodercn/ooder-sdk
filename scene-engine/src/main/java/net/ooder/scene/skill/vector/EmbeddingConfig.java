package net.ooder.scene.skill.vector;

import java.util.HashMap;
import java.util.Map;

public class EmbeddingConfig {

    public static final String PROVIDER_OPENAI = "openai";
    public static final String PROVIDER_OLLAMA = "ollama";
    public static final String PROVIDER_DASHSCOPE = "dashscope";
    public static final String PROVIDER_LOCAL = "local";

    private String provider = PROVIDER_OPENAI;
    private String model = "text-embedding-3-small";
    private String apiKey;
    private String baseUrl;
    private int dimension = 1536;
    private int maxRetries = 3;
    private int timeoutMs = 30000;
    private int batchSize = 100;
    private Map<String, Object> options = new HashMap<>();

    public EmbeddingConfig() {}

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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public EmbeddingConfig option(String key, Object value) {
        this.options.put(key, value);
        return this;
    }

    public static EmbeddingConfig openai(String apiKey, String model) {
        EmbeddingConfig config = new EmbeddingConfig();
        config.setProvider(PROVIDER_OPENAI);
        config.setApiKey(apiKey);
        config.setModel(model);
        config.setBaseUrl("https://api.openai.com/v1");
        config.setDimension(getDimensionForModel(model));
        return config;
    }

    public static EmbeddingConfig ollama(String baseUrl, String model) {
        EmbeddingConfig config = new EmbeddingConfig();
        config.setProvider(PROVIDER_OLLAMA);
        config.setBaseUrl(baseUrl != null ? baseUrl : "http://localhost:11434");
        config.setModel(model);
        config.setDimension(getDimensionForModel(model));
        return config;
    }

    public static EmbeddingConfig dashscope(String apiKey, String model) {
        EmbeddingConfig config = new EmbeddingConfig();
        config.setProvider(PROVIDER_DASHSCOPE);
        config.setApiKey(apiKey);
        config.setModel(model != null ? model : "text-embedding-v2");
        config.setBaseUrl("https://dashscope.aliyuncs.com/api/v1");
        config.setDimension(1536);
        return config;
    }

    public static EmbeddingConfig local(String modelPath, int dimension) {
        EmbeddingConfig config = new EmbeddingConfig();
        config.setProvider(PROVIDER_LOCAL);
        config.setModel(modelPath);
        config.setDimension(dimension);
        return config;
    }

    private static int getDimensionForModel(String model) {
        if (model == null) {
            return 1536;
        }
        if (model.contains("large") || model.contains("3072")) {
            return 3072;
        }
        if (model.contains("bge-large") || model.contains("bge-m3")) {
            return 1024;
        }
        if (model.contains("bge-small")) {
            return 384;
        }
        if (model.contains("bge-base") || model.contains("bge")) {
            return 768;
        }
        return 1536;
    }
}
