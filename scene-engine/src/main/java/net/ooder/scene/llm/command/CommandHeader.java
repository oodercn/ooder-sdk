package net.ooder.scene.llm.command;

import java.io.Serializable;

/**
 * A2A 命令头
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class CommandHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    private String protocolVersion = "2.4";
    private A2ACommandType commandType;
    private String commandId;
    private long timestamp;
    private String traceId;

    public CommandHeader() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
    
    public A2ACommandType getCommandType() { return commandType; }
    public void setCommandType(A2ACommandType commandType) { this.commandType = commandType; }
    
    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public static class Builder {
        private CommandHeader header = new CommandHeader();
        
        public Builder protocolVersion(String version) {
            header.setProtocolVersion(version);
            return this;
        }
        
        public Builder commandType(A2ACommandType type) {
            header.setCommandType(type);
            return this;
        }
        
        public Builder commandId(String commandId) {
            header.setCommandId(commandId);
            return this;
        }
        
        public Builder timestamp(long timestamp) {
            header.setTimestamp(timestamp);
            return this;
        }
        
        public Builder traceId(String traceId) {
            header.setTraceId(traceId);
            return this;
        }
        
        public CommandHeader build() {
            return header;
        }
    }
}
