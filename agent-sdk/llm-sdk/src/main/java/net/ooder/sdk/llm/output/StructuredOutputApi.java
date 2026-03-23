package net.ooder.sdk.llm.output;

import net.ooder.sdk.llm.tool.ToolChatRequest;

import java.util.Map;

/**
 * 结构化输出 API
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public interface StructuredOutputApi {

    /**
     * 结构化输出
     *
     * @param request 结构化对话请求（需包含 responseSchema）
     * @param responseType 响应类型
     * @return 结构化响应对象
     */
    <T> T structuredOutput(StructuredChatRequest request, Class<T> responseType);

    /**
     * 带验证的结构化输出
     *
     * @param request 结构化对话请求
     * @param responseSchema 响应Schema定义
     * @param validator 验证器（可选）
     * @return 验证后的结构化响应
     */
    <T> T structuredOutputWithValidation(
            StructuredChatRequest request,
            ResponseSchema responseSchema,
            OutputValidator<T> validator);

    /**
     * 注册输出 Schema
     * @param schemaId Schema ID
     * @param schema JSON Schema
     */
    void registerSchema(String schemaId, Map<String, Object> schema);

    /**
     * 注册 ResponseSchema
     * @param schemaId Schema ID
     * @param schema ResponseSchema 对象
     */
    void registerSchema(String schemaId, ResponseSchema schema);

    /**
     * 注销 Schema
     * @param schemaId Schema ID
     */
    void unregisterSchema(String schemaId);

    /**
     * 获取 Schema
     * @param schemaId Schema ID
     * @return JSON Schema
     */
    Map<String, Object> getSchema(String schemaId);

    /**
     * 检查 Schema 是否存在
     * @param schemaId Schema ID
     * @return 是否存在
     */
    boolean hasSchema(String schemaId);

    /**
     * 结构化对话
     * @param request 对话请求
     * @param schemaId Schema ID
     * @param type 返回类型
     * @return 结构化响应
     */
    <T> StructuredResponse<T> chatStructured(ToolChatRequest request, String schemaId, Class<T> type);

    /**
     * 结构化对话（带重试）
     * @param request 对话请求
     * @param schemaId Schema ID
     * @param type 返回类型
     * @param maxRetries 最大重试次数
     * @return 结构化响应
     */
    <T> StructuredResponse<T> chatStructured(ToolChatRequest request, String schemaId, Class<T> type, int maxRetries);

    /**
     * 验证输出
     * @param output 输出内容
     * @param schemaId Schema ID
     * @return 验证结果
     */
    ValidationResult validateOutput(Object output, String schemaId);

    /**
     * 验证输出
     * @param jsonOutput JSON输出字符串
     * @param schemaId Schema ID
     * @return 验证结果
     */
    ValidationResult validateOutput(String jsonOutput, String schemaId);

    /**
     * 解析并验证
     * @param jsonOutput JSON输出字符串
     * @param schemaId Schema ID
     * @param type 目标类型
     * @return 结构化响应
     */
    <T> StructuredResponse<T> parseAndValidate(String jsonOutput, String schemaId, Class<T> type);
}
