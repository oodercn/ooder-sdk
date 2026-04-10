package net.ooder.skill.common.spi.orgsync;

import java.util.List;

public class OrgUserInfo {
    
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String departmentId;
    private String title;
    private String avatar;
    private List<String> departmentIds;
    private boolean active;
    
    public OrgUserInfo() {}
    
    public OrgUserInfo(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.active = true;
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public List<String> getDepartmentIds() { return departmentIds; }
    public void setDepartmentIds(List<String> departmentIds) { this.departmentIds = departmentIds; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
