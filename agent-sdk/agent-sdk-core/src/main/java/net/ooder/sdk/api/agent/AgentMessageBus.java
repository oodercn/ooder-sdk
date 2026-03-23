package net.ooder.sdk.api.agent;

import java.util.List;

public interface AgentMessageBus {

    String send(AgentMessage message);

    List<AgentMessage> receive(String agentId);

    void subscribe(String agentId, MessageHandler handler);

    void unsubscribe(String agentId, MessageHandler handler);

    void acknowledge(String agentId, String messageId);

    int getPendingCount(String agentId);

    void broadcast(AgentMessage message, List<String> targetAgentIds);

    AgentMessage getMessage(String messageId);

    void markDelivered(String messageId);

    void markRead(String messageId);

    void purge(String agentId);

    interface MessageHandler {
        void onMessage(AgentMessage message);
        void onError(Exception error);
    }
}
