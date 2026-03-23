package net.ooder.scene.agent;

import java.util.List;

public interface AgentMessageBus {

    String send(AgentMessage message);

    List<AgentMessage> receive(String agentId);

    void subscribe(String agentId, MessageHandler handler);

    void unsubscribe(String agentId);

    void acknowledge(String agentId, String messageId);

    int getPendingCount(String agentId);

    void clearMessages(String agentId);
}
