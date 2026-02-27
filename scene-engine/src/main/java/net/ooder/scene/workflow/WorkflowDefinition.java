package net.ooder.scene.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowDefinition {
    
    private String workflowId;
    private String name;
    private String description;
    private String version;
    private List<WorkflowStep> steps = new ArrayList<>();
    private Map<String, Object> variables = new HashMap<>();
    private WorkflowConfig config = new WorkflowConfig();
    
    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public List<WorkflowStep> getSteps() { return steps; }
    public void setSteps(List<WorkflowStep> steps) { this.steps = steps; }
    
    public void addStep(WorkflowStep step) {
        this.steps.add(step);
    }
    
    public Map<String, Object> getVariables() { return variables; }
    public void setVariables(Map<String, Object> variables) { this.variables = variables; }
    
    public WorkflowConfig getConfig() { return config; }
    public void setConfig(WorkflowConfig config) { this.config = config; }
    
    public WorkflowStep getStep(String stepId) {
        for (WorkflowStep step : steps) {
            if (stepId.equals(step.getStepId())) {
                return step;
            }
        }
        return null;
    }
    
    public List<WorkflowStep> getEntrySteps() {
        List<WorkflowStep> entrySteps = new ArrayList<>();
        for (WorkflowStep step : steps) {
            if (step.getDependsOn() == null || step.getDependsOn().isEmpty()) {
                entrySteps.add(step);
            }
        }
        return entrySteps;
    }
    
    public static class WorkflowConfig {
        private int timeout = 300000;
        private int retryCount = 3;
        private int retryDelay = 1000;
        private boolean parallel = false;
        private boolean failFast = true;
        private String errorHandling = "stop";
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        
        public int getRetryDelay() { return retryDelay; }
        public void setRetryDelay(int retryDelay) { this.retryDelay = retryDelay; }
        
        public boolean isParallel() { return parallel; }
        public void setParallel(boolean parallel) { this.parallel = parallel; }
        
        public boolean isFailFast() { return failFast; }
        public void setFailFast(boolean failFast) { this.failFast = failFast; }
        
        public String getErrorHandling() { return errorHandling; }
        public void setErrorHandling(String errorHandling) { this.errorHandling = errorHandling; }
    }
}
