package net.ooder.sdk.api.driver;

public class DriverException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private String driverName;
    private String errorCode;
    
    public DriverException(String message) {
        super(message);
    }
    
    public DriverException(String driverName, String message) {
        super(message);
        this.driverName = driverName;
    }
    
    public DriverException(String driverName, String errorCode, String message) {
        super(message);
        this.driverName = driverName;
        this.errorCode = errorCode;
    }
    
    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DriverException(String driverName, String message, Throwable cause) {
        super(message, cause);
        this.driverName = driverName;
    }
    
    public String getDriverName() { return driverName; }
    public String getErrorCode() { return errorCode; }
}
