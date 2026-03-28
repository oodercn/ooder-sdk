package net.ooder.scene.message.reliability;

import net.ooder.scene.message.queue.MessageEnvelope;

import java.util.List;

/**
 * 消息可靠性服务接口
 *
 * <p>提供消息投递的可靠性保证</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>消息追踪 - 追踪消息投递状态</li>
 *   <li>投递确认 - 送达和已读确认</li>
 *   <li>重试机制 - 失败消息自动重试</li>
 *   <li>统计信息 - 投递统计数据</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface MessageReliabilityService {
    
    // ========== 消息追踪 ==========
    
    /**
     * 开始追踪消息
     * 
     * @param messageId 消息ID
     * @param envelope 消息信封
     * @return 追踪记录
     */
    MessageTrack startTracking(String messageId, MessageEnvelope envelope);
    
    /**
     * 获取消息追踪状态
     * 
     * @param messageId 消息ID
     * @return 追踪记录
     */
    MessageTrack getTrackStatus(String messageId);
    
    /**
     * 获取待确认消息
     * 
     * @param senderId 发送者ID
     * @return 待确认消息列表
     */
    List<MessageTrack> getUnacknowledgedMessages(String senderId);
    
    /**
     * 获取待重试消息
     * 
     * @return 待重试消息列表
     */
    List<MessageTrack> getRetryQueue();
    
    /**
     * 获取失败消息
     * 
     * @param senderId 发送者ID（可选）
     * @return 失败消息列表
     */
    List<MessageTrack> getFailedMessages(String senderId);
    
    // ========== 投递确认 ==========
    
    /**
     * 确认消息已送达
     * 
     * @param messageId 消息ID
     * @param recipientId 接收者ID
     */
    void confirmDelivered(String messageId, String recipientId);
    
    /**
     * 确认消息已读
     * 
     * @param messageId 消息ID
     * @param recipientId 接收者ID
     */
    void confirmRead(String messageId, String recipientId);
    
    /**
     * 确认消息已确认
     * 
     * @param messageId 消息ID
     * @param recipientId 接收者ID
     */
    void confirmAcknowledged(String messageId, String recipientId);
    
    /**
     * 标记消息投递失败
     * 
     * @param messageId 消息ID
     * @param errorMessage 错误信息
     */
    void markFailed(String messageId, String errorMessage);
    
    /**
     * 记录投递尝试
     * 
     * @param messageId 消息ID
     * @param success 是否成功
     * @param errorMessage 错误信息
     * @return 投递尝试记录
     */
    DeliveryAttempt recordAttempt(String messageId, boolean success, String errorMessage);
    
    // ========== 重试机制 ==========
    
    /**
     * 设置重试策略
     * 
     * @param messageId 消息ID
     * @param policy 重试策略
     */
    void setRetryPolicy(String messageId, RetryPolicy policy);
    
    /**
     * 手动重试消息
     * 
     * @param messageId 消息ID
     * @return 是否成功发起重试
     */
    boolean retryMessage(String messageId);
    
    /**
     * 批量重试消息
     * 
     * @param messageIds 消息ID列表
     * @return 成功重试的消息数量
     */
    int retryMessages(List<String> messageIds);
    
    /**
     * 取消重试
     * 
     * @param messageId 消息ID
     */
    void cancelRetry(String messageId);
    
    // ========== 回调机制 ==========
    
    /**
     * 设置投递回调
     * 
     * @param callback 投递回调
     */
    void setDeliveryCallback(DeliveryCallback callback);
    
    /**
     * 设置重试回调
     * 
     * @param callback 重试回调
     */
    void setRetryCallback(RetryCallback callback);
    
    // ========== 统计信息 ==========
    
    /**
     * 获取可靠性统计
     * 
     * @return 统计信息
     */
    ReliabilityStats getStats();
    
    /**
     * 清理过期追踪记录
     * 
     * @return 清理的记录数
     */
    int cleanupExpiredTracks();
    
    /**
     * 投递回调接口
     */
    interface DeliveryCallback {
        /**
         * 投递完成回调
         * 
         * @param track 追踪记录
         */
        void onDelivered(MessageTrack track);
        
        /**
         * 投递失败回调
         * 
         * @param track 追踪记录
         */
        void onFailed(MessageTrack track);
    }
    
    /**
     * 重试回调接口
     */
    interface RetryCallback {
        /**
         * 重试前回调
         * 
         * @param track 追踪记录
         * @return 是否继续重试
         */
        boolean beforeRetry(MessageTrack track);
        
        /**
         * 重试后回调
         * 
         * @param track 追踪记录
         * @param success 是否成功
         */
        void afterRetry(MessageTrack track, boolean success);
    }
    
    /**
     * 可靠性统计信息
     */
    class ReliabilityStats {
        private long totalMessages;
        private long deliveredMessages;
        private long readMessages;
        private long acknowledgedMessages;
        private long failedMessages;
        private long retriedMessages;
        private long expiredMessages;
        private double deliveryRate;
        private double readRate;
        private double avgDeliveryTime;
        private int pendingCount;
        private int retryQueueSize;
        
        public long getTotalMessages() { return totalMessages; }
        public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }
        
        public long getDeliveredMessages() { return deliveredMessages; }
        public void setDeliveredMessages(long deliveredMessages) { this.deliveredMessages = deliveredMessages; }
        
        public long getReadMessages() { return readMessages; }
        public void setReadMessages(long readMessages) { this.readMessages = readMessages; }
        
        public long getAcknowledgedMessages() { return acknowledgedMessages; }
        public void setAcknowledgedMessages(long acknowledgedMessages) { this.acknowledgedMessages = acknowledgedMessages; }
        
        public long getFailedMessages() { return failedMessages; }
        public void setFailedMessages(long failedMessages) { this.failedMessages = failedMessages; }
        
        public long getRetriedMessages() { return retriedMessages; }
        public void setRetriedMessages(long retriedMessages) { this.retriedMessages = retriedMessages; }
        
        public long getExpiredMessages() { return expiredMessages; }
        public void setExpiredMessages(long expiredMessages) { this.expiredMessages = expiredMessages; }
        
        public double getDeliveryRate() { return deliveryRate; }
        public void setDeliveryRate(double deliveryRate) { this.deliveryRate = deliveryRate; }
        
        public double getReadRate() { return readRate; }
        public void setReadRate(double readRate) { this.readRate = readRate; }
        
        public double getAvgDeliveryTime() { return avgDeliveryTime; }
        public void setAvgDeliveryTime(double avgDeliveryTime) { this.avgDeliveryTime = avgDeliveryTime; }
        
        public int getPendingCount() { return pendingCount; }
        public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
        
        public int getRetryQueueSize() { return retryQueueSize; }
        public void setRetryQueueSize(int retryQueueSize) { this.retryQueueSize = retryQueueSize; }
    }
}
