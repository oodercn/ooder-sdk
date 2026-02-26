package net.ooder.sdk.discovery;

/**
 * Skill解析异常
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillParseException extends Exception {

    public SkillParseException(String message) {
        super(message);
    }

    public SkillParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
