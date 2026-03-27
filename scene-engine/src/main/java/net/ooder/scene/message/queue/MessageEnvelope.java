package net.ooder.scene.message.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息信封
 *
 * <p>标准化消息格式，支持 P2A、A2A、P2P 等多种通信模式。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MessageEnvelope {
    
    private String messageId;
    private String conversationId;
    private String sceneGroupId;
    
    private MessageParticipant from;
    private MessageParticipant to;
    
    private String messageType;
    private String contentType;
    private Object content;
    
    private MessagePriority priority = MessagePriority.NORMAL;
    private DeliveryGuarantee deliveryGuarantee = DeliveryGuarantee.AT_LEAST_ONCE;
    
    private long createdAt;
    private long expireAt;
    
    private Map<String, Object> metadata = new HashMap<>();
    
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    private int retryCount = 0;
    private int maxRetries = 3;
    
    public MessageEnvelope() {
        this.messageId = UUID.randomUUID().toString().replace("-", "");
        this.createdAt = System.currentTimeMillis();
    }
    
    public MessageEnvelope(MessageParticipant from, MessageParticipant to) {
        this();
        this.from = from;
        this.to = to;
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
    
    public MessageParticipant getFrom() {
        return from;
    }
    
    public void setFrom(MessageParticipant from) {
        this.from = from;
    }
    
    public MessageParticipant getTo() {
        return to;
    }
    
    public void setTo(MessageParticipant to) {
        this.to = to;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Object getContent() {
        return content;
    }
    
    public void setContent(Object content) {
        this.content = content;
    }
    
    public MessagePriority getPriority() {
        return priority;
    }
    
    public void setPriority(MessagePriority priority) {
        this.priority = priority;
    }
    
    public DeliveryGuarantee getDeliveryGuarantee() {
        return deliveryGuarantee;
    }
    
    public void setDeliveryGuarantee(DeliveryGuarantee deliveryGuarantee) {
        this.deliveryGuarantee = deliveryGuarantee;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getExpireAt() {
        return expireAt;
    }
    
    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
    
    public void setTtl(long ttlMs) {
        this.expireAt = System.currentTimeMillis() + ttlMs;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key) {
        return (T) this.metadata.get(key);
    }
    
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public boolean isExpired() {
        return expireAt > 0 && System.currentTimeMillis() > expireAt;
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries && !isExpired();
    }
    
    public void incrementRetry() {
        this.retryCount++;
    }
    
    public boolean isP2A() {
        return from != null && from.isUser() && to != null && to.isAgent();
    }
    
    public boolean isA2A() {
        return from != null && from.isAgent() && to != null && to.isAgent();
    }
    
    public boolean isP2P() {
        return from != null && from.isUser() && to != null && to.isUser();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final MessageEnvelope envelope = new MessageEnvelope();
        
        public Builder messageId(String messageId) {
            envelope.setMessageId(messageId);
            return this;
        }
        
        public Builder conversationId(String conversationId) {
            envelope.setConversationId(conversationId);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            envelope.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder from(MessageParticipant from) {
            envelope.setFrom(from);
            return this;
        }
        
        public Builder to(MessageParticipant to) {
            envelope.setTo(to);
            return this;
        }
        
        public Builder fromUser(String userId) {
            envelope.setFrom(MessageParticipant.user(userId));
            return this;
        }
        
        public Builder toAgent(String agentId) {
            envelope.setTo(MessageParticipant.virtualAgent(agentId));
            return this;
        }
        
        public Builder messageType(String messageType) {
            envelope.setMessageType(messageType);
            return this;
        }
        
        public Builder contentType(String contentType) {
            envelope.setContentType(contentType);
            return this;
        }
        
        public Builder content(Object content) {
            envelope.setContent(content);
            return this;
        }
        
        public Builder priority(MessagePriority priority) {
            envelope.setPriority(priority);
            return this;
        }
        
        public Builder deliveryGuarantee(DeliveryGuarantee guarantee) {
            envelope.setDeliveryGuarantee(guarantee);
            return this;
        }
        
        public Builder ttl(long ttlMs) {
            envelope.setTtl(ttlMs);
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            envelope.setMetadata(key, value);
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            envelope.setMaxRetries(maxRetries);
            return this;
        }
        
        public MessageEnvelope build() {
            return envelope;
        }
    }
    
    @Override
    public String toString() {
        return "MessageEnvelope{" +
                "messageId='" + messageId + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", messageType='" + messageType + '\'' +
                ", priority=" + priority +
                ", status=" + deliveryStatus +
                '}';
    }
}
