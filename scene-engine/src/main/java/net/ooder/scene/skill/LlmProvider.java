package net.ooder.scene.skill;

import java.util.List;
import java.util.Map;

/**
 * LLM Provider 接口
 *
 * <p>定义语言模型能力接口，由 Skills Team 实现</p>
 * <p>实现类通过 ServiceLoader 注册</p>
 */
public interface LlmProvider {

    /**
     * 获取提供者类型
     * @return 如 "openai", "ollama", "local", "mock"
     */
    String getProviderType();

    /**
     * 获取支持的模型列表
     */
    List<String> getSupportedModels();

    /**
     * 对话补全
     * @param model 模型名称
     * @param messages 消息列表 [{"role": "user", "content": "..."}]
     * @param options 可选参数 (temperature, max_tokens 等)
     * @return 响应结果 {"content": "...", "role": "assistant", "tokens": {...}}
     */
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);

    /**
     * 文本补全
     * @param model 模型名称
     * @param prompt 提示文本
     * @param options 可选参数
     * @return 补全文本
     */
    String complete(String model, String prompt, Map<String, Object> options);

    /**
     * 文本嵌入
     * @param model 模型名称
     * @param texts 文本列表
     * @return 嵌入向量列表
     */
    List<double[]> embed(String model, List<String> texts);

    /**
     * 文本翻译
     * @param model 模型名称
     * @param text 源文本
     * @param targetLanguage 目标语言
     * @param sourceLanguage 源语言（可选）
     * @return 翻译结果
     */
    String translate(String model, String text, String targetLanguage, String sourceLanguage);

    /**
     * 文本摘要
     * @param model 模型名称
     * @param text 源文本
     * @param maxLength 最大长度
     * @return 摘要文本
     */
    String summarize(String model, String text, int maxLength);

    /**
     * 检查是否支持流式响应
     */
    default boolean supportsStreaming() {
        return false;
    }

    /**
     * 检查是否支持函数调用
     */
    default boolean supportsFunctionCalling() {
        return false;
    }
}
