package net.ooder.sdk.llm.tool;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 工具执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolExecutionResult {

    /**
     * 工具ID
     */
    private String toolId;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 执行结果
     */
    private Object result;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 执行耗时(毫秒)
     */
    private long executionTime;

    /**
     * 调用ID
     */
    private String invocationId;

    /**
     * 执行时间戳
     */
    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    /**
     * 创建成功结果
     */
    public static ToolExecutionResult success(String toolId, Object result) {
        return ToolExecutionResult.builder()
                .toolId(toolId)
                .success(true)
                .result(result)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static ToolExecutionResult failure(String toolId, String error) {
        return ToolExecutionResult.builder()
                .toolId(toolId)
                .success(false)
                .error(error)
                .build();
    }
}
