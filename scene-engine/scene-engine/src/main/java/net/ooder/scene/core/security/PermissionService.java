package net.ooder.scene.core.security;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 权限服务接口
 *
 * <p>所有操作前必须进行权限检查</p>
 */
public interface PermissionService {

    /**
     * 检查权限
     *
     * @param userId 用户ID
     * @param resource 资源类型
     * @param action 操作类型
     * @return 是否有权限
     */
    boolean checkPermission(String userId, String resource, String action);

    /**
     * 检查权限（带上下文）
     */
    PermissionCheckResult checkPermissionWithContext(
        OperationContext context,
        String resource,
        String action
    );

    /**
     * 获取用户权限列表
     */
    CompletableFuture<List<Permission>> getUserPermissions(String userId);

    /**
     * 获取角色权限列表
     */
    CompletableFuture<List<Permission>> getRolePermissions(String roleId);

    /**
     * 授予权限
     */
    CompletableFuture<Void> grantPermission(String roleId, Permission permission);

    /**
     * 撤销权限
     */
    CompletableFuture<Void> revokePermission(String roleId, String permissionId);

    /**
     * 添加权限变更监听器
     */
    void addPermissionChangeListener(PermissionChangeListener listener);
}

/**
 * 权限检查结果
 */
class PermissionCheckResult {
    private boolean allowed;
    private String denyReason;
    private String requiredPermission;
    private String userRole;
    private List<String> missingPermissions;

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    public String getDenyReason() { return denyReason; }
    public void setDenyReason(String denyReason) { this.denyReason = denyReason; }
    public String getRequiredPermission() { return requiredPermission; }
    public void setRequiredPermission(String requiredPermission) { this.requiredPermission = requiredPermission; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public List<String> getMissingPermissions() { return missingPermissions; }
    public void setMissingPermissions(List<String> missingPermissions) { this.missingPermissions = missingPermissions; }
}

/**
 * 权限变更监听器
 */
interface PermissionChangeListener {
    void onPermissionChanged(String roleId, Permission permission, boolean granted);
    void onRolePermissionsChanged(String roleId);
}
