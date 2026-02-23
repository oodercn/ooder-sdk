package net.ooder.sdk.vfs;

import java.io.Serializable;

public class VfsResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String path;
    private String errorCode;
    private String errorMessage;
    private long timestamp;
    
    public VfsResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static VfsResult success(String path) {
        VfsResult result = new VfsResult();
        result.setSuccess(true);
        result.setPath(path);
        return result;
    }
    
    public static VfsResult failure(String errorCode, String errorMessage) {
        VfsResult result = new VfsResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
