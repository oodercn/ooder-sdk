package net.ooder.scene.skill.permission;

/**
 * 知识库权限
 *
 * @author ooder
 * @since 2.3
 */
public class KbPermission {
    
    private String id;
    private String kbId;
    private String kbName;
    private String userId;
    private String userName;
    private Permission permission;
    private String grantedBy;
    private long grantedAt;
    private long expiresAt;
    
    public KbPermission() {
    }
    
    public KbPermission(String kbId, String userId, Permission permission) {
        this.kbId = kbId;
        this.userId = userId;
        this.permission = permission;
        this.grantedAt = System.currentTimeMillis();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    public String getGrantedBy() {
        return grantedBy;
    }
    
    public void setGrantedBy(String grantedBy) {
        this.grantedBy = grantedBy;
    }
    
    public long getGrantedAt() {
        return grantedAt;
    }
    
    public void setGrantedAt(long grantedAt) {
        this.grantedAt = grantedAt;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isValid() {
        return !isExpired();
    }
}
