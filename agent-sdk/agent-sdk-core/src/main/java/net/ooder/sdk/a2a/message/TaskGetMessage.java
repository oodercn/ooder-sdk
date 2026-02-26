package net.ooder.sdk.a2a.message;

/**
 * 任务获取消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class TaskGetMessage extends A2AMessage {

    private String taskId;

    public TaskGetMessage() {
        super(A2AMessageType.TASK_GET);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TaskGetMessage message = new TaskGetMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder taskId(String taskId) {
            message.setTaskId(taskId);
            return this;
        }

        public TaskGetMessage build() {
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
