package net.ooder.sdk.llm.tool;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 工具调用响应
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallingResponse {

    private String responseId;
    private String conversationId;
    private String content;
    private List<ToolCallInfo> toolCalls;
    private FinishReason finishReason;
    private long latencyMs;
    private Map<String, Object> metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCallInfo {
        private String id;
        private String type;
        private String name;
        private Map<String, Object> arguments;
        private ToolCallResult result;
    }

    public enum FinishReason {
        STOP,
        TOOL_CALLS,
        LENGTH,
        ERROR
    }

    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }

    public static ToolCallingResponse stop(String responseId, String content, long latencyMs) {
        return ToolCallingResponse.builder()
                .responseId(responseId)
                .content(content)
                .finishReason(FinishReason.STOP)
                .latencyMs(latencyMs)
                .build();
    }

    public static ToolCallingResponse toolCalls(String responseId, List<ToolCallInfo> toolCalls, long latencyMs) {
        return ToolCallingResponse.builder()
                .responseId(responseId)
                .toolCalls(toolCalls)
                .finishReason(FinishReason.TOOL_CALLS)
                .latencyMs(latencyMs)
                .build();
    }
}
