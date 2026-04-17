package net.ooder.skill.role.dto;

import java.util.List;

public class PermissionDTO {
    
    private String permissionId;
    private String name;
    private String code;
    private String description;
    private String resource;
    private String action;
    private String type;
    private String parentId;
    private List<PermissionDTO> children;

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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public List<PermissionDTO> getChildren() { return children; }
    public void setChildren(List<PermissionDTO> children) { this.children = children; }
}
