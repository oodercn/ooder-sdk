package net.ooder.skills.api;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景模板解析器接口
 *
 * 支持YAML/JSON格式的场景模板解析
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneTemplateParser {
    
    /**
     * 从输入流解析场景模板
     *
     * @param input 输入流
     * @return 场景模板
     */
    CompletableFuture<SceneTemplate> parse(InputStream input);
    
    /**
     * 从YAML内容解析场景模板
     *
     * @param yamlContent YAML内容
     * @return 场景模板
     */
    CompletableFuture<SceneTemplate> parseFromYaml(String yamlContent);
    
    /**
     * 从JSON内容解析场景模板
     *
     * @param jsonContent JSON内容
     * @return 场景模板
     */
    CompletableFuture<SceneTemplate> parseFromJson(String jsonContent);
    
    /**
     * 从文件路径解析场景模板
     *
     * @param filePath 文件路径
     * @return 场景模板
     */
    CompletableFuture<SceneTemplate> parseFromFile(String filePath);
    
    /**
     * 验证场景模板
     *
     * @param template 场景模板
     * @return 验证结果
     */
    ValidationResult validate(SceneTemplate template);
    
    /**
     * 验证模板内容
     *
     * @param content 模板内容
     * @param format 格式（yaml/json）
     * @return 验证结果
     */
    ValidationResult validate(String content, String format);
    
    /**
     * 将场景模板转换为YAML
     *
     * @param template 场景模板
     * @return YAML字符串
     */
    String toYaml(SceneTemplate template);
    
    /**
     * 将场景模板转换为JSON
     *
     * @param template 场景模板
     * @return JSON字符串
     */
    String toJson(SceneTemplate template);
    
    /**
     * 合并模板
     *
     * @param baseTemplate 基础模板
     * @param overrideTemplate 覆盖模板
     * @return 合并后的模板
     */
    SceneTemplate merge(SceneTemplate baseTemplate, SceneTemplate overrideTemplate);
    
    /**
     * 验证结果
     */
    class ValidationResult {
        private boolean valid;
        private String message;
        private java.util.List<ValidationError> errors;
        private java.util.List<ValidationWarning> warnings;
        
        public ValidationResult() {
            this.errors = new java.util.ArrayList<>();
            this.warnings = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public java.util.List<ValidationError> getErrors() { return errors; }
        public void setErrors(java.util.List<ValidationError> errors) { this.errors = errors; }
        public java.util.List<ValidationWarning> getWarnings() { return warnings; }
        public void setWarnings(java.util.List<ValidationWarning> warnings) { this.warnings = warnings; }
        
        public void addError(ValidationError error) {
            if (this.errors == null) {
                this.errors = new java.util.ArrayList<>();
            }
            this.errors.add(error);
            this.valid = false;
        }
        
        public void addWarning(ValidationWarning warning) {
            if (this.warnings == null) {
                this.warnings = new java.util.ArrayList<>();
            }
            this.warnings.add(warning);
        }
        
        public static ValidationResult success() {
            ValidationResult result = new ValidationResult();
            result.setValid(true);
            result.setMessage("验证通过");
            return result;
        }
        
        public static ValidationResult failure(String message) {
            ValidationResult result = new ValidationResult();
            result.setValid(false);
            result.setMessage(message);
            return result;
        }
    }
    
    /**
     * 验证错误
     */
    class ValidationError {
        private String field;
        private String code;
        private String message;
        
        public ValidationError() {}
        
        public ValidationError(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }
        
        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 验证警告
     */
    class ValidationWarning {
        private String field;
        private String code;
        private String message;
        
        public ValidationWarning() {}
        
        public ValidationWarning(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }
        
        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
