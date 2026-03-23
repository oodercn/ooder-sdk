package net.ooder.sdk.llm.output;

/**
 * 结构化输出异常
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public class StructuredOutputException extends RuntimeException {

    public StructuredOutputException(String message) {
        super(message);
    }

    public StructuredOutputException(String message, Throwable cause) {
        super(message, cause);
    }
}
