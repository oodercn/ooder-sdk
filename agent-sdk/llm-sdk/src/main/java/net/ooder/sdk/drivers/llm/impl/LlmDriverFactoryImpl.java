package net.ooder.sdk.drivers.llm.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.drivers.llm.LlmDriver;
import net.ooder.sdk.drivers.llm.LlmDriverFactory;
import net.ooder.sdk.llm.model.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM 驱动工厂实现
 */
@Slf4j
public class LlmDriverFactoryImpl implements LlmDriverFactory {

    private final Map<String, DriverProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, LlmDriver> driverCache = new ConcurrentHashMap<>();

    public LlmDriverFactoryImpl() {
        // 注册内置驱动提供者
        registerBuiltInProviders();
    }

    @Override
    public LlmDriver createDriver(ModelInfo modelInfo) {
        String modelId = modelInfo.getModelId();

        // 检查缓存
        LlmDriver cachedDriver = driverCache.get(modelId);
        if (cachedDriver != null) {
            log.debug("Using cached driver for model: {}", modelId);
            return cachedDriver;
        }

        // 查找合适的提供者
        for (DriverProvider provider : providers.values()) {
            if (provider.supports(modelId)) {
                LlmDriver driver = provider.createDriver(modelInfo);
                driverCache.put(modelId, driver);
                log.info("Created driver for model: {} using provider: {}",
                        modelId, provider.getName());
                return driver;
            }
        }

        throw new IllegalArgumentException("No provider found for model: " + modelId);
    }

    @Override
    public void registerProvider(DriverProvider provider) {
        providers.put(provider.getName(), provider);
        log.info("Registered driver provider: {}", provider.getName());
    }

    @Override
    public List<String> getSupportedProviders() {
        return new ArrayList<>(providers.keySet());
    }

    @Override
    public boolean supports(String modelId) {
        for (DriverProvider provider : providers.values()) {
            if (provider.supports(modelId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注册内置驱动提供者
     */
    private void registerBuiltInProviders() {
        // OpenAI Provider
        registerProvider(new DriverProvider() {
            @Override
            public String getName() {
                return "openai";
            }

            @Override
            public List<String> getSupportedModels() {
                return Arrays.asList("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo");
            }

            @Override
            public LlmDriver createDriver(ModelInfo modelInfo) {
                log.warn("[STUB] Creating OpenAI driver for model: {}", modelInfo.getModelId());
                return createStubDriver(modelInfo);
            }

            @Override
            public boolean supports(String modelId) {
                return getSupportedModels().contains(modelId);
            }
        });

        // Anthropic Provider
        registerProvider(new DriverProvider() {
            @Override
            public String getName() {
                return "anthropic";
            }

            @Override
            public List<String> getSupportedModels() {
                return Arrays.asList("claude-3-opus", "claude-3-sonnet", "claude-3-haiku");
            }

            @Override
            public LlmDriver createDriver(ModelInfo modelInfo) {
                log.warn("[STUB] Creating Anthropic driver for model: {}", modelInfo.getModelId());
                return createStubDriver(modelInfo);
            }

            @Override
            public boolean supports(String modelId) {
                return getSupportedModels().contains(modelId);
            }
        });

        // 百度文心 Provider
        registerProvider(new DriverProvider() {
            @Override
            public String getName() {
                return "baidu";
            }

            @Override
            public List<String> getSupportedModels() {
                return Arrays.asList("ernie-bot", "ernie-bot-turbo");
            }

            @Override
            public LlmDriver createDriver(ModelInfo modelInfo) {
                log.warn("[STUB] Creating Baidu driver for model: {}", modelInfo.getModelId());
                return createStubDriver(modelInfo);
            }

            @Override
            public boolean supports(String modelId) {
                return getSupportedModels().contains(modelId);
            }
        });

        // 讯飞星火 Provider
        registerProvider(new DriverProvider() {
            @Override
            public String getName() {
                return "spark";
            }

            @Override
            public List<String> getSupportedModels() {
                return Arrays.asList("spark-v3.5", "spark-v3.0", "spark-v2.0");
            }

            @Override
            public LlmDriver createDriver(ModelInfo modelInfo) {
                log.warn("[STUB] Creating Spark driver for model: {}", modelInfo.getModelId());
                return createStubDriver(modelInfo);
            }

            @Override
            public boolean supports(String modelId) {
                return getSupportedModels().contains(modelId);
            }
        });
    }

    /**
     * 创建存根驱动
     */
    private LlmDriver createStubDriver(ModelInfo modelInfo) {
        return new LlmDriver() {
            @Override
            public void init(LlmConfig config) {
                log.warn("[STUB] Stub driver init() called");
            }

            @Override
            public CompletableFuture<DriverChatResponse> chat(DriverChatRequest request) {
                log.warn("[STUB] Stub driver chat() called");
                DriverChatResponse response = new DriverChatResponse();
                response.setId("stub-" + System.currentTimeMillis());
                response.setModel(modelInfo.getModelId());
                ChatMessage message = ChatMessage.assistant("[STUB] This is a stub driver response");
                response.setMessage(message);
                response.setCreatedTime(System.currentTimeMillis());
                return CompletableFuture.completedFuture(response);
            }

            @Override
            public CompletableFuture<DriverChatResponse> chatStream(DriverChatRequest request, ChatStreamHandler handler) {
                log.warn("[STUB] Stub driver chatStream() called");
                handler.onToken("[STUB] ");
                handler.onToken("Stream ");
                handler.onToken("response");
                
                DriverChatResponse response = new DriverChatResponse();
                response.setId("stub-stream-" + System.currentTimeMillis());
                response.setModel(modelInfo.getModelId());
                ChatMessage message = ChatMessage.assistant("[STUB] Stream response");
                response.setMessage(message);
                response.setCreatedTime(System.currentTimeMillis());
                
                handler.onComplete(response);
                return CompletableFuture.completedFuture(response);
            }

            @Override
            public CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request) {
                log.warn("[STUB] Stub driver embed() called");
                EmbeddingResponse response = new EmbeddingResponse();
                response.setModel(modelInfo.getModelId());
                return CompletableFuture.completedFuture(response);
            }

            @Override
            public CompletableFuture<CompletionResponse> complete(CompletionRequest request) {
                log.warn("[STUB] Stub driver complete() called");
                CompletionResponse response = new CompletionResponse();
                response.setId("stub-completion-" + System.currentTimeMillis());
                response.setModel(modelInfo.getModelId());
                return CompletableFuture.completedFuture(response);
            }

            @Override
            public CompletableFuture<TokenCountResponse> countTokens(String text) {
                log.warn("[STUB] Stub driver countTokens() called");
                TokenCountResponse response = new TokenCountResponse();
                response.setTokenCount(text != null ? text.length() / 4 : 0);
                return CompletableFuture.completedFuture(response);
            }

            @Override
            public CompletableFuture<List<String>> listModels() {
                log.warn("[STUB] Stub driver listModels() called");
                return CompletableFuture.completedFuture(Arrays.asList(modelInfo.getModelId()));
            }

            @Override
            public CompletableFuture<ModelInfo> getModelInfo(String modelId) {
                log.warn("[STUB] Stub driver getModelInfo() called");
                ModelInfo info = new ModelInfo();
                info.setModelId(modelId);
                info.setModelName(modelId);
                info.setProvider("stub");
                return CompletableFuture.completedFuture(info);
            }

            @Override
            public boolean supportsStreaming() {
                return true;
            }

            @Override
            public boolean supportsEmbeddings() {
                return true;
            }

            @Override
            public boolean supportsFunctionCalling() {
                return true;
            }

            @Override
            public int getMaxContextLength(String modelId) {
                return 4096;
            }

            @Override
            public void close() {
                log.warn("[STUB] Stub driver close() called");
            }

            @Override
            public boolean isConnected() {
                return true;
            }

            @Override
            public String getDriverName() {
                return "StubDriver";
            }

            @Override
            public String getDriverVersion() {
                return "1.0.0";
            }
        };
    }
}
