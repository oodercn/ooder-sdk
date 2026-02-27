package net.ooder.scene.core.security;

import java.util.List;

/**
 * 审计导出结果
 */
public class AuditExportResult {
    private String exportId;
    private String fileName;
    private long recordCount;
    private long fileSize;
    private String downloadUrl;
    private long timestamp;
    private long exportedAt;
    private long totalRecords;
    private List<AuditLog> records;
    private String format;
    private String status;

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getRecordCount() { return recordCount; }
    public void setRecordCount(long recordCount) { this.recordCount = recordCount; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getExportedAt() { return exportedAt; }
    public void setExportedAt(long exportedAt) { this.exportedAt = exportedAt; }
    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
    public List<AuditLog> getRecords() { return records; }
    public void setRecords(List<AuditLog> records) { this.records = records; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
