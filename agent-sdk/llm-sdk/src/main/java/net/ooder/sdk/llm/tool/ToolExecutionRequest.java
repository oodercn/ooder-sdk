package net.ooder.sdk.llm.tool;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 工具执行请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolExecutionRequest {

    /**
     * 工具ID
     */
    private String toolId;

    /**
     * 调用参数
     */
    private Map<String, Object> parameters;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 调用ID
     */
    private String invocationId;

    /**
     * 调用时间戳
     */
    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    /**
     * 调用来源
     */
    private String source;

    /**
     * 用户ID
     */
    private String userId;
}
