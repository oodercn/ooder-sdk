package net.ooder.scene.message.offline;

import net.ooder.scene.message.queue.MessageEnvelope;

/**
 * 离线消息实体类
 *
 * <p>表示存储在离线队列中的消息</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class OfflineMessage {
    
    private String offlineMessageId;
    private String recipientId;
    private MessageEnvelope envelope;
    
    private OfflineStatus status;
    private int deliveryAttempts;
    private long createdAt;
    private long expireAt;
    private long deliveredAt;
    private long acknowledgedAt;
    private String lastError;
    
    public OfflineMessage() {
        this.status = OfflineStatus.PENDING;
        this.deliveryAttempts = 0;
        this.createdAt = System.currentTimeMillis();
    }
    
    public OfflineMessage(String recipientId, MessageEnvelope envelope) {
        this();
        this.recipientId = recipientId;
        this.envelope = envelope;
    }
    
    public String getOfflineMessageId() {
        return offlineMessageId;
    }
    
    public void setOfflineMessageId(String offlineMessageId) {
        this.offlineMessageId = offlineMessageId;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public MessageEnvelope getEnvelope() {
        return envelope;
    }
    
    public void setEnvelope(MessageEnvelope envelope) {
        this.envelope = envelope;
    }
    
    public OfflineStatus getStatus() {
        return status;
    }
    
    public void setStatus(OfflineStatus status) {
        this.status = status;
    }
    
    public int getDeliveryAttempts() {
        return deliveryAttempts;
    }
    
    public void setDeliveryAttempts(int deliveryAttempts) {
        this.deliveryAttempts = deliveryAttempts;
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
    
    public long getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public long getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public void setAcknowledgedAt(long acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
    
    public boolean isExpired() {
        return expireAt > 0 && System.currentTimeMillis() > expireAt;
    }
    
    public boolean isPending() {
        return status == OfflineStatus.PENDING;
    }
    
    public boolean isDelivered() {
        return status == OfflineStatus.DELIVERED;
    }
    
    public boolean isAcknowledged() {
        return status == OfflineStatus.ACKNOWLEDGED;
    }
    
    public void incrementDeliveryAttempt() {
        this.deliveryAttempts++;
    }
    
    public void markDelivered() {
        this.status = OfflineStatus.DELIVERED;
        this.deliveredAt = System.currentTimeMillis();
    }
    
    public void markAcknowledged() {
        this.status = OfflineStatus.ACKNOWLEDGED;
        this.acknowledgedAt = System.currentTimeMillis();
    }
    
    public void markFailed(String error) {
        this.status = OfflineStatus.FAILED;
        this.lastError = error;
    }
    
    public void markExpired() {
        this.status = OfflineStatus.EXPIRED;
    }
    
    public String getMessageId() {
        return envelope != null ? envelope.getMessageId() : null;
    }
    
    public String getMessageType() {
        return envelope != null ? envelope.getMessageType() : null;
    }
    
    public String getSceneGroupId() {
        return envelope != null ? envelope.getSceneGroupId() : null;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final OfflineMessage message = new OfflineMessage();
        
        public Builder offlineMessageId(String offlineMessageId) {
            message.setOfflineMessageId(offlineMessageId);
            return this;
        }
        
        public Builder recipientId(String recipientId) {
            message.setRecipientId(recipientId);
            return this;
        }
        
        public Builder envelope(MessageEnvelope envelope) {
            message.setEnvelope(envelope);
            return this;
        }
        
        public Builder status(OfflineStatus status) {
            message.setStatus(status);
            return this;
        }
        
        public Builder deliveryAttempts(int deliveryAttempts) {
            message.setDeliveryAttempts(deliveryAttempts);
            return this;
        }
        
        public Builder createdAt(long createdAt) {
            message.setCreatedAt(createdAt);
            return this;
        }
        
        public Builder expireAt(long expireAt) {
            message.setExpireAt(expireAt);
            return this;
        }
        
        public Builder deliveredAt(long deliveredAt) {
            message.setDeliveredAt(deliveredAt);
            return this;
        }
        
        public Builder acknowledgedAt(long acknowledgedAt) {
            message.setAcknowledgedAt(acknowledgedAt);
            return this;
        }
        
        public Builder lastError(String lastError) {
            message.setLastError(lastError);
            return this;
        }
        
        public OfflineMessage build() {
            return message;
        }
    }
    
    @Override
    public String toString() {
        return "OfflineMessage{" +
                "offlineMessageId='" + offlineMessageId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", status=" + status +
                ", deliveryAttempts=" + deliveryAttempts +
                '}';
    }
}
