package net.ooder.config.scene.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean valid = true;
    private List<String> errors = new ArrayList<String>();
    private List<String> warnings = new ArrayList<String>();
    private String configType;
    private long validatedTime;
    
    public ValidationResult() {
        this.validatedTime = System.currentTimeMillis();
    }
    
    public ValidationResult(String configType) {
        this();
        this.configType = configType;
    }
    
    public boolean isValid() {
        return valid && errors.isEmpty();
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors != null ? errors : new ArrayList<String>();
        if (!this.errors.isEmpty()) {
            this.valid = false;
        }
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<String>();
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    public String getConfigType() {
        return configType;
    }
    
    public void setConfigType(String configType) {
        this.configType = configType;
    }
    
    public long getValidatedTime() {
        return validatedTime;
    }
    
    public void setValidatedTime(long validatedTime) {
        this.validatedTime = validatedTime;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public int getWarningCount() {
        return warnings.size();
    }
    
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
    
    public String getFirstWarning() {
        return warnings.isEmpty() ? null : warnings.get(0);
    }
    
    public void merge(ValidationResult other) {
        if (other == null) {
            return;
        }
        
        if (!other.valid) {
            this.valid = false;
        }
        
        this.errors.addAll(other.errors);
        this.warnings.addAll(other.warnings);
    }
    
    public static ValidationResult success() {
        return new ValidationResult();
    }
    
    public static ValidationResult success(String configType) {
        return new ValidationResult(configType);
    }
    
    public static ValidationResult error(String error) {
        ValidationResult result = new ValidationResult();
        result.addError(error);
        return result;
    }
    
    public static ValidationResult error(String configType, String error) {
        ValidationResult result = new ValidationResult(configType);
        result.addError(error);
        return result;
    }
    
    @Override
    public String toString() {
        return "ValidationResult{" +
            "valid=" + valid +
            ", errors=" + errors.size() +
            ", warnings=" + warnings.size() +
            ", configType='" + configType + '\'' +
            '}';
    }
}
