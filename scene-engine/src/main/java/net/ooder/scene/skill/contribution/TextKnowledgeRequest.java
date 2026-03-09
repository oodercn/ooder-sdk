package net.ooder.scene.skill.contribution;

import java.util.List;
import java.util.Map;

/**
 * 文本知识请求
 *
 * @author ooder
 * @since 2.3
 */
public class TextKnowledgeRequest {
    
    private String title;
    private String content;
    private List<String> tags;
    private Map<String, Object> metadata;
    
    public TextKnowledgeRequest() {
    }
    
    public TextKnowledgeRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
}
