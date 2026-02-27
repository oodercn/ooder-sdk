package net.ooder.sdk.a2a.message;

/**
 * 任务取消消息（泛型版本）
 *
 * @param <T> 数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class TaskCancelMessage<T> extends A2AMessage<T> {

    private String taskId;

    public TaskCancelMessage() {
        super(A2AMessageType.TASK_CANCEL);
    }
    
    public static TaskCancelMessage<Void> createGeneric() {
        return new TaskCancelMessage<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private TaskCancelMessage<T> message = new TaskCancelMessage<>();

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

        public TaskCancelMessage<T> build() {
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
