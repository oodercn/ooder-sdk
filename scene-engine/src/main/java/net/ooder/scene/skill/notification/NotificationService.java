package net.ooder.scene.skill.notification;

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

    /**
     * 推送渠道枚举
     */
    enum PushChannel {
        EMAIL("邮件"),
        WECOM("企业微信"),
        DINGTALK("钉钉"),
        SMS("短信"),
        IN_APP("站内信");

        private final String description;

        PushChannel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
