package net.ooder.scene.skill.llm;

import java.util.List;
import java.util.Map;

/**
 * 增强的 LLM Provider 接口
 * 
 * <p>扩展基础 LLM Provider，增加函数调用、多模态等高级能力</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface EnhancedLlmProvider extends LlmProvider {

    /**
     * 带函数调用的对话
     *
     * @param model 模型名称
     * @param messages 消息列表
     * @param functions 可用函数列表
     * @param options 可选参数
     * @return 对话结果，可能包含 function_call
     */
    Map<String, Object> chatWithFunctions(String model, 
                                          List<Map<String, Object>> messages,
                                          List<FunctionCall> functions,
                                          Map<String, Object> options);

    /**
     * 执行函数调用
     *
     * @param model 模型名称
     * @param messages 消息列表
     * @param functionName 函数名称
     * @param functionArgs 函数参数
     * @param functionResult 函数执行结果
     * @param options 可选参数
     * @return 对话结果
     */
    Map<String, Object> executeFunctionCall(String model,
                                            List<Map<String, Object>> messages,
                                            String functionName,
                                            Map<String, Object> functionArgs,
                                            Object functionResult,
                                            Map<String, Object> options);

    /**
     * 多模态对话（支持图片）
     *
     * @param model 模型名称
     * @param messages 消息列表（可包含图片）
     * @param options 可选参数
     * @return 对话结果
     */
    Map<String, Object> chatMultimodal(String model,
                                       List<Map<String, Object>> messages,
                                       Map<String, Object> options);

    /**
     * 带上下文管理的对话
     *
     * @param model 模型名称
     * @param messages 消息列表
     * @param systemPrompt 系统提示
     * @param context 上下文信息
     * @param options 可选参数
     * @return 对话结果
     */
    Map<String, Object> chatWithContext(String model,
                                        List<Map<String, Object>> messages,
                                        String systemPrompt,
                                        Map<String, Object> context,
                                        Map<String, Object> options);

    /**
     * 批量对话
     *
     * @param requests 请求列表
     * @return 结果列表
     */
    List<Map<String, Object>> batchChat(List<net.ooder.scene.llm.SceneChatRequest> requests);

    /**
     * 检查模型是否支持函数调用
     *
     * @param model 模型名称
     * @return 是否支持
     */
    boolean supportsFunctionCalling(String model);

    /**
     * 检查模型是否支持多模态
     *
     * @param model 模型名称
     * @return 是否支持
     */
    boolean supportsMultimodal(String model);

    /**
     * 获取模型上下文窗口大小
     *
     * @param model 模型名称
     * @return 上下文窗口大小
     */
    int getContextWindowSize(String model);

    /**
     * 计算文本 token 数量
     *
     * @param model 模型名称
     * @param text 文本
     * @return token 数量
     */
    int countTokens(String model, String text);

    /**
     * 带工具调用的对话
     * <p>自动处理工具注册、调用、结果反馈的完整流程</p>
     *
     * @param model 模型名称
     * @param messages 消息列表
     * @param toolNames 要使用的工具名称列表（null表示使用所有可用工具）
     * @param options 可选参数
     * @return 对话结果，包含最终响应
     */
    Map<String, Object> chatWithTools(String model,
                                      List<Map<String, Object>> messages,
                                      List<String> toolNames,
                                      Map<String, Object> options);
}
