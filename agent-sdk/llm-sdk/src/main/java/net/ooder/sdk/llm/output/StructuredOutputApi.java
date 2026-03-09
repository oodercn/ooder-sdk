package net.ooder.sdk.llm.output;

import net.ooder.sdk.llm.tool.ToolChatRequest;

import java.util.Map;

/**
 * 结构化输出 API
 */
public interface StructuredOutputApi {

    /**
     * 注册输出 Schema
     * @param schemaId Schema ID
     * @param schema JSON Schema
     */
    void registerSchema(String schemaId, Map<String, Object> schema);

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
