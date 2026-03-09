package net.ooder.sdk.llm.tool;

/**
 * 工具处理器接口
 */
public interface ToolHandler {

    /**
     * 执行工具
     * @param request 执行请求
     * @return 执行结果
     */
    ToolExecutionResult execute(ToolExecutionRequest request);

    /**
     * 获取工具定义
     * @return 工具定义
     */
    ToolDefinition getDefinition();

    /**
     * 验证参数
     * @param parameters 参数
     * @return 是否有效
     */
    default boolean validateParameters(java.util.Map<String, Object> parameters) {
        return true;
    }
}
