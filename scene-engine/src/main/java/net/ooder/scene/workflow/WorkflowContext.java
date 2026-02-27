package net.ooder.scene.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowContext {
    
    private final String executionId;
    private final String workflowId;
    private final String sceneId;
    private final Map<String, Object> variables = new ConcurrentHashMap<>();
    private final Map<String, StepResult> stepResults = new ConcurrentHashMap<>();
    private final Map<String, Object> sharedState = new ConcurrentHashMap<>();
    
    public WorkflowContext(String executionId, String workflowId, String sceneId) {
        this.executionId = executionId;
        this.workflowId = workflowId;
        this.sceneId = sceneId;
    }
    
    public String getExecutionId() { return executionId; }
    public String getWorkflowId() { return workflowId; }
    public String getSceneId() { return sceneId; }
    
    public Map<String, Object> getVariables() { return variables; }
    
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }
    
    public Object getVariable(String key) {
        return variables.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getVariable(String key, Class<T> type) {
        Object value = variables.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    public void setVariables(Map<String, Object> vars) {
        if (vars != null) {
            variables.putAll(vars);
        }
    }
    
    public Map<String, StepResult> getStepResults() { return stepResults; }
    
    public void setStepResult(String stepId, StepResult result) {
        stepResults.put(stepId, result);
    }
    
    public StepResult getStepResult(String stepId) {
        return stepResults.get(stepId);
    }
    
    public Object getStepOutput(String stepId) {
        StepResult result = stepResults.get(stepId);
        return result != null ? result.getOutput() : null;
    }
    
    public boolean hasStepCompleted(String stepId) {
        StepResult result = stepResults.get(stepId);
        return result != null && result.getStatus() == StepStatus.COMPLETED;
    }
    
    public boolean hasStepFailed(String stepId) {
        StepResult result = stepResults.get(stepId);
        return result != null && result.getStatus() == StepStatus.FAILED;
    }
    
    public boolean canExecuteStep(WorkflowStep step) {
        if (!step.hasDependencies()) {
            return true;
        }
        
        for (String depId : step.getDependsOn()) {
            if (!hasStepCompleted(depId)) {
                return false;
            }
        }
        return true;
    }
    
    public Map<String, Object> getSharedState() { return sharedState; }
    
    public void updateSharedState(Map<String, Object> state) {
        if (state != null) {
            sharedState.putAll(state);
        }
    }
    
    public WorkflowContext copy() {
        WorkflowContext copy = new WorkflowContext(executionId, workflowId, sceneId);
        copy.variables.putAll(this.variables);
        copy.stepResults.putAll(this.stepResults);
        copy.sharedState.putAll(this.sharedState);
        return copy;
    }
    
    public static class StepResult {
        private final String stepId;
        private final StepStatus status;
        private final Object output;
        private final String errorMessage;
        private final long startTime;
        private final long endTime;
        
        public StepResult(String stepId, StepStatus status, Object output, String errorMessage, 
                         long startTime, long endTime) {
            this.stepId = stepId;
            this.status = status;
            this.output = output;
            this.errorMessage = errorMessage;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public static StepResult success(String stepId, Object output, long startTime, long endTime) {
            return new StepResult(stepId, StepStatus.COMPLETED, output, null, startTime, endTime);
        }
        
        public static StepResult failure(String stepId, String errorMessage, long startTime, long endTime) {
            return new StepResult(stepId, StepStatus.FAILED, null, errorMessage, startTime, endTime);
        }
        
        public static StepResult skipped(String stepId, String reason, long startTime, long endTime) {
            return new StepResult(stepId, StepStatus.SKIPPED, null, reason, startTime, endTime);
        }
        
        public String getStepId() { return stepId; }
        public StepStatus getStatus() { return status; }
        public Object getOutput() { return output; }
        public String getErrorMessage() { return errorMessage; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public long getDuration() { return endTime - startTime; }
    }
    
    public enum StepStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        SKIPPED,
        TIMEOUT
    }
}
