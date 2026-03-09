package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityContext {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户角色
     */
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    /**
     * 权限列表
     */
    @Builder.Default
    private List<String> permissions = new ArrayList<>();

    /**
     * 认证令牌
     */
    private String authToken;

    /**
     * 认证类型
     */
    private AuthType authType;

    /**
     * 安全级别
     */
    private SecurityLevel securityLevel;

    /**
     * 安全策略
     */
    @Builder.Default
    private Map<String, Object> securityPolicies = new HashMap<>();

    /**
     * 认证类型枚举
     */
    public enum AuthType {
        NONE,
        API_KEY,
        OAUTH2,
        JWT,
        BASIC_AUTH
    }

    /**
     * 安全级别枚举
     */
    public enum SecurityLevel {
        PUBLIC,
        INTERNAL,
        CONFIDENTIAL,
        RESTRICTED
    }

    /**
     * 检查权限
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("*");
    }

    /**
     * 检查角色
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
