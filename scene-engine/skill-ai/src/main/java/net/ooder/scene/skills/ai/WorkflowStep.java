package net.ooder.scene.skills.ai;

import java.util.Map;

/**
 * 工作流步骤
 */
public class WorkflowStep {

    public enum StepType {
        AI_GENERATION, MCP_CALL, CONDITION, PARALLEL, WAIT, CUSTOM
    }

    private String stepId;
    private String name;
    private StepType type;
    private String ref;
    private Map<String, Object> params;
    private String condition;
    private String nextStepId;
    private String onFailureStepId;
    private long timeout;
    private int retryCount;

    // Getters and Setters
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public StepType getType() { return type; }
    public void setType(StepType type) { this.type = type; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getNextStepId() { return nextStepId; }
    public void setNextStepId(String nextStepId) { this.nextStepId = nextStepId; }

    public String getOnFailureStepId() { return onFailureStepId; }
    public void setOnFailureStepId(String onFailureStepId) { this.onFailureStepId = onFailureStepId; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
