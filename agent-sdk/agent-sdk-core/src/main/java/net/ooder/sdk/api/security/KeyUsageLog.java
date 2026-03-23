package net.ooder.sdk.api.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class KeyUsageLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String logId;
    private String keyId;
    private String keyValue;
    
    private String operation;
    private String resource;
    private String action;
    
    private String operatorId;
    private OwnerType operatorType;
    
    private String sceneGroupId;
    private String agentId;
    
    private boolean success;
    private String errorCode;
    private String errorMessage;
    
    private String clientIp;
    private String userAgent;
    
    private long timestamp;
    private long duration;
    
    private Map<String, Object> context;
    
    public KeyUsageLog() {
        this.timestamp = System.currentTimeMillis();
        this.context = new HashMap<String, Object>();
    }
    
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
    
    public OwnerType getOperatorType() { return operatorType; }
    public void setOperatorType(OwnerType operatorType) { this.operatorType = operatorType; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    public static KeyUsageLog success(String keyId, String operation, String resource) {
        KeyUsageLog log = new KeyUsageLog();
        log.setKeyId(keyId);
        log.setOperation(operation);
        log.setResource(resource);
        log.setSuccess(true);
        return log;
    }
    
    public static KeyUsageLog failure(String keyId, String operation, String resource, String errorCode, String errorMessage) {
        KeyUsageLog log = new KeyUsageLog();
        log.setKeyId(keyId);
        log.setOperation(operation);
        log.setResource(resource);
        log.setSuccess(false);
        log.setErrorCode(errorCode);
        log.setErrorMessage(errorMessage);
        return log;
    }
}
