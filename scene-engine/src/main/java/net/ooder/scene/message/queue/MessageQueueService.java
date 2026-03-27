package net.ooder.scene.message.queue;

import java.util.List;

/**
 * 消息队列服务接口
 *
 * <p>提供统一的消息队列能力，支持 P2A、A2A、P2P 通信模式。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>消息发送 - 支持同步/异步发送</li>
 *   <li>离线消息 - 支持离线消息存储和获取</li>
 *   <li>消息确认 - 支持消息送达确认</li>
 *   <li>消息重试 - 支持失败消息重试</li>
 *   <li>消息查询 - 支持对话历史查询</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface MessageQueueService {

    String sendMessage(MessageEnvelope message);

    MessageReceipt sendMessageSync(MessageEnvelope message, long timeoutMs);

    String sendPriorityMessage(MessageEnvelope message, MessagePriority priority);

    List<MessageEnvelope> getOfflineMessages(String recipientId);

    void acknowledgeMessage(String messageId, String recipientId);

    List<MessageEnvelope> getUnacknowledgedMessages(String senderId);

    void retryMessage(String messageId);

    void setRetryPolicy(String messageId, int maxRetries, long retryIntervalMs);

    List<MessageEnvelope> getConversationHistory(String conversationId, long since, int limit);

    List<MessageEnvelope> getConversationHistory(String conversationId, int limit);

    void subscribe(String recipientId, MessageHandler handler);

    void unsubscribe(String recipientId);

    int getPendingCount(String recipientId);

    int getTotalMessageCount();

    void clearMessages(String recipientId);

    void cleanupExpired();

    MessageQueueStats getStats();

    class MessageQueueStats {
        private int totalMessages;
        private int pendingMessages;
        private int deliveredMessages;
        private int acknowledgedMessages;
        private int failedMessages;
        private int expiredMessages;
        
        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        
        public int getPendingMessages() { return pendingMessages; }
        public void setPendingMessages(int pendingMessages) { this.pendingMessages = pendingMessages; }
        
        public int getDeliveredMessages() { return deliveredMessages; }
        public void setDeliveredMessages(int deliveredMessages) { this.deliveredMessages = deliveredMessages; }
        
        public int getAcknowledgedMessages() { return acknowledgedMessages; }
        public void setAcknowledgedMessages(int acknowledgedMessages) { this.acknowledgedMessages = acknowledgedMessages; }
        
        public int getFailedMessages() { return failedMessages; }
        public void setFailedMessages(int failedMessages) { this.failedMessages = failedMessages; }
        
        public int getExpiredMessages() { return expiredMessages; }
        public void setExpiredMessages(int expiredMessages) { this.expiredMessages = expiredMessages; }
    }
}
