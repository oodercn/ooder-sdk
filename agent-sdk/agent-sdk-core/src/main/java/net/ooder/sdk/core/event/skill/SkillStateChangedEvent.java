package net.ooder.sdk.core.event.skill;

import net.ooder.sdk.core.event.CoreEvent;
import net.ooder.sdk.lifecycle.LifecycleState;

/**
 * Skill 状态变更事件（Core 层）
 *
 * <p>当 Skill 生命周期状态发生变更时触发</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>无状态</b>：事件对象不可变</li>
 *   <li><b>只观察</b>：监听者不能中断状态变更流程</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 */
public final class SkillStateChangedEvent extends CoreEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Skill ID
     */
    private final String skillId;

    /**
     * 旧状态
     */
    private final LifecycleState oldState;

    /**
     * 新状态
     */
    private final LifecycleState newState;

    /**
     * 状态变更原因
     */
    private final String reason;

    public SkillStateChangedEvent(String skillId, LifecycleState oldState, LifecycleState newState) {
        this(skillId, oldState, newState, null);
    }

    public SkillStateChangedEvent(String skillId, LifecycleState oldState, LifecycleState newState, String reason) {
        super("SkillLifecycleManager");
        this.skillId = skillId;
        this.oldState = oldState;
        this.newState = newState;
        this.reason = reason;
    }

    // ==================== 只读访问器 ====================

    public String getSkillId() {
        return skillId;
    }

    public LifecycleState getOldState() {
        return oldState;
    }

    public LifecycleState getNewState() {
        return newState;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String getDescription() {
        return String.format("Skill %s state changed from %s to %s",
            skillId, oldState, newState);
    }

    @Override
    public EventPriority getPriority() {
        // 状态变更事件为高优先级
        return EventPriority.HIGH;
    }

    @Override
    public String toString() {
        return String.format("SkillStateChangedEvent[skillId=%s, %s -> %s, time=%s]",
            skillId, oldState, newState, getInstant());
    }
}
