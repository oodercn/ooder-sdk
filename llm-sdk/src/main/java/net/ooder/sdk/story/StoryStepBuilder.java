package net.ooder.sdk.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryStepBuilder {
    
    String stepId;
    String name;
    String description;
    StoryStep.StepType type = StoryStep.StepType.ACTION;
    String capabilityId;
    Map<String, Object> params = new HashMap<>();
    List<String> dependencies = new ArrayList<>();
    StoryStep.StepStatus status = StoryStep.StepStatus.PENDING;
    StoryStep.StepResult result;
    
    public StoryStepBuilder stepId(String stepId) { this.stepId = stepId; return this; }
    public StoryStepBuilder name(String name) { this.name = name; return this; }
    public StoryStepBuilder description(String description) { this.description = description; return this; }
    public StoryStepBuilder type(StoryStep.StepType type) { this.type = type; return this; }
    public StoryStepBuilder capabilityId(String capabilityId) { this.capabilityId = capabilityId; return this; }
    public StoryStepBuilder params(Map<String, Object> params) { this.params = params; return this; }
    public StoryStepBuilder addParam(String key, Object value) { this.params.put(key, value); return this; }
    public StoryStepBuilder dependencies(List<String> dependencies) { this.dependencies = dependencies; return this; }
    public StoryStepBuilder addDependency(String stepId) { this.dependencies.add(stepId); return this; }
    public StoryStepBuilder status(StoryStep.StepStatus status) { this.status = status; return this; }
    public StoryStepBuilder result(StoryStep.StepResult result) { this.result = result; return this; }
    
    public StoryStep build() {
        if (stepId == null) stepId = "step-" + System.currentTimeMillis();
        return new StoryStepImpl(this);
    }
}
