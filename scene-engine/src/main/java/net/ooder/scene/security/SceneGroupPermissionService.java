package net.ooder.scene.security;

import net.ooder.scene.participant.Participant;

import java.util.List;

/**
 * 场景组权限服务接口
 * 
 * <p>提供场景组级别的权限控制能力。</p>
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>检查用户在场景组中的权限</li>
 *   <li>基于角色的权限管理</li>
 *   <li>操作级别的权限控制</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 检查权限
 * boolean canInvite = permissionService.checkPermission(
 *     "sg-001", "user-001", "PARTICIPANT_INVITE"
 * );
 * 
 * // 获取用户角色
 * Participant.Role role = permissionService.getRole("sg-001", "user-001");
 * </pre>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SceneGroupPermissionService {
    
    /**
     * 检查用户在场景组中的权限
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean checkPermission(String sceneGroupId, String userId, String permission);
    
    /**
     * 检查用户在场景组中的权限
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @param permission 权限枚举
     * @return 是否有权限
     */
    boolean checkPermission(String sceneGroupId, String userId, SceneGroupPermission permission);
    
    /**
     * 获取用户在场景组中的角色
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @return 角色枚举
     */
    Participant.Role getRole(String sceneGroupId, String userId);
    
    /**
     * 获取角色的权限列表
     * 
     * @param role 角色枚举
     * @return 权限列表
     */
    List<String> getRolePermissions(Participant.Role role);
    
    /**
     * 设置角色权限
     * 
     * @param role 角色枚举
     * @param permissions 权限列表
     */
    void setRolePermissions(Participant.Role role, List<String> permissions);
    
    /**
     * 检查用户是否可以执行操作
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @param operation 操作类型
     * @param resource 资源类型
     * @return 是否可以执行
     */
    boolean canExecute(String sceneGroupId, String userId, String operation, String resource);
    
    /**
     * 获取用户可访问的场景组列表
     * 
     * @param userId 用户ID
     * @return 场景组ID列表
     */
    List<String> getAccessibleSceneGroups(String userId);
    
    /**
     * 检查用户是否是场景组成员
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @return 是否是成员
     */
    boolean isMember(String sceneGroupId, String userId);
    
    /**
     * 检查用户是否是场景组管理员
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @return 是否是管理员
     */
    boolean isManager(String sceneGroupId, String userId);
    
    /**
     * 检查用户是否是场景组所有者
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @return 是否是所有者
     */
    boolean isOwner(String sceneGroupId, String userId);
}
