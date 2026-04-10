package net.ooder.skill.common.spi.message;

public class SendMessageResult {
    
    private boolean success;
    private String messageId;
    private String errorMessage;
    
    public SendMessageResult() {}
    
    public SendMessageResult(boolean success, String messageId, String errorMessage) {
        this.success = success;
        this.messageId = messageId;
        this.errorMessage = errorMessage;
    }
    
    public static SendMessageResult success(String messageId) {
        return new SendMessageResult(true, messageId, null);
    }
    
    public static SendMessageResult failure(String errorMessage) {
        return new SendMessageResult(false, null, errorMessage);
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
