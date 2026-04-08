package net.ooder.scene.workflow;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 工作流执行记录
 *
 * <p>记录工作流的一次执行实例，包含执行状态、结果和日志。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class WorkflowExecution {

    private String executionId;
    private String workflowId;
    private String sceneGroupId;
    private WorkflowStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String triggerType;
    private String triggerSource;
    private Map<String, Object> inputData;
    private Map<String, Object> outputData;
    private String result;
    private String errorMessage;
    private int currentStepIndex;
    private String executorId;

    public WorkflowExecution() {
        this.status = WorkflowStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.inputData = new HashMap<>();
        this.outputData = new HashMap<>();
        this.currentStepIndex = 0;
    }

    // ===== Getters and Setters =====

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerSource() {
        return triggerSource;
    }

    public void setTriggerSource(String triggerSource) {
        this.triggerSource = triggerSource;
    }

    public Map<String, Object> getInputData() {
        return inputData;
    }

    public void setInputData(Map<String, Object> inputData) {
        this.inputData = inputData;
    }

    public Map<String, Object> getOutputData() {
        return outputData;
    }

    public void setOutputData(Map<String, Object> outputData) {
        this.outputData = outputData;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    // ===== 便捷方法 =====

    /**
     * 获取执行时长（毫秒）
     */
    public long getDuration() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }
        return 0;
    }

    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return status == WorkflowStatus.COMPLETED ||
               status == WorkflowStatus.ERROR ||
               status == WorkflowStatus.CANCELLED;
    }

    /**
     * 标记为成功完成
     */
    public void markAsCompleted(String result) {
        this.status = WorkflowStatus.COMPLETED;
        this.result = result;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 标记为失败
     */
    public void markAsFailed(String errorMessage) {
        this.status = WorkflowStatus.ERROR;
        this.errorMessage = errorMessage;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "WorkflowExecution{" +
            "executionId='" + executionId + '\'' +
            ", workflowId='" + workflowId + '\'' +
            ", status=" + status +
            ", startTime=" + startTime +
            ", currentStepIndex=" + currentStepIndex +
            '}';
    }
}
