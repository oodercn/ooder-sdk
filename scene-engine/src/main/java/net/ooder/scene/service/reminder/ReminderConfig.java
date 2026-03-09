package net.ooder.scene.service.reminder;

import java.util.HashMap;
import java.util.Map;

/**
 * 提醒配置
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ReminderConfig {

    private String sceneId;
    private String userId;
    private String cronExpression;
    private ReminderType reminderType;
    private String messageTemplate;
    private String title;
    private int maxRetries = 3;
    private int retryIntervalMinutes = 30;
    private boolean enabled = true;
    private Map<String, Object> extraConfig = new HashMap<>();

    public ReminderConfig() {}

    public ReminderConfig(String sceneId, String userId) {
        this.sceneId = sceneId;
        this.userId = userId;
    }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public ReminderType getReminderType() { return reminderType; }
    public void setReminderType(ReminderType reminderType) { this.reminderType = reminderType; }

    public String getMessageTemplate() { return messageTemplate; }
    public void setMessageTemplate(String messageTemplate) { this.messageTemplate = messageTemplate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public int getRetryIntervalMinutes() { return retryIntervalMinutes; }
    public void setRetryIntervalMinutes(int retryIntervalMinutes) { this.retryIntervalMinutes = retryIntervalMinutes; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Map<String, Object> getExtraConfig() { return extraConfig; }
    public void setExtraConfig(Map<String, Object> extraConfig) { this.extraConfig = extraConfig; }

    /**
     * 创建每周五下午5点提醒的默认配置
     */
    public static ReminderConfig weeklyFriday(String sceneId, String userId) {
        ReminderConfig config = new ReminderConfig(sceneId, userId);
        config.setCronExpression("0 0 17 ? * FRI");
        config.setReminderType(ReminderType.PUSH);
        config.setTitle("周报提醒");
        config.setMessageTemplate("请及时提交本周周报");
        return config;
    }

    /**
     * 创建每日提醒配置
     */
    public static ReminderConfig daily(String sceneId, String userId, int hour, int minute) {
        ReminderConfig config = new ReminderConfig(sceneId, userId);
        config.setCronExpression(String.format("0 %d %d ? * *", minute, hour));
        config.setReminderType(ReminderType.PUSH);
        return config;
    }

    /**
     * 提醒类型枚举
     */
    public enum ReminderType {
        EMAIL("邮件"),
        PUSH("推送"),
        SMS("短信"),
        IN_APP("应用内");

        private final String description;

        ReminderType(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
