package net.ooder.skills.config.exception;

/**
 * 配置异常基类
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class ConfigException extends RuntimeException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
