package net.ooder.skill.common.spi.orgsync;

import java.util.List;

public class OrgDepartmentInfo {
    
    private String departmentId;
    private String name;
    private String parentId;
    private String managerId;
    private int memberCount;
    private List<String> memberIds;
    private int level;
    
    public OrgDepartmentInfo() {}
    
    public OrgDepartmentInfo(String departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
    }
    
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
