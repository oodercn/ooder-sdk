package net.ooder.scene.skill.knowledge;

import java.util.List;
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

    /** 可见性 */
    private String visibility;

    /** 分块大小 */
    private Integer chunkSize;

    /** 分块重叠 */
    private Integer chunkOverlap;

    /** 标签 */
    private List<String> tags;

    /** 扩展属性 */
    private Map<String, Object> metadata;

    public KnowledgeBaseUpdateRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }

    public Integer getChunkOverlap() { return chunkOverlap; }
    public void setChunkOverlap(Integer chunkOverlap) { this.chunkOverlap = chunkOverlap; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
