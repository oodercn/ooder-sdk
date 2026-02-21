package net.ooder.scene.core;

/**
 * 认证异常
 */
public class AuthenticationException extends RuntimeException {
    private String errorCode;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
