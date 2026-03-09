package net.ooder.scene.llm.command;

import java.io.Serializable;

/**
 * 命令元数据
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class CommandMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    private Priority priority = Priority.MEDIUM;
    private long timeoutMs = 30000;
    private int retryCount = 0;
    private boolean requireAck = false;
    private String callbackUrl;

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public CommandMetadata() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public long getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(long timeoutMs) { this.timeoutMs = timeoutMs; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    
    public boolean isRequireAck() { return requireAck; }
    public void setRequireAck(boolean requireAck) { this.requireAck = requireAck; }
    
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    
    public static class Builder {
        private CommandMetadata metadata = new CommandMetadata();
        
        public Builder priority(Priority priority) {
            metadata.setPriority(priority);
            return this;
        }
        
        public Builder timeoutMs(long timeoutMs) {
            metadata.setTimeoutMs(timeoutMs);
            return this;
        }
        
        public Builder retryCount(int retryCount) {
            metadata.setRetryCount(retryCount);
            return this;
        }
        
        public Builder requireAck(boolean requireAck) {
            metadata.setRequireAck(requireAck);
            return this;
        }
        
        public Builder callbackUrl(String callbackUrl) {
            metadata.setCallbackUrl(callbackUrl);
            return this;
        }
        
        public CommandMetadata build() {
            return metadata;
        }
    }
}
