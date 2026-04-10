package net.ooder.skill.common.spi.user;

import java.util.Map;

public class UserInfo {
    
    private String userId;
    private String username;
    private String displayName;
    private String email;
    private String phone;
    private String avatar;
    private String departmentId;
    private String departmentName;
    private Map<String, Object> attributes;
    
    public UserInfo() {}
    
    public UserInfo(String userId, String username, String displayName, String email) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
