package net.ooder.sdk.agent.llm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 服务发现 API
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface LlmDiscoveryApi {

    /**
     * 发现 LLM Provider
     * @return Provider列表
     */
    CompletableFuture<List<LlmProviderInfo>> discoverProviders();

    /**
     * 获取 Provider 端点
     * @param providerId Provider ID
     * @return 端点信息
     */
    CompletableFuture<LlmEndpoint> getProviderEndpoint(String providerId);

    /**
     * 选择最佳端点
     * @param providerId Provider ID
     * @param criteria 选择条件
     * @return 端点信息
     */
    CompletableFuture<LlmEndpoint> selectBestEndpoint(String providerId, EndpointSelectionCriteria criteria);

    /**
     * 检查端点健康状态
     * @param endpointId 端点ID
     * @return 健康状态
     */
    CompletableFuture<EndpointHealth> checkEndpointHealth(String endpointId);

    /**
     * 获取 Provider 能力
     * @param providerId Provider ID
     * @return 能力列表
     */
    CompletableFuture<List<String>> getProviderCapabilities(String providerId);

    /**
     * LLM Provider 信息
     */
    class LlmProviderInfo {
        private String id;
        private String name;
        private String type;
        private String version;
        private List<String> supportedModels;
        private Map<String, Object> metadata;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public List<String> getSupportedModels() { return supportedModels; }
        public void setSupportedModels(List<String> supportedModels) { this.supportedModels = supportedModels; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * LLM 端点
     */
    class LlmEndpoint {
        private String id;
        private String providerId;
        private String url;
        private String protocol;
        private Map<String, String> headers;
        private int priority;
        private Map<String, Object> config;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getProviderId() { return providerId; }
        public void setProviderId(String providerId) { this.providerId = providerId; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    /**
     * 端点选择条件
     */
    class EndpointSelectionCriteria {
        private String preferredModel;
        private int maxLatency;
        private int minThroughput;
        private Map<String, Object> requirements;

        // Getters and Setters
        public String getPreferredModel() { return preferredModel; }
        public void setPreferredModel(String preferredModel) { this.preferredModel = preferredModel; }
        public int getMaxLatency() { return maxLatency; }
        public void setMaxLatency(int maxLatency) { this.maxLatency = maxLatency; }
        public int getMinThroughput() { return minThroughput; }
        public void setMinThroughput(int minThroughput) { this.minThroughput = minThroughput; }
        public Map<String, Object> getRequirements() { return requirements; }
        public void setRequirements(Map<String, Object> requirements) { this.requirements = requirements; }
    }

    /**
     * 端点健康状态
     */
    class EndpointHealth {
        private String endpointId;
        private String status;
        private int latency;
        private double availability;
        private long lastChecked;
        private Map<String, Object> metrics;

        // Getters and Setters
        public String getEndpointId() { return endpointId; }
        public void setEndpointId(String endpointId) { this.endpointId = endpointId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getLatency() { return latency; }
        public void setLatency(int latency) { this.latency = latency; }
        public double getAvailability() { return availability; }
        public void setAvailability(double availability) { this.availability = availability; }
        public long getLastChecked() { return lastChecked; }
        public void setLastChecked(long lastChecked) { this.lastChecked = lastChecked; }
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }
}
