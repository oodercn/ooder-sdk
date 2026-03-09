package net.ooder.sdk.llm.token;

import lombok.Builder;
import lombok.Data;

/**
 * Token 管理器接口
 * 由 LLM-SDK 实现，Engine 调用
 */
public interface TokenManager {

    /**
     * 计算文本 Token 数
     *
     * @param model 模型名称
     * @param text  文本内容
     * @return Token 数量
     */
    int countTokens(String model, String text);

    /**
     * 计算消息 Token 数
     *
     * @param model   模型名称
     * @param message 消息内容
     * @return Token 数量
     */
    int countMessageTokens(String model, String message);

    /**
     * 截断文本到指定 Token 数
     *
     * @param model     模型名称
     * @param text      文本内容
     * @param maxTokens 最大 Token 数
     * @return 截断后的文本
     */
    String truncateToTokens(String model, String text, int maxTokens);

    /**
     * 获取模型 Token 限制
     *
     * @param model 模型名称
     * @return Token 限制信息
     */
    TokenLimit getTokenLimit(String model);

    /**
     * Token 限制信息
     */
    @Data
    @Builder
    class TokenLimit {
        private String model;           // 模型名称
        private int maxTotalTokens;     // 最大总 Token 数
        private int maxInputTokens;     // 最大输入 Token 数
        private int maxOutputTokens;    // 最大输出 Token 数
    }
}
