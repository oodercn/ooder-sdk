package net.ooder.scene.core.security;

/**
 * 权限变更监听器
 */
public interface PermissionChangeListener {
    void onPermissionChanged(String roleId, SecurityPermission permission, boolean granted);
    void onRolePermissionsChanged(String roleId);
}
