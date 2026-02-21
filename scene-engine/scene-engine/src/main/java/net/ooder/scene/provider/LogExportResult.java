package net.ooder.scene.provider;

/**
 * 日志导出结果
 */
public class LogExportResult {
    private String exportId;
    private String fileName;
    private String format;
    private long recordCount;
    private long fileSize;
    private String filePath;
    private long timestamp;

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public long getRecordCount() { return recordCount; }
    public void setRecordCount(long recordCount) { this.recordCount = recordCount; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
