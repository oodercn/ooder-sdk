package net.ooder.scene.skills.ai;

import java.util.List;
import java.util.Map;

/**
 * 工作流定义
 */
public class WorkflowDefinition {

    private String workflowId;
    private String name;
    private String description;
    private String version;
    private List<WorkflowStep> steps;
    private Map<String, Object> parameters;
    private boolean enabled;

    // Getters and Setters
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

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
