package net.ooder.sdk.reach;

import java.util.HashMap;
import java.util.Map;

public class ReachResult {
    
    private boolean success;
    private String message;
    private Map<String, Object> data;
    private long executionTime;
    private String errorCode;
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public static ReachResult success(Map<String, Object> data) {
        ReachResult result = new ReachResult();
        result.setSuccess(true);
        result.setMessage("Success");
        result.setData(data != null ? data : new HashMap<>());
        return result;
    }
    
    public static ReachResult success() {
        return success(null);
    }
    
    public static ReachResult failure(String message) {
        ReachResult result = new ReachResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setData(new HashMap<>());
        return result;
    }
    
    public static ReachResult unauthorized(String message) {
        ReachResult result = new ReachResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setErrorCode("UNAUTHORIZED");
        result.setData(new HashMap<>());
        return result;
    }
}
