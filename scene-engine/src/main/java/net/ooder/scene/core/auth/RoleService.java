package net.ooder.scene.core.auth;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 角色服务
 *
 * <p>提供统一的角色定义和权限管理</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@Service
public class RoleService {

    /**
     * 获取所有预定义角色
     *
     * @return 角色列表
     */
    public List<Role> getAllRoles() {
        return Arrays.asList(
            new Role(
                "installer",
                "系统安装者",
                "负责系统初始化和安装配置",
                Arrays.asList(
                    "skill:install",
                    "skill:view",
                    "system:init",
                    "config:manage",
                    "user:create"
                ),
                100
            ),
            new Role(
                "admin",
                "系统管理员",
                "负责系统管理和维护",
                Arrays.asList(
                    "capability:discover",
                    "capability:install",
                    "capability:configure",
                    "skill:manage",
                    "user:manage",
                    "system:monitor",
                    "audit:view",
                    "config:system"
                ),
                80
            ),
            new Role(
                "leader",
                "主导者",
                "负责场景激活和管理",
                Arrays.asList(
                    "scene:activate",
                    "scene:manage",
                    "scene:configure",
                    "task:create",
                    "task:assign",
                    "collaborator:invite",
                    "skill:activate"
                ),
                60
            ),
            new Role(
                "collaborator",
                "协作者",
                "参与任务执行和协作",
                Arrays.asList(
                    "task:view",
                    "task:execute",
                    "scene:view",
                    "document:edit",
                    "comment:create",
                    "skill:use"
                ),
                40
            )
        );
    }

    /**
     * 根据ID获取角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    public Role getRoleById(String roleId) {
        return getAllRoles().stream()
            .filter(role -> role.getId().equals(roleId))
            .findFirst()
            .orElse(null);
    }

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public List<String> getRolePermissions(String roleId) {
        Role role = getRoleById(roleId);
        return role != null ? role.getPermissions() : null;
    }

    /**
     * 检查角色是否存在
     *
     * @param roleId 角色ID
     * @return 是否存在
     */
    public boolean roleExists(String roleId) {
        return getAllRoles().stream()
            .anyMatch(role -> role.getId().equals(roleId));
    }
}
