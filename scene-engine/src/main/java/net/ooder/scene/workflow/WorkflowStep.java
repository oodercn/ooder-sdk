package net.ooder.scene.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowStep {
    
    private String stepId;
    private String name;
    private String description;
    private String type;
    private String action;
    private String agentId;
    private String capId;
    private String condition;
    private String input;
    private String output;
    private StepTimeout timeout;
    private List<String> dependsOn = new ArrayList<>();
    private Map<String, Object> inputs = new HashMap<>();
    private Map<String, Object> outputs = new HashMap<>();
    private StepConfig config = new StepConfig();
    
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    
    public StepTimeout getTimeout() { return timeout; }
    public void setTimeout(StepTimeout timeout) { this.timeout = timeout; }
    
    public List<String> getDependsOn() { return dependsOn; }
    public void setDependsOn(List<String> dependsOn) { this.dependsOn = dependsOn; }
    
    public Map<String, Object> getInputs() { return inputs; }
    public void setInputs(Map<String, Object> inputs) { this.inputs = inputs; }
    
    public Map<String, Object> getOutputs() { return outputs; }
    public void setOutputs(Map<String, Object> outputs) { this.outputs = outputs; }
    
    public StepConfig getConfig() { return config; }
    public void setConfig(StepConfig config) { this.config = config; }
    
    public boolean hasDependencies() {
        return dependsOn != null && !dependsOn.isEmpty();
    }
    
    public static class StepConfig {
        private int timeout = 60000;
        private int retryCount = 3;
        private int retryDelay = 1000;
        private boolean continueOnError = false;
        private Map<String, Object> params = new HashMap<>();
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        
        public int getRetryDelay() { return retryDelay; }
        public void setRetryDelay(int retryDelay) { this.retryDelay = retryDelay; }
        
        public boolean isContinueOnError() { return continueOnError; }
        public void setContinueOnError(boolean continueOnError) { this.continueOnError = continueOnError; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    public static class StepTimeout {
        private long duration = 30000;
        private String unit = "MILLISECONDS";
        
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
}
