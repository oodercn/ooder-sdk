package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 任务取消消息
 * 对应Ooder-A2A规范v1.0 task_cancel类型
 */
public class TaskCancelMessage extends A2AMessage {

    /**
     * 任务ID
     */
    @JsonProperty("taskId")
    private String taskId;

    /**
     * 取消原因
     */
    @JsonProperty("reason")
    private String reason;

    public TaskCancelMessage() {
        super(MessageType.TASK_CANCEL);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
