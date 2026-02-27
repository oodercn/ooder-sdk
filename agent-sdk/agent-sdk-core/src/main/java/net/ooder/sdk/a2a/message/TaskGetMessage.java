package net.ooder.sdk.a2a.message;

/**
 * 任务获取消息
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class TaskGetMessage<T> extends A2AMessage<T> {

    private String taskId;

    public TaskGetMessage() {
        super(A2AMessageType.TASK_GET);
    }
    
    public static TaskGetMessage<Void> createGeneric() {
        return new TaskGetMessage<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private TaskGetMessage<T> message = new TaskGetMessage<>();

        public Builder<T> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<T> taskId(String taskId) {
            message.setTaskId(taskId);
            return this;
        }
        
        public Builder<T> data(T data) {
            message.setData(data);
            return this;
        }

        public TaskGetMessage<T> build() {
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
