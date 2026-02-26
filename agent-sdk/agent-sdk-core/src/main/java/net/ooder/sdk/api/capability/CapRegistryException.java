package net.ooder.sdk.api.capability;

/**
 * CAP 注册表异常
 *
 * @author Ooder Team
 * @version 2.3
 */
public class CapRegistryException extends Exception {

    private final ErrorCode errorCode;

    public enum ErrorCode {
        ADDRESS_OCCUPIED,
        CAPABILITY_NOT_FOUND,
        INVALID_ADDRESS,
        INVALID_DOMAIN,
        REGISTRY_FULL,
        REGISTRY_LOCKED,
        VERSION_CONFLICT,
        UNKNOWN_ERROR
    }

    public CapRegistryException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public CapRegistryException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CapRegistryException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }

    public CapRegistryException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
