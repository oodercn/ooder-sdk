package net.ooder.scene.a2a;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A2A 消息
 *
 * <p>Agent 到 Agent 的标准消息格式。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class A2AMessage {
    
    private String messageId;
    private String conversationId;
    private String sceneGroupId;
    
    private String fromAgentId;
    private String toAgentId;
    
    private A2AMessageType messageType;
    private Object payload;
    
    private int priority;
    private long timestamp;
    
    private Map<String, Object> headers = new HashMap<>();
    
    private String protocolVersion = "1.0";
    private String traceId;
    
    public A2AMessage() {
        this.messageId = UUID.randomUUID().toString().replace("-", "");
        this.timestamp = System.currentTimeMillis();
        this.priority = 5;
    }
    
    public A2AMessage(String fromAgentId, String toAgentId, A2AMessageType messageType) {
        this();
        this.fromAgentId = fromAgentId;
        this.toAgentId = toAgentId;
        this.messageType = messageType;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getFromAgentId() {
        return fromAgentId;
    }
    
    public void setFromAgentId(String fromAgentId) {
        this.fromAgentId = fromAgentId;
    }
    
    public String getToAgentId() {
        return toAgentId;
    }
    
    public void setToAgentId(String toAgentId) {
        this.toAgentId = toAgentId;
    }
    
    public A2AMessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(A2AMessageType messageType) {
        this.messageType = messageType;
    }
    
    public Object getPayload() {
        return payload;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Object> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }
    
    public void setHeader(String key, Object value) {
        this.headers.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getHeader(String key) {
        return (T) this.headers.get(key);
    }
    
    public String getProtocolVersion() {
        return protocolVersion;
    }
    
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    
    public boolean isRequest() {
        return messageType == A2AMessageType.TASK_REQUEST ||
               messageType == A2AMessageType.DATA_REQUEST ||
               messageType == A2AMessageType.QUERY;
    }
    
    public boolean isResponse() {
        return messageType == A2AMessageType.TASK_RESPONSE;
    }
    
    public boolean isCollaboration() {
        return messageType == A2AMessageType.COLLABORATION_INVITE ||
               messageType == A2AMessageType.COLLABORATION_ACCEPT ||
               messageType == A2AMessageType.COLLABORATION_REJECT;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final A2AMessage message = new A2AMessage();
        
        public Builder messageId(String messageId) {
            message.setMessageId(messageId);
            return this;
        }
        
        public Builder conversationId(String conversationId) {
            message.setConversationId(conversationId);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            message.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder from(String fromAgentId) {
            message.setFromAgentId(fromAgentId);
            return this;
        }
        
        public Builder to(String toAgentId) {
            message.setToAgentId(toAgentId);
            return this;
        }
        
        public Builder type(A2AMessageType messageType) {
            message.setMessageType(messageType);
            return this;
        }
        
        public Builder payload(Object payload) {
            message.setPayload(payload);
            return this;
        }
        
        public Builder priority(int priority) {
            message.setPriority(priority);
            return this;
        }
        
        public Builder header(String key, Object value) {
            message.setHeader(key, value);
            return this;
        }
        
        public Builder traceId(String traceId) {
            message.setTraceId(traceId);
            return this;
        }
        
        public A2AMessage build() {
            return message;
        }
    }
    
    @Override
    public String toString() {
        return "A2AMessage{" +
                "messageId='" + messageId + '\'' +
                ", fromAgentId='" + fromAgentId + '\'' +
                ", toAgentId='" + toAgentId + '\'' +
                ", messageType=" + messageType +
                ", priority=" + priority +
                '}';
    }
}
