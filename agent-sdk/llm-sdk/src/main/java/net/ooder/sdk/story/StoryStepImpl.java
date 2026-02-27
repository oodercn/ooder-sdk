package net.ooder.sdk.story;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class StoryStepImpl implements StoryStep {
    
    private final String stepId;
    private final String name;
    private final String description;
    private final StepType type;
    private final String capabilityId;
    private final Map<String, Object> params;
    private final List<String> dependencies;
    private StepStatus status;
    private StepResult result;
    
    StoryStepImpl(StoryStepBuilder builder) {
        this.stepId = builder.stepId;
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.capabilityId = builder.capabilityId;
        this.params = Collections.unmodifiableMap(builder.params);
        this.dependencies = Collections.unmodifiableList(builder.dependencies);
        this.status = builder.status;
        this.result = builder.result;
    }
    
    @Override public String getStepId() { return stepId; }
    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    @Override public StepType getType() { return type; }
    @Override public String getCapabilityId() { return capabilityId; }
    @Override public Map<String, Object> getParams() { return params; }
    @Override public List<String> getDependencies() { return dependencies; }
    @Override public StepStatus getStatus() { return status; }
    @Override public void setStatus(StepStatus status) { this.status = status; }
    @Override public StepResult getResult() { return result; }
    @Override public void setResult(StepResult result) { this.result = result; }
    
    @Override
    public String toString() {
        return "StoryStep{" +
            "stepId='" + stepId + '\'' +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", status=" + status +
            '}';
    }
}
