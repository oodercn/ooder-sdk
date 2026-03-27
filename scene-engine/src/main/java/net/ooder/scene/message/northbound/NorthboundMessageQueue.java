package net.ooder.scene.message.northbound;

import net.ooder.scene.message.queue.MessageEnvelope;
import net.ooder.scene.message.queue.MessageReceipt;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 北向消息队列接口
 *
 * <p>提供对外的消息队列接口，支持 P2A（用户到Agent）和 P2P（用户到用户）通信。</p>
 *
 * <h3>通信模式：</h3>
 * <ul>
 *   <li>P2A - 用户与 Agent 之间的消息</li>
 *   <li>P2P - 用户与用户之间的消息</li>
 *   <li>A2A - Agent 与 Agent 之间的消息（通过 A2AProtocolService）</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface NorthboundMessageQueue {

    String sendToAgent(String userId, String agentId, Object content);

    String sendToAgent(String userId, String agentId, Object content, String conversationId);

    CompletableFuture<MessageReceipt> sendToAgentAsync(String userId, String agentId, Object content);

    String sendToUser(String fromUserId, String toUserId, Object content);

    String sendToUser(String fromUserId, String toUserId, Object content, String conversationId);

    CompletableFuture<MessageReceipt> sendToUserAsync(String fromUserId, String toUserId, Object content);

    List<MessageEnvelope> getMessagesForUser(String userId);

    List<MessageEnvelope> getMessagesForAgent(String agentId);

    List<MessageEnvelope> getConversationMessages(String conversationId, int limit);

    void acknowledgeUserMessage(String userId, String messageId);

    void acknowledgeAgentMessage(String agentId, String messageId);

    void subscribeUser(String userId, NorthboundMessageHandler handler);

    void subscribeAgent(String agentId, NorthboundMessageHandler handler);

    void unsubscribeUser(String userId);

    void unsubscribeAgent(String agentId);

    NorthboundStats getStats();

    class NorthboundStats {
        private int p2aMessages;
        private int p2pMessages;
        private int pendingUserMessages;
        private int pendingAgentMessages;
        
        public int getP2aMessages() { return p2aMessages; }
        public void setP2aMessages(int p2aMessages) { this.p2aMessages = p2aMessages; }
        
        public int getP2pMessages() { return p2pMessages; }
        public void setP2pMessages(int p2pMessages) { this.p2pMessages = p2pMessages; }
        
        public int getPendingUserMessages() { return pendingUserMessages; }
        public void setPendingUserMessages(int pendingUserMessages) { this.pendingUserMessages = pendingUserMessages; }
        
        public int getPendingAgentMessages() { return pendingAgentMessages; }
        public void setPendingAgentMessages(int pendingAgentMessages) { this.pendingAgentMessages = pendingAgentMessages; }
    }
}
