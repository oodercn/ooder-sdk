package net.ooder.sdk.a2a.message;

/**
 * 消息序列化异常
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class MessageSerializationException extends Exception {

    public MessageSerializationException(String message) {
        super(message);
    }

    public MessageSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
