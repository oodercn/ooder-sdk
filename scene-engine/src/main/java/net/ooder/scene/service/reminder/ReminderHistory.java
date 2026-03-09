package net.ooder.scene.service.reminder;

/**
 * 提醒历史记录
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ReminderHistory {

    private String historyId;
    private String reminderId;
    private String sceneId;
    private String userId;
    private long triggerTime;
    private ReminderResult result;
    private String errorMessage;
    private String message;
    private boolean acknowledged;

    public ReminderHistory() {
        this.triggerTime = System.currentTimeMillis();
    }

    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }

    public String getReminderId() { return reminderId; }
    public void setReminderId(String reminderId) { this.reminderId = reminderId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTriggerTime() { return triggerTime; }
    public void setTriggerTime(long triggerTime) { this.triggerTime = triggerTime; }

    public ReminderResult getResult() { return result; }
    public void setResult(ReminderResult result) { this.result = result; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }

    public boolean isSuccess() {
        return result == ReminderResult.SUCCESS;
    }

    /**
     * 提醒结果枚举
     */
    public enum ReminderResult {
        SUCCESS("成功"),
        FAILED("失败"),
        RETRY("重试中"),
        SKIPPED("跳过");

        private final String description;

        ReminderResult(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
