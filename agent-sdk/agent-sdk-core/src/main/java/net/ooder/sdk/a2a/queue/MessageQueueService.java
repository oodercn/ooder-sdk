package net.ooder.sdk.a2a.queue;

import net.ooder.sdk.a2a.A2ACommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 消息队列服务
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface MessageQueueService {

    /**
     * 发送消息到队列
     *
     * @param queueName 队列名称
     * @param command   命令
     * @return 消息ID
     */
    CompletableFuture<String> sendMessage(String queueName, A2ACommand command);

    /**
     * 发送场景消息
     *
     * @param message 场景消息
     * @return 发送结果
     */
    SendResult sendSceneMessage(SceneMessage message);

    /**
     * 接收消息
     *
     * @param queueName 队列名称
     * @return 命令
     */
    CompletableFuture<A2ACommand> receiveMessage(String queueName);

    /**
     * 订阅队列
     *
     * @param queueName 队列名称
     * @param listener  监听器
     * @return 订阅ID
     */
    CompletableFuture<String> subscribe(String queueName, MessageListener listener);

    /**
     * 订阅场景消息
     *
     * @param subscription 订阅配置
     * @return 订阅ID
     */
    String subscribeSceneMessages(MessageSubscription subscription);

    /**
     * 取消订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unsubscribe(String subscriptionId);

    /**
     * 确认消息
     *
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean acknowledge(String messageId);

    /**
     * 获取队列信息
     *
     * @param queueName 队列名称
     * @return 队列信息
     */
    CompletableFuture<QueueInfo> getQueueInfo(String queueName);

    /**
     * 创建队列
     *
     * @param queueName 队列名称
     * @param config    配置
     * @return 是否成功
     */
    CompletableFuture<Boolean> createQueue(String queueName, QueueConfig config);

    /**
     * 删除队列
     *
     * @param queueName 队列名称
     * @return 是否成功
     */
    CompletableFuture<Boolean> deleteQueue(String queueName);

    /**
     * 消息监听器
     */
    interface MessageListener {
        void onMessage(A2ACommand command);

        void onError(Exception error);
    }

    /**
     * 队列信息
     */
    class QueueInfo {
        private String queueName;
        private int messageCount;
        private int consumerCount;
        private String status;
        private long createdAt;

        // Getters and Setters
        public String getQueueName() { return queueName; }
        public void setQueueName(String queueName) { this.queueName = queueName; }
        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
        public int getConsumerCount() { return consumerCount; }
        public void setConsumerCount(int consumerCount) { this.consumerCount = consumerCount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }

    /**
     * 队列配置
     */
    class QueueConfig {
        private int maxSize;
        private int ttl;
        private boolean durable;
        private boolean autoDelete;

        // Getters and Setters
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
        public int getTtl() { return ttl; }
        public void setTtl(int ttl) { this.ttl = ttl; }
        public boolean isDurable() { return durable; }
        public void setDurable(boolean durable) { this.durable = durable; }
        public boolean isAutoDelete() { return autoDelete; }
        public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
    }

    /**
     * 场景消息
     */
    class SceneMessage {
        private String messageId;
        private String sourceSceneId;
        private String targetSceneId;
        private String messageType;
        private Map<String, Object> payload;
        private MessagePriority priority;
        private long ttl;
        private Map<String, String> headers;

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getSourceSceneId() { return sourceSceneId; }
        public void setSourceSceneId(String sourceSceneId) { this.sourceSceneId = sourceSceneId; }
        public String getTargetSceneId() { return targetSceneId; }
        public void setTargetSceneId(String targetSceneId) { this.targetSceneId = targetSceneId; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        public MessagePriority getPriority() { return priority; }
        public void setPriority(MessagePriority priority) { this.priority = priority; }
        public long getTtl() { return ttl; }
        public void setTtl(long ttl) { this.ttl = ttl; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    }

    /**
     * 消息优先级
     */
    enum MessagePriority {
        LOW, NORMAL, HIGH
    }

    /**
     * 消息订阅
     */
    class MessageSubscription {
        private String sceneId;
        private List<String> messageTypes;
        private MessageHandler handler;
        private SubscriptionOptions options;

        // Getters and Setters
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public List<String> getMessageTypes() { return messageTypes; }
        public void setMessageTypes(List<String> messageTypes) { this.messageTypes = messageTypes; }
        public MessageHandler getHandler() { return handler; }
        public void setHandler(MessageHandler handler) { this.handler = handler; }
        public SubscriptionOptions getOptions() { return options; }
        public void setOptions(SubscriptionOptions options) { this.options = options; }
    }

    /**
     * 订阅选项
     */
    class SubscriptionOptions {
        private boolean autoAck;
        private int prefetchCount;
        private int maxRetries;

        // Getters and Setters
        public boolean isAutoAck() { return autoAck; }
        public void setAutoAck(boolean autoAck) { this.autoAck = autoAck; }
        public int getPrefetchCount() { return prefetchCount; }
        public void setPrefetchCount(int prefetchCount) { this.prefetchCount = prefetchCount; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    }

    /**
     * 消息处理器
     */
    interface MessageHandler {
        void handle(SceneMessage message);
        void onError(SceneMessage message, Exception error);
    }

    /**
     * 发送结果
     */
    class SendResult {
        private boolean success;
        private String messageId;
        private String errorMessage;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
