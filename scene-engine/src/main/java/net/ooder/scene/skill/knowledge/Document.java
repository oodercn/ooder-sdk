package net.ooder.scene.skill.knowledge;

import java.util.List;
import java.util.Map;

/**
 * 知识库文档
 *
 * @author ooder
 * @since 2.3
 */
public class Document {

    public static final String SOURCE_UPLOAD = "upload";
    public static final String SOURCE_TEXT = "text";
    public static final String SOURCE_URL = "url";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_INDEXED = "indexed";
    public static final String STATUS_FAILED = "failed";

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

    /** 来源URL */
    private String sourceUrl;

    /** 文件路径 */
    private String filePath;

    /** MIME类型 */
    private String mimeType;

    /** 文件大小 */
    private long fileSize;

    /** 标签 */
    private List<String> tags;

    /** 创建时间 */
    private long createdAt;

    /** 更新时间 */
    private long updatedAt;

    /** 索引状态 */
    private String indexStatus;

    /** 分块数量 */
    private int chunkCount;

    /** 扩展属性 */
    private Map<String, Object> metadata;

    public Document() {}

    public Document(String docId, String kbId, String title, String content) {
        this.docId = docId;
        this.kbId = kbId;
        this.title = title;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getIndexStatus() { return indexStatus; }
    public void setIndexStatus(String indexStatus) { this.indexStatus = indexStatus; }

    /**
     * 设置状态（与 setIndexStatus 相同）
     * @param status 状态
     */
    public void setStatus(String status) { this.indexStatus = status; }

    public int getChunkCount() { return chunkCount; }
    public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    // 业务方法
    public void markProcessing() {
        this.indexStatus = STATUS_PROCESSING;
        this.updatedAt = System.currentTimeMillis();
    }

    public void markIndexed(int chunkCount) {
        this.indexStatus = STATUS_INDEXED;
        this.chunkCount = chunkCount;
        this.updatedAt = System.currentTimeMillis();
    }

    public void markFailed(String errorMessage) {
        this.indexStatus = STATUS_FAILED;
        this.updatedAt = System.currentTimeMillis();
    }
}
