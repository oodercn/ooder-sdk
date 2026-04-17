package net.ooder.skill.role.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String roleId;
    private String name;
    private String code;
    private String description;
    private RoleType type;
    private RoleStatus status;
    private String orgId;
    private List<String> permissions;
    private List<String> users;
    private long createTime;
    private long updateTime;

    public Role() {
        this.permissions = new ArrayList<String>();
        this.users = new ArrayList<String>();
        this.status = RoleStatus.ACTIVE;
        this.type = RoleType.CUSTOM;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RoleType getType() { return type; }
    public void setType(RoleType type) { this.type = type; }
    public RoleStatus getStatus() { return status; }
    public void setStatus(RoleStatus status) { this.status = status; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<String> getUsers() { return users; }
    public void setUsers(List<String> users) { this.users = users; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}
