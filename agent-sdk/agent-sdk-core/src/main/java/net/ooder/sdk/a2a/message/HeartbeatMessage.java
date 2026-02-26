package net.ooder.sdk.a2a.message;

/**
 * 心跳消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class HeartbeatMessage extends A2AMessage {

    private long sequence;

    public HeartbeatMessage() {
        super(A2AMessageType.HEARTBEAT);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HeartbeatMessage message = new HeartbeatMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder sequence(long sequence) {
            message.setSequence(sequence);
            return this;
        }

        public HeartbeatMessage build() {
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
