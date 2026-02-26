package net.ooder.sdk.drivers.llm;

import java.util.Arrays;

/**
 * 百度文心 LLM 测试
 * 使用你的 API Key: bce-v3/ALTAK-KFPlgVE1cIisuNRIh0Sip/5bd30a81960da845cb065d5c522f2d57aa477078
 */
public class BaiduWenxinTest {

    // 你的百度 API Key
    private static final String API_KEY = "bce-v3/ALTAK-KFPlgVE1cIisuNRIh0Sip/5bd30a81960da845cb065d5c522f2d57aa477078";

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        百度文心 LLM Driver 测试                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // 1. 初始化 Driver
            System.out.println("1. 初始化 BaiduWenxinDriver...");
            BaiduWenxinDriver driver = new BaiduWenxinDriver();

            LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
            config.setApiKey(API_KEY);
            config.setTemperature(0.7);
            driver.init(config);
            System.out.println("   ✓ Driver 初始化成功");
            System.out.println();

            // 2. 获取可用模型列表
            System.out.println("2. 获取可用模型列表...");
            System.out.println("   可用模型: " + driver.listModels().get());
            System.out.println();

            // 3. 测试模型信息
            System.out.println("3. 获取模型信息 (ernie-speed)...");
            LlmDriver.ModelInfo info = driver.getModelInfo("ernie-speed").get();
            System.out.println("   模型名称: " + info.getName());
            System.out.println("   上下文长度: " + info.getContextLength());
            System.out.println("   描述: " + info.getDescription());
            System.out.println();

            // 4. 测试聊天功能
            System.out.println("4. 测试聊天功能...");
            System.out.println("   发送消息: 你好，请介绍一下你自己");

            LlmDriver.ChatRequest chatRequest = new LlmDriver.ChatRequest();
            chatRequest.setModel("ernie-speed");  // 使用免费模型
            chatRequest.setMessages(Arrays.asList(
                LlmDriver.ChatMessage.user("你好，请介绍一下你自己")
            ));

            LlmDriver.ChatResponse chatResponse = driver.chat(chatRequest).get();

            System.out.println("   AI回复: " + chatResponse.getMessage().getContent());
            System.out.println("   使用 Token: " + chatResponse.getUsage().getTotalTokens());
            System.out.println("   模型: " + chatResponse.getModel());
            System.out.println();

            // 5. 测试文本补全
            System.out.println("5. 测试文本补全...");
            System.out.println("   Prompt: Java 是一种");

            LlmDriver.CompletionRequest compRequest = new LlmDriver.CompletionRequest();
            compRequest.setModel("ernie-speed");
            compRequest.setPrompt("Java 是一种");
            compRequest.setMaxTokens(100);

            LlmDriver.CompletionResponse compResponse = driver.complete(compRequest).get();
            System.out.println("   补全结果: " + compResponse.getChoices().get(0).getText());
            System.out.println();

            // 6. 测试嵌入向量
            System.out.println("6. 测试嵌入向量...");
            LlmDriver.EmbeddingRequest embedRequest = new LlmDriver.EmbeddingRequest();
            embedRequest.setInput(Arrays.asList("你好世界", "Hello World"));

            LlmDriver.EmbeddingResponse embedResponse = driver.embed(embedRequest).get();
            System.out.println("   嵌入向量数量: " + embedResponse.getData().size());
            System.out.println("   第一个向量维度: " + embedResponse.getData().get(0).getEmbedding().length);
            System.out.println("   使用 Token: " + embedResponse.getUsage().getTotalTokens());
            System.out.println();

            // 7. 多轮对话测试
            System.out.println("7. 测试多轮对话...");
            System.out.println("   用户: 什么是人工智能？");

            LlmDriver.ChatRequest multiRequest1 = new LlmDriver.ChatRequest();
            multiRequest1.setModel("ernie-speed");
            multiRequest1.setMessages(Arrays.asList(
                LlmDriver.ChatMessage.user("什么是人工智能？")
            ));

            LlmDriver.ChatResponse multiResponse1 = driver.chat(multiRequest1).get();
            String firstReply = multiResponse1.getMessage().getContent();
            System.out.println("   AI: " + firstReply.substring(0, Math.min(firstReply.length(), 100)) + "...");
            System.out.println();

            System.out.println("   用户: 它有哪些应用场景？");
            LlmDriver.ChatRequest multiRequest2 = new LlmDriver.ChatRequest();
            multiRequest2.setModel("ernie-speed");
            multiRequest2.setMessages(Arrays.asList(
                LlmDriver.ChatMessage.user("什么是人工智能？"),
                LlmDriver.ChatMessage.assistant(firstReply),
                LlmDriver.ChatMessage.user("它有哪些应用场景？")
            ));

            LlmDriver.ChatResponse multiResponse2 = driver.chat(multiRequest2).get();
            String secondReply = multiResponse2.getMessage().getContent();
            System.out.println("   AI: " + secondReply.substring(0, Math.min(secondReply.length(), 100)) + "...");
            System.out.println();

            // 关闭 Driver
            driver.close();

            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              所有测试通过！                                ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("\n❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
