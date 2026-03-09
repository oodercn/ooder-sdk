package net.ooder.scene.skill.knowledge;

/**
 * 文档分块
 *
 * <p>文档分块是文档内容的片段，用于向量化和检索。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class DocumentChunk {
    
    private String chunkId;
    private String docId;
    private String kbId;
    
    private int chunkIndex;
    private String content;
    
    private String vectorId;
    private int tokenCount;
    
    private long createdAt;
    
    public DocumentChunk() {
        this.createdAt = System.currentTimeMillis();
    }
    
    public DocumentChunk(String chunkId, String docId, String kbId, int chunkIndex, String content) {
        this();
        this.chunkId = chunkId;
        this.docId = docId;
        this.kbId = kbId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.tokenCount = estimateTokenCount(content);
    }
    
    // Getters and Setters
    
    public String getChunkId() { return chunkId; }
    public void setChunkId(String chunkId) { this.chunkId = chunkId; }
    
    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content; 
        this.tokenCount = estimateTokenCount(content);
    }
    
    public String getVectorId() { return vectorId; }
    public void setVectorId(String vectorId) { this.vectorId = vectorId; }
    
    public int getTokenCount() { return tokenCount; }
    public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    // Utility methods
    
    private int estimateTokenCount(String text) {
        if (text == null) return 0;
        return text.length() / 4;
    }
    
    public boolean hasVector() {
        return vectorId != null && !vectorId.isEmpty();
    }
}
