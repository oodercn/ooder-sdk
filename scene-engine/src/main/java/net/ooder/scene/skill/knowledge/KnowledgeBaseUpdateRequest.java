package net.ooder.scene.skill.knowledge;

import java.util.Map;

/**
 * 知识库更新请求
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeBaseUpdateRequest {
    
    /** 知识库名称 */
    private String name;
    
    /** 知识库描述 */
    private String description;
    
    /** 扩展属性 */
    private Map<String, Object> metadata;
    
    public KnowledgeBaseUpdateRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
