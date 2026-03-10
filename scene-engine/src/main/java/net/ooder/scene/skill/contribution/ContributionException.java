package net.ooder.scene.skill.contribution;

/**
 * 知识贡献异常
 *
 * <p>用户知识贡献过程中发生的异常</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ContributionException extends RuntimeException {

    public ContributionException(String message) {
        super(message);
    }

    public ContributionException(String message, Throwable cause) {
        super(message, cause);
    }
}
