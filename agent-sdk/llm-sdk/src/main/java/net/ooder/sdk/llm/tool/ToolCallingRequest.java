package net.ooder.sdk.llm.tool;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 工具调用对话请求
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallingRequest {

    private String conversationId;
    private List<Message> messages;
    private String model;
    private List<ToolDefinition> tools;
    private ToolChoiceStrategy toolChoice;
    private Integer maxToolCalls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private MessageRole role;
        private String content;
        private String name;
        private List<ToolCall> toolCalls;
        private String toolCallId;
    }

    public enum MessageRole {
        SYSTEM,
        USER,
        ASSISTANT,
        TOOL
    }

    public enum ToolChoiceStrategy {
        AUTO,
        SPECIFIC,
        NONE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCall {
        private String id;
        private String type;
        private FunctionCall function;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionCall {
        private String name;
        private String arguments;
    }
}
