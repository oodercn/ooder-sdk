package net.ooder.scene.session;

/**
 * Session信息类
 * 存储用户会话的完整信息，包括身份认证、过期时间和状态
 *
 * @author ooder
 * @since 2.3
 */
public class SessionInfo {
    /** 会话ID */
    private String sessionId;
    /** 用户ID */
    private String userId;
    /** 用户名 */
    private String username;
    /** 访问令牌 */
    private String token;
    /** 刷新令牌 */
    private String refreshToken;
    /** 域名 */
    private String domain;
    /** 客户端IP */
    private String clientIp;
    /** 用户代理 */
    private String userAgent;
    /** 创建时间 */
    private long createdAt;
    /** 过期时间 */
    private long expiresAt;
    /** 最后活跃时间 */
    private long lastActiveAt;
    /** 状态 */
    private String status;

    /**
     * 默认构造函数
     */
    public SessionInfo() {}

    /**
     * 获取会话ID
     * @return 会话ID
     */
    public String getSessionId() { return sessionId; }

    /**
     * 设置会话ID
     * @param sessionId 会话ID
     */
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public String getUserId() { return userId; }

    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() { return username; }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * 获取访问令牌
     * @return 访问令牌
     */
    public String getToken() { return token; }

    /**
     * 设置访问令牌
     * @param token 访问令牌
     */
    public void setToken(String token) { this.token = token; }

    /**
     * 获取刷新令牌
     * @return 刷新令牌
     */
    public String getRefreshToken() { return refreshToken; }

    /**
     * 设置刷新令牌
     * @param refreshToken 刷新令牌
     */
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    /**
     * 获取域名
     * @return 域名
     */
    public String getDomain() { return domain; }

    /**
     * 设置域名
     * @param domain 域名
     */
    public void setDomain(String domain) { this.domain = domain; }

    /**
     * 获取客户端IP
     * @return 客户端IP
     */
    public String getClientIp() { return clientIp; }

    /**
     * 设置客户端IP
     * @param clientIp 客户端IP
     */
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    /**
     * 获取用户代理
     * @return 用户代理
     */
    public String getUserAgent() { return userAgent; }

    /**
     * 设置用户代理
     * @param userAgent 用户代理
     */
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    /**
     * 获取创建时间
     * @return 创建时间
     */
    public long getCreatedAt() { return createdAt; }

    /**
     * 设置创建时间
     * @param createdAt 创建时间
     */
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * 获取过期时间
     * @return 过期时间
     */
    public long getExpiresAt() { return expiresAt; }

    /**
     * 设置过期时间
     * @param expiresAt 过期时间
     */
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }

    /**
     * 获取最后活跃时间
     * @return 最后活跃时间
     */
    public long getLastActiveAt() { return lastActiveAt; }

    /**
     * 设置最后活跃时间
     * @param lastActiveAt 最后活跃时间
     */
    public void setLastActiveAt(long lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    /**
     * 获取状态
     * @return 状态
     */
    public String getStatus() { return status; }

    /**
     * 设置状态
     * @param status 状态
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 检查会话是否已过期
     * @return true表示已过期
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    /**
     * 检查会话是否处于活跃状态
     * @return true表示活跃
     */
    public boolean isActive() {
        return "ACTIVE".equals(status) && !isExpired();
    }
}
