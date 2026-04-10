package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 任务获取消息
 * 对应Ooder-A2A规范v1.0 task_get类型
 */
public class TaskGetMessage extends A2AMessage {

    /**
     * 任务ID
     */
    @JsonProperty("taskId")
    private String taskId;

    /**
     * 是否包含历史记录
     */
    @JsonProperty("includeHistory")
    private Boolean includeHistory;

    public TaskGetMessage() {
        super(MessageType.TASK_GET);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getIncludeHistory() {
        return includeHistory;
    }

    public void setIncludeHistory(Boolean includeHistory) {
        this.includeHistory = includeHistory;
    }
}
