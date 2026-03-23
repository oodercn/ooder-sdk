package net.ooder.sdk.llm.tool;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 工具调用结果
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallResult {

    private String toolId;
    private String toolName;
    private Map<String, Object> arguments;
    private Object result;
    private boolean success;
    private String errorMessage;
    private long executionTimeMs;

    public static ToolCallResult success(String toolId, String toolName, Object result, long executionTimeMs) {
        return ToolCallResult.builder()
                .toolId(toolId)
                .toolName(toolName)
                .result(result)
                .success(true)
                .executionTimeMs(executionTimeMs)
                .build();
    }

    public static ToolCallResult failure(String toolId, String toolName, String errorMessage) {
        return ToolCallResult.builder()
                .toolId(toolId)
                .toolName(toolName)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
