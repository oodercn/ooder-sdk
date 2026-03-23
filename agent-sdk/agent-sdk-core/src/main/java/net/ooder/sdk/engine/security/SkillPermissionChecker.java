package net.ooder.sdk.engine.security;

import net.ooder.sdk.engine.event.skill.SkillInvocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill 权限检查器
 *
 * <p>监听 SkillInvocationEvent，检查调用者是否有权限调用该 Skill</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
@Component
public class SkillPermissionChecker {

    private static final Logger log = LoggerFactory.getLogger(SkillPermissionChecker.class);

    /**
     * 权限缓存：Skill ID -> 允许的用户角色集合
     */
    private final Map<String, Set<String>> skillRolePermissions = new ConcurrentHashMap<>();

    /**
     * 权限缓存：Skill ID -> 允许的用户 ID 集合
     */
    private final Map<String, Set<String>> skillUserPermissions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("SkillPermissionChecker initialized");
        // 从本地配置文件加载权限配置
        loadPermissionsFromConfig();
    }
    
    /**
     * 从本地配置文件加载权限配置
     */
    private void loadPermissionsFromConfig() {
        try {
            // 加载角色权限配置
            Map<String, Set<String>> roleConfigs = loadRolePermissionsFromConfig();
            skillRolePermissions.putAll(roleConfigs);
            
            // 加载用户权限配置
            Map<String, Set<String>> userConfigs = loadUserPermissionsFromConfig();
            skillUserPermissions.putAll(userConfigs);
            
            log.info("Loaded permissions for {} skills (role-based) and {} skills (user-based)", 
                roleConfigs.size(), userConfigs.size());
                
        } catch (Exception e) {
            log.warn("Failed to load permissions from config: {}", e.getMessage());
        }
    }
    
    /**
     * 从配置加载角色权限
     */
    private Map<String, Set<String>> loadRolePermissionsFromConfig() {
        Map<String, Set<String>> configs = new ConcurrentHashMap<>();
        // 可以扩展为从配置文件、配置中心或数据库加载
        // 例如：从 application.yml 或 nacos 配置中心加载
        return configs;
    }
    
    /**
     * 从配置加载用户权限
     */
    private Map<String, Set<String>> loadUserPermissionsFromConfig() {
        Map<String, Set<String>> configs = new ConcurrentHashMap<>();
        // 可以扩展为从配置文件、配置中心或数据库加载
        return configs;
    }

    /**
     * 监听 Skill 调用事件
     *
     * <p>如果没有权限，调用 event.cancel() 取消调用</p>
     */
    @EventListener
    public void onSkillInvocation(SkillInvocationEvent event) {
        String skillId = event.getSkillId();
        String userId = event.getCallerInfo().getUserId();

        log.debug("Checking permission for user {} to invoke skill {}", userId, skillId);

        // 1. 系统用户始终允许
        if ("system".equals(userId)) {
            log.debug("System user allowed for all skills");
            return;
        }

        // 2. 检查用户级权限
        if (hasUserPermission(skillId, userId)) {
            log.debug("User {} has direct permission for skill {}", userId, skillId);
            return;
        }

        // 3. 检查角色级权限
        Set<String> userRoles = getUserRoles(userId);
        if (hasRolePermission(skillId, userRoles)) {
            log.debug("User {} has role permission for skill {}", userId, skillId);
            return;
        }

        // 4. 无权限，取消事件
        log.warn("User {} does not have permission to invoke skill {}, cancelling", userId, skillId);
        event.cancel("Permission denied: user " + userId + " cannot invoke skill " + skillId);
    }

    /**
     * 检查用户是否有直接权限
     */
    private boolean hasUserPermission(String skillId, String userId) {
        Set<String> allowedUsers = skillUserPermissions.get(skillId);
        return allowedUsers != null && allowedUsers.contains(userId);
    }

    /**
     * 检查用户角色是否有权限
     */
    private boolean hasRolePermission(String skillId, Set<String> userRoles) {
        Set<String> allowedRoles = skillRolePermissions.get(skillId);
        if (allowedRoles == null || allowedRoles.isEmpty()) {
            return false;
        }

        // 检查用户角色与允许角色的交集
        for (String role : userRoles) {
            if (allowedRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取用户角色
     *
     * <p>从用户服务或本地缓存获取用户角色</p>
     */
    private Set<String> getUserRoles(String userId) {
        Set<String> roles = new java.util.HashSet<>();
        
        try {
            // 1. 首先检查本地缓存
            Set<String> cachedRoles = userRoleCache.get(userId);
            if (cachedRoles != null && !cachedRoles.isEmpty()) {
                return cachedRoles;
            }
            
            // 2. 从配置中获取角色映射
            Map<String, Set<String>> roleMappings = getRoleMappingsFromConfig();
            Set<String> configRoles = roleMappings.get(userId);
            if (configRoles != null) {
                roles.addAll(configRoles);
            }
            
            // 3. 如果还是没有角色，添加默认角色
            if (roles.isEmpty()) {
                roles.add("user");
            }
            
            // 4. 缓存角色信息
            userRoleCache.put(userId, roles);
            
        } catch (Exception e) {
            log.warn("Failed to get roles for user [{}], using default role: {}", userId, e.getMessage());
            roles.add("user");
        }
        
        return roles;
    }
    
    /**
     * 用户角色缓存
     */
    private final Map<String, Set<String>> userRoleCache = new ConcurrentHashMap<>();
    
    /**
     * 从配置获取角色映射
     */
    private Map<String, Set<String>> getRoleMappingsFromConfig() {
        Map<String, Set<String>> mappings = new HashMap<>();
        
        // 可以从配置文件、数据库或用户服务获取
        // 这里提供一个简单的硬编码示例，实际应该从外部配置获取
        
        // 管理员角色
        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("admin");
        adminRoles.add("user");
        mappings.put("admin", adminRoles);
        
        // 开发者角色
        Set<String> devRoles = new HashSet<>();
        devRoles.add("developer");
        devRoles.add("user");
        mappings.put("developer", devRoles);
        
        return mappings;
    }
    
    /**
     * 清除用户角色缓存
     */
    public void clearUserRoleCache(String userId) {
        userRoleCache.remove(userId);
        log.debug("Cleared role cache for user: {}", userId);
    }
    
    /**
     * 清除所有用户角色缓存
     */
    public void clearAllUserRoleCache() {
        userRoleCache.clear();
        log.debug("Cleared all user role cache");
    }

    /**
     * 配置 Skill 的角色权限
     */
    public void configureSkillRolePermission(String skillId, Set<String> roles) {
        skillRolePermissions.put(skillId, roles);
        log.info("Configured role permissions for skill {}: {}", skillId, roles);
    }

    /**
     * 配置 Skill 的用户权限
     */
    public void configureSkillUserPermission(String skillId, Set<String> userIds) {
        skillUserPermissions.put(skillId, userIds);
        log.info("Configured user permissions for skill {}: {}", skillId, userIds);
    }

    /**
     * 清除 Skill 的权限配置
     */
    public void clearSkillPermissions(String skillId) {
        skillRolePermissions.remove(skillId);
        skillUserPermissions.remove(skillId);
        log.info("Cleared permissions for skill {}", skillId);
    }
}
