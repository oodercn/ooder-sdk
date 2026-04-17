package net.ooder.skill.capability.dto;

import java.util.List;

public class SceneTypeUpdateRequest {
    
    private String action;
    private String sceneType;
    private String approvedBy;
    private List<String> sceneTypes;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public List<String> getSceneTypes() { return sceneTypes; }
    public void setSceneTypes(List<String> sceneTypes) { this.sceneTypes = sceneTypes; }
}
