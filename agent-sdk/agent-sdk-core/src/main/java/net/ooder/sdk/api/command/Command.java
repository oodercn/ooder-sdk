package net.ooder.sdk.api.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Command {
    
    private final String commandId;
    private final String commandType;
    private final String namespace;
    private final String name;
    private final String targetDevice;
    private final String targetCap;
    private final Map<String, Object> params = new HashMap<>();
    private final Map<String, Object> metadata = new HashMap<>();
    private final long timestamp;
    private int timeout = 30000;
    private int retryCount = 0;
    private int retryDelay = 1000;
    
    private Command(Builder builder) {
        this.commandId = builder.commandId != null ? builder.commandId : generateCommandId();
        this.commandType = builder.commandType;
        this.namespace = builder.namespace;
        this.name = builder.name;
        this.targetDevice = builder.targetDevice;
        this.targetCap = builder.targetCap;
        this.params.putAll(builder.params);
        this.metadata.putAll(builder.metadata);
        this.timestamp = System.currentTimeMillis();
        this.timeout = builder.timeout;
        this.retryCount = builder.retryCount;
        this.retryDelay = builder.retryDelay;
    }
    
    private static String generateCommandId() {
        return "cmd-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    public String getCommandId() { return commandId; }
    public String getCommandType() { return commandType; }
    public String getNamespace() { return namespace; }
    public String getName() { return name; }
    public String getTargetDevice() { return targetDevice; }
    public String getTargetCap() { return targetCap; }
    public Map<String, Object> getParams() { return params; }
    public Map<String, Object> getMetadata() { return metadata; }
    public long getTimestamp() { return timestamp; }
    public int getTimeout() { return timeout; }
    public int getRetryCount() { return retryCount; }
    public int getRetryDelay() { return retryDelay; }
    
    public boolean isStandardCommand() {
        return "standard".equals(commandType);
    }
    
    public boolean isCustomCommand() {
        return "custom".equals(commandType);
    }
    
    public String getFullCommandName() {
        if (isStandardCommand()) {
            return "standard://" + name;
        } else {
            return "custom://" + namespace + "/" + name;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String commandId;
        private String commandType = "standard";
        private String namespace;
        private String name;
        private String targetDevice;
        private String targetCap;
        private Map<String, Object> params = new HashMap<>();
        private Map<String, Object> metadata = new HashMap<>();
        private int timeout = 30000;
        private int retryCount = 0;
        private int retryDelay = 1000;
        
        public Builder commandId(String commandId) {
            this.commandId = commandId;
            return this;
        }
        
        public Builder standard() {
            this.commandType = "standard";
            return this;
        }
        
        public Builder custom(String namespace) {
            this.commandType = "custom";
            this.namespace = namespace;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder target(String device, String cap) {
            this.targetDevice = device;
            this.targetCap = cap;
            return this;
        }
        
        public Builder targetDevice(String device) {
            this.targetDevice = device;
            return this;
        }
        
        public Builder targetCap(String cap) {
            this.targetCap = cap;
            return this;
        }
        
        public Builder param(String key, Object value) {
            this.params.put(key, value);
            return this;
        }
        
        public Builder params(Map<String, Object> params) {
            this.params.putAll(params);
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder retry(int count, int delay) {
            this.retryCount = count;
            this.retryDelay = delay;
            return this;
        }
        
        public Command build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Command name is required");
            }
            if ("custom".equals(commandType) && (namespace == null || namespace.isEmpty())) {
                throw new IllegalArgumentException("Namespace is required for custom commands");
            }
            return new Command(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("Command{id=%s, type=%s, name=%s, target=%s/%s}",
            commandId, commandType, getFullCommandName(), targetDevice, targetCap);
    }
}
