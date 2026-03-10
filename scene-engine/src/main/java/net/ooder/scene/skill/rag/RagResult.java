package net.ooder.scene.skill.rag;

import java.util.ArrayList;
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

    /** 检索到的块列表 */
    private List<RetrievedChunk> chunks;

    /** 合并后的上下文 */
    private String context;

    /** 检索耗时（毫秒） */
    private long retrievalTime;

    /** 使用的知识库ID列表 */
    private List<String> kbIds;

    /** 找到的总数 */
    private int totalFound;

    /** 扩展信息 */
    private Map<String, Object> metadata;

    public RagResult() {
        this.documents = new ArrayList<>();
        this.chunks = new ArrayList<>();
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<RetrievedDocument> getDocuments() { return documents; }
    public void setDocuments(List<RetrievedDocument> documents) { this.documents = documents; }

    public List<RetrievedChunk> getChunks() { return chunks; }
    public void setChunks(List<RetrievedChunk> chunks) { this.chunks = chunks; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public long getRetrievalTime() { return retrievalTime; }
    public void setRetrievalTime(long retrievalTime) { this.retrievalTime = retrievalTime; }

    public List<String> getKbIds() { return kbIds; }
    public void setKbIds(List<String> kbIds) { this.kbIds = kbIds; }

    public int getTotalFound() { return totalFound; }
    public void setTotalFound(int totalFound) { this.totalFound = totalFound; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    /**
     * 检索到的文档
     */
    public static class RetrievedDocument {
        private String docId;
        private String title;
        private String content;
        private float score;
        private Map<String, Object> metadata;

        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 检索到的块
     */
    public static class RetrievedChunk {
        private String chunkId;
        private String docId;
        private String docTitle;
        private String content;
        private float score;
        private Map<String, Object> metadata;

        public String getChunkId() { return chunkId; }
        public void setChunkId(String chunkId) { this.chunkId = chunkId; }

        public String getDocId() { return docId; }
        public void setDocId(String docId) { this.docId = docId; }

        public String getDocTitle() { return docTitle; }
        public void setDocTitle(String docTitle) { this.docTitle = docTitle; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}
