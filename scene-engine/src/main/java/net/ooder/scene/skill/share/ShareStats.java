package net.ooder.scene.skill.share;

/**
 * 分享统计
 *
 * @author ooder
 * @since 2.3
 */
public class ShareStats {
    
    private String shareId;
    private int totalAccessCount;
    private int uniqueVisitorCount;
    private long lastAccessTime;
    private long createdAt;
    
    public ShareStats() {
    }
    
    public ShareStats(String shareId) {
        this.shareId = shareId;
    }
    
    public String getShareId() {
        return shareId;
    }
    
    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
    
    public int getTotalAccessCount() {
        return totalAccessCount;
    }
    
    public void setTotalAccessCount(int totalAccessCount) {
        this.totalAccessCount = totalAccessCount;
    }
    
    public int getUniqueVisitorCount() {
        return uniqueVisitorCount;
    }
    
    public void setUniqueVisitorCount(int uniqueVisitorCount) {
        this.uniqueVisitorCount = uniqueVisitorCount;
    }
    
    public long getLastAccessTime() {
        return lastAccessTime;
    }
    
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
