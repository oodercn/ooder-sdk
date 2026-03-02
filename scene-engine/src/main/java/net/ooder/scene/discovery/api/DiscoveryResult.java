package net.ooder.scene.discovery.api;

import java.util.List;

/**
 * 发现结果
 * 
 * @author ooder Team
 * @since 2.3
 */
public class DiscoveryResult {
    
    private List<DiscoveryService.SkillInfo> skills;
    private int totalCount;
    private int page;
    private int pageSize;
    private String source;
    private long timestamp;
    private String errorMessage;
    private boolean fromCache;
    
    public List<DiscoveryService.SkillInfo> getSkills() { return skills; }
    public void setSkills(List<DiscoveryService.SkillInfo> skills) { this.skills = skills; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isFromCache() { return fromCache; }
    public void setFromCache(boolean fromCache) { this.fromCache = fromCache; }
}
