package net.ooder.sdk.a2a.queue;

import net.ooder.sdk.a2a.A2ACommand;

import java.util.List;
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
     * 取消订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unsubscribe(String subscriptionId);

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
}
