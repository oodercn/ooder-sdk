package net.ooder.scene.core;

import java.util.List;

/**
 * 心跳结果
 */
public class HeartbeatResult {
    private String groupId;
    private boolean success;
    private List<String> activeMembers;
    private List<String> inactiveMembers;
    private String primaryId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(List<String> activeMembers) {
        this.activeMembers = activeMembers;
    }

    public List<String> getInactiveMembers() {
        return inactiveMembers;
    }

    public void setInactiveMembers(List<String> inactiveMembers) {
        this.inactiveMembers = inactiveMembers;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }
}