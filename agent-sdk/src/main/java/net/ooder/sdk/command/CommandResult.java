package net.ooder.sdk.command;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CommandResult {
    
    private final String commandId;
    private final boolean success;
    private final Object data;
    private final String errorMessage;
    private final String errorCode;
    private final long executionTime;
    private final Map<String, Object> metadata;
    
    private CommandResult(Builder builder) {
        this.commandId = builder.commandId;
        this.success = builder.success;
        this.data = builder.data;
        this.errorMessage = builder.errorMessage;
        this.errorCode = builder.errorCode;
        this.executionTime = builder.executionTime;
        this.metadata = builder.metadata;
    }
    
    public static CommandResult success(String commandId, Object data) {
        return builder()
            .commandId(commandId)
            .success(true)
            .data(data)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult success(String commandId, Object data, long executionTime) {
        return builder()
            .commandId(commandId)
            .success(true)
            .data(data)
            .executionTime(executionTime)
            .build();
    }
    
    public static CommandResult failure(String commandId, String errorCode, String errorMessage) {
        return builder()
            .commandId(commandId)
            .success(false)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult failure(String commandId, String errorCode, String errorMessage, long executionTime) {
        return builder()
            .commandId(commandId)
            .success(false)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .executionTime(executionTime)
            .build();
    }
    
    public String getCommandId() { return commandId; }
    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public String getErrorMessage() { return errorMessage; }
    public String getErrorCode() { return errorCode; }
    public long getExecutionTime() { return executionTime; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String commandId;
        private boolean success;
        private Object data;
        private String errorMessage;
        private String errorCode;
        private long executionTime;
        private Map<String, Object> metadata;
        
        public Builder commandId(String commandId) {
            this.commandId = commandId;
            return this;
        }
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder data(Object data) {
            this.data = data;
            return this;
        }
        
        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder executionTime(long executionTime) {
            this.executionTime = executionTime;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public CommandResult build() {
            return new CommandResult(this);
        }
    }
    
    @Override
    public String toString() {
        if (success) {
            return String.format("CommandResult{id=%s, success=true, data=%s}", commandId, data);
        } else {
            return String.format("CommandResult{id=%s, success=false, error=%s: %s}", 
                commandId, errorCode, errorMessage);
        }
    }
}
