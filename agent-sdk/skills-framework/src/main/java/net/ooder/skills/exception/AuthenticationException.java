package net.ooder.skills.exception;

public class AuthenticationException extends DiscoveryException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super("AUTHENTICATION_ERROR", message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super("AUTHENTICATION_ERROR", message, cause);
    }
}
