package net.ooder.scene.core.output;

import java.util.List;
import java.util.Map;

/**
 * 结构化输出支持接口
 *
 * <p>提供LLM结构化输出能力，支持Schema约束和验证。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface StructuredOutputSupport {

    /**
     * 注册输出Schema
     *
     * @param schemaId Schema ID
     * @param schema Schema定义
     * @return 注册结果
     */
    SchemaRegistration registerSchema(String schemaId, OutputSchema schema);

    /**
     * 注销Schema
     *
     * @param schemaId Schema ID
     */
    void unregisterSchema(String schemaId);

    /**
     * 获取Schema
     *
     * @param schemaId Schema ID
     * @return Schema定义
     */
    OutputSchema getSchema(String schemaId);

    /**
     * 结构化对话
     *
     * @param request 对话请求
     * @return 结构化响应
     */
    <T> StructuredResponse<T> chatStructured(StructuredRequest<T> request);

    /**
     * 验证输出
     *
     * @param output 输出内容
     * @param schemaId Schema ID
     * @return 验证结果
     */
    ValidationResult validate(Object output, String schemaId);

    /**
     * 批量验证
     *
     * @param outputs 输出列表
     * @param schemaId Schema ID
     * @return 验证结果列表
     */
    List<ValidationResult> validateBatch(List<Object> outputs, String schemaId);

    /**
     * 转换输出类型
     *
     * @param output 原始输出
     * @param targetClass 目标类型
     * @param schemaId Schema ID
     * @return 转换后的输出
     */
    <T> T transform(Object output, Class<T> targetClass, String schemaId);

    /**
     * 输出Schema定义
     */
    class OutputSchema {
        private String schemaId;
        private String name;
        private String description;
        private SchemaType type;
        private Map<String, Object> definition;
        private List<FieldSchema> fields;

        public String getSchemaId() { return schemaId; }
        public void setSchemaId(String schemaId) { this.schemaId = schemaId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public SchemaType getType() { return type; }
        public void setType(SchemaType type) { this.type = type; }
        public Map<String, Object> getDefinition() { return definition; }
        public void setDefinition(Map<String, Object> definition) { this.definition = definition; }
        public List<FieldSchema> getFields() { return fields; }
        public void setFields(List<FieldSchema> fields) { this.fields = fields; }
    }

    /**
     * 字段Schema
     */
    class FieldSchema {
        private String name;
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;
        private List<String> enumValues;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
        public List<String> getEnumValues() { return enumValues; }
        public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
    }

    /**
     * Schema类型
     */
    enum SchemaType {
        OBJECT, ARRAY, STRING, NUMBER, INTEGER, BOOLEAN
    }

    /**
     * Schema注册结果
     */
    class SchemaRegistration {
        private String schemaId;
        private boolean success;
        private String errorMessage;

        public String getSchemaId() { return schemaId; }
        public void setSchemaId(String schemaId) { this.schemaId = schemaId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 结构化请求
     */
    class StructuredRequest<T> {
        private String schemaId;
        private String prompt;
        private Class<T> targetType;
        private Map<String, Object> context;

        public String getSchemaId() { return schemaId; }
        public void setSchemaId(String schemaId) { this.schemaId = schemaId; }
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        public Class<T> getTargetType() { return targetType; }
        public void setTargetType(Class<T> targetType) { this.targetType = targetType; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }

    /**
     * 结构化响应
     */
    class StructuredResponse<T> {
        private T data;
        private boolean valid;
        private String rawResponse;
        private List<String> validationErrors;
        private long latency;

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getRawResponse() { return rawResponse; }
        public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
        public List<String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }
        public long getLatency() { return latency; }
        public void setLatency(long latency) { this.latency = latency; }
    }

    /**
     * 验证结果
     */
    class ValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }

        public static ValidationResult success() {
            ValidationResult result = new ValidationResult();
            result.setValid(true);
            return result;
        }

        public static ValidationResult failure(List<String> errors) {
            ValidationResult result = new ValidationResult();
            result.setValid(false);
            result.setErrors(errors);
            return result;
        }
    }
}
