package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.ooder.skill.hotplug.model.PluginState;

/**
 * 状态变更消息
 * 对应Ooder-A2A规范v1.0 state_change类型
 */
public class StateChangeMessage extends A2AMessage {

    /**
     * 原状态
     */
    @JsonProperty("from")
    private PluginState fromState;

    /**
     * 新状态
     */
    @JsonProperty("to")
    private PluginState toState;

    /**
     * 变更原因
     */
    @JsonProperty("reason")
    private String reason;

    public StateChangeMessage() {
        super(MessageType.STATE_CHANGE);
    }

    public PluginState getFromState() {
        return fromState;
    }

    public void setFromState(PluginState fromState) {
        this.fromState = fromState;
    }

    public PluginState getToState() {
        return toState;
    }

    public void setToState(PluginState toState) {
        this.toState = toState;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static StateChangeMessage of(String skillId, PluginState from, PluginState to, String reason) {
        StateChangeMessage message = new StateChangeMessage();
        message.setSkillId(skillId);
        message.setFromState(from);
        message.setToState(to);
        message.setReason(reason);
        return message;
    }
}
