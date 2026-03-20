package net.ooder.scene.org;

import java.util.List;

/**
 * 组织-部门
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class OrgDepartment {
    
    private String departmentId;
    private String companyId;
    private String name;
    private String description;
    private String parentId;
    private String managerId;
    private List<String> memberIds;
    private int level;
    private String fullPath;
    private long createTime;
    private long updateTime;
    
    public OrgDepartment() {}
    
    public OrgDepartment(String departmentId, String companyId, String name) {
        this.departmentId = departmentId;
        this.companyId = companyId;
        this.name = name;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }
    
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getFullPath() { return fullPath; }
    public void setFullPath(String fullPath) { this.fullPath = fullPath; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}
