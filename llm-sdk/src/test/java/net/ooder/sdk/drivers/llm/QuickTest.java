package net.ooder.sdk.drivers.llm;

import java.util.Arrays;

/**
 * 快速测试 - 直接运行 main 方法
 */
public class QuickTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=== LLM SDK Quick Test ===\n");

        // 1. 测试 MockLlmDriver
        System.out.println("1. Testing MockLlmDriver...");
        testMockLlmDriver();

        // 2. 测试 SparkLlmDriver（模拟模式）
        System.out.println("\n2. Testing SparkLlmDriver (Mock Mode)...");
        testSparkLlmDriver();

        System.out.println("\n=== All Tests Passed! ===");
    }

    private static void testMockLlmDriver() throws Exception {
        LlmDriver driver = new MockLlmDriver();
        driver.init(new LlmDriver.LlmConfig());

        // 测试聊天
        LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
        request.setModel("mock-model");
        request.setMessages(Arrays.asList(LlmDriver.ChatMessage.user("Hello!")));

        LlmDriver.ChatResponse response = driver.chat(request).get();
        System.out.println("  Chat Response: " + response.getMessage().getContent().substring(0, 50) + "...");
        System.out.println("  Model: " + response.getModel());
        System.out.println("  Tokens: " + response.getUsage().getTotalTokens());

        // 测试补全
        LlmDriver.CompletionRequest compRequest = new LlmDriver.CompletionRequest();
        compRequest.setPrompt("Java is");
        LlmDriver.CompletionResponse compResponse = driver.complete(compRequest).get();
        System.out.println("  Completion: " + compResponse.getChoices().get(0).getText().substring(0, 50) + "...");

        driver.close();
        System.out.println("  ✓ MockLlmDriver test passed");
    }

    private static void testSparkLlmDriver() throws Exception {
        LlmDriver driver = new SparkLlmDriver();

        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setAppId("test-app-id");
        config.setApiKey("test-api-key");
        config.setApiSecret("test-api-secret");
        driver.init(config);

        // 测试聊天（会返回模拟响应）
        LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
        request.setModel("spark-lite");
        request.setMessages(Arrays.asList(LlmDriver.ChatMessage.user("你好")));

        LlmDriver.ChatResponse response = driver.chat(request).get();
        System.out.println("  Chat Response: " + response.getMessage().getContent().substring(0, 50) + "...");

        // 测试模型列表
        System.out.println("  Available Models: " + driver.listModels().get());

        driver.close();
        System.out.println("  ✓ SparkLlmDriver test passed");
    }
}
