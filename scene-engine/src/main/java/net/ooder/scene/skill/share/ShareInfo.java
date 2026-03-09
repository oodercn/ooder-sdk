package net.ooder.scene.skill.share;

/**
 * 分享信息
 *
 * @author ooder
 * @since 2.3
 */
public class ShareInfo {
    
    private String shareId;
    private String shareCode;
    private String kbId;
    private String kbName;
    private String creatorId;
    private String creatorName;
    private String password;
    private boolean passwordProtected;
    private long expiresAt;
    private int maxAccessCount;
    private int accessCount;
    private long createdAt;
    private long updatedAt;
    private boolean active;
    
    public ShareInfo() {
    }
    
    public String getShareId() {
        return shareId;
    }
    
    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
    
    public String getShareCode() {
        return shareCode;
    }
    
    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }
    
    public String getKbId() {
        return kbId;
    }
    
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }
    
    public String getKbName() {
        return kbName;
    }
    
    public void setKbName(String kbName) {
        this.kbName = kbName;
    }
    
    public String getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.passwordProtected = password != null && !password.isEmpty();
    }
    
    public boolean isPasswordProtected() {
        return passwordProtected;
    }
    
    public void setPasswordProtected(boolean passwordProtected) {
        this.passwordProtected = passwordProtected;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public int getMaxAccessCount() {
        return maxAccessCount;
    }
    
    public void setMaxAccessCount(int maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }
    
    public int getAccessCount() {
        return accessCount;
    }
    
    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isAccessLimitReached() {
        return maxAccessCount > 0 && accessCount >= maxAccessCount;
    }
    
    public boolean isValid() {
        return active && !isExpired() && !isAccessLimitReached();
    }
}
