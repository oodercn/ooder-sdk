package net.ooder.skill.hotplug.a2a.message;

/**
 * 消息序列化异常
 */
public class MessageSerializationException extends Exception {

    public MessageSerializationException(String message) {
        super(message);
    }

    public MessageSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
