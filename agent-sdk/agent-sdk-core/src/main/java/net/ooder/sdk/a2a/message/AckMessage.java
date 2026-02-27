package net.ooder.sdk.a2a.message;

/**
 * 确认消息（泛型版本）
 *
 * @param <T> 数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class AckMessage<T> extends A2AMessage<T> {

    private String originalMessageId;
    private boolean success;

    public AckMessage() {
        super(A2AMessageType.ACK);
    }
    
    public static AckMessage<Void> createGeneric() {
        return new AckMessage<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private AckMessage<T> message = new AckMessage<>();

        public Builder<T> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<T> originalMessageId(String originalMessageId) {
            message.setOriginalMessageId(originalMessageId);
            return this;
        }

        public Builder<T> success(boolean success) {
            message.setSuccess(success);
            return this;
        }
        
        public Builder<T> data(T data) {
            message.setData(data);
            return this;
        }

        public AckMessage<T> build() {
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
