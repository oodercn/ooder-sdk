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
     * 获取用户权限列表（SecurityPermission格式）
     */
    CompletableFuture<List<SecurityPermission>> getUserPermissions(String userId);

    /**
     * 获取用户权限列表（字符串格式）
     */
    default CompletableFuture<List<String>> getUserPermissionStrings(String userId) {
        return getUserPermissions(userId).thenApply(permissions ->
            permissions.stream()
                .map(p -> p.getResource() + ":" + p.getAction())
                .collect(java.util.stream.Collectors.toList())
        );
    }

    /**
     * 获取角色权限列表
     */
    CompletableFuture<List<SecurityPermission>> getRolePermissions(String roleId);

    /**
     * 授予权限
     */
    CompletableFuture<Void> grantPermission(String roleId, SecurityPermission permission);

    /**
     * 撤销权限
     */
    CompletableFuture<Void> revokePermission(String roleId, String permissionId);

    /**
     * 添加权限变更监听器
     */
    void addPermissionChangeListener(PermissionChangeListener listener);
}
