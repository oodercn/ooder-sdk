package net.ooder.sdk.proxy;

public class DriverInvocationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String interfaceId;
    private final String methodName;
    
    public DriverInvocationException(String message) {
        super(message);
        this.interfaceId = null;
        this.methodName = null;
    }
    
    public DriverInvocationException(String message, Throwable cause) {
        super(message, cause);
        this.interfaceId = null;
        this.methodName = null;
    }
    
    public DriverInvocationException(String interfaceId, String methodName, String message) {
        super(message);
        this.interfaceId = interfaceId;
        this.methodName = methodName;
    }
    
    public DriverInvocationException(String interfaceId, String methodName, String message, Throwable cause) {
        super(message, cause);
        this.interfaceId = interfaceId;
        this.methodName = methodName;
    }
    
    public String getInterfaceId() { return interfaceId; }
    public String getMethodName() { return methodName; }
}
