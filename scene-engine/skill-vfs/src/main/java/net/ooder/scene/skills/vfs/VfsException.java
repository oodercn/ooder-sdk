package net.ooder.scene.skills.vfs;

/**
 * VfsException VFS异常
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
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
