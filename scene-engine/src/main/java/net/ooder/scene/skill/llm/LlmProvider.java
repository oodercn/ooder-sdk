package net.ooder.scene.skill.llm;

import java.util.List;
import java.util.Map;

/**
 * LLM Provider 接口
 * 定义大语言模型服务的标准接口
 *
 * <p>实现类需要提供真实的 LLM API 调用能力，支持多种模型和流式输出</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface LlmProvider {
    
    /**
     * 获取 Provider 类型
     * @return Provider 类型标识，如 "openai", "qianwen", "deepseek"
     */
    String getProviderType();
    
    /**
     * 获取支持的模型列表
     * @return 支持的模型名称列表
     */
    List<String> getSupportedModels();
    
    /**
     * 对话补全
     * @param model 模型名称
     * @param messages 消息列表，每个消息包含 role 和 content
     * @param options 可选参数，如 temperature, max_tokens 等
     * @return 对话结果，包含 choices, usage 等信息
     */
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, 
                             Map<String, Object> options);
    
    /**
     * 文本补全
     * @param model 模型名称
     * @param prompt 提示文本
     * @param options 可选参数
     * @return 补全结果文本
     */
    String complete(String model, String prompt, Map<String, Object> options);
    
    /**
     * 文本嵌入
     * @param model 嵌入模型名称
     * @param texts 待嵌入的文本列表
     * @return 嵌入向量列表
     */
    List<double[]> embed(String model, List<String> texts);
    
    /**
     * 翻译文本
     * @param model 模型名称
     * @param text 待翻译文本
     * @param targetLanguage 目标语言
     * @param sourceLanguage 源语言（可选，auto 表示自动检测）
     * @return 翻译后的文本
     */
    String translate(String model, String text, String targetLanguage, String sourceLanguage);
    
    /**
     * 文本摘要
     * @param model 模型名称
     * @param text 待摘要文本
     * @param maxLength 最大长度
     * @return 摘要文本
     */
    String summarize(String model, String text, int maxLength);
    
    /**
     * 是否支持流式输出
     * @return true 如果支持流式输出
     */
    boolean supportsStreaming();
    
    /**
     * 是否支持函数调用
     * @return true 如果支持函数调用
     */
    boolean supportsFunctionCalling();
    
    /**
     * 流式对话
     * @param model 模型名称
     * @param messages 消息列表
     * @param options 可选参数
     * @param handler 流式处理回调
     */
    void chatStream(String model, List<Map<String, Object>> messages,
                    Map<String, Object> options, StreamHandler handler);
}
