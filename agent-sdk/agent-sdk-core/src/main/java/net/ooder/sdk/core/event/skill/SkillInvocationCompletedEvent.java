package net.ooder.sdk.core.event.skill;

import net.ooder.sdk.core.event.CoreEvent;

/**
 * Skill 调用完成事件（Core 层）
 *
 * <p>当 Skill 调用完成时触发（成功或失败）</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public final class SkillInvocationCompletedEvent extends CoreEvent {

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
     * 是否成功
     */
    private final boolean success;

    /**
     * 调用耗时（毫秒）
     */
    private final long durationMs;

    /**
     * 结果摘要（脱敏）
     */
    private final Object resultSummary;

    /**
     * 错误信息（如果失败）
     */
    private final String errorMessage;

    public SkillInvocationCompletedEvent(String skillId, String capabilityId, boolean success,
                                          long durationMs, Object resultSummary, String errorMessage) {
        super("SceneAgent");
        this.skillId = skillId;
        this.capabilityId = capabilityId;
        this.success = success;
        this.durationMs = durationMs;
        this.resultSummary = resultSummary;
        this.errorMessage = errorMessage;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public Object getResultSummary() {
        return resultSummary;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getDescription() {
        if (success) {
            return String.format("Skill %s capability %s invocation completed in %dms",
                skillId, capabilityId, durationMs);
        } else {
            return String.format("Skill %s capability %s invocation failed in %dms: %s",
                skillId, capabilityId, durationMs, errorMessage);
        }
    }

    @Override
    public EventPriority getPriority() {
        return success ? EventPriority.NORMAL : EventPriority.HIGH;
    }

    @Override
    public String toString() {
        return String.format("SkillInvocationCompletedEvent[skill=%s, capability=%s, success=%s, duration=%dms, time=%s]",
            skillId, capabilityId, success, durationMs, getInstant());
    }
}
