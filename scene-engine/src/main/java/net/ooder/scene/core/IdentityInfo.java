package net.ooder.scene.core;

/**
 * 身份信息类
 *
 * <p>封装用户的完整身份信息，从 Session 中提取并转换为标准格式</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class IdentityInfo {

    /** 用户ID */
    private String userId;
    /** 用户名 */
    private String username;
    /** 会话ID */
    private String sessionId;
    /** 域名 */
    private String domain;
    /** 客户端IP */
    private String clientIp;
    /** 用户代理 */
    private String userAgent;
    /** 状态 */
    private String status;
    /** 创建时间 */
    private long createdAt;
    /** 过期时间 */
    private long expiresAt;

    public IdentityInfo() {}

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * 检查身份是否有效（未过期）
     * @return true 如果有效
     */
    public boolean isValid() {
        return System.currentTimeMillis() < expiresAt;
    }

    @Override
    public String toString() {
        return "IdentityInfo{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", domain='" + domain + '\'' +
                ", status='" + status + '\'' +
                ", valid=" + isValid() +
                '}';
    }
}
