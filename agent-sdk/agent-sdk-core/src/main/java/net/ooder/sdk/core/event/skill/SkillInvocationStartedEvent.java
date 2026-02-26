package net.ooder.sdk.core.event.skill;

import net.ooder.sdk.core.event.CoreEvent;

import java.util.Map;

/**
 * Skill 调用开始事件（Core 层）
 *
 * <p>当 Skill 调用开始时触发</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public final class SkillInvocationStartedEvent extends CoreEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Skill ID
     */
    private final String skillId;

    /**
     * 能力 ID
     */
    private final String capabilityId;

    /**
     * 调用参数（脱敏）
     */
    private final Map<String, Object> params;

    public SkillInvocationStartedEvent(String skillId, String capabilityId, Map<String, Object> params) {
        super("SceneAgent");
        this.skillId = skillId;
        this.capabilityId = capabilityId;
        this.params = params;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public String getDescription() {
        return String.format("Skill %s capability %s invocation started", skillId, capabilityId);
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.NORMAL;
    }

    @Override
    public String toString() {
        return String.format("SkillInvocationStartedEvent[skill=%s, capability=%s, time=%s]",
            skillId, capabilityId, getInstant());
    }
}
