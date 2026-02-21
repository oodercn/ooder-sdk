package net.ooder.config.scene.capability;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CapabilityResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private Object data;
    private String errorCode;
    private String errorMessage;
    
    private long executionTime;
    private boolean fromCache;
    
    private Map<String, Object> metadata;
    
    public CapabilityResult() {
        this.metadata = new HashMap<String, Object>();
    }
    
    public static CapabilityResult success(Object data) {
        CapabilityResult result = new CapabilityResult();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }
    
    public static CapabilityResult success(Object data, long executionTime) {
        CapabilityResult result = success(data);
        result.setExecutionTime(executionTime);
        return result;
    }
    
    public static CapabilityResult failure(String errorCode, String errorMessage) {
        CapabilityResult result = new CapabilityResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public static CapabilityResult failure(String errorCode, String errorMessage, Throwable cause) {
        CapabilityResult result = failure(errorCode, errorMessage);
        if (cause != null) {
            result.addMetadata("exception", cause.getClass().getName());
            result.addMetadata("exceptionMessage", cause.getMessage());
        }
        return result;
    }
    
    public static CapabilityResult fromCache(Object data) {
        CapabilityResult result = success(data);
        result.setFromCache(true);
        return result;
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
    
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> type) {
        if (data != null && type.isInstance(data)) {
            return (T) data;
        }
        return null;
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
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public boolean isFromCache() {
        return fromCache;
    }
    
    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<String, Object>();
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public boolean hasError() {
        return !success;
    }
    
    @Override
    public String toString() {
        return "CapabilityResult{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", executionTime=" + executionTime +
                ", fromCache=" + fromCache +
                '}';
    }
}
