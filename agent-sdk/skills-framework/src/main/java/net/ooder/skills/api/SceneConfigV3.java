package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * SceneConfig v3.0
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SceneConfigV3 {

    private SceneRunMode sceneType;
    private OrchestrationConfig orchestration;
    private List<String> internalCapabilities;
    private List<String> childSkills;
    private Map<String, Object> params;

    public SceneRunMode getSceneType() { return sceneType; }
    public void setSceneType(SceneRunMode sceneType) { this.sceneType = sceneType; }

    public OrchestrationConfig getOrchestration() { return orchestration; }
    public void setOrchestration(OrchestrationConfig orchestration) { this.orchestration = orchestration; }

    public List<String> getInternalCapabilities() { return internalCapabilities; }
    public void setInternalCapabilities(List<String> internalCapabilities) { this.internalCapabilities = internalCapabilities; }

    public List<String> getChildSkills() { return childSkills; }
    public void setChildSkills(List<String> childSkills) { this.childSkills = childSkills; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
