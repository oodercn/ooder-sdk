package net.ooder.scene.message.reliability;

import net.ooder.scene.message.queue.DeliveryStatus;
import net.ooder.scene.message.queue.MessageEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 消息追踪记录
 *
 * <p>追踪消息的完整投递生命周期</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MessageTrack {
    
    private String trackId;
    private String messageId;
    private MessageEnvelope envelope;
    
    private DeliveryStatus status;
    private int attemptCount;
    private int maxAttempts;
    
    private long createdAt;
    private long lastAttemptAt;
    private long deliveredAt;
    private long readAt;
    private long acknowledgedAt;
    
    private List<DeliveryAttempt> attempts;
    private String errorMessage;
    private RetryPolicy retryPolicy;
    
    public MessageTrack() {
        this.trackId = UUID.randomUUID().toString().replace("-", "");
        this.status = DeliveryStatus.CREATED;
        this.attemptCount = 0;
        this.maxAttempts = 3;
        this.createdAt = System.currentTimeMillis();
        this.attempts = new ArrayList<>();
    }
    
    public MessageTrack(String messageId, MessageEnvelope envelope) {
        this();
        this.messageId = messageId;
        this.envelope = envelope;
    }
    
    public String getTrackId() {
        return trackId;
    }
    
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public MessageEnvelope getEnvelope() {
        return envelope;
    }
    
    public void setEnvelope(MessageEnvelope envelope) {
        this.envelope = envelope;
    }
    
    public DeliveryStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
    
    public int getAttemptCount() {
        return attemptCount;
    }
    
    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
    
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastAttemptAt() {
        return lastAttemptAt;
    }
    
    public void setLastAttemptAt(long lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }
    
    public long getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public long getReadAt() {
        return readAt;
    }
    
    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }
    
    public long getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public void setAcknowledgedAt(long acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }
    
    public List<DeliveryAttempt> getAttempts() {
        return attempts;
    }
    
    public void setAttempts(List<DeliveryAttempt> attempts) {
        this.attempts = attempts != null ? attempts : new ArrayList<>();
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }
    
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        if (retryPolicy != null) {
            this.maxAttempts = retryPolicy.getMaxAttempts();
        }
    }
    
    public void addAttempt(DeliveryAttempt attempt) {
        this.attempts.add(attempt);
        this.attemptCount = attempts.size();
        this.lastAttemptAt = attempt.getAttemptTime();
    }
    
    public DeliveryAttempt recordAttempt(boolean success, String errorMessage) {
        int attemptNumber = attemptCount + 1;
        long attemptTime = System.currentTimeMillis();
        
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setAttemptNumber(attemptNumber);
        attempt.setAttemptTime(attemptTime);
        attempt.setSuccess(success);
        attempt.setErrorMessage(errorMessage);
        attempt.setResponseTime(0);
        
        addAttempt(attempt);
        
        if (success) {
            this.status = DeliveryStatus.DELIVERED;
            this.deliveredAt = attemptTime;
            this.errorMessage = null;
        } else {
            this.errorMessage = errorMessage;
            if (canRetry()) {
                this.status = DeliveryStatus.RETRYING;
            } else {
                this.status = DeliveryStatus.FAILED;
            }
        }
        
        return attempt;
    }
    
    public DeliveryAttempt recordAttempt(boolean success, String errorMessage, long responseTime) {
        DeliveryAttempt attempt = recordAttempt(success, errorMessage);
        attempt.setResponseTime(responseTime);
        return attempt;
    }
    
    public void markSending() {
        this.status = DeliveryStatus.SENDING;
    }
    
    public void markDelivered() {
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = System.currentTimeMillis();
    }
    
    public void markRead() {
        this.status = DeliveryStatus.READ;
        this.readAt = System.currentTimeMillis();
    }
    
    public void markAcknowledged() {
        this.status = DeliveryStatus.ACKNOWLEDGED;
        this.acknowledgedAt = System.currentTimeMillis();
    }
    
    public void markFailed(String errorMessage) {
        this.status = DeliveryStatus.FAILED;
        this.errorMessage = errorMessage;
    }
    
    public void markExpired() {
        this.status = DeliveryStatus.EXPIRED;
    }
    
    public boolean canRetry() {
        return attemptCount < maxAttempts && status != DeliveryStatus.EXPIRED;
    }
    
    public boolean isTerminal() {
        return status.isTerminal();
    }
    
    public boolean isSuccess() {
        return status.isSuccess();
    }
    
    public long getNextRetryDelay() {
        if (retryPolicy == null || !canRetry()) {
            return 0;
        }
        return retryPolicy.calculateDelay(attemptCount);
    }
    
    public long getTotalDuration() {
        if (deliveredAt > 0) {
            return deliveredAt - createdAt;
        }
        return System.currentTimeMillis() - createdAt;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final MessageTrack track = new MessageTrack();
        
        public Builder trackId(String trackId) {
            track.setTrackId(trackId);
            return this;
        }
        
        public Builder messageId(String messageId) {
            track.setMessageId(messageId);
            return this;
        }
        
        public Builder envelope(MessageEnvelope envelope) {
            track.setEnvelope(envelope);
            return this;
        }
        
        public Builder status(DeliveryStatus status) {
            track.setStatus(status);
            return this;
        }
        
        public Builder maxAttempts(int maxAttempts) {
            track.setMaxAttempts(maxAttempts);
            return this;
        }
        
        public Builder retryPolicy(RetryPolicy retryPolicy) {
            track.setRetryPolicy(retryPolicy);
            return this;
        }
        
        public MessageTrack build() {
            return track;
        }
    }
    
    @Override
    public String toString() {
        return "MessageTrack{" +
                "trackId='" + trackId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", status=" + status +
                ", attemptCount=" + attemptCount +
                '}';
    }
}
