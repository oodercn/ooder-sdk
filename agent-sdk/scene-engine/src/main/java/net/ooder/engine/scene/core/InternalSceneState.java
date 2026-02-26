package net.ooder.engine.scene.core;

import net.ooder.sdk.api.scene.SceneDefinition;
import net.ooder.sdk.api.scene.model.SceneState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InternalSceneState {
    private String sceneId;
    private SceneDefinition definition;
    private SceneState state;
    private Map<String, Object> runtimeData;
    private long createdAt;
    private long activatedAt;
    private long lastUpdateTime;
    private boolean active;
    private int memberCount;
    private List<String> installedSkills;
    private String currentWorkflowId;
    private String workflowStatus;
    private long createTime;
    
    public InternalSceneState() {
        this.runtimeData = new ConcurrentHashMap<>();
        this.installedSkills = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.createTime = this.createdAt;
        this.lastUpdateTime = this.createdAt;
        this.active = false;
        this.memberCount = 0;
    }
    
    public InternalSceneState(String sceneId, SceneDefinition definition) {
        this();
        this.sceneId = sceneId;
        this.definition = definition;
        this.state = SceneState.CREATED;
    }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public SceneDefinition getDefinition() { return definition; }
    public void setDefinition(SceneDefinition definition) { this.definition = definition; }
    
    public SceneState getState() { return state; }
    public void setState(SceneState state) { this.state = state; }
    
    public Map<String, Object> getRuntimeData() { return runtimeData; }
    public void setRuntimeData(Map<String, Object> runtimeData) { this.runtimeData = runtimeData; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getActivatedAt() { return activatedAt; }
    public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }
    
    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    
    public List<String> getInstalledSkills() { return installedSkills; }
    public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
    
    public String getCurrentWorkflowId() { return currentWorkflowId; }
    public void setCurrentWorkflowId(String currentWorkflowId) { this.currentWorkflowId = currentWorkflowId; }
    
    public String getWorkflowStatus() { return workflowStatus; }
    public void setWorkflowStatus(String workflowStatus) { this.workflowStatus = workflowStatus; }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
