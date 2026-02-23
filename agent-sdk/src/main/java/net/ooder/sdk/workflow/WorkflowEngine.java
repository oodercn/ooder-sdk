package net.ooder.sdk.workflow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface WorkflowEngine {
    
    WorkflowResult execute(String workflowId, WorkflowContext context);
    
    CompletableFuture<WorkflowResult> executeAsync(String workflowId, WorkflowContext context);
    
    WorkflowResult execute(WorkflowDefinition definition, WorkflowContext context);
    
    CompletableFuture<WorkflowResult> executeAsync(WorkflowDefinition definition, WorkflowContext context);
    
    void registerWorkflow(WorkflowDefinition definition);
    
    void unregisterWorkflow(String workflowId);
    
    WorkflowDefinition getWorkflow(String workflowId);
    
    List<WorkflowDefinition> getAllWorkflows();
    
    void pause(String executionId);
    
    void resume(String executionId);
    
    void cancel(String executionId);
    
    WorkflowStatus getStatus(String executionId);
    
    List<WorkflowExecution> getActiveExecutions();
    
    WorkflowExecution getExecution(String executionId);
    
    public static class WorkflowResult {
        private final String executionId;
        private final String workflowId;
        private final WorkflowStatus status;
        private final Map<String, WorkflowContext.StepResult> stepResults;
        private final Map<String, Object> outputs;
        private final String errorMessage;
        private final long startTime;
        private final long endTime;
        
        public WorkflowResult(String executionId, String workflowId, WorkflowStatus status,
                             Map<String, WorkflowContext.StepResult> stepResults,
                             Map<String, Object> outputs, String errorMessage,
                             long startTime, long endTime) {
            this.executionId = executionId;
            this.workflowId = workflowId;
            this.status = status;
            this.stepResults = stepResults;
            this.outputs = outputs;
            this.errorMessage = errorMessage;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public static WorkflowResult success(String executionId, String workflowId,
                                            Map<String, WorkflowContext.StepResult> stepResults,
                                            Map<String, Object> outputs,
                                            long startTime, long endTime) {
            return new WorkflowResult(executionId, workflowId, WorkflowStatus.COMPLETED,
                stepResults, outputs, null, startTime, endTime);
        }
        
        public static WorkflowResult failure(String executionId, String workflowId,
                                            String errorMessage,
                                            Map<String, WorkflowContext.StepResult> stepResults,
                                            long startTime, long endTime) {
            return new WorkflowResult(executionId, workflowId, WorkflowStatus.FAILED,
                stepResults, null, errorMessage, startTime, endTime);
        }
        
        public String getExecutionId() { return executionId; }
        public String getWorkflowId() { return workflowId; }
        public WorkflowStatus getStatus() { return status; }
        public Map<String, WorkflowContext.StepResult> getStepResults() { return stepResults; }
        public Map<String, Object> getOutputs() { return outputs; }
        public String getErrorMessage() { return errorMessage; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public long getDuration() { return endTime - startTime; }
        public boolean isSuccess() { return status == WorkflowStatus.COMPLETED; }
    }
    
    public static class WorkflowExecution {
        private final String executionId;
        private final String workflowId;
        private final WorkflowStatus status;
        private final long startTime;
        private volatile long endTime;
        private volatile String currentStepId;
        
        public WorkflowExecution(String executionId, String workflowId) {
            this.executionId = executionId;
            this.workflowId = workflowId;
            this.status = WorkflowStatus.RUNNING;
            this.startTime = System.currentTimeMillis();
        }
        
        public String getExecutionId() { return executionId; }
        public String getWorkflowId() { return workflowId; }
        public WorkflowStatus getStatus() { return status; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getCurrentStepId() { return currentStepId; }
        public void setCurrentStepId(String stepId) { this.currentStepId = stepId; }
    }
    
    public enum WorkflowStatus {
        PENDING,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
}
