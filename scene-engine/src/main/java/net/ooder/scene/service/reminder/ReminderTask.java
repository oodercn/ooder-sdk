package net.ooder.scene.service.reminder;

/**
 * 提醒任务
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ReminderTask {

    private String reminderId;
    private String sceneId;
    private String userId;
    private String cronExpression;
    private ReminderConfig.ReminderType reminderType;
    private String title;
    private String messageTemplate;
    private ReminderStatus status;
    private long createTime;
    private long nextTriggerTime;
    private long lastTriggerTime;
    private int triggerCount;
    private int maxTriggers;
    private boolean enabled;

    public ReminderTask() {
        this.status = ReminderStatus.PENDING;
        this.enabled = true;
        this.createTime = System.currentTimeMillis();
    }

    public String getReminderId() { return reminderId; }
    public void setReminderId(String reminderId) { this.reminderId = reminderId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public ReminderConfig.ReminderType getReminderType() { return reminderType; }
    public void setReminderType(ReminderConfig.ReminderType reminderType) { this.reminderType = reminderType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessageTemplate() { return messageTemplate; }
    public void setMessageTemplate(String messageTemplate) { this.messageTemplate = messageTemplate; }

    public ReminderStatus getStatus() { return status; }
    public void setStatus(ReminderStatus status) { this.status = status; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getNextTriggerTime() { return nextTriggerTime; }
    public void setNextTriggerTime(long nextTriggerTime) { this.nextTriggerTime = nextTriggerTime; }

    public long getLastTriggerTime() { return lastTriggerTime; }
    public void setLastTriggerTime(long lastTriggerTime) { this.lastTriggerTime = lastTriggerTime; }

    public int getTriggerCount() { return triggerCount; }
    public void setTriggerCount(int triggerCount) { this.triggerCount = triggerCount; }

    public int getMaxTriggers() { return maxTriggers; }
    public void setMaxTriggers(int maxTriggers) { this.maxTriggers = maxTriggers; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isActive() {
        return enabled && status == ReminderStatus.ACTIVE;
    }

    public boolean isExpired() {
        return maxTriggers > 0 && triggerCount >= maxTriggers;
    }

    /**
     * 提醒状态枚举
     */
    public enum ReminderStatus {
        PENDING("待激活"),
        ACTIVE("运行中"),
        PAUSED("已暂停"),
        COMPLETED("已完成"),
        CANCELLED("已取消"),
        ERROR("错误");

        private final String description;

        ReminderStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
