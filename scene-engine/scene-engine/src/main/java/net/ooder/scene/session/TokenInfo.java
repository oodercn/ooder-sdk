package net.ooder.scene.session;

import java.util.Map;

/**
 * Token信息
 */
public class TokenInfo {
    private String token;
    private String refreshToken;
    private String subject;
    private long issuedAt;
    private long expiresAt;
    private long refreshExpiresAt;
    private String issuer;
    private String audience;
    private Map<String, Object> claims;

    public TokenInfo() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public long getIssuedAt() { return issuedAt; }
    public void setIssuedAt(long issuedAt) { this.issuedAt = issuedAt; }
    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
    public long getRefreshExpiresAt() { return refreshExpiresAt; }
    public void setRefreshExpiresAt(long refreshExpiresAt) { this.refreshExpiresAt = refreshExpiresAt; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public Map<String, Object> getClaims() { return claims; }
    public void setClaims(Map<String, Object> claims) { this.claims = claims; }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isRefreshExpired() {
        return System.currentTimeMillis() > refreshExpiresAt;
    }
}
