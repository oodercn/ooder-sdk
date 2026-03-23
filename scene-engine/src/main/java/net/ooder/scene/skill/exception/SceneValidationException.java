package net.ooder.scene.skill.exception;

import net.ooder.scene.skill.state.SkillLifecycleState;

/**
 * 场景验证异常
 *
 * <p>场景技能安装过程中配置验证失败时抛出</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneValidationException extends SkillException {

    private final String validationType;

    public SceneValidationException(String message) {
        super(message);
        this.validationType = null;
    }

    public SceneValidationException(String skillId, String message) {
        super(skillId, message);
        this.validationType = null;
    }

    public SceneValidationException(String skillId, String validationType, String message) {
        super(skillId, message);
        this.validationType = validationType;
    }

    public SceneValidationException(String skillId, SkillLifecycleState currentState, String message) {
        super(skillId, currentState, message);
        this.validationType = null;
    }

    public SceneValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationType = null;
    }

    public SceneValidationException(String skillId, String message, Throwable cause) {
        super(skillId, message, cause);
        this.validationType = null;
    }

    public String getValidationType() {
        return validationType;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (validationType != null) {
            sb.append("[").append(validationType).append("] ");
        }
        sb.append(super.getMessage());
        return sb.toString();
    }
}
