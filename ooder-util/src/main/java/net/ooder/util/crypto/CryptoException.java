package net.ooder.util.crypto;

/**
 * 加密异常
 *
 * @author ooder
 * @since 2.3
 */
public class CryptoException extends RuntimeException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
