package net.ooder.scene.skill.permission;

/**
 * 授权请求
 *
 * @author ooder
 * @since 2.3
 */
public class GrantPermissionRequest {
    
    private String kbId;
    private String userId;
    private Permission permission;
    private String grantedBy;
    private long expiresIn;
    
    public GrantPermissionRequest() {
    }
    
    public GrantPermissionRequest(String kbId, String userId, Permission permission) {
        this.kbId = kbId;
        this.userId = userId;
        this.permission = permission;
    }
    
    public GrantPermissionRequest(String kbId, String userId, Permission permission, String grantedBy) {
        this.kbId = kbId;
        this.userId = userId;
        this.permission = permission;
        this.grantedBy = grantedBy;
    }
    
    public String getKbId() {
        return kbId;
    }
    
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
