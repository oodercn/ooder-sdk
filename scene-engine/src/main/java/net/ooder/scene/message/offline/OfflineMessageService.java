package net.ooder.scene.message.offline;

import net.ooder.scene.message.queue.MessageEnvelope;
import net.ooder.scene.core.PageResult;

import java.util.List;

/**
 * 离线消息服务接口
 *
 * <p>提供离线消息的存储、检索和管理能力</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>消息存储 - 存储离线消息</li>
 *   <li>消息检索 - 分页、按类型检索</li>
 *   <li>消息确认 - 确认消息已接收</li>
 *   <li>消息清理 - 过期消息自动清理</li>
 *   <li>推送通知 - 用户上线时推送</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface OfflineMessageService {
    
    // ========== 消息存储 ==========
    
    /**
     * 存储离线消息
     * 
     * @param recipientId 接收者ID
     * @param message 消息信封
     * @return 离线消息ID
     */
    String storeOfflineMessage(String recipientId, MessageEnvelope message);
    
    /**
     * 批量存储离线消息
     * 
     * @param recipientId 接收者ID
     * @param messages 消息列表
     * @return 离线消息ID列表
     */
    List<String> storeOfflineMessages(String recipientId, List<MessageEnvelope> messages);
    
    // ========== 消息检索 ==========
    
    /**
     * 获取离线消息
     * 
     * @param recipientId 接收者ID
     * @return 离线消息列表
     */
    List<OfflineMessage> getOfflineMessages(String recipientId);
    
    /**
     * 获取离线消息（分页）
     * 
     * @param recipientId 接收者ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 离线消息分页结果
     */
    PageResult<OfflineMessage> getOfflineMessages(String recipientId, int pageNum, int pageSize);
    
    /**
     * 获取离线消息数量
     * 
     * @param recipientId 接收者ID
     * @return 消息数量
     */
    int getOfflineMessageCount(String recipientId);
    
    /**
     * 按类型获取离线消息
     * 
     * @param recipientId 接收者ID
     * @param messageType 消息类型
     * @return 离线消息列表
     */
    List<OfflineMessage> getOfflineMessagesByType(String recipientId, String messageType);
    
    /**
     * 按场景组获取离线消息
     * 
     * @param recipientId 接收者ID
     * @param sceneGroupId 场景组ID
     * @return 离线消息列表
     */
    List<OfflineMessage> getOfflineMessagesBySceneGroup(String recipientId, String sceneGroupId);
    
    /**
     * 获取指定离线消息
     * 
     * @param offlineMessageId 离线消息ID
     * @return 离线消息
     */
    OfflineMessage getOfflineMessage(String offlineMessageId);
    
    // ========== 消息确认 ==========
    
    /**
     * 确认消息已接收
     * 
     * @param recipientId 接收者ID
     * @param messageId 消息ID
     */
    void acknowledgeMessage(String recipientId, String messageId);
    
    /**
     * 批量确认消息已接收
     * 
     * @param recipientId 接收者ID
     * @param messageIds 消息ID列表
     */
    void acknowledgeMessages(String recipientId, List<String> messageIds);
    
    /**
     * 确认所有离线消息
     * 
     * @param recipientId 接收者ID
     */
    void acknowledgeAllMessages(String recipientId);
    
    // ========== 消息清理 ==========
    
    /**
     * 删除过期消息
     * 
     * @param recipientId 接收者ID（可选，null表示所有）
     * @return 删除的消息数量
     */
    int deleteExpiredMessages(String recipientId);
    
    /**
     * 清理指定时间之前的消息
     * 
     * @param beforeTimestamp 时间戳
     * @return 删除的消息数量
     */
    int cleanupMessagesBefore(long beforeTimestamp);
    
    /**
     * 删除指定离线消息
     * 
     * @param offlineMessageId 离线消息ID
     */
    void deleteOfflineMessage(String offlineMessageId);
    
    // ========== 推送通知 ==========
    
    /**
     * 设置用户上线回调
     * 
     * @param callback 上线回调
     */
    void setOnlineCallback(OnlineCallback callback);
    
    /**
     * 用户上线时推送离线消息
     * 
     * @param userId 用户ID
     */
    void pushOfflineMessagesOnOnline(String userId);
    
    /**
     * 用户上线时推送离线消息（带场景组过滤）
     * 
     * @param userId 用户ID
     * @param sceneGroupId 场景组ID
     */
    void pushOfflineMessagesOnOnline(String userId, String sceneGroupId);
    
    // ========== 统计信息 ==========
    
    /**
     * 获取服务统计信息
     * 
     * @return 统计信息
     */
    OfflineMessageStats getStats();
    
    /**
     * 上线回调接口
     */
    interface OnlineCallback {
        /**
         * 用户上线回调
         * 
         * @param userId 用户ID
         * @param offlineMessages 离线消息
         */
        void onOnline(String userId, List<OfflineMessage> offlineMessages);
    }
    
    /**
     * 离线消息统计信息
     */
    class OfflineMessageStats {
        private int totalMessages;
        private int pendingMessages;
        private int deliveredMessages;
        private int acknowledgedMessages;
        private int expiredMessages;
        private int totalRecipients;
        
        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        
        public int getPendingMessages() { return pendingMessages; }
        public void setPendingMessages(int pendingMessages) { this.pendingMessages = pendingMessages; }
        
        public int getDeliveredMessages() { return deliveredMessages; }
        public void setDeliveredMessages(int deliveredMessages) { this.deliveredMessages = deliveredMessages; }
        
        public int getAcknowledgedMessages() { return acknowledgedMessages; }
        public void setAcknowledgedMessages(int acknowledgedMessages) { this.acknowledgedMessages = acknowledgedMessages; }
        
        public int getExpiredMessages() { return expiredMessages; }
        public void setExpiredMessages(int expiredMessages) { this.expiredMessages = expiredMessages; }
        
        public int getTotalRecipients() { return totalRecipients; }
        public void setTotalRecipients(int totalRecipients) { this.totalRecipients = totalRecipients; }
    }
}
