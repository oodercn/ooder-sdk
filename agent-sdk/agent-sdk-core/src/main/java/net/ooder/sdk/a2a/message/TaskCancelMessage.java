package net.ooder.sdk.a2a.message;

/**
 * 任务取消消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class TaskCancelMessage extends A2AMessage {

    private String taskId;

    public TaskCancelMessage() {
        super(A2AMessageType.TASK_CANCEL);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TaskCancelMessage message = new TaskCancelMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder taskId(String taskId) {
            message.setTaskId(taskId);
            return this;
        }

        public TaskCancelMessage build() {
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
