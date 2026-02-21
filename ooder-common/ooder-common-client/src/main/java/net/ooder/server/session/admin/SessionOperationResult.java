package net.ooder.server.session.admin;

import java.io.Serializable;
import java.util.Date;

public class SessionOperationResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String operation;
    private String sessionId;
    private String message;
    private String errorCode;
    private long timestamp;
    private Object data;
    
    public SessionOperationResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static SessionOperationResult success(String operation, String sessionId) {
        SessionOperationResult result = new SessionOperationResult();
        result.setSuccess(true);
        result.setOperation(operation);
        result.setSessionId(sessionId);
        result.setMessage("Operation completed successfully");
        return result;
    }
    
    public static SessionOperationResult success(String operation, String sessionId, String message) {
        SessionOperationResult result = success(operation, sessionId);
        result.setMessage(message);
        return result;
    }
    
    public static SessionOperationResult success(String operation, String sessionId, Object data) {
        SessionOperationResult result = success(operation, sessionId);
        result.setData(data);
        return result;
    }
    
    public static SessionOperationResult failure(String operation, String sessionId, String message) {
        SessionOperationResult result = new SessionOperationResult();
        result.setSuccess(false);
        result.setOperation(operation);
        result.setSessionId(sessionId);
        result.setMessage(message);
        return result;
    }
    
    public static SessionOperationResult failure(String operation, String sessionId, String errorCode, String message) {
        SessionOperationResult result = failure(operation, sessionId, message);
        result.setErrorCode(errorCode);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Date getTimestampAsDate() {
        return new Date(timestamp);
    }
    
    @Override
    public String toString() {
        return "SessionOperationResult{" +
                "success=" + success +
                ", operation='" + operation + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
