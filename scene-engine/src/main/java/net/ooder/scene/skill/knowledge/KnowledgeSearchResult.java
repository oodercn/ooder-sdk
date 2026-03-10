package net.ooder.scene.skill.knowledge;

import java.util.Map;

/**
 * 知识库搜索结果
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeSearchResult {

    /** 文档ID */
    private String docId;

    /** 知识库ID */
    private String kbId;

    /** 文档标题 */
    private String title;

    /** 相关内容片段 */
    private String content;

    /** 相似度分数 */
    private float score;

    /** 搜索类型 */
    private String searchType;

    /** 分块ID */
    private String chunkId;

    /** 扩展属性 */
    private Map<String, Object> metadata;

    public KnowledgeSearchResult() {}

    public KnowledgeSearchResult(Document doc, String content, float score) {
        this.docId = doc.getDocId();
        this.kbId = doc.getKbId();
        this.title = doc.getTitle();
        this.content = content;
        this.score = score;
    }

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

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public String getChunkId() { return chunkId; }
    public void setChunkId(String chunkId) { this.chunkId = chunkId; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
