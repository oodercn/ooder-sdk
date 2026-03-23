package net.ooder.scene.agent;

public interface MessageHandler {

    void onMessage(AgentMessage message);

    default boolean canHandle(MessageType type) {
        return true;
    }
}
