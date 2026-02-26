package net.ooder.sdk.core.scene.capability;

public class RuntimeCapabilityException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityId;
    private String errorCode;
    
    public RuntimeCapabilityException(String message) {
        super(message);
    }
    
    public RuntimeCapabilityException(String capabilityId, String message) {
        super(message);
        this.capabilityId = capabilityId;
    }
    
    public RuntimeCapabilityException(String capabilityId, String errorCode, String message) {
        super(message);
        this.capabilityId = capabilityId;
        this.errorCode = errorCode;
    }
    
    public RuntimeCapabilityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getCapabilityId() { return capabilityId; }
    public String getErrorCode() { return errorCode; }
}
