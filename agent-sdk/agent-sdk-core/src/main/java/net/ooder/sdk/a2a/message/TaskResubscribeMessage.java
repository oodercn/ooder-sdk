package net.ooder.sdk.a2a.message;

/**
 * 任务重新订阅消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class TaskResubscribeMessage extends A2AMessage {

    private String taskId;

    public TaskResubscribeMessage() {
        super(A2AMessageType.TASK_RESUBSCRIBE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TaskResubscribeMessage message = new TaskResubscribeMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder taskId(String taskId) {
            message.setTaskId(taskId);
            return this;
        }

        public TaskResubscribeMessage build() {
            return message;
        }
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
