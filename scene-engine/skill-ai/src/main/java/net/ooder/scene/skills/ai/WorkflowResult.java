package net.ooder.scene.skills.ai;

import java.util.Map;

/**
 * 工作流执行结果
 */
public class WorkflowResult {

    private boolean success;
    private String executionId;
    private String workflowId;
    private Map<String, Object> output;
    private String errorMessage;
    private long executionTime;
    private int completedSteps;
    private int totalSteps;

    public static WorkflowResult success(String executionId, String workflowId, Map<String, Object> output) {
        WorkflowResult result = new WorkflowResult();
        result.success = true;
        result.executionId = executionId;
        result.workflowId = workflowId;
        result.output = output;
        return result;
    }

    public static WorkflowResult failure(String executionId, String workflowId, String errorMessage) {
        WorkflowResult result = new WorkflowResult();
        result.success = false;
        result.executionId = executionId;
        result.workflowId = workflowId;
        result.errorMessage = errorMessage;
        return result;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public Map<String, Object> getOutput() { return output; }
    public void setOutput(Map<String, Object> output) { this.output = output; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public int getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
}
