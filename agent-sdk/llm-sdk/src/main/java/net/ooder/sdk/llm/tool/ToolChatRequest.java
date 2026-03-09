package net.ooder.sdk.llm.tool;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 工具层对话请求
 * 用于工具调用场景
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolChatRequest {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户输入
     */
    private String userInput;

    /**
     * 消息历史
     */
    private List<Message> messages;

    /**
     * 可用工具列表
     */
    private List<String> availableTools;

    /**
     * 上下文变量
     */
    private Map<String, Object> variables;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 模型配置
     */
    private ModelConfig modelConfig;

    /**
     * 消息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private long timestamp;
    }

    /**
     * 模型配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {
        private String modelId;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;
    }
}
