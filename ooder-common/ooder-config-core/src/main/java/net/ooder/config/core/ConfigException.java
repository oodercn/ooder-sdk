package net.ooder.config.core;

public class ConfigException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public ConfigException(String message) {
        super(message);
    }
    
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
