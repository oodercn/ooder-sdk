package net.ooder.scene.discovery.api;

import java.util.List;
import java.util.Map;

/**
 * 发现请求
 * 
 * @author ooder Team
 * @since 2.3
 */
public class DiscoveryRequest {
    
    private String source;
    private String sceneId;
    private String category;
    private String subCategory;
    private List<String> tags;
    private String keyword;
    private String version;
    private Map<String, String> filters;
    private boolean includeInstalled;
    private boolean includeCached;
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Map<String, String> getFilters() { return filters; }
    public void setFilters(Map<String, String> filters) { this.filters = filters; }
    public boolean isIncludeInstalled() { return includeInstalled; }
    public void setIncludeInstalled(boolean includeInstalled) { this.includeInstalled = includeInstalled; }
    public boolean isIncludeCached() { return includeCached; }
    public void setIncludeCached(boolean includeCached) { this.includeCached = includeCached; }
}
