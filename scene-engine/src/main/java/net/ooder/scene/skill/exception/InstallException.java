package net.ooder.scene.skill.exception;

/**
 * 安装异常
 *
 * <p>Skill安装过程中发生的异常</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class InstallException extends SkillException {

    public InstallException(String message) {
        super(message);
    }

    public InstallException(String skillId, String message) {
        super(skillId, message);
    }

    public InstallException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstallException(String skillId, String message, Throwable cause) {
        super(skillId, message, cause);
    }
}
