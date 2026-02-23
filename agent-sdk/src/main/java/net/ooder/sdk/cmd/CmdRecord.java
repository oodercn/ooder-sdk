package net.ooder.sdk.cmd;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CmdRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String commandId;
    private String commandType;
    private String sourceId;
    private String targetId;
    private CmdStatus status;
    private Object result;
    private String errorCode;
    private String errorMessage;
    private long startTime;
    private long endTime;
    private long duration;
    private int retryCount;
    private Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    
    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }
    
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public CmdStatus getStatus() { return status; }
    public void setStatus(CmdStatus status) { this.status = status; }
    
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>(); 
    }
    
    public boolean isSuccess() {
        return status == CmdStatus.SUCCESS;
    }
    
    public boolean isFailed() {
        return status == CmdStatus.FAILED || status == CmdStatus.TIMEOUT;
    }
}
