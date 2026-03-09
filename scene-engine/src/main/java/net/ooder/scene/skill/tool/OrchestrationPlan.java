package net.ooder.scene.skill.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具编排计划
 *
 * @author ooder
 * @since 2.3
 */
public class OrchestrationPlan {
    
    private String planId;
    private String description;
    private List<ExecutionStep> steps = new ArrayList<>();
    private ExecutionStrategy strategy = ExecutionStrategy.SEQUENTIAL;
    private int maxParallelism = 4;
    private long timeout = 60000;
    
    public enum ExecutionStrategy {
        SEQUENTIAL,
        PARALLEL,
        CONDITIONAL,
        PIPELINE
    }
    
    public OrchestrationPlan() {
    }
    
    public String getPlanId() {
        return planId;
    }
    
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<ExecutionStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps;
    }
    
    public void addStep(ExecutionStep step) {
        this.steps.add(step);
    }
    
    public ExecutionStrategy getStrategy() {
        return strategy;
    }
    
    public void setStrategy(ExecutionStrategy strategy) {
        this.strategy = strategy;
    }
    
    public int getMaxParallelism() {
        return maxParallelism;
    }
    
    public void setMaxParallelism(int maxParallelism) {
        this.maxParallelism = maxParallelism;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public static class ExecutionStep {
        
        private String stepId;
        private String name;
        private ToolCall toolCall;
        private List<String> dependencies = new ArrayList<>();
        private String condition;
        private boolean optional;
        private int retryCount = 0;
        private long retryDelay = 1000;
        
        public ExecutionStep() {
        }
        
        public ExecutionStep(String stepId, ToolCall toolCall) {
            this.stepId = stepId;
            this.toolCall = toolCall;
        }
        
        public String getStepId() {
            return stepId;
        }
        
        public void setStepId(String stepId) {
            this.stepId = stepId;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public ToolCall getToolCall() {
            return toolCall;
        }
        
        public void setToolCall(ToolCall toolCall) {
            this.toolCall = toolCall;
        }
        
        public List<String> getDependencies() {
            return dependencies;
        }
        
        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies;
        }
        
        public void addDependency(String stepId) {
            this.dependencies.add(stepId);
        }
        
        public String getCondition() {
            return condition;
        }
        
        public void setCondition(String condition) {
            this.condition = condition;
        }
        
        public boolean isOptional() {
            return optional;
        }
        
        public void setOptional(boolean optional) {
            this.optional = optional;
        }
        
        public int getRetryCount() {
            return retryCount;
        }
        
        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }
        
        public long getRetryDelay() {
            return retryDelay;
        }
        
        public void setRetryDelay(long retryDelay) {
            this.retryDelay = retryDelay;
        }
    }
}
