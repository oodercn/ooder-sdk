package net.ooder.sdk.drivers.llm;

import java.util.Arrays;

/**
 * 百度文心 LLM 快速测试
 * 在 IDE 中右键运行此类的 main 方法
 */
public class BaiduWenxinQuickTest {

    // 请替换为你的百度 API Key
    private static final String API_KEY = System.getenv("BAIDU_API_KEY");

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        百度文心 LLM Driver 测试                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("错误: 请设置环境变量 BAIDU_API_KEY");
            System.exit(1);
        }

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

            // 2. 测试简单对话
            System.out.println("2. 测试简单对话...");
            LlmDriver.ChatMessage message = LlmDriver.ChatMessage.user("你好，请介绍一下你自己");
            LlmDriver.ChatRequest request = LlmDriver.ChatRequest.create(null, Arrays.asList(message));
            LlmDriver.ChatResponse response = driver.chat(request).join();
            System.out.println("   用户: 你好，请介绍一下你自己");
            System.out.println("   AI: " + response.getMessage().getContent());
            System.out.println();

            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                  所有测试通过！                            ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
