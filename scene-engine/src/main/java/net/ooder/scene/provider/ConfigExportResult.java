package net.ooder.scene.provider;

/**
 * 配置导出结果
 */
public class ConfigExportResult {
    private String exportId;
    private String fileName;
    private String format;
    private long configCount;
    private long fileSize;
    private String content;
    private long timestamp;

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public long getConfigCount() { return configCount; }
    public void setConfigCount(long configCount) { this.configCount = configCount; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
