package net.ooder.scene.llm.context;

/**
 * 上下文传递异常
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class ContextTransferException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ContextTransferException(String message) {
        super(message);
    }

    public ContextTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
