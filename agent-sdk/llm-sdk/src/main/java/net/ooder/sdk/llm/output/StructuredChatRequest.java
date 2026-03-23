package net.ooder.sdk.llm.output;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 结构化对话请求
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructuredChatRequest {

    private String conversationId;
    private List<Message> messages;
    private String model;
    private ResponseSchema responseSchema;
    private Integer maxTokens;
    private Double temperature;
    private Map<String, Object> options;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private MessageRole role;
        private String content;
        private String name;
    }

    public enum MessageRole {
        SYSTEM,
        USER,
        ASSISTANT
    }
}
