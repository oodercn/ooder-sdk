package net.ooder.sdk.llm.tool;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 对话响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 响应内容
     */
    private String content;

    /**
     * 是否调用了工具
     */
    private boolean toolCalled;

    /**
     * 工具调用请求列表
     */
    private List<ToolCallRequest> toolCalls;

    /**
     * 工具执行结果
     */
    private List<ToolExecutionResult> toolResults;

    /**
     * 是否完成
     */
    private boolean completed;

    /**
     * 错误信息
     */
    private String error;

    /**
     * Token使用量
     */
    private TokenUsage tokenUsage;

    /**
     * 工具调用请求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCallRequest {
        private String toolId;
        private String invocationId;
        private java.util.Map<String, Object> parameters;
    }

    /**
     * Token使用量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }

    /**
     * 创建成功响应
     */
    public static ChatResponse success(String sessionId, String content) {
        return ChatResponse.builder()
                .sessionId(sessionId)
                .content(content)
                .completed(true)
                .build();
    }

    /**
     * 创建工具调用响应
     */
    public static ChatResponse toolCall(String sessionId, List<ToolCallRequest> toolCalls) {
        return ChatResponse.builder()
                .sessionId(sessionId)
                .toolCalled(true)
                .toolCalls(toolCalls)
                .completed(false)
                .build();
    }

    /**
     * 创建错误响应
     */
    public static ChatResponse error(String sessionId, String error) {
        return ChatResponse.builder()
                .sessionId(sessionId)
                .error(error)
                .completed(false)
                .build();
    }
}
