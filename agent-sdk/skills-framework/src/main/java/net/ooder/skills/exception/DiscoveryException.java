package net.ooder.skills.exception;

public class DiscoveryException extends SDKException {

    private static final long serialVersionUID = 1L;

    public DiscoveryException(String message) {
        super("DISCOVERY_ERROR", message);
    }

    public DiscoveryException(String errorCode, String message) {
        super(errorCode, message);
    }

    public DiscoveryException(String message, Throwable cause) {
        super("DISCOVERY_ERROR", message, cause);
    }

    public DiscoveryException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
