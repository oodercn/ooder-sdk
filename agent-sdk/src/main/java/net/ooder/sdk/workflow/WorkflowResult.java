package net.ooder.sdk.workflow;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String workflowId;
    private String executionId;
    private boolean success;
    private Object output;
    private String errorCode;
    private String errorMessage;
    private long startTime;
    private long endTime;
    private long duration;
    private Map<String, Object> stepResults = new ConcurrentHashMap<>();
    private Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    public WorkflowResult() {}
    
    public static WorkflowResult success(String workflowId, Object output) {
        WorkflowResult result = new WorkflowResult();
        result.setWorkflowId(workflowId);
        result.setSuccess(true);
        result.setOutput(output);
        return result;
    }
    
    public static WorkflowResult failure(String workflowId, String errorCode, String errorMessage) {
        WorkflowResult result = new WorkflowResult();
        result.setWorkflowId(workflowId);
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public Object getOutput() { return output; }
    public void setOutput(Object output) { this.output = output; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public Map<String, Object> getStepResults() { return stepResults; }
    public void setStepResults(Map<String, Object> stepResults) { 
        this.stepResults = stepResults != null ? stepResults : new ConcurrentHashMap<>(); 
    }
    
    public void addStepResult(String stepId, Object result) {
        this.stepResults.put(stepId, result);
    }
    
    public Object getStepResult(String stepId) {
        return this.stepResults.get(stepId);
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>(); 
    }
    
    public void complete() {
        this.endTime = System.currentTimeMillis();
        if (this.startTime > 0) {
            this.duration = this.endTime - this.startTime;
        }
    }
}
