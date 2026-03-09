package net.ooder.scene.skill.exception;

import net.ooder.scene.skill.state.SkillLifecycleState;

/**
 * Skill未运行异常
 *
 * <p>当尝试调用一个未处于RUNNING状态的Skill时抛出</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillNotRunningException extends SkillException {

    public SkillNotRunningException(String skillId, SkillLifecycleState currentState) {
        super(skillId, currentState, "Skill is not running. Current state: " + currentState);
    }

    public SkillNotRunningException(String skillId) {
        super(skillId, "Skill is not running");
    }
}
