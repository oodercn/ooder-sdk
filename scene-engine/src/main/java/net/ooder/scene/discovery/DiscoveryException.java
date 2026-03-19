package net.ooder.scene.discovery;

/**
 * 发现异常
 * 
 * <p>发现流程中抛出的异常，用于表示发现过程中的错误。</p>
 * 
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class DiscoveryException extends RuntimeException {
    
    private final String errorCode;
    private final String source;
    
    public DiscoveryException(String message) {
        super(message);
        this.errorCode = "DISCOVERY_ERROR";
        this.source = null;
    }
    
    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DISCOVERY_ERROR";
        this.source = null;
    }
    
    public DiscoveryException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.source = null;
    }
    
    public DiscoveryException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.source = null;
    }
    
    public DiscoveryException(String errorCode, String message, String source) {
        super(message);
        this.errorCode = errorCode;
        this.source = source;
    }
    
    public DiscoveryException(String errorCode, String message, String source, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.source = source;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getSource() {
        return source;
    }
}
