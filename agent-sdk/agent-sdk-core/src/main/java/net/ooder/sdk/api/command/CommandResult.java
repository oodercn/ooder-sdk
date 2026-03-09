package net.ooder.sdk.api.command;

import java.util.Map;
import java.util.HashMap;

public class CommandResult {
    
    private final String commandId;
    private final boolean success;
    private final Object data;
    private final String message;
    private final String errorMessage;
    private final String errorCode;
    private final long executionTime;
    private final long processingTime;
    private final String responderId;
    private final Map<String, Object> metadata;
    
    private CommandResult(Builder builder) {
        this.commandId = builder.commandId;
        this.success = builder.success;
        this.data = builder.data;
        this.message = builder.message;
        this.errorMessage = builder.errorMessage;
        this.errorCode = builder.errorCode;
        this.executionTime = builder.executionTime;
        this.processingTime = builder.processingTime;
        this.responderId = builder.responderId;
        this.metadata = builder.metadata;
    }
    
    public static CommandResult success() {
        return builder()
            .success(true)
            .message("Success")
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult success(Object data) {
        CommandResult result = success();
        return builder()
            .success(true)
            .message("Success")
            .data(data)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult success(String commandId, Object data) {
        return builder()
            .commandId(commandId)
            .success(true)
            .message("Success")
            .data(data)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult success(String commandId, Object data, long executionTime) {
        return builder()
            .commandId(commandId)
            .success(true)
            .message("Success")
            .data(data)
            .executionTime(executionTime)
            .build();
    }
    
    public static CommandResult failure(String message) {
        return builder()
            .success(false)
            .message(message)
            .errorMessage(message)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult failure(String errorCode, String message) {
        return builder()
            .success(false)
            .message(message)
            .errorMessage(message)
            .errorCode(errorCode)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult failure(String commandId, String errorCode, String errorMessage) {
        return builder()
            .commandId(commandId)
            .success(false)
            .message(errorMessage)
            .errorMessage(errorMessage)
            .errorCode(errorCode)
            .executionTime(System.currentTimeMillis())
            .build();
    }
    
    public static CommandResult failure(String commandId, String errorCode, String errorMessage, long executionTime) {
        return builder()
            .commandId(commandId)
            .success(false)
            .message(errorMessage)
            .errorMessage(errorMessage)
            .errorCode(errorCode)
            .executionTime(executionTime)
            .build();
    }
    
    public String getCommandId() { return commandId; }
    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public String getMessage() { return message; }
    public String getErrorMessage() { return errorMessage; }
    public String getErrorCode() { return errorCode; }
    public long getExecutionTime() { return executionTime; }
    public long getProcessingTime() { return processingTime; }
    public String getResponderId() { return responderId; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public void setProcessingTime(long processingTime) { 
    }
    
    public void setResponderId(String responderId) { 
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String commandId;
        private boolean success;
        private Object data;
        private String message;
        private String errorMessage;
        private String errorCode;
        private long executionTime;
        private long processingTime;
        private String responderId;
        private Map<String, Object> metadata = new HashMap<>();
        
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
        
        public Builder message(String message) {
            this.message = message;
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
        
        public Builder processingTime(long processingTime) {
            this.processingTime = processingTime;
            return this;
        }
        
        public Builder responderId(String responderId) {
            this.responderId = responderId;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder addMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public CommandResult build() {
            return new CommandResult(this);
        }
    }
    
    @Override
    public String toString() {
        if (success) {
            return String.format("CommandResult{id=%s, success=true, data=%s, message=%s}", 
                commandId, data, message);
        } else {
            return String.format("CommandResult{id=%s, success=false, error=%s: %s}", 
                commandId, errorCode, errorMessage);
        }
    }
}
