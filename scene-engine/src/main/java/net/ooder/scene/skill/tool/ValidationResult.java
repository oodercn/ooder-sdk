package net.ooder.scene.skill.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class ValidationResult {

    private boolean valid;
    private List<String> errors = new ArrayList<>();

    public ValidationResult() {
        this.valid = true;
    }

    public ValidationResult(boolean valid) {
        this.valid = valid;
    }

    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    public static ValidationResult failure(String error) {
        ValidationResult result = new ValidationResult(false);
        result.addError(error);
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
        this.valid = false;
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
}
