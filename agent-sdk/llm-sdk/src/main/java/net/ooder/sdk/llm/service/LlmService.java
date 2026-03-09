package net.ooder.sdk.llm.service;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * LLM 服务接口
 * 由 LLM-SDK 实现，Engine 调用
 */
public interface LlmService {

    /**
     * 普通对话
     *
     * @param request 对话请求
     * @return 对话响应
     */
    LlmResponse chat(ChatRequest request);

    /**
     * 流式对话
     *
     * @param request 对话请求
     * @param handler 流式响应处理器
     */
    void chatStream(ChatRequest request, StreamResponseHandler handler);

    /**
     * 带工具调用的对话
     *
     * @param request 对话请求
     * @return 对话响应（可能包含工具调用）
     */
    LlmResponse chatWithTools(ChatRequest request);

    /**
     * 对话请求
     */
    @Data
    @Builder
    class ChatRequest {
        private String requestId;           // 请求ID
        private String model;               // 模型名称 (gpt-4, claude-3, etc.)
        private List<Message> messages;     // 消息列表
        private List<ToolDefinition> tools; // 可用工具定义
        private Float temperature;          // 温度
        private Integer maxTokens;          // 最大Token数
        private Map<String, Object> extraParams;  // 额外参数
    }

    /**
     * 对话响应
     */
    @Data
    @Builder
    class LlmResponse {
        private String responseId;          // 响应ID
        private String model;               // 实际使用的模型
        private String content;             // 响应内容
        private List<ToolCall> toolCalls;   // 工具调用
        private FinishReason finishReason;  // 结束原因
        private TokenUsage tokenUsage;      // Token使用量
        private long latency;               // 延迟(ms)
    }

    /**
     * 消息
     */
    @Data
    @Builder
    class Message {
        private MessageRole role;           // 角色 (system/user/assistant/tool)
        private String content;             // 内容
        private String name;                // 名称（用于tool消息）
        private List<ToolCall> toolCalls;   // 工具调用（assistant消息）
    }

    /**
     * 工具定义
     */
    @Data
    @Builder
    class ToolDefinition {
        private String type;                // 类型 (function)
        private FunctionDefinition function; // 函数定义
    }

    /**
     * 函数定义
     */
    @Data
    @Builder
    class FunctionDefinition {
        private String name;                // 函数名
        private String description;         // 函数描述
        private JsonSchema parameters;      // 参数Schema
    }

    /**
     * JSON Schema
     */
    @Data
    @Builder
    class JsonSchema {
        private String type;
        private Map<String, Object> properties;
        private List<String> required;
    }

    /**
     * 工具调用
     */
    @Data
    @Builder
    class ToolCall {
        private String id;                  // 调用ID
        private String type;                // 类型 (function)
        private FunctionCall function;      // 函数调用
    }

    /**
     * 函数调用
     */
    @Data
    @Builder
    class FunctionCall {
        private String name;                // 函数名
        private String arguments;           // 参数JSON字符串
    }

    /**
     * Token 使用量
     */
    @Data
    @Builder
    class TokenUsage {
        private int promptTokens;       // Prompt Token 数
        private int completionTokens;   // 补全 Token 数
        private int totalTokens;        // 总 Token 数
    }

    /**
     * 消息角色枚举
     */
    enum MessageRole {
        SYSTEM,
        USER,
        ASSISTANT,
        TOOL
    }

    /**
     * 结束原因枚举
     */
    enum FinishReason {
        STOP,
        LENGTH,
        TOOL_CALLS,
        CONTENT_FILTER,
        ERROR
    }

    /**
     * 流式响应处理器
     */
    interface StreamResponseHandler {
        /**
         * 收到内容片段
         */
        void onContent(String content);

        /**
         * 收到工具调用
         */
        void onToolCall(ToolCall toolCall);

        /**
         * 完成
         */
        void onComplete(LlmResponse response);

        /**
         * 发生错误
         */
        void onError(Throwable error);
    }
}
