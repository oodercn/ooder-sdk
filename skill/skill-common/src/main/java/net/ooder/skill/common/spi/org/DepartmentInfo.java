package net.ooder.skill.common.spi.org;

import java.util.Map;

public class DepartmentInfo {
    
    private String departmentId;
    private String departmentName;
    private String parentId;
    private int level;
    private String managerId;
    private int memberCount;
    private Map<String, Object> attributes;
    
    public DepartmentInfo() {}
    
    public DepartmentInfo(String departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }
    
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
