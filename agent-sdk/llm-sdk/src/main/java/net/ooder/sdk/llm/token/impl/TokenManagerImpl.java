package net.ooder.sdk.llm.token.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.token.TokenManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Token 管理器实现
 */
@Slf4j
public class TokenManagerImpl implements TokenManager {

    // 简化的 Token 限制配置
    private final Map<String, TokenLimit> tokenLimits = new HashMap<>();

    public TokenManagerImpl() {
        // 初始化常见模型的 Token 限制
        tokenLimits.put("gpt-4", TokenLimit.builder()
                .model("gpt-4")
                .maxTotalTokens(8192)
                .maxInputTokens(6000)
                .maxOutputTokens(2000)
                .build());

        tokenLimits.put("gpt-4-turbo", TokenLimit.builder()
                .model("gpt-4-turbo")
                .maxTotalTokens(128000)
                .maxInputTokens(100000)
                .maxOutputTokens(4000)
                .build());

        tokenLimits.put("gpt-3.5-turbo", TokenLimit.builder()
                .model("gpt-3.5-turbo")
                .maxTotalTokens(4096)
                .maxInputTokens(3000)
                .maxOutputTokens(1000)
                .build());

        tokenLimits.put("claude-3-opus", TokenLimit.builder()
                .model("claude-3-opus")
                .maxTotalTokens(200000)
                .maxInputTokens(150000)
                .maxOutputTokens(4096)
                .build());
    }

    @Override
    public int countTokens(String model, String text) {
        /**
         * FIXME: 伪实现 - 需要使用真实的 Tokenizer
         *
         * 预期实现：
         * 1. 根据 model 选择合适的 Tokenizer (tiktoken, claude tokenizer, etc.)
         * 2. 调用 Tokenizer 计算 Token 数
         */
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 简化估算：约 4 个字符 = 1 个 Token
        return text.length() / 4;
    }

    @Override
    public int countMessageTokens(String model, String message) {
        // 消息通常有一些额外开销（角色标记等）
        int baseTokens = countTokens(model, message);
        return baseTokens + 4; // 添加消息格式开销
    }

    @Override
    public String truncateToTokens(String model, String text, int maxTokens) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        int currentTokens = countTokens(model, text);
        if (currentTokens <= maxTokens) {
            return text;
        }

        // 简化截断：按字符比例截断
        int targetLength = maxTokens * 4;
        if (targetLength >= text.length()) {
            return text;
        }

        return text.substring(0, targetLength) + "...";
    }

    @Override
    public TokenLimit getTokenLimit(String model) {
        TokenLimit limit = tokenLimits.get(model);
        if (limit == null) {
            // 返回默认值
            return TokenLimit.builder()
                    .model(model)
                    .maxTotalTokens(4096)
                    .maxInputTokens(3000)
                    .maxOutputTokens(1000)
                    .build();
        }
        return limit;
    }
}
