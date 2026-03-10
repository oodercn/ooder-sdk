package net.ooder.scene.skill.knowledge;

/**
 * 索引状态
 *
 * @author ooder
 * @since 2.3
 */
public class IndexStatus {

    public static final String PENDING = "PENDING";
    public static final String INDEXING = "INDEXING";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
    public static final String INDEXED = "INDEXED";

    /** 知识库ID */
    private String kbId;

    /** 状态：PENDING, INDEXING, COMPLETED, FAILED */
    private String status;

    /** 文档总数 */
    private int totalDocuments;

    /** 已索引文档数 */
    private int indexedDocuments;

    /** 失败文档数 */
    private int failedDocuments;

    /** 进度百分比 */
    private int progress;

    /** 最后更新时间 */
    private long lastUpdated;

    /** 错误信息 */
    private String errorMessage;

    /** 总块数 */
    private int totalChunks;

    /** 总大小 */
    private long totalSize;

    public IndexStatus() {}

    public IndexStatus(String kbId) {
        this.kbId = kbId;
        this.status = PENDING;
        this.progress = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 开始索引
     * @param totalDocuments 文档总数
     * @param totalSize 总大小
     */
    public void start(int totalDocuments, long totalSize) {
        this.status = INDEXING;
        this.totalDocuments = totalDocuments;
        this.totalSize = totalSize;
        this.indexedDocuments = 0;
        this.failedDocuments = 0;
        this.progress = 0;
        this.lastUpdated = System.currentTimeMillis();
        this.errorMessage = null;
    }

    /**
     * 更新进度
     * @param indexedDocuments 已索引文档数
     * @param totalChunks 总块数
     * @param failedDocuments 失败文档数
     */
    public void updateProgress(int indexedDocuments, int totalChunks, int failedDocuments) {
        this.indexedDocuments = indexedDocuments;
        this.totalChunks = totalChunks;
        this.failedDocuments = failedDocuments;
        if (totalDocuments > 0) {
            this.progress = (int) ((indexedDocuments * 100.0) / totalDocuments);
        }
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 完成索引
     */
    public void complete() {
        this.status = COMPLETED;
        this.progress = 100;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 索引失败
     * @param errorMessage 错误信息
     */
    public void fail(String errorMessage) {
        this.status = FAILED;
        this.errorMessage = errorMessage;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalDocuments() { return totalDocuments; }
    public void setTotalDocuments(int totalDocuments) { this.totalDocuments = totalDocuments; }

    public int getIndexedDocuments() { return indexedDocuments; }
    public void setIndexedDocuments(int indexedDocuments) { this.indexedDocuments = indexedDocuments; }

    public int getFailedDocuments() { return failedDocuments; }
    public void setFailedDocuments(int failedDocuments) { this.failedDocuments = failedDocuments; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public int getTotalChunks() { return totalChunks; }
    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }

    public long getTotalSize() { return totalSize; }
    public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
}
