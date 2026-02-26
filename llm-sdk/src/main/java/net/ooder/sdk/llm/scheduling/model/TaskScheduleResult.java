package net.ooder.sdk.llm.scheduling.model;

import java.time.Instant;

public class TaskScheduleResult {
    private String taskId;
    private String assignmentId;
    private String status;
    private Instant scheduledTime;
    private Instant estimatedStartTime;
    private String message;

    public TaskScheduleResult() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Instant scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Instant getEstimatedStartTime() {
        return estimatedStartTime;
    }

    public void setEstimatedStartTime(Instant estimatedStartTime) {
        this.estimatedStartTime = estimatedStartTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
