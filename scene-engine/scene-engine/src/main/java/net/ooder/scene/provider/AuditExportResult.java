package net.ooder.scene.provider;

public class AuditExportResult {
    private String format;
    private byte[] data;
    private String filename;
    private int recordCount;

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public int getRecordCount() { return recordCount; }
    public void setRecordCount(int recordCount) { this.recordCount = recordCount; }
}
