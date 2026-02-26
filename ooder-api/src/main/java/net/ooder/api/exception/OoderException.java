package net.ooder.api.exception;

/**
 * Ooder 基础异常类
 * 所有 Ooder 异常的根类
 */
public class OoderException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    
    public OoderException() {
        super();
    }
    
    public OoderException(String message) {
        super(message);
    }
    
    public OoderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OoderException(Throwable cause) {
        super(cause);
    }
    
    public OoderException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public OoderException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
