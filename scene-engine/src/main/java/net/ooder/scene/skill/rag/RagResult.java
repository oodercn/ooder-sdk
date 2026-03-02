package net.ooder.scene.skill.rag;

import java.util.List;
import java.util.Map;

/**
 * RAG 检索结果
 *
 * @author ooder
 * @since 2.3
 */
public class RagResult {
    
    /** 查询 */
    private String query;
    
    /** 检索到的文档列表 */
    private List<RetrievedDocument> documents;
    
    /** 合并后的上下文 */
    private String context;
    
    /** 检索耗时（毫秒） */
    private long retrievalTime;
    
    /** 使用的知识库ID列表 */
    private List<String> kbIds;
    
    /** 扩展信息 */
    private Map<String, Object> metadata;
    
    public RagResult() {}
    
    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public List<RetrievedDocument> getDocuments() { return documents; }
    public void setDocuments(List<RetrievedDocument> documents) { this.documents = documents; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public long getRetrievalTime() { return retrievalTime; }
    public void setRetrievalTime(long retrievalTime) { this.retrievalTime = retrievalTime; }
    
    public List<String> getKbIds() { return kbIds; }
    public void setKbIds(List<String> kbIds) { this.kbIds = kbIds; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
