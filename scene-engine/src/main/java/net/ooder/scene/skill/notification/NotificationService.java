package net.ooder.scene.skill.notification;

import net.ooder.scene.core.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 推送通知服务接口
 *
 * <p>提供多渠道推送通知能力，支持：</p>
 * <ul>
 *   <li>邮件推送</li>
 *   <li>企业微信推送</li>
 *   <li>钉钉推送</li>
 *   <li>短信推送</li>
 *   <li>站内信推送</li>
 *   <li>WebSocket实时推送</li>
 * </ul>
 *
 * @author ooder
 * @since 2.4
 */
public interface NotificationService {

    /**
     * 推送通知给指定用户
     *
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param channel 推送渠道
     */
    void push(String userId, String title, String content, PushChannel channel);

    /**
     * 推送通知给场景参与者
     *
     * @param activationId 激活ID
     * @param message 通知消息
     */
    void pushToParticipants(String activationId, NotificationMessage message);
    
    // ========== 扩展接口 ==========
    
    /**
     * 发送通知
     *
     * @param request 通知请求
     * @return 通知ID
     */
    default String sendNotification(NotificationRequest request) {
        push(request.getRecipientId(), request.getTitle(), request.getContent(), 
             request.getChannel() != null ? request.getChannel() : PushChannel.IN_APP);
        return null;
    }
    
    /**
     * 批量发送通知
     *
     * @param requests 通知请求列表
     * @return 通知ID列表
     */
    default List<String> sendNotifications(List<NotificationRequest> requests) {
        return java.util.Collections.emptyList();
    }
    
    /**
     * 发送模板通知
     *
     * @param templateId 模板ID
     * @param params 模板参数
     * @param recipientIds 接收者ID列表
     * @return 通知ID
     */
    default String sendTemplateNotification(String templateId, Map<String, Object> params, List<String> recipientIds) {
        return null;
    }
    
    /**
     * 获取用户通知列表
     *
     * @param userId 用户ID
     * @param query 查询条件
     * @return 分页结果
     */
    default PageResult<NotificationRecord> listUserNotifications(String userId, NotificationQuery query) {
        return new PageResult<>(java.util.Collections.emptyList(), 0, 1, 20);
    }
    
    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    default int getUnreadCount(String userId) {
        return 0;
    }
    
    /**
     * 标记为已读
     *
     * @param notificationId 通知ID
     * @return 是否成功
     */
    default boolean markAsRead(String notificationId) {
        return false;
    }
    
    /**
     * 标记全部已读
     *
     * @param userId 用户ID
     * @return 成功数量
     */
    default int markAllAsRead(String userId) {
        return 0;
    }
    
    /**
     * 订阅实时通知
     *
     * @param userId 用户ID
     * @param listener 监听器
     */
    default void subscribe(String userId, NotificationListener listener) {
    }
    
    /**
     * 取消订阅
     *
     * @param userId 用户ID
     */
    default void unsubscribe(String userId) {
    }
    
    /**
     * 创建通知模板
     *
     * @param template 模板
     * @return 创建的模板
     */
    default NotificationTemplate createTemplate(NotificationTemplate template) {
        return null;
    }
    
    /**
     * 获取通知模板
     *
     * @param templateId 模板ID
     * @return 模板
     */
    default NotificationTemplate getTemplate(String templateId) {
        return null;
    }
    
    /**
     * 删除通知模板
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    default boolean deleteTemplate(String templateId) {
        return false;
    }
    
    /**
     * 获取所有模板
     *
     * @return 模板列表
     */
    default List<NotificationTemplate> listTemplates() {
        return java.util.Collections.emptyList();
    }

    /**
     * 推送渠道枚举
     */
    enum PushChannel {
        EMAIL("邮件"),
        WECOM("企业微信"),
        DINGTALK("钉钉"),
        SMS("短信"),
        IN_APP("站内信"),
        WEBSOCKET("WebSocket");

        private final String description;

        PushChannel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
