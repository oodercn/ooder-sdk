package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 技能请求
 */
public class SkillRequest {
    private String requestId;
    private String operation;
    private String resourceId;
    private Object parameters;
    private String skillId;
    private String sceneId;
    private Map<String, Object> metadata;

    public SkillRequest() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public Object getParameters() { return parameters; }
    public void setParameters(Object parameters) { this.parameters = parameters; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
