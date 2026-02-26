package net.ooder.sdk.a2a.capability;

/**
 * A2A认证类型枚举
 *
 * <p>定义了A2A通信中支持的各种认证方式</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public enum AuthType {

    /**
     * 无认证
     */
    NONE("none", "无认证"),

    /**
     * API密钥认证
     */
    API_KEY("api_key", "API密钥"),

    /**
     * JWT令牌认证
     */
    JWT("jwt", "JWT令牌"),

    /**
     * OAuth2认证
     */
    OAUTH2("oauth2", "OAuth2"),

    /**
     * 基础认证（用户名密码）
     */
    BASIC("basic", "基础认证"),

    /**
     * 自定义认证
     */
    CUSTOM("custom", "自定义认证");

    private final String code;
    private final String description;

    AuthType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取认证类型代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取认证类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取认证类型
     *
     * @param code 认证类型代码
     * @return 对应的AuthType，如果找不到则返回CUSTOM
     */
    public static AuthType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return NONE;
        }
        for (AuthType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        return CUSTOM;
    }

    /**
     * 是否需要认证
     */
    public boolean requiresAuth() {
        return this != NONE;
    }
}
