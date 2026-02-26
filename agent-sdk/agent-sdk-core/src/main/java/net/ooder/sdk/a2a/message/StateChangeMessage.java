package net.ooder.sdk.a2a.message;

/**
 * 状态变更消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class StateChangeMessage extends A2AMessage {

    private String fromState;
    private String toState;
    private String reason;

    public StateChangeMessage() {
        super(A2AMessageType.STATE_CHANGE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private StateChangeMessage message = new StateChangeMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder fromState(String fromState) {
            message.setFromState(fromState);
            return this;
        }

        public Builder toState(String toState) {
            message.setToState(toState);
            return this;
        }

        public Builder reason(String reason) {
            message.setReason(reason);
            return this;
        }

        public StateChangeMessage build() {
            return message;
        }
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
