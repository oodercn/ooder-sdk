package net.ooder.scene.skill.exception;

import net.ooder.scene.skill.state.SkillLifecycleState;

/**
 * Skill状态转换异常
 *
 * <p>当尝试进行非法的状态转换时抛出</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillStateTransitionException extends SkillException {

    private final SkillLifecycleState fromState;
    private final SkillLifecycleState toState;

    public SkillStateTransitionException(String skillId, SkillLifecycleState fromState, SkillLifecycleState toState) {
        super(skillId, fromState, "Cannot transition from " + fromState + " to " + toState);
        this.fromState = fromState;
        this.toState = toState;
    }

    public SkillLifecycleState getFromState() {
        return fromState;
    }

    public SkillLifecycleState getToState() {
        return toState;
    }
}
