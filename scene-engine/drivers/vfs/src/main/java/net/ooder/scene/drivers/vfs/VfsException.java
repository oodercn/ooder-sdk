package net.ooder.scene.drivers.vfs;

public class VfsException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    
    public VfsException(String message) {
        super(message);
    }
    
    public VfsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VfsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
