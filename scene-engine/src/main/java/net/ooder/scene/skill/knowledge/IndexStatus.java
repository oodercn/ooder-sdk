package net.ooder.scene.skill.knowledge;

/**
 * 索引状态
 *
 * @author ooder
 * @since 2.3
 */
public class IndexStatus {
    
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
    
    public IndexStatus() {}
    
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
}
