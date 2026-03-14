package net.ooder.scene.core.auth;

import java.util.List;

/**
 * 统一角色定义
 *
 * <p>统一后的角色体系：installer, admin, leader, collaborator</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
public class Role {

    private String id;
    private String name;
    private String description;
    private List<String> permissions;
    private int level;

    public Role() {}

    public Role(String id, String name, String description, List<String> permissions, int level) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.level = level;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
