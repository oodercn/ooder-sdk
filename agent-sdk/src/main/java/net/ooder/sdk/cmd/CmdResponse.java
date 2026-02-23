package net.ooder.sdk.cmd;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CmdResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String commandId;
    private boolean success;
    private Object data;
    private String errorCode;
    private String errorMessage;
    private int progress = 100;
    private long timestamp;
    private Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    public CmdResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static CmdResponse success(String commandId, Object data) {
        CmdResponse response = new CmdResponse();
        response.setCommandId(commandId);
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    
    public static CmdResponse failure(String commandId, String errorCode, String errorMessage) {
        CmdResponse response = new CmdResponse();
        response.setCommandId(commandId);
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
    
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>(); 
    }
}
