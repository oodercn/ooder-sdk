package net.ooder.skill.role.dto;

import java.util.List;

public class RoleDTO {
    
    private String id;
    private String roleId;
    private String name;
    private String code;
    private String description;
    private String icon;
    private String orgRole;
    private String defaultUsername;
    private String defaultPassword;
    private String type;
    private String status;
    private String orgId;
    private List<String> permissions;
    private List<String> menuIds;
    private int userCount;
    private long createTime;
    private long updateTime;

    public String getId() { return id != null ? id : roleId; }
    public void setId(String id) { this.id = id; this.roleId = id; }
    public String getRoleId() { return roleId != null ? roleId : id; }
    public void setRoleId(String roleId) { this.roleId = roleId; this.id = roleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getOrgRole() { return orgRole; }
    public void setOrgRole(String orgRole) { this.orgRole = orgRole; }
    public String getDefaultUsername() { return defaultUsername; }
    public void setDefaultUsername(String defaultUsername) { this.defaultUsername = defaultUsername; }
    public String getDefaultPassword() { return defaultPassword; }
    public void setDefaultPassword(String defaultPassword) { this.defaultPassword = defaultPassword; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<String> getMenuIds() { return menuIds; }
    public void setMenuIds(List<String> menuIds) { this.menuIds = menuIds; }
    public int getUserCount() { return userCount; }
    public void setUserCount(int userCount) { this.userCount = userCount; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}
