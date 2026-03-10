package net.ooder.scene.skill.notification;

import java.util.Map;

/**
 * 通知消息
 *
 * @author ooder
 * @since 2.4
 */
public class NotificationMessage {

    private String title;
    private String content;
    private String templateId;
    private Map<String, Object> params;
    private NotificationService.PushChannel channel;
    private int priority;
    private long expireTime;

    public NotificationMessage() {}

    public NotificationMessage(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public NotificationService.PushChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationService.PushChannel channel) {
        this.channel = channel;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public static NotificationMessage of(String title, String content) {
        return new NotificationMessage(title, content);
    }
}
