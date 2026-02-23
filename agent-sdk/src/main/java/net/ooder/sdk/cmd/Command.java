package net.ooder.sdk.cmd;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Command implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String commandId;
    private CommandDirection direction;
    private String protocolType;
    private String commandType;
    private String sourceId;
    private String targetId;
    private Map<String, Object> payload = new ConcurrentHashMap<>();
    private int priority = 5;
    private long timeout = 30000;
    private Map<String, Object> headers = new ConcurrentHashMap<>();
    private long timestamp;
    
    public Command() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    
    public CommandDirection getDirection() { return direction; }
    public void setDirection(CommandDirection direction) { this.direction = direction; }
    
    public String getProtocolType() { return protocolType; }
    public void setProtocolType(String protocolType) { this.protocolType = protocolType; }
    
    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }
    
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { 
        this.payload = payload != null ? payload : new ConcurrentHashMap<>(); 
    }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
    
    public Map<String, Object> getHeaders() { return headers; }
    public void setHeaders(Map<String, Object> headers) { 
        this.headers = headers != null ? headers : new ConcurrentHashMap<>(); 
    }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public boolean isNorth() {
        return direction == CommandDirection.NORTH;
    }
    
    public boolean isSouth() {
        return direction == CommandDirection.SOUTH;
    }
}
