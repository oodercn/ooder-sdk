package net.ooder.scene.skill.contribution;

/**
 * 用户贡献统计
 *
 * @author ooder
 * @since 2.3
 */
public class ContributionStats {
    
    private String userId;
    private int totalDocuments;
    private int totalFiles;
    private int totalTexts;
    private int totalUrls;
    private long totalSize;
    private long lastContributionTime;
    
    public ContributionStats() {
    }
    
    public ContributionStats(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public int getTotalDocuments() {
        return totalDocuments;
    }
    
    public void setTotalDocuments(int totalDocuments) {
        this.totalDocuments = totalDocuments;
    }
    
    public int getTotalFiles() {
        return totalFiles;
    }
    
    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }
    
    public int getTotalTexts() {
        return totalTexts;
    }
    
    public void setTotalTexts(int totalTexts) {
        this.totalTexts = totalTexts;
    }
    
    public int getTotalUrls() {
        return totalUrls;
    }
    
    public void setTotalUrls(int totalUrls) {
        this.totalUrls = totalUrls;
    }
    
    public long getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    
    public long getLastContributionTime() {
        return lastContributionTime;
    }
    
    public void setLastContributionTime(long lastContributionTime) {
        this.lastContributionTime = lastContributionTime;
    }
    
    public void incrementFiles() {
        this.totalFiles++;
        this.totalDocuments++;
    }
    
    public void incrementTexts() {
        this.totalTexts++;
        this.totalDocuments++;
    }
    
    public void incrementUrls() {
        this.totalUrls++;
        this.totalDocuments++;
    }
    
    public void addSize(long size) {
        this.totalSize += size;
    }
}
