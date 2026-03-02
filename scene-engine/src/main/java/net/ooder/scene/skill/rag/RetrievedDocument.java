package net.ooder.scene.skill.rag;

import java.util.Map;

/**
 * 检索到的文档
 *
 * @author ooder
 * @since 2.3
 */
public class RetrievedDocument {
    
    /** 文档ID */
    private String docId;
    
    /** 知识库ID */
    private String kbId;
    
    /** 文档标题 */
    private String title;
    
    /** 内容片段 */
    private String content;
    
    /** 相似度分数 */
    private float score;
    
    /** 检索类型 */
    private String retrievalType;
    
    /** 扩展属性 */
    private Map<String, Object> metadata;
    
    public RetrievedDocument() {}
    
    // Getters and Setters
    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
    
    public String getRetrievalType() { return retrievalType; }
    public void setRetrievalType(String retrievalType) { this.retrievalType = retrievalType; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
