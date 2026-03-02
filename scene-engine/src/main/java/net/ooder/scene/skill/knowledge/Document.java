package net.ooder.scene.skill.knowledge;

import java.util.Map;

/**
 * 知识库文档
 *
 * @author ooder
 * @since 2.3
 */
public class Document {
    
    /** 文档ID */
    private String docId;
    
    /** 知识库ID */
    private String kbId;
    
    /** 文档标题 */
    private String title;
    
    /** 文档内容 */
    private String content;
    
    /** 文档类型 */
    private String type;
    
    /** 文档来源 */
    private String source;
    
    /** 创建时间 */
    private long createdAt;
    
    /** 更新时间 */
    private long updatedAt;
    
    /** 索引状态 */
    private String indexStatus;
    
    /** 扩展属性 */
    private Map<String, Object> metadata;
    
    public Document() {}
    
    // Getters and Setters
    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    public String getIndexStatus() { return indexStatus; }
    public void setIndexStatus(String indexStatus) { this.indexStatus = indexStatus; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
