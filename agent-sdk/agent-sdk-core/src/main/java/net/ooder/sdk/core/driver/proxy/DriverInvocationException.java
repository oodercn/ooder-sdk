package net.ooder.sdk.core.driver.proxy;

public class DriverInvocationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String interfaceId;
    private String methodName;
    
    public DriverInvocationException(String message) {
        super(message);
    }
    
    public DriverInvocationException(String interfaceId, String message) {
        super(message);
        this.interfaceId = interfaceId;
    }
    
    public DriverInvocationException(String interfaceId, String message, Throwable cause) {
        super(message, cause);
        this.interfaceId = interfaceId;
    }
    
    public DriverInvocationException(String interfaceId, String methodName, String message, Throwable cause) {
        super(message, cause);
        this.interfaceId = interfaceId;
        this.methodName = methodName;
    }
    
    public String getInterfaceId() { return interfaceId; }
    public String getMethodName() { return methodName; }
}
