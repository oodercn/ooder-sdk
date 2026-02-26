package net.ooder.sdk.engine.event.skill;

import net.ooder.sdk.engine.event.EngineEvent;

import java.util.Map;

/**
 * Skill 调用事件（Engine 层）
 *
 * <p>在 Skill 被调用时触发，支持权限检查和审计</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public class SkillInvocationEvent extends EngineEvent {

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
     * 调用参数
     */
    private final Map<String, Object> params;

    /**
     * 调用结果（在调用完成后设置）
     */
    private Object result;

    /**
     * 调用异常（如果发生）
     */
    private Throwable error;

    /**
     * 调用耗时（毫秒）
     */
    private long durationMs;

    public SkillInvocationEvent(CallerInfo callerInfo, String skillId, String capabilityId, Map<String, Object> params) {
        super(callerInfo);
        this.skillId = skillId;
        this.capabilityId = capabilityId;
        this.params = params;
    }

    // ==================== 访问器 ====================

    public String getSkillId() {
        return skillId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    /**
     * 是否调用成功
     */
    public boolean isSuccess() {
        return error == null;
    }

    @Override
    public String getDescription() {
        return String.format("Skill %s capability %s invoked by %s",
            skillId, capabilityId, getCallerInfo().getUsername());
    }

    @Override
    public AuditLevel getAuditLevel() {
        // 如果调用被取消或失败，记录为警告级别
        return isCancelled() || !isSuccess() ? AuditLevel.WARNING : AuditLevel.INFO;
    }

    @Override
    public String toString() {
        return String.format("SkillInvocationEvent[skill=%s, capability=%s, caller=%s, cancelled=%s, time=%s]",
            skillId, capabilityId, getCallerInfo().getUserId(), isCancelled(), getInstant());
    }
}
