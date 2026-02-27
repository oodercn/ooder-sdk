package net.ooder.sdk.a2a.message;

/**
 * 状态变更消息（泛型版本）
 *
 * @param <T> 数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class StateChangeMessage<T> extends A2AMessage<T> {

    private String fromState;
    private String toState;
    private String reason;

    public StateChangeMessage() {
        super(A2AMessageType.STATE_CHANGE);
    }
    
    public static StateChangeMessage<Void> createGeneric() {
        return new StateChangeMessage<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private StateChangeMessage<T> message = new StateChangeMessage<>();

        public Builder<T> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<T> fromState(String fromState) {
            message.setFromState(fromState);
            return this;
        }

        public Builder<T> toState(String toState) {
            message.setToState(toState);
            return this;
        }

        public Builder<T> reason(String reason) {
            message.setReason(reason);
            return this;
        }
        
        public Builder<T> data(T data) {
            message.setData(data);
            return this;
        }

        public StateChangeMessage<T> build() {
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
