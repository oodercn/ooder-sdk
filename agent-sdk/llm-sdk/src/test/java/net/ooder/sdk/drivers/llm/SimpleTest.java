package net.ooder.sdk.drivers.llm;

import java.util.Arrays;

/**
 * 简化的测试类 - 直接在 IDE 中运行
 */
public class SimpleTest {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    百度文心 LLM 测试");
        System.out.println("===========================================");
        System.out.println();

        try {
            // 1. 初始化 Driver
            System.out.println("1. 初始化 BaiduWenxinDriver...");
            BaiduWenxinDriver driver = new BaiduWenxinDriver();

            LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
            config.setApiKey("bce-v3/ALTAK-KFPlgVE1cIisuNRIh0Sip/5bd30a81960da845cb065d5c522f2d57aa477078");
            config.setTemperature(0.7);
            driver.init(config);
            System.out.println("   ✓ Driver 初始化成功");
            System.out.println();

            // 2. 测试聊天功能
            System.out.println("2. 测试聊天功能...");
            System.out.println("   发送消息: 你好，请介绍一下你自己");

            LlmDriver.ChatRequest chatRequest = new LlmDriver.ChatRequest();
            chatRequest.setModel("ernie-speed");
            chatRequest.setMessages(Arrays.asList(
                LlmDriver.ChatMessage.user("你好，请介绍一下你自己")
            ));

            LlmDriver.ChatResponse chatResponse = driver.chat(chatRequest).get();

            System.out.println("   AI回复: " + chatResponse.getMessage().getContent());
            System.out.println("   使用 Token: " + chatResponse.getUsage().getTotalTokens());
            System.out.println("   模型: " + chatResponse.getModel());
            System.out.println();

            // 3. 测试文本补全
            System.out.println("3. 测试文本补全...");
            System.out.println("   Prompt: Java 是一种");

            LlmDriver.CompletionRequest compRequest = new LlmDriver.CompletionRequest();
            compRequest.setModel("ernie-speed");
            compRequest.setPrompt("Java 是一种");
            compRequest.setMaxTokens(100);

            LlmDriver.CompletionResponse compResponse = driver.complete(compRequest).get();
            System.out.println("   补全结果: " + compResponse.getChoices().get(0).getText());
            System.out.println();

            // 关闭 Driver
            driver.close();

            System.out.println("===========================================");
            System.out.println("            测试成功！");
            System.out.println("===========================================");

        } catch (Exception e) {
            System.err.println("\n❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
