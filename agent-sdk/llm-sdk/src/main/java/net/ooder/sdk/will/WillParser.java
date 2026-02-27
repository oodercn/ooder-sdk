package net.ooder.sdk.will;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 意志解析器接口
 */
public interface WillParser {
    
    /**
     * 解析自然语言为意志表达式
     * @param naturalLanguage 自然语言文本
     * @return 解析结果
     */
    WillParseResult parse(String naturalLanguage);
    
    /**
     * 异步解析
     * @param naturalLanguage 自然语言文本
     * @return 异步解析结果
     */
    CompletableFuture<WillParseResult> parseAsync(String naturalLanguage);
    
    /**
     * 批量解析
     * @param naturalLanguages 自然语言文本列表
     * @return 意志表达式列表
     */
    List<WillExpression> parseBatch(List<String> naturalLanguages);
    
    /**
     * 根据反馈优化解析
     * @param will 原始意志
     * @param feedback 反馈
     * @return 优化后的解析结果
     */
    WillParseResult refine(WillExpression will, String feedback);
    
    /**
     * 验证意志表达式
     * @param will 意志表达式
     * @return 验证结果
     */
    ValidationResult validate(WillExpression will);
    
    /**
     * 解析结果
     */
    class WillParseResult {
        private boolean success;
        private String errorMessage;
        private WillExpression will;
        private double confidence;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public WillExpression getWill() { return will; }
        public void setWill(WillExpression will) { this.will = will; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public static WillParseResult success(WillExpression will, double confidence) {
            WillParseResult result = new WillParseResult();
            result.setSuccess(true);
            result.setWill(will);
            result.setConfidence(confidence);
            return result;
        }
        
        public static WillParseResult failure(String errorMessage) {
            WillParseResult result = new WillParseResult();
            result.setSuccess(false);
            result.setErrorMessage(errorMessage);
            result.setConfidence(0.0);
            return result;
        }
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
    }
}
