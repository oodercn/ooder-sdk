package net.ooder.scene.skill.share;

/**
 * 分享更新请求
 *
 * @author ooder
 * @since 2.3
 */
public class ShareUpdateRequest {
    
    private String password;
    private long expiresIn;
    private int maxAccessCount;
    private Boolean active;
    
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
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
}
