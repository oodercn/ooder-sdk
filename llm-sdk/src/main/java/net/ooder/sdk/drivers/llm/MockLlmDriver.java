package net.ooder.sdk.drivers.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * 模拟 LLM 驱动实现
 * 用于开发和测试环境，不依赖外部 LLM 服务
 */
public class MockLlmDriver extends AbstractLlmDriver {

    private static final Logger log = LoggerFactory.getLogger(MockLlmDriver.class);
    private static final Random random = new Random();

    private final String driverName;
    private final double latencyMs;
    private final double errorRate;

    public MockLlmDriver() {
        this("mock-llm", 100, 0.05);
    }

    public MockLlmDriver(String driverName, double latencyMs, double errorRate) {
        this.driverName = driverName;
        this.latencyMs = latencyMs;
        this.errorRate = errorRate;
    }

    @Override
    protected void doInit() {
        log.info("Mock LLM driver initialized: {}", driverName);
    }

    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return simulateLatency().thenCompose(v -> {
            if (shouldFail()) {
                CompletableFuture<ChatResponse> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Simulated chat error"));
                return future;
            }

            String responseText = generateMockResponse(request);
            return CompletableFuture.completedFuture(
                createChatResponse(responseText, "mock-model")
            );
        });
    }

    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, LlmDriver.ChatStreamHandler handler) {
        return simulateLatency().thenCompose(v -> {
            if (shouldFail()) {
                CompletableFuture<ChatResponse> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Simulated stream error"));
                return future;
            }

            String responseText = generateMockResponse(request);
            String[] words = responseText.split(" ");

            // 模拟流式输出
            StringBuilder contentBuilder = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                String chunk = words[i] + (i < words.length - 1 ? " " : "");
                contentBuilder.append(chunk);
                handler.onToken(chunk);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    CompletableFuture<ChatResponse> future = new CompletableFuture<>();
                    future.completeExceptionally(e);
                    return future;
                }
            }
            
            // 构建完整消息
            ChatMessage message = ChatMessage.assistant(contentBuilder.toString());
            handler.onMessage(message);
            
            ChatResponse response = createChatResponse(contentBuilder.toString(), "mock-model");
            handler.onComplete(response);

            return CompletableFuture.completedFuture(response);
        });
    }

    @Override
    protected CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request) {
        return simulateLatency().thenCompose(v -> {
            if (shouldFail()) {
                CompletableFuture<EmbeddingResponse> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Simulated embed error"));
                return future;
            }

            EmbeddingResponse response = new EmbeddingResponse();
            response.setModel("mock-embedding-model");

            // 生成随机嵌入向量
            List<EmbeddingData> dataList = new java.util.ArrayList<>();
            for (int i = 0; i < request.getInput().size(); i++) {
                EmbeddingData data = new EmbeddingData();
                data.setIndex(i);
                float[] embedding = new float[1536];
                for (int j = 0; j < embedding.length; j++) {
                    embedding[j] = random.nextFloat() * 2 - 1;
                }
                data.setEmbedding(embedding);
                dataList.add(data);
            }
            response.setData(dataList);

            UsageInfo usage = new UsageInfo();
            int totalTokens = 0;
            for (String input : request.getInput()) {
                totalTokens += input.length() / 4;
            }
            usage.setPromptTokens(totalTokens);
            usage.setTotalTokens(totalTokens);
            response.setUsage(usage);

            return CompletableFuture.completedFuture(response);
        });
    }

    @Override
    protected CompletableFuture<CompletionResponse> doComplete(CompletionRequest request) {
        return simulateLatency().thenCompose(v -> {
            if (shouldFail()) {
                CompletableFuture<CompletionResponse> future = new CompletableFuture<>();
                future.completeExceptionally(new RuntimeException("Simulated completion error"));
                return future;
            }

            String responseText = generateMockCompletion(request);
            return CompletableFuture.completedFuture(
                createCompletionResponse(responseText, "mock-model")
            );
        });
    }

    @Override
    protected CompletableFuture<List<String>> doListModels() {
        return CompletableFuture.completedFuture(Arrays.asList(
            "mock-model",
            "mock-model-large",
            "mock-embedding-model"
        ));
    }

    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return simulateLatency().thenApply(v -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setName("Mock " + modelId);
            info.setDescription("Mock model for testing");
            info.setContextLength(4096);
            info.setSupportsStreaming(true);
            info.setSupportsEmbeddings(modelId.contains("embedding"));
            return info;
        });
    }

    @Override
    protected void doClose() {
        log.info("Mock LLM driver closed: {}", driverName);
    }

    @Override
    public String getDriverName() {
        return driverName;
    }

    @Override
    public String getDriverVersion() {
        return "1.0.0-mock";
    }

    /**
     * 模拟网络延迟
     */
    private CompletableFuture<Void> simulateLatency() {
        return CompletableFuture.runAsync(() -> {
            try {
                long delay = (long) (latencyMs + random.nextGaussian() * latencyMs * 0.2);
                Thread.sleep(Math.max(0, delay));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * 判断是否模拟失败
     */
    private boolean shouldFail() {
        return random.nextDouble() < errorRate;
    }

    /**
     * 生成模拟聊天响应
     */
    private String generateMockResponse(ChatRequest request) {
        String userMessage = request.getMessages().stream()
            .filter(m -> "user".equals(m.getRole()))
            .reduce((first, second) -> second)
            .map(ChatMessage::getContent)
            .orElse("");

        if (userMessage.toLowerCase().contains("hello") || userMessage.toLowerCase().contains("你好")) {
            return "Hello! I'm a mock LLM. How can I help you today?";
        }

        if (userMessage.toLowerCase().contains("help") || userMessage.contains("帮助")) {
            return "I can help you with:\n1. Answering questions\n2. Providing information\n3. Assisting with tasks\n\nWhat would you like to know?";
        }

        if (userMessage.toLowerCase().contains("code") || userMessage.contains("代码")) {
            return "Here's a simple example:\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}\n```";
        }

        return "This is a mock response from the " + driverName + ". " +
               "You asked: \"" + userMessage.substring(0, Math.min(userMessage.length(), 50)) + "...\" " +
               "In a real implementation, this would be processed by an actual LLM.";
    }

    /**
     * 生成模拟补全响应
     */
    private String generateMockCompletion(CompletionRequest request) {
        String prompt = request.getPrompt();

        if (prompt.toLowerCase().contains("json")) {
            return "{\n  \"status\": \"success\",\n  \"data\": {\n    \"message\": \"Mock JSON response\"\n  }\n}";
        }

        if (prompt.toLowerCase().contains("list") || prompt.contains("列表")) {
            return "1. First item\n2. Second item\n3. Third item\n4. Fourth item\n5. Fifth item";
        }

        return "This is a mock completion for the prompt: \"" +
               prompt.substring(0, Math.min(prompt.length(), 50)) + "...\"";
    }
}
