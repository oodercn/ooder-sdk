package net.ooder.scene.skill.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数验证结果
 *
 * @author ooder
 * @since 2.3
 */
public class ValidationResult {
    
    private boolean valid;
    private List<String> errors = new ArrayList<>();
    
    public ValidationResult() {
    }
    
    public static ValidationResult success() {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        return result;
    }
    
    public static ValidationResult failure(String error) {
        ValidationResult result = new ValidationResult();
        result.setValid(false);
        result.addError(error);
        return result;
    }
    
    public static ValidationResult failure(List<String> errors) {
        ValidationResult result = new ValidationResult();
        result.setValid(false);
        result.setErrors(errors);
        return result;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
}
