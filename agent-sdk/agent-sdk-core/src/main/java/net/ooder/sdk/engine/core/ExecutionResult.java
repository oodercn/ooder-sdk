package net.ooder.sdk.engine.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行结果
 */
public class ExecutionResult {
    
    private String executionId;
    private boolean success;
    private Object data;
    private String errorCode;
    private String errorMessage;
    private Throwable error;
    private long executionTime;
    private Map<String, Object> metadata;
    
    public ExecutionResult() {
        this.metadata = new ConcurrentHashMap<>();
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Throwable getError() {
        return error;
    }
    
    public void setError(Throwable error) {
        this.error = error;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? new ConcurrentHashMap<>(metadata) : new ConcurrentHashMap<>();
    }
    
    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public static ExecutionResult success(Object data) {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }
    
    public static ExecutionResult failure(String errorCode, String errorMessage) {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public static ExecutionResult failure(String errorCode, String errorMessage, Throwable error) {
        ExecutionResult result = failure(errorCode, errorMessage);
        result.setError(error);
        return result;
    }
}
