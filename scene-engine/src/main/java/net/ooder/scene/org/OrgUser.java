package net.ooder.scene.org;

import java.util.List;

/**
 * 组织-用户
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class OrgUser {
    
    private String userId;
    private String companyId;
    private String departmentId;
    private String departmentName;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String title;
    private String avatar;
    private List<String> permissions;
    private long createTime;
    private long updateTime;
    private boolean active;
    
    public OrgUser() {}
    
    public OrgUser(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
        this.active = true;
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
