package net.ooder.scene.skills.org;

import java.util.List;

/**
 * 组织信息
 */
public class OrgInfo {
    private String orgId;
    private String name;
    private String brief;
    private String parentId;
    private String leaderId;
    private String leaderName;
    private Integer tier;
    private Integer index;
    private List<OrgInfo> children;
    private int memberCount;

    public OrgInfo() {}

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrief() { return brief; }
    public void setBrief(String brief) { this.brief = brief; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
    public Integer getTier() { return tier; }
    public void setTier(Integer tier) { this.tier = tier; }
    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
    public List<OrgInfo> getChildren() { return children; }
    public void setChildren(List<OrgInfo> children) { this.children = children; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public boolean isRoot() {
        return parentId == null || parentId.isEmpty();
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}
