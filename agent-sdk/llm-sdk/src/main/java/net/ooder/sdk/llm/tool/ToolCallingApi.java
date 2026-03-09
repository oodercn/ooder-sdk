package net.ooder.sdk.llm.tool;

import java.util.List;

/**
 * 工具调用 API
 */
public interface ToolCallingApi {

    /**
     * 注册工具
     * @param toolDef 工具定义
     * @return 注册结果
     */
    ToolRegistration registerTool(ToolDefinition toolDef);

    /**
     * 注销工具
     * @param toolId 工具ID
     */
    void unregisterTool(String toolId);

    /**
     * 执行工具调用
     * @param request 调用请求
     * @return 执行结果
     */
    ToolExecutionResult executeTool(ToolExecutionRequest request);

    /**
     * 获取工具列表
     * @return 工具列表
     */
    List<ToolDefinition> listTools();

    /**
     * 获取指定工具
     * @param toolId 工具ID
     * @return 工具定义
     */
    ToolDefinition getTool(String toolId);

    /**
     * 检查工具是否存在
     * @param toolId 工具ID
     * @return 是否存在
     */
    boolean hasTool(String toolId);

    /**
     * LLM对话+工具调用
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chatWithTools(ChatRequest request);

    /**
     * 执行工具调用并继续对话
     * @param sessionId 会话ID
     * @param toolResults 工具执行结果
     * @return 对话响应
     */
    ChatResponse continueWithToolResults(String sessionId, List<ToolExecutionResult> toolResults);
}
