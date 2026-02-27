package net.ooder.scene.core;

/**
 * 授权异常
 */
public class AuthorizationException extends RuntimeException {
    private String errorCode;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
