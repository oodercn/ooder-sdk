package net.ooder.sdk.llm.output;

/**
 * 输出验证器
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public interface OutputValidator<T> {

    ValidationResult validate(T output);

    static <T> OutputValidator<T> noOp() {
        return output -> ValidationResult.success();
    }

    static <T> OutputValidator<T> notNull() {
        return output -> {
            if (output == null) {
                return ValidationResult.failure("Output cannot be null");
            }
            return ValidationResult.success();
        };
    }
}
