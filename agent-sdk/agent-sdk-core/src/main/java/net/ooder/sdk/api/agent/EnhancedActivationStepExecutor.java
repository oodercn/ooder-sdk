package net.ooder.sdk.api.agent;

import net.ooder.sdk.api.PublicAPI;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Activation Step Executor Interface
 * 
 * <p>Extends the basic activation step executor with additional capabilities
 * including dependency management, validation, rollback support, and lifecycle hooks.</p>
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface EnhancedActivationStepExecutor {

    String getStepType();

    boolean canExecute(Map<String, Object> stepConfig);

    StepResult execute(Map<String, Object> stepConfig, 
                       Map<String, Object> process, 
                       Map<String, Object> context);

    default List<String> getDependencies() {
        return Collections.emptyList();
    }

    default void beforeExecute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
    }

    default void afterExecute(Map<String, Object> stepConfig, 
                              Map<String, Object> process, 
                              Map<String, Object> context,
                              StepResult result) {
    }

    default ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        return ValidationResult.success();
    }

    default Map<String, Class<?>> getOutputSchema() {
        return Collections.emptyMap();
    }

    default boolean supportsRollback() {
        return false;
    }

    default void rollback(Map<String, Object> stepConfig, 
                          Map<String, Object> process, 
                          Map<String, Object> context) {
    }

    /**
     * Step execution result interface
     */
    interface StepResult {
        boolean isSuccess();
        void setSuccess(boolean success);
        String getMessage();
        void setMessage(String message);
        Map<String, Object> getData();
        void setData(Map<String, Object> data);

        static StepResult success(String message) {
            return new StepResult() {
                private boolean success = true;
                private String msg = message;
                private Map<String, Object> data = Collections.emptyMap();

                @Override
                public boolean isSuccess() { return success; }
                @Override
                public void setSuccess(boolean success) { this.success = success; }
                @Override
                public String getMessage() { return msg; }
                @Override
                public void setMessage(String message) { this.msg = message; }
                @Override
                public Map<String, Object> getData() { return data; }
                @Override
                public void setData(Map<String, Object> data) { this.data = data; }
            };
        }

        static StepResult failure(String message) {
            return new StepResult() {
                private boolean success = false;
                private String msg = message;
                private Map<String, Object> data = Collections.emptyMap();

                @Override
                public boolean isSuccess() { return success; }
                @Override
                public void setSuccess(boolean success) { this.success = success; }
                @Override
                public String getMessage() { return msg; }
                @Override
                public void setMessage(String message) { this.msg = message; }
                @Override
                public Map<String, Object> getData() { return data; }
                @Override
                public void setData(Map<String, Object> data) { this.data = data; }
            };
        }
    }

    /**
     * Validation result interface
     */
    interface ValidationResult {
        boolean isValid();
        void setValid(boolean valid);
        List<String> getErrors();
        void setErrors(List<String> errors);
        List<String> getWarnings();
        void setWarnings(List<String> warnings);

        static ValidationResult success() {
            return new ValidationResult() {
                private boolean valid = true;
                private List<String> errors = Collections.emptyList();
                private List<String> warnings = Collections.emptyList();

                @Override
                public boolean isValid() { return valid; }
                @Override
                public void setValid(boolean valid) { this.valid = valid; }
                @Override
                public List<String> getErrors() { return errors; }
                @Override
                public void setErrors(List<String> errors) { this.errors = errors; }
                @Override
                public List<String> getWarnings() { return warnings; }
                @Override
                public void setWarnings(List<String> warnings) { this.warnings = warnings; }
            };
        }

        static ValidationResult failure(List<String> errors) {
            return new ValidationResult() {
                private boolean valid = false;
                private List<String> errorsList = errors;
                private List<String> warnings = Collections.emptyList();

                @Override
                public boolean isValid() { return valid; }
                @Override
                public void setValid(boolean valid) { this.valid = valid; }
                @Override
                public List<String> getErrors() { return errorsList; }
                @Override
                public void setErrors(List<String> errors) { this.errorsList = errors; }
                @Override
                public List<String> getWarnings() { return warnings; }
                @Override
                public void setWarnings(List<String> warnings) { this.warnings = warnings; }
            };
        }
    }
}
