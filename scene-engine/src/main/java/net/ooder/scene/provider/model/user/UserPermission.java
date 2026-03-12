package net.ooder.scene.provider.model.user;

/**
 * 用户权限实体
 *
 * <p>原 Permission 类重命名，避免与 skill/permission/Permission 枚举冲突</p>
 *
 * @author Ooder Team
 * @since 2.3
 */
public class UserPermission {

    private String permissionId;
    private String name;
    private String resource;
    private String action;
    private String description;
    private long createdAt;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
