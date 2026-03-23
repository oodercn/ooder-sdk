package net.ooder.scene.agent.persistence;

import net.ooder.scene.agent.AgentMessage;

import java.time.Instant;

public class PersistedMessage {

    private String storageId;
    private AgentMessage message;
    private MessageStatus status;
    private Instant createdAt;
    private Instant deliveredAt;
    private Instant acknowledgedAt;
    private int deliveryAttempts;
    private String lastError;
    private long size;

    public PersistedMessage() {
        this.status = MessageStatus.PENDING;
        this.createdAt = Instant.now();
        this.deliveryAttempts = 0;
    }

    public PersistedMessage(AgentMessage message) {
        this();
        this.message = message;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public AgentMessage getMessage() {
        return message;
    }

    public void setMessage(AgentMessage message) {
        this.message = message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(Instant acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public int getDeliveryAttempts() {
        return deliveryAttempts;
    }

    public void setDeliveryAttempts(int deliveryAttempts) {
        this.deliveryAttempts = deliveryAttempts;
    }

    public void incrementDeliveryAttempts() {
        this.deliveryAttempts++;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isPending() {
        return status == MessageStatus.PENDING;
    }

    public boolean isExpired() {
        return message != null && message.isExpired();
    }

    public void markDelivered() {
        this.status = MessageStatus.DELIVERED;
        this.deliveredAt = Instant.now();
    }

    public void markAcknowledged() {
        this.status = MessageStatus.ACKNOWLEDGED;
        this.acknowledgedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.status = MessageStatus.FAILED;
        this.lastError = error;
    }

    @Override
    public String toString() {
        return "PersistedMessage{" +
                "storageId='" + storageId + '\'' +
                ", status=" + status +
                ", deliveryAttempts=" + deliveryAttempts +
                '}';
    }
}
