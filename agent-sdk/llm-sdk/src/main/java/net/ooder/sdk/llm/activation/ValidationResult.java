package net.ooder.sdk.llm.activation;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 验证结果
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
    private Object correctedValue;

    public static ValidationResult valid() {
        return ValidationResult.builder()
                .valid(true)
                .errors(Collections.emptyList())
                .warnings(Collections.emptyList())
                .build();
    }

    public static ValidationResult invalid(List<String> errors) {
        return ValidationResult.builder()
                .valid(false)
                .errors(errors)
                .warnings(Collections.emptyList())
                .build();
    }

    public static ValidationResult invalid(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return ValidationResult.builder()
                .valid(false)
                .errors(errors)
                .warnings(Collections.emptyList())
                .build();
    }

    public static ValidationResult withWarnings(List<String> warnings) {
        return ValidationResult.builder()
                .valid(true)
                .errors(Collections.emptyList())
                .warnings(warnings)
                .build();
    }

    public static ValidationResult corrected(Object correctedValue) {
        return ValidationResult.builder()
                .valid(true)
                .errors(Collections.emptyList())
                .warnings(Collections.emptyList())
                .correctedValue(correctedValue)
                .build();
    }
}
