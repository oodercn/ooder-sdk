package net.ooder.scene.skill.permission;

import java.util.List;

/**
 * 权限管理服务接口
 *
 * <p>提供知识库权限的完整管理能力，包括：</p>
 * <ul>
 *   <li>权限检查</li>
 *   <li>权限授予/撤销</li>
 *   <li>权限继承</li>
 *   <li>权限审计</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 权限管理</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface PermissionService {
    
    /**
     * 检查用户是否有指定权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permission 权限类型
     * @return 是否有权限
     */
    boolean hasPermission(String kbId, String userId, Permission permission);
    
    /**
     * 检查用户是否有任意一个指定权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permissions 权限类型列表
     * @return 是否有权限
     */
    boolean hasAnyPermission(String kbId, String userId, Permission... permissions);
    
    /**
     * 获取用户在知识库的权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @return 权限信息
     */
    KbPermission getPermission(String kbId, String userId);
    
    /**
     * 授予权限
     *
     * @param request 授权请求
     * @return 授权结果
     */
    KbPermission grantPermission(GrantPermissionRequest request);
    
    /**
     * 批量授予权限
     *
     * @param requests 授权请求列表
     * @return 授权结果列表
     */
    List<KbPermission> grantPermissions(List<GrantPermissionRequest> requests);
    
    /**
     * 撤销权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     */
    void revokePermission(String kbId, String userId);
    
    /**
     * 批量撤销权限
     *
     * @param kbId 知识库ID
     * @param userIds 用户ID列表
     */
    void revokePermissions(String kbId, List<String> userIds);
    
    /**
     * 列出知识库的所有权限
     *
     * @param kbId 知识库ID
     * @return 权限列表
     */
    List<KbPermission> listPermissions(String kbId);
    
    /**
     * 列出用户拥有的所有知识库权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<KbPermission> listUserPermissions(String userId);
    
    /**
     * 转移知识库所有权
     *
     * @param kbId 知识库ID
     * @param currentOwnerId 当前所有者ID
     * @param newOwnerId 新所有者ID
     */
    void transferOwnership(String kbId, String currentOwnerId, String newOwnerId);
}
