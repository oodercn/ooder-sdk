package net.ooder.sdk.api.security;

/**
 * Token Information（泛型版本）
 *
 * @param <C> 声明类型
 * @author ooder Team
 * @since 2.3
 */
public class TokenInfo<C> {

    private String subject;
    private String issuer;
    private long issuedAt;
    private long expiresAt;
    private boolean valid;
    private String error;
    /** Token 声明信息 */
    private java.util.Map<String, C> claims;

    public TokenInfo() {
        this.claims = new java.util.HashMap<String, C>();
    }
    
    /**
     * 创建通用 TokenInfo（向后兼容）
     */
    public static TokenInfo<Object> createGeneric() {
        return new TokenInfo<>();
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public long getIssuedAt() { return issuedAt; }
    public void setIssuedAt(long issuedAt) { this.issuedAt = issuedAt; }

    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    /**
     * 获取 Token 声明
     * @return 声明映射
     */
    public java.util.Map<String, C> getClaims() { return claims; }
    /**
     * 设置 Token 声明
     * @param claims 声明映射
     */
    public void setClaims(java.util.Map<String, C> claims) { this.claims = claims; }

    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }
}
