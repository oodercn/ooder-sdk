package net.ooder.sdk.drivers.llm;

import java.util.Arrays;

/**
 * 百度文心 LLM 快速测试
 * 在 IDE 中右键运行此类的 main 方法
 */
public class BaiduWenxinQuickTest {

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

            // 2.