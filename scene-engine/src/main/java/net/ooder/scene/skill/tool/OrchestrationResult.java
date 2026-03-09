package net.ooder.scene.skill.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 编排执行结果
 *
 * @author ooder
 * @since 2.3
 */
public class OrchestrationResult {
    
    private String planId;
    private String status;
    private List<StepResult> stepResults = new ArrayList<>();
    private Map<String, Object> outputs;
    private long totalTime;
    private String errorMessage;
    
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_PARTIAL = "partial";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_TIMEOUT = "timeout";
    
    public OrchestrationResult() {
    }
    
    public String getPlanId() {
        return planId;
    }
    
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<StepResult> getStepResults() {
        return stepResults;
    }
    
    public void setStepResults(List<StepResult> stepResults) {
        this.stepResults = stepResults;
    }
    
    public void addStepResult(StepResult result) {
        this.stepResults.add(result);
    }
    
    public Map<String, Object> getOutputs() {
        return outputs;
    }
    
    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }
    
    public long getTotalTime() {
        return totalTime;
    }
    
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(status);
    }
    
    public int getSuccessCount() {
        return (int) stepResults.stream().filter(StepResult::isSuccess).count();
    }
    
    public int getFailureCount() {
        return (int) stepResults.stream().filter(s -> !s.isSuccess()).count();
    }
    
    public static class StepResult {
        
        public static final String STATUS_SUCCESS = "success";
        public static final String STATUS_FAILURE = "failure";
        public static final String STATUS_SKIPPED = "skipped";
        
        private String stepId;
        private String toolName;
        private ToolResult toolResult;
        private long executionTime;
        private String status;
        
        public StepResult() {
        }
        
        public StepResult(String stepId, String toolName, ToolResult toolResult) {
            this.stepId = stepId;
            this.toolName = toolName;
            this.toolResult = toolResult;
            this.status = toolResult.isSuccess() ? STATUS_SUCCESS : STATUS_FAILURE;
        }
        
        public String getStepId() {
            return stepId;
        }
        
        public void setStepId(String stepId) {
            this.stepId = stepId;
        }
        
        public String getToolName() {
            return toolName;
        }
        
        public void setToolName(String toolName) {
            this.toolName = toolName;
        }
        
        public ToolResult getToolResult() {
            return toolResult;
        }
        
        public void setToolResult(ToolResult toolResult) {
            this.toolResult = toolResult;
        }
        
        public long getExecutionTime() {
            return executionTime;
        }
        
        public void setExecutionTime(long executionTime) {
            this.executionTime = executionTime;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public boolean isSuccess() {
            return STATUS_SUCCESS.equals(status);
        }
    }
}
