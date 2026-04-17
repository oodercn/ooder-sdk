package net.ooder.skill.role.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String permissionId;
    private String name;
    private String code;
    private String description;
    private String resource;
    private String action;
    private PermissionType type;
    private List<String> children;
    private String parentId;

    public Permission() {
        this.children = new ArrayList<String>();
    }

    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public PermissionType getType() { return type; }
    public void setType(PermissionType type) { this.type = type; }
    public List<String> getChildren() { return children; }
    public void setChildren(List<String> children) { this.children = children; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}
