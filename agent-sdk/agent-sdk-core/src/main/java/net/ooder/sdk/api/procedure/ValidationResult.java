package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Validation Result Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ValidationResult extends Serializable {

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
