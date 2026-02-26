package net.ooder.sdk.a2a.message;

/**
 * 确认消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class AckMessage extends A2AMessage {

    private String originalMessageId;
    private boolean success;

    public AckMessage() {
        super(A2AMessageType.ACK);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AckMessage message = new AckMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder originalMessageId(String originalMessageId) {
            message.setOriginalMessageId(originalMessageId);
            return this;
        }

        public Builder success(boolean success) {
            message.setSuccess(success);
            return this;
        }

        public AckMessage build() {
            return message;
        }
    }

    public String getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
