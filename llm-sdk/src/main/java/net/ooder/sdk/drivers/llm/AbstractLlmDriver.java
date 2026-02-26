package net.ooder.sdk.drivers.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * LLM驱动抽象基类
 * 提供通用的实现逻辑，具体驱动只需实现核心方法
 */
public abstract class AbstractLlmDriver implements LlmDriver {

    private static final Logger log = LoggerFactory.getLogger(AbstractLlmDriver.class);

    protected LlmConfig config;
    protected final AtomicBoolean initialized = new AtomicBoolean(false);
    protected final AtomicBoolean connected = new AtomicBoolean(false);
    protected final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public void init(LlmConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.config = config;
        doInit();
        initialized.set(true);
        log.info("LLM driver initialized: {}", getDriverName());
    }

    /**
     * 子类实现的初始化逻辑
     */
    protected abstract void doInit();

    @Override
    public CompletableFuture<ChatResponse> chat(ChatRequest request) {
        checkInitialized();
        return doChat(request);
    }

    /**
     * 子类实现的聊天逻辑
     */
    protected abstract CompletableFuture<ChatResponse> doChat(ChatRequest request);

    @Override
    public CompletableFuture<ChatResponse> chatStream(ChatRequest request, LlmDriver.ChatStreamHandler handler) {
        checkInitialized();
        if (!supportsStreaming()) {
            CompletableFuture<ChatResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new UnsupportedOperationException("Streaming not supported"));
            return future;
        }
        return doChatStream(request, handler);
    }

    /**
     * 子类实现的流式聊天逻辑
     */
    protected abstract CompletableFuture<ChatResponse> doChatStream(
        ChatRequest request, LlmDriver.ChatStreamHandler handler);

    @Override
    public CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request) {
        checkInitialized();
        if (!supportsEmbeddings()) {
            CompletableFuture<EmbeddingResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new UnsupportedOperationException("Embeddings not supported"));
            return future;
        }
        return doEmbed(request);
    }

    /**
     * 子类实现的嵌入逻辑
     */
    protected abstract CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request);

    @Override
    public CompletableFuture<CompletionResponse> complete(CompletionRequest request) {
        checkInitialized();
        return doComplete(request);
    }

    /**
     * 子类实现的补全逻辑
     */
    protected abstract CompletableFuture<CompletionResponse> doComplete(CompletionRequest request);

    @Override
    public CompletableFuture<TokenCountResponse> countTokens(String text) {
        checkInitialized();
        // 默认实现：简单估算（实际应由子类覆盖）
        return CompletableFuture.supplyAsync(() -> {
            TokenCountResponse response = new TokenCountResponse();
            // 粗略估算：1个token约等于4个字符
            response.setTokenCount(text.length() / 4);
            return response;
        });
    }

    @Override
    public CompletableFuture<List<String>> listModels() {
        checkInitialized();
        return doListModels();
    }

    /**
     * 子类实现的模型列表逻辑
     */
    protected abstract CompletableFuture<List<String>> doListModels();

    @Override
    public CompletableFuture<ModelInfo> getModelInfo(String modelId) {
        checkInitialized();
        return doGetModelInfo(modelId);
    }

    /**
     * 子类实现的模型信息逻辑
     */
    protected abstract CompletableFuture<ModelInfo> doGetModelInfo(String modelId);

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
        return false;
    }

    @Override
    public int getMaxContextLength(String modelId) {
        return config != null ? config.getMaxTokens() : 4096;
    }

    @Override
    public void close() {
        if (initialized.compareAndSet(true, false)) {
            doClose();
            connected.set(false);
            cache.clear();
            log.info("LLM driver closed: {}", getDriverName());
        }
    }

    /**
     * 子类实现的关闭逻辑
     */
    protected abstract void doClose();

    @Override
    public String getDriverVersion() {
        return "1.0.0";
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * 设置连接状态
     */
    protected void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    /**
     * 检查是否已初始化
     */
    protected void checkInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("Driver not initialized");
        }
    }

    /**
     * 获取配置
     */
    protected LlmConfig getConfig() {
        return config;
    }

    /**
     * 带重试的执行
     */
    protected <T> CompletableFuture<T> executeWithRetry(
            java.util.function.Supplier<CompletableFuture<T>> action, int maxRetries) {
        return action.get().exceptionally(ex -> {
            if (maxRetries > 0) {
                log.warn("Operation failed, retrying... ({} retries left)", maxRetries);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return executeWithRetry(action, maxRetries - 1).join();
            }
            throw new RuntimeException("Max retries exceeded", ex);
        });
    }

    /**
     * 创建成功的聊天响应
     */
    protected ChatResponse createChatResponse(String content, String model) {
        ChatResponse response = new ChatResponse();
        response.setId("chat-" + System.currentTimeMillis());
        response.setModel(model);
        response.setMessage(ChatMessage.assistant(content));
        response.setCreatedTime(System.currentTimeMillis());
        response.setFinishReason("stop");

        UsageInfo usage = new UsageInfo();
        usage.setPromptTokens(0);
        usage.setCompletionTokens(content.length() / 4);
        usage.setTotalTokens(usage.getCompletionTokens());
        response.setUsage(usage);

        return response;
    }

    /**
     * 创建成功的补全响应
     */
    protected CompletionResponse createCompletionResponse(String text, String model) {
        CompletionResponse response = new CompletionResponse();
        response.setId("comp-" + System.currentTimeMillis());
        response.setModel(model);

        CompletionChoice choice = new CompletionChoice();
        choice.setText(text);
        choice.setIndex(0);
        choice.setFinishReason("stop");
        List<CompletionChoice> choices = new java.util.ArrayList<>();
        choices.add(choice);
        response.setChoices(choices);

        UsageInfo usage = new UsageInfo();
        usage.setPromptTokens(0);
        usage.setCompletionTokens(text.length() / 4);
        usage.setTotalTokens(usage.getCompletionTokens());
        response.setUsage(usage);

        return response;
    }
}
