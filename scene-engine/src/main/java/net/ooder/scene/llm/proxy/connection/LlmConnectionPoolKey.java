package net.ooder.scene.llm.proxy.connection;

import net.ooder.llm.api.LlmConfig;

import java.util.Objects;

/**
 * LLM连接池标识符
 * 用于判断相同配置的Agent是否可以共享连接池
 */
public class LlmConnectionPoolKey {
    
    private final String provider;
    private final String baseUrl;
    private final String apiKeyHash;
    private final String model;
    
    public LlmConnectionPoolKey(String provider, String baseUrl, String apiKeyHash, String model) {
        this.provider = provider != null ? provider : "unknown";
        this.baseUrl = baseUrl != null ? baseUrl : "";
        this.apiKeyHash = apiKeyHash != null ? apiKeyHash : "";
        this.model = model != null ? model : "";
    }
    
    /**
     * 从LlmConfig创建池标识符
     */
    public static LlmConnectionPoolKey fromConfig(LlmConfig config) {
        return new LlmConnectionPoolKey(
            config.getProvider(),
            config.getBaseUrl(),
            hashApiKey(config.getApiKey()),
            config.getDefaultModel()
        );
    }
    
    /**
     * 对API Key进行hash（不存储明文）
     */
    private static String hashApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "";
        }
        // 使用简单的hashCode，实际生产环境建议使用更安全的hash算法
        return String.valueOf(apiKey.hashCode());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LlmConnectionPoolKey that = (LlmConnectionPoolKey) o;
        return Objects.equals(provider, that.provider) &&
               Objects.equals(baseUrl, that.baseUrl) &&
               Objects.equals(apiKeyHash, that.apiKeyHash) &&
               Objects.equals(model, that.model);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(provider, baseUrl, apiKeyHash, model);
    }
    
    @Override
    public String toString() {
        return provider + "-" + model + "-" + apiKeyHash;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public String getApiKeyHash() {
        return apiKeyHash;
    }
    
    public String getModel() {
        return model;
    }
}
