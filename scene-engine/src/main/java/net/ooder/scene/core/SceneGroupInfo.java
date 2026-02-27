package net.ooder.scene.core;

import java.util.List;

/**
 * 场景组信息
 */
public class SceneGroupInfo {
    private String groupId;
    private String sceneId;
    private String name;
    private String description;
    private String status;
    private String primaryMember;
    private List<SceneMemberInfo> members;
    private int memberCount;
    private long createdAt;
    private long updatedAt;

    public SceneGroupInfo() {}

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrimaryMember() { return primaryMember; }
    public void setPrimaryMember(String primaryMember) { this.primaryMember = primaryMember; }
    public List<SceneMemberInfo> getMembers() { return members; }
    public void setMembers(List<SceneMemberInfo> members) { this.members = members; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
