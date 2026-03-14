package net.ooder.scene.llm.proxy.connection;

import net.ooder.scene.llm.config.SceneLlmConfig;

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
     * 从SceneLlmConfig创建池标识符
     */
    public static LlmConnectionPoolKey fromConfig(SceneLlmConfig config) {
        // 从 endpoint 提取 provider 和 baseUrl
        String endpoint = config.getEndpoint();
        String provider = extractProvider(endpoint);
        String baseUrl = endpoint;
        String apiKeyHash = config.getApiKey() != null ?
                String.valueOf(config.getApiKey().hashCode()) : "";
        String model = config.getModel();

        return new LlmConnectionPoolKey(provider, baseUrl, apiKeyHash, model);
    }

    /**
     * 从 endpoint 提取 provider
     */
    private static String extractProvider(String endpoint) {
        if (endpoint == null || endpoint.isEmpty()) {
            return "unknown";
        }
        if (endpoint.contains("openai")) {
            return "openai";
        } else if (endpoint.contains("baidu") || endpoint.contains("wenxin")) {
            return "baidu";
        } else if (endpoint.contains("spark") || endpoint.contains("xfyun")) {
            return "spark";
        } else if (endpoint.contains("qianwen") || endpoint.contains("aliyun")) {
            return "qianwen";
        }
        return "custom";
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
        return provider + "|" + baseUrl + "|" + apiKeyHash + "|" + model;
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
