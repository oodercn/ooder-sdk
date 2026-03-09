package net.ooder.scene.skill.tool;

import java.util.List;
import java.util.Map;

/**
 * 工具编排服务接口
 *
 * <p>提供多工具编排执行能力，支持：</p>
 * <ul>
 *   <li>顺序执行</li>
 *   <li>并行执行</li>
 *   <li>条件执行</li>
 *   <li>依赖管理</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ToolOrchestrator {
    
    /**
     * 执行单个工具调用
     *
     * @param toolCall 工具调用
     * @param context 执行上下文
     * @return 执行结果
     */
    ToolCallResult executeToolCall(ToolCall toolCall, ToolExecutionContext context);
    
    /**
     * 批量执行工具调用（并行）
     *
     * @param toolCalls 工具调用列表
     * @param context 执行上下文
     * @return 执行结果列表
     */
    List<ToolCallResult> executeToolCalls(List<ToolCall> toolCalls, ToolExecutionContext context);
    
    /**
     * 执行工具编排计划
     *
     * @param plan 编排计划
     * @param context 执行上下文
     * @return 编排执行结果
     */
    OrchestrationResult executePlan(OrchestrationPlan plan, ToolExecutionContext context);
    
    /**
     * 从 LLM 响应解析工具调用
     *
     * @param llmResponse LLM 响应
     * @return 工具调用列表
     */
    List<ToolCall> parseToolCalls(String llmResponse);
    
    /**
     * 将工具调用结果格式化为 LLM 消息
     *
     * @param results 工具调用结果列表
     * @return 格式化后的消息
     */
    String formatToolResults(List<ToolCallResult> results);
}
