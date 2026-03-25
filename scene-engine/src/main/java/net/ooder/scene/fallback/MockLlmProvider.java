package net.ooder.scene.fallback;

import net.ooder.scene.spi.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Mock LLM 提供者 - 降级实现
 *
 * <p>当没有其他 LlmProvider 实现时自动启用</p>
 *
 * <p>返回固定响应，用于测试和开发</p>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(LlmProvider.class)
@ConditionalOnProperty(prefix = "scene.engine.fallback", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MockLlmProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(MockLlmProvider.class);

    public MockLlmProvider() {
        log.warn("Using MockLlmProvider - responses are simulated!");
    }

    @Override
    public String getProviderType() {
        return "fallback";
    }

    @Override
    public String chat(String prompt, Map<String, Object> params) {
        log.debug("Mock LLM response for prompt: {}", prompt.substring(0, Math.min(50, prompt.length())));

        if (prompt.contains("你好") || prompt.contains("hello")) {
            return "你好！我是 Mock LLM，这是一个模拟响应。请配置真实的 LLM 提供者以获得实际功能。";
        }

        if (prompt.contains("?") || prompt.contains("？")) {
            return "这是一个 Mock 响应。您的问题是：" + prompt + "\n\n请配置真实的 LLM 提供者以获得实际回答。";
        }

        return "Mock LLM 响应: 已收到您的请求。请配置 scene.engine.driver 以使用真实的 LLM 服务。";
    }

    @Override
    public CompletableFuture<String> chatAsync(String prompt, Map<String, Object> params) {
        return CompletableFuture.completedFuture(chat(prompt, params));
    }

    @Override
    public void chatStream(String prompt, Map<String, Object> params, StreamCallback callback) {
        String response = chat(prompt, params);
        String[] words = response.split("");

        for (String word : words) {
            callback.onNext(word);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        callback.onComplete(response);
    }

    @Override
    public String chatWithContext(String prompt, List<Map<String, String>> context, Map<String, Object> params) {
        return chat(prompt, params);
    }

    @Override
    public String getModelName() {
        return "mock-llm";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
