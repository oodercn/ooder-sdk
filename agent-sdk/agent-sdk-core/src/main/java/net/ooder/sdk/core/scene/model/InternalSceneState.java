package net.ooder.sdk.core.scene.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景内部状态
 */
public class InternalSceneState {
    private String sceneId;
    private boolean active;
    private int memberCount;
    private List<String> installedSkills = new ArrayList<>();
    private long createTime;
    private long lastUpdateTime;
    private String currentWorkflowId;
    private String workflowStatus;

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public List<String> getInstalledSkills() {
        return installedSkills;
    }

    public void setInstalledSkills(List<String> installedSkills) {
        this.installedSkills = installedSkills != null ? installedSkills : new ArrayList<>();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCurrentWorkflowId() {
        return currentWorkflowId;
    }

    public void setCurrentWorkflowId(String currentWorkflowId) {
        this.currentWorkflowId = currentWorkflowId;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
}
