package net.ooder.scene.config;

import net.ooder.scene.skill.notification.NotificationMessage;
import net.ooder.scene.skill.notification.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认通知服务实现
 *
 * @author ooder
 * @since 2.4
 */
public class DefaultNotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultNotificationServiceImpl.class);

    @Override
    public void push(String userId, String title, String content, PushChannel channel) {
        log.info("[Notification] Pushing to user: {}, channel: {}, title: {}", userId, channel, title);
    }

    @Override
    public void pushToParticipants(String activationId, NotificationMessage message) {
        log.info("[Notification] Pushing to participants of activation: {}, title: {}", activationId, message.getTitle());
    }
}
