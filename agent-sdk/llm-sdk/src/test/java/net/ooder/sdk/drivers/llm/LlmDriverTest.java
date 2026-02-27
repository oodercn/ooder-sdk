package net.ooder.sdk.drivers.llm;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * LLM Driver 测试类
 * 演示如何使用 MockLlmDriver 进行测试
 */
public class LlmDriverTest {

    private static final Logger log = LoggerFactory.getLogger(LlmDriverTest.class);

    /**
     * 测试基本的聊天功能
     */
    @Test
    public void testChat() throws Exception {
        // 创建 Mock LLM Driver
        LlmDriver driver = new MockLlmDriver();

        // 初始化配置
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setModel("mock-model");
        config.setMaxTokens(1000);
        driver.init(config);

        // 创建聊天请求
        LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
        request.setModel("mock-model");

        LlmDriver.ChatMessage userMessage = LlmDriver.ChatMessage.user("Hello, how are you?");
        request.setMessages(Arrays.asList(userMessage));

        // 发送请求
        LlmDriver.ChatResponse response = driver.chat(request).get();

        // 验证响应
        assertNotNull(response);
        assertNotNull(response.getMessage());
        assertNotNull(response.getMessage().getContent());
        assertFalse(response.getMessage().getContent().isEmpty());

        log.info("Chat response: {}", response.getMessage().getContent());

        driver.close();
    }

    /**
     * 测试文本补全功能
     */
    @Test
    public void testComplete() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        LlmDriver.CompletionRequest request = new LlmDriver.CompletionRequest();
        request.setPrompt("List three benefits of Java:");
        request.setMaxTokens(100);

        LlmDriver.CompletionResponse response = driver.complete(request).get();

        assertNotNull(response);
        assertNotNull(response.getChoices());
        assertFalse(response.getChoices().isEmpty());

        log.info("Completion result: {}", response.getChoices().get(0).getText());

        driver.close();
    }

    /**
     * 测试嵌入向量功能
     */
    @Test
    public void testEmbed() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        LlmDriver.EmbeddingRequest request = new LlmDriver.EmbeddingRequest();
        request.setModel("mock-embedding-model");
        request.setInput(Arrays.asList("Hello world", "Test sentence"));

        LlmDriver.EmbeddingResponse response = driver.embed(request).get();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size()); // 两个输入，两个输出

        LlmDriver.EmbeddingData embedding = response.getData().get(0);
        assertNotNull(embedding.getEmbedding());
        assertTrue(embedding.getEmbedding().length > 0);

        log.info("Embedding vector length: {}", embedding.getEmbedding().length);

        driver.close();
    }

    /**
     * 测试流式聊天
     */
    @Test
    public void testChatStream() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
        request.setModel("mock-model");
        request.setMessages(Arrays.asList(LlmDriver.ChatMessage.user("Tell me a story")));

        StringBuilder contentBuilder = new StringBuilder();

        LlmDriver.ChatStreamHandler handler = new LlmDriver.ChatStreamHandler() {
            @Override
            public void onToken(String token) {
                contentBuilder.append(token);
                log.debug("Received token: {}", token);
            }

            @Override
            public void onMessage(LlmDriver.ChatMessage message) {
                log.info("Received message: {}", message.getContent());
            }

            @Override
            public void onComplete(LlmDriver.ChatResponse response) {
                log.info("Stream completed");
            }

            @Override
            public void onError(Throwable error) {
                log.error("Stream error", error);
            }
        };

        LlmDriver.ChatResponse response = driver.chatStream(request, handler).get();

        assertNotNull(response);
        log.info("Stream response content length: {}", contentBuilder.length());

        driver.close();
    }

    /**
     * 测试模型列表
     */
    @Test
    public void testListModels() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        List<String> models = driver.listModels().get();

        assertNotNull(models);
        assertFalse(models.isEmpty());

        log.info("Available models: {}", models);

        driver.close();
    }

    /**
     * 测试模型信息
     */
    @Test
    public void testGetModelInfo() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        LlmDriver.ModelInfo info = driver.getModelInfo("mock-model").get();

        assertNotNull(info);
        assertEquals("mock-model", info.getId());
        assertTrue(info.getContextLength() > 0);

        log.info("Model info: name={}, contextLength={}", info.getName(), info.getContextLength());

        driver.close();
    }

    /**
     * 测试 Token 计数
     */
    @Test
    public void testCountTokens() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        String text = "This is a test sentence for token counting.";
        LlmDriver.TokenCountResponse response = driver.countTokens(text).get();

        assertNotNull(response);
        assertTrue(response.getTokenCount() > 0);

        log.info("Text: '{}' has {} tokens", text, response.getTokenCount());

        driver.close();
    }

    /**
     * 集成测试：完整的对话流程
     */
    @Test
    public void testFullConversation() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        // 第一轮对话
        LlmDriver.ChatMessage msg1 = LlmDriver.ChatMessage.user("What is Java?");
        LlmDriver.ChatRequest req1 = new LlmDriver.ChatRequest();
        req1.setMessages(Arrays.asList(msg1));
        LlmDriver.ChatResponse resp1 = driver.chat(req1).get();

        log.info("User: What is Java?");
        log.info("Assistant: {}", resp1.getMessage().getContent());

        // 第二轮对话（带上下文）
        LlmDriver.ChatMessage msg2 = LlmDriver.ChatMessage.user("What are its main features?");
        LlmDriver.ChatRequest req2 = new LlmDriver.ChatRequest();
        req2.setMessages(Arrays.asList(
            msg1,
            LlmDriver.ChatMessage.assistant(resp1.getMessage().getContent()),
            msg2
        ));
        LlmDriver.ChatResponse resp2 = driver.chat(req2).get();

        log.info("User: What are its main features?");
        log.info("Assistant: {}", resp2.getMessage().getContent());

        driver.close();
    }
}
