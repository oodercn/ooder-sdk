package net.ooder.sdk.drivers.llm;

import java.util.Arrays;
import java.util.Scanner;

/**
 * LLM Driver 演示程序
 * 运行方式: mvn test -Dtest=LlmDriverDemo -q
 */
public class LlmDriverDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           LLM SDK Demo - Mock LLM Driver                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 创建并初始化 Mock LLM Driver
        LlmDriver driver = new MockLlmDriver();
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setModel("mock-model");
        config.setMaxTokens(500);
        driver.init(config);

        System.out.println("✓ Mock LLM Driver 初始化完成");
        System.out.println();

        // 显示可用模型
        System.out.println("可用模型:");
        driver.listModels().get().forEach(model -> {
            System.out.println("  - " + model);
        });
        System.out.println();

        // 交互式对话
        Scanner scanner = new Scanner(System.in);
        System.out.println("开始对话 (输入 'exit' 退出):");
        System.out.println("─────────────────────────────────");

        while (true) {
            System.out.print("\n你: ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                System.out.println("\n再见!");
                break;
            }

            if (input.trim().isEmpty()) {
                continue;
            }

            // 创建聊天请求
            LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
            request.setModel("mock-model");
            request.setMessages(Arrays.asList(LlmDriver.ChatMessage.user(input)));

            try {
                // 发送请求并获取响应
                LlmDriver.ChatResponse response = driver.chat(request).get();
                String content = response.getMessage().getContent();

                System.out.println("AI: " + content);
                System.out.println("   (使用 token: " + response.getUsage().getTotalTokens() + ")");

            } catch (Exception e) {
                System.err.println("错误: " + e.getMessage());
            }
        }

        driver.close();
        scanner.close();
    }
}
