package net.ooder.sdk.engine.security;

import net.ooder.sdk.engine.event.skill.SkillInvocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        // TODO: 从配置中心或数据库加载权限配置
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
     * <p>TODO: 从用户服务获取</p>
     */
    private Set<String> getUserRoles(String userId) {
        // 简化实现，实际应该从用户服务获取
        // 默认所有用户都有 'user' 角色
        Set<String> roles = new java.util.HashSet<>();
        roles.add("user");
        return roles;
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
