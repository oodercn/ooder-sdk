package net.ooder.scene.skill.contribution;

import java.util.List;
import java.util.Map;

/**
 * URL导入请求
 *
 * @author ooder
 * @since 2.3
 */
public class UrlImportRequest {
    
    private String url;
    private String title;
    private List<String> tags;
    private Map<String, Object> metadata;
    private int timeout = 30000;
    private boolean followRedirects = true;
    private int maxContentLength = 1024 * 1024;
    
    public UrlImportRequest() {
    }
    
    public UrlImportRequest(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public boolean isFollowRedirects() {
        return followRedirects;
    }
    
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }
    
    public int getMaxContentLength() {
        return maxContentLength;
    }
    
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
}
