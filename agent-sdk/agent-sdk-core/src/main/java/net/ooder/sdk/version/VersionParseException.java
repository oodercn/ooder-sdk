package net.ooder.sdk.version;

/**
 * 版本解析异常
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class VersionParseException extends Exception {

    public VersionParseException(String message) {
        super(message);
    }

    public VersionParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
