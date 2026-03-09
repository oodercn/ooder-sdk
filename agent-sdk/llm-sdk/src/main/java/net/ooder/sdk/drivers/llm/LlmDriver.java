package net.ooder.sdk.drivers.llm;

import net.ooder.sdk.llm.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM驱动接口
 * 用于与各种LLM提供商（OpenAI、Claude等）进行交互
 */
public interface LlmDriver {

    void init(LlmConfig config);

    CompletableFuture<DriverChatResponse> chat(DriverChatRequest request);

    CompletableFuture<DriverChatResponse> chatStream(DriverChatRequest request, ChatStreamHandler handler);

    CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request);

    CompletableFuture<CompletionResponse> complete(CompletionRequest request);

    CompletableFuture<TokenCountResponse> countTokens(String text);

    CompletableFuture<List<String>> listModels();

    CompletableFuture<ModelInfo> getModelInfo(String modelId);

    boolean supportsStreaming();

    boolean supportsEmbeddings();

    boolean supportsFunctionCalling();

    int getMaxContextLength(String modelId);

    void close();

    boolean isConnected();

    String getDriverName();

    String getDriverVersion();

    /**
     * LLM配置
     */
    class LlmConfig {
        private String endpoint;
        private String apiKey;
        private String apiSecret;
        private String appId;
        private String baseUrl;
        private String model;
        private String defaultModel;
        private int maxTokens = 4096;
        private double temperature = 0.7;
        private double topP = 1.0;
        private int timeout = 60000;
        private int maxRetries = 3;
        private boolean simulationMode = false;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getApiSecret() { return apiSecret; }
        public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }

        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getDefaultModel() { return defaultModel; }
        public void setDefaultModel(String defaultModel) { this.defaultModel = defaultModel; }

        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public double getTopP() { return topP; }
        public void setTopP(double topP) { this.topP = topP; }

        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }

        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

        public boolean isSimulationMode() { return simulationMode; }
        public void setSimulationMode(boolean simulationMode) { this.simulationMode = simulationMode; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * 流式处理处理器
     */
    interface ChatStreamHandler {
        void onToken(String token);
        void onMessage(ChatMessage message);
        void onComplete(DriverChatResponse response);
        void onError(Throwable error);
    }
}
