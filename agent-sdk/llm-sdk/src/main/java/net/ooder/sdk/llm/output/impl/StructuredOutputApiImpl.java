package net.ooder.sdk.llm.output.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.output.*;
import net.ooder.sdk.llm.tool.ToolChatRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结构化输出 API 实现
 */
@Slf4j
public class StructuredOutputApiImpl implements StructuredOutputApi {

    private final Map<String, Map<String, Object>> schemaRegistry = new ConcurrentHashMap<>();

    @Override
    public void registerSchema(String schemaId, Map<String, Object> schema) {
        if (schemaId == null || schema == null) {
            throw new IllegalArgumentException("SchemaId and schema cannot be null");
        }
        schemaRegistry.put(schemaId, new HashMap<>(schema));
        log.info("Schema registered: {}", schemaId);
    }

    @Override
    public void unregisterSchema(String schemaId) {
        if (schemaId != null) {
            schemaRegistry.remove(schemaId);
            log.info("Schema unregistered: {}", schemaId);
        }
    }

    @Override
    public Map<String, Object> getSchema(String schemaId) {
        Map<String, Object> schema = schemaRegistry.get(schemaId);
        return schema != null ? new HashMap<>(schema) : null;
    }

    @Override
    public boolean hasSchema(String schemaId) {
        return schemaRegistry.containsKey(schemaId);
    }

    @Override
    public <T> StructuredResponse<T> chatStructured(ToolChatRequest request, String schemaId, Class<T> type) {
        return chatStructured(request, schemaId, type, 0);
    }

    @Override
    public <T> StructuredResponse<T> chatStructured(ToolChatRequest request, String schemaId, Class<T> type, int maxRetries) {
        /**
         * FIXME: 伪实现 - 需要集成真实LLM驱动
         *
         * 此方法是给外部 Skills 调用的核心接口，当前返回基于Schema生成的模拟数据。
         *
         * 预期实现：
         * 1. 将 request 和 schema 转换为 LLM 驱动的输入格式
         * 2. 调用 MultiLlmAdapterApi 选择合适的 LLM 提供商
         * 3. 使用结构化输出模式（如OpenAI的JSON mode）请求LLM
         * 4. 解析LLM返回的JSON响应
         * 5. 验证响应是否符合schema
         * 6. 如果验证失败且未超过maxRetries，重试
         *
         * 依赖：
         * - MultiLlmAdapterApi 用于模型选择和协议适配
         * - LlmDriver 需要支持结构化输出模式
         */
        log.warn("[STUB] chatStructured() called but LLM integration not implemented. Session: {}, Schema: {}",
                request.getSessionId(), schemaId);

        // 临时返回基于Schema生成的模拟数据
        String mockJson = generateMockResponse(schemaId);
        log.info("[STUB] Generated mock response based on schema: {}", schemaId);

        return parseAndValidate(mockJson, schemaId, type);
    }

    @Override
    public ValidationResult validateOutput(Object output, String schemaId) {
        if (output == null) {
            return ValidationResult.failure("Output is null");
        }

        Map<String, Object> schema = schemaRegistry.get(schemaId);
        if (schema == null) {
            return ValidationResult.failure("Schema not found: " + schemaId);
        }

        String jsonOutput;
        if (output instanceof String) {
            jsonOutput = (String) output;
        } else {
            jsonOutput = JSON.toJSONString(output);
        }

        return validateOutput(jsonOutput, schemaId);
    }

    @Override
    public ValidationResult validateOutput(String jsonOutput, String schemaId) {
        Map<String, Object> schema = schemaRegistry.get(schemaId);
        if (schema == null) {
            return ValidationResult.failure("Schema not found: " + schemaId);
        }

        ValidationResult result = ValidationResult.success();

        try {
            JSONObject jsonObject = JSON.parseObject(jsonOutput);
            validateObject(jsonObject, schema, "", result);
        } catch (Exception e) {
            result.addError("Invalid JSON format: " + e.getMessage());
        }

        return result;
    }

    @Override
    public <T> StructuredResponse<T> parseAndValidate(String jsonOutput, String schemaId, Class<T> type) {
        ValidationResult validationResult = validateOutput(jsonOutput, schemaId);

        if (!validationResult.isValid()) {
            return StructuredResponse.validationFailure(jsonOutput, validationResult.getErrors());
        }

        try {
            T data = JSON.parseObject(jsonOutput, type);
            return StructuredResponse.success(data, jsonOutput);
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add("Failed to parse JSON to target type: " + e.getMessage());
            return StructuredResponse.validationFailure(jsonOutput, errors);
        }
    }

    /**
     * 验证对象
     */
    @SuppressWarnings("unchecked")
    private void validateObject(JSONObject jsonObject, Map<String, Object> schema, String path, ValidationResult result) {
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
        List<String> required = (List<String>) schema.get("required");

        if (required != null) {
            for (String field : required) {
                if (!jsonObject.containsKey(field)) {
                    result.addError(path + "." + field + " is required");
                }
            }
        }

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String fieldName = entry.getKey();
                Map<String, Object> fieldSchema = (Map<String, Object>) entry.getValue();

                if (jsonObject.containsKey(fieldName)) {
                    Object fieldValue = jsonObject.get(fieldName);
                    String fieldType = (String) fieldSchema.get("type");

                    validateFieldType(fieldValue, fieldType, fieldSchema, path + "." + fieldName, result);
                }
            }
        }
    }

    /**
     * 验证字段类型
     */
    @SuppressWarnings("unchecked")
    private void validateFieldType(Object value, String expectedType, Map<String, Object> fieldSchema,
                                   String path, ValidationResult result) {
        if (expectedType == null) {
            return;
        }

        switch (expectedType) {
            case "string":
                if (!(value instanceof String)) {
                    result.addError(path + " should be a string");
                }
                break;
            case "integer":
                if (!(value instanceof Integer || value instanceof Long)) {
                    result.addError(path + " should be an integer");
                }
                break;
            case "number":
                if (!(value instanceof Number)) {
                    result.addError(path + " should be a number");
                }
                break;
            case "boolean":
                if (!(value instanceof Boolean)) {
                    result.addError(path + " should be a boolean");
                }
                break;
            case "array":
                if (!(value instanceof JSONArray)) {
                    result.addError(path + " should be an array");
                } else {
                    JSONArray array = (JSONArray) value;
                    Map<String, Object> itemSchema = (Map<String, Object>) fieldSchema.get("items");
                    if (itemSchema != null) {
                        for (int i = 0; i < array.size(); i++) {
                            Object item = array.get(i);
                            String itemType = (String) itemSchema.get("type");
                            validateFieldType(item, itemType, itemSchema, path + "[" + i + "]", result);
                        }
                    }
                }
                break;
            case "object":
                if (!(value instanceof JSONObject)) {
                    result.addError(path + " should be an object");
                } else {
                    validateObject((JSONObject) value, fieldSchema, path, result);
                }
                break;
        }
    }

    /**
     * 生成模拟响应
     */
    private String generateMockResponse(String schemaId) {
        Map<String, Object> schema = schemaRegistry.get(schemaId);
        if (schema == null) {
            return "{}";
        }

        JSONObject mockObject = new JSONObject();
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");

        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String fieldName = entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> fieldSchema = (Map<String, Object>) entry.getValue();
                String fieldType = (String) fieldSchema.get("type");

                mockObject.put(fieldName, generateMockValue(fieldType, fieldSchema));
            }
        }

        return mockObject.toJSONString();
    }

    /**
     * 生成模拟值
     */
    @SuppressWarnings("unchecked")
    private Object generateMockValue(String type, Map<String, Object> schema) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case "string":
                return "mock_string";
            case "integer":
                return 0;
            case "number":
                return 0.0;
            case "boolean":
                return true;
            case "array":
                return new JSONArray();
            case "object":
                JSONObject obj = new JSONObject();
                Map<String, Object> props = (Map<String, Object>) schema.get("properties");
                if (props != null) {
                    for (Map.Entry<String, Object> entry : props.entrySet()) {
                        Map<String, Object> propSchema = (Map<String, Object>) entry.getValue();
                        String propType = (String) propSchema.get("type");
                        obj.put(entry.getKey(), generateMockValue(propType, propSchema));
                    }
                }
                return obj;
            default:
                return null;
        }
    }
}
