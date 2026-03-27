package net.ooder.scene.a2a;

/**
 * A2A 消息处理器接口
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface A2AMessageHandler {
    
    void handle(A2AMessage message);
    
    default boolean canHandle(A2AMessage message) {
        return true;
    }
    
    default String getHandlerId() {
        return this.getClass().getSimpleName();
    }
}
