package net.ooder.sdk.a2a.message;

/**
 * 心跳消息（泛型版本）
 *
 * @param <T> 数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class HeartbeatMessage<T> extends A2AMessage<T> {

    private long sequence;

    public HeartbeatMessage() {
        super(A2AMessageType.HEARTBEAT);
    }
    
    public static HeartbeatMessage<Void> createGeneric() {
        return new HeartbeatMessage<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private HeartbeatMessage<T> message = new HeartbeatMessage<>();

        public Builder<T> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<T> sequence(long sequence) {
            message.setSequence(sequence);
            return this;
        }
        
        public Builder<T> data(T data) {
            message.setData(data);
            return this;
        }

        public HeartbeatMessage<T> build() {
            return message;
        }
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
