package net.ooder.scene.skill.notification;

/**
 * 通知监听器
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface NotificationListener {
    
    /**
     * 收到通知时调用
     * 
     * @param notification 通知内容
     */
    void onNotification(NotificationRecord notification);
}
