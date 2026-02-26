package net.ooder.scene.skills.ai;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AI Skill 接口
 *
 * <p>提供AIGC能力、MCP协议支持、工作流编排等AI相关能力。</p>
 *
 * @author Ooder Team
 * @version 0.8.0
 */
public interface AISkill {

    String getSkillId();

    String getSkillName();

    String getSkillVersion();

    List<String> getCapabilities();

    // ==================== AIGC 能力 ====================

    /**
     * 执行文本生成任务
     *
     * @param modelId 模型ID
     * @param prompt 提示词
     * @param params 额外参数
     * @return 生成结果
     */
    CompletableFuture<AIGCResult> generateText(String modelId, String prompt, Map<String, Object> params);

    /**
     * 执行对话任务
     *
     * @param modelId 模型ID
     * @param messages 消息列表
     * @param params 额外参数
     * @return 对话结果
     */
    CompletableFuture<AIGCResult> chat(String modelId, List<Message> messages, Map<String, Object> params);

    /**
     * 获取可用模型列表
     *
     * @return 模型信息列表
     */
    CompletableFuture<List<ModelInfo>> getAvailableModels();

    // ==================== MCP 能力 ====================

    /**
     * 注册MCP客户端
     *
     * @param clientId 客户端ID
     * @param config MCP配置
     * @return 注册结果
     */
    CompletableFuture<MCPResult> registerMCPClient(String clientId, MCPConfig config);

    /**
     * 调用MCP工具
     *
     * @param clientId 客户端ID
     * @param toolName 工具名称
     * @param params 参数
     * @return 调用结果
     */
    CompletableFuture<MCPResult> callMCPTool(String clientId, String toolName, Map<String, Object> params);

    /**
     * 获取MCP客户端列表
     *
     * @return 客户端列表
     */
    List<MCPClientInfo> getMCPClients();

    // ==================== 工作流能力 ====================

    /**
     * 执行工作流
     *
     * @param workflowId 工作流ID
     * @param params 参数
     * @return 执行结果
     */
    CompletableFuture<WorkflowResult> executeWorkflow(String workflowId, Map<String, Object> params);

    /**
     * 注册工作流
     *
     * @param workflow 工作流定义
     * @return 注册结果
     */
    CompletableFuture<WorkflowResult> registerWorkflow(WorkflowDefinition workflow);

    /**
     * 获取工作流列表
     *
     * @return 工作流列表
     */
    List<WorkflowDefinition> getWorkflows();

    // ==================== 能力调用 ====================

    /**
     * 调用能力
     *
     * @param capability 能力名称
     * @param params 参数
     * @return 调用结果
     */
    Object invoke(String capability, Map<String, Object> params);
}
