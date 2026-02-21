package net.ooder.scene.drivers.msg;

public class MsgException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    
    public MsgException(String message) {
        super(message);
    }
    
    public MsgException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MsgException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
