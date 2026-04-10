package net.ooder.skill.common.model;

import java.util.List;

public class RoleConfig {

    private String id;
    private String name;
    private String description;
    private String icon;
    private String orgRole;
    private List<String> permissions;

    public RoleConfig() {}

    public RoleConfig(String id, String name, String description, 
                      String icon, String orgRole, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.orgRole = orgRole;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOrgRole() {
        return orgRole;
    }

    public void setOrgRole(String orgRole) {
        this.orgRole = orgRole;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
