package net.ooder.scene.message.queue;

/**
 * 消息回执
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MessageReceipt {
    
    private String messageId;
    private DeliveryStatus status;
    private long deliveredAt;
    private String recipientId;
    private String errorMessage;
    
    public MessageReceipt() {
    }
    
    public MessageReceipt(String messageId, DeliveryStatus status) {
        this.messageId = messageId;
        this.status = status;
        this.deliveredAt = System.currentTimeMillis();
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public DeliveryStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
    
    public long getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public boolean isDelivered() {
        return status == DeliveryStatus.DELIVERED || status == DeliveryStatus.ACKNOWLEDGED;
    }
    
    public boolean isFailed() {
        return status == DeliveryStatus.FAILED || status == DeliveryStatus.EXPIRED;
    }
    
    public static MessageReceipt delivered(String messageId, String recipientId) {
        MessageReceipt receipt = new MessageReceipt(messageId, DeliveryStatus.DELIVERED);
        receipt.setRecipientId(recipientId);
        return receipt;
    }
    
    public static MessageReceipt failed(String messageId, String errorMessage) {
        MessageReceipt receipt = new MessageReceipt(messageId, DeliveryStatus.FAILED);
        receipt.setErrorMessage(errorMessage);
        return receipt;
    }
    
    public static MessageReceipt acknowledged(String messageId, String recipientId) {
        MessageReceipt receipt = new MessageReceipt(messageId, DeliveryStatus.ACKNOWLEDGED);
        receipt.setRecipientId(recipientId);
        return receipt;
    }
    
    @Override
    public String toString() {
        return "MessageReceipt{" +
                "messageId='" + messageId + '\'' +
                ", status=" + status +
                ", recipientId='" + recipientId + '\'' +
                '}';
    }
}
