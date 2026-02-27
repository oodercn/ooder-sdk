package net.ooder.sdk.a2a.message;

/**
 * 消息序列化异常
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class MessageSerializationException extends Exception {

    public MessageSerializationException(String message) {
        super(message);
    }

    public MessageSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
