package net.ooder.scene.message.queue;

/**
 * 消息处理器接口
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface MessageHandler {
    
    void onMessage(MessageEnvelope message);
    
    default boolean canHandle(MessageEnvelope message) {
        return true;
    }
    
    default String getHandlerId() {
        return this.getClass().getSimpleName();
    }
}
