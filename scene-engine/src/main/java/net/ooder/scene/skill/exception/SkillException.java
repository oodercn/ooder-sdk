package net.ooder.scene.skill.exception;

import net.ooder.scene.skill.state.SkillLifecycleState;

/**
 * Skill基础异常
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillException extends RuntimeException {

    private final String skillId;
    private final SkillLifecycleState currentState;

    public SkillException(String message) {
        super(message);
        this.skillId = null;
        this.currentState = null;
    }

    public SkillException(String skillId, String message) {
        super(message);
        this.skillId = skillId;
        this.currentState = null;
    }

    public SkillException(String skillId, SkillLifecycleState currentState, String message) {
        super(message);
        this.skillId = skillId;
        this.currentState = currentState;
    }

    public SkillException(String message, Throwable cause) {
        super(message, cause);
        this.skillId = null;
        this.currentState = null;
    }

    public SkillException(String skillId, String message, Throwable cause) {
        super(message, cause);
        this.skillId = skillId;
        this.currentState = null;
    }

    public String getSkillId() {
        return skillId;
    }

    public SkillLifecycleState getCurrentState() {
        return currentState;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (skillId != null) {
            sb.append("[").append(skillId).append("] ");
        }
        if (currentState != null) {
            sb.append("(").append(currentState).append(") ");
        }
        sb.append(super.getMessage());
        return sb.toString();
    }
}
