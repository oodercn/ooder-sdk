package net.ooder.scene.skill.share;

/**
 * 分享创建请求
 *
 * @author ooder
 * @since 2.3
 */
public class ShareCreateRequest {
    
    private String kbId;
    private String userId;
    private String password;
    private long expiresIn;
    private int maxAccessCount;
    
    public ShareCreateRequest() {
    }
    
    public ShareCreateRequest(String kbId, String userId) {
        this.kbId = kbId;
        this.userId = userId;
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public int getMaxAccessCount() {
        return maxAccessCount;
    }
    
    public void setMaxAccessCount(int maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }
}
