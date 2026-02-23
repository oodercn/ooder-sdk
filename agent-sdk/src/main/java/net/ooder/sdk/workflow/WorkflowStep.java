package net.ooder.sdk.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowStep {
    
    private String stepId;
    private String name;
    private String description;
    private String agentId;
    private String capId;
    private String action;
    private List<String> dependsOn = new ArrayList<>();
    private Map<String, Object> input = new HashMap<>();
    private String output;
    private StepConfig config = new StepConfig();
    private StepCondition condition;
    private StepRetry retry;
    private StepTimeout timeout;
    
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public List<String> getDependsOn() { return dependsOn; }
    public void setDependsOn(List<String> dependsOn) { this.dependsOn = dependsOn; }
    
    public void addDependency(String stepId) {
        this.dependsOn.add(stepId);
    }
    
    public Map<String, Object> getInput() { return input; }
    public void setInput(Map<String, Object> input) { this.input = input; }
    
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    
    public StepConfig getConfig() { return config; }
    public void setConfig(StepConfig config) { this.config = config; }
    
    public StepCondition getCondition() { return condition; }
    public void setCondition(StepCondition condition) { this.condition = condition; }
    
    public StepRetry getRetry() { return retry; }
    public void setRetry(StepRetry retry) { this.retry = retry; }
    
    public StepTimeout getTimeout() { return timeout; }
    public void setTimeout(StepTimeout timeout) { this.timeout = timeout; }
    
    public boolean hasDependencies() {
        return dependsOn != null && !dependsOn.isEmpty();
    }
    
    public boolean isEntryStep() {
        return !hasDependencies();
    }
    
    public static class StepConfig {
        private boolean async = false;
        private boolean ignoreError = false;
        private int priority = 0;
        private Map<String, Object> params = new HashMap<>();
        
        public boolean isAsync() { return async; }
        public void setAsync(boolean async) { this.async = async; }
        
        public boolean isIgnoreError() { return ignoreError; }
        public void setIgnoreError(boolean ignoreError) { this.ignoreError = ignoreError; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    public static class StepCondition {
        private String expression;
        private String type;
        private Map<String, Object> then_;
        private Map<String, Object> else_;
        
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Map<String, Object> getThen() { return then_; }
        public void setThen(Map<String, Object> then_) { this.then_ = then_; }
        
        public Map<String, Object> getElse() { return else_; }
        public void setElse(Map<String, Object> else_) { this.else_ = else_; }
    }
    
    public static class StepRetry {
        private int maxAttempts = 3;
        private int delay = 1000;
        private double backoffMultiplier = 1.5;
        private List<String> retryableErrors = new ArrayList<>();
        
        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        
        public int getDelay() { return delay; }
        public void setDelay(int delay) { this.delay = delay; }
        
        public double getBackoffMultiplier() { return backoffMultiplier; }
        public void setBackoffMultiplier(double backoffMultiplier) { this.backoffMultiplier = backoffMultiplier; }
        
        public List<String> getRetryableErrors() { return retryableErrors; }
        public void setRetryableErrors(List<String> retryableErrors) { this.retryableErrors = retryableErrors; }
    }
    
    public static class StepTimeout {
        private int duration = 30000;
        private String onTimeout = "fail";
        
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        
        public String getOnTimeout() { return onTimeout; }
        public void setOnTimeout(String onTimeout) { this.onTimeout = onTimeout; }
    }
}
