package net.ooder.scene.skill.contribution;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 文件上传请求
 *
 * @author ooder
 * @since 2.3
 */
public class FileUploadRequest {
    
    private String fileName;
    private InputStream inputStream;
    private long fileSize;
    private String mimeType;
    private String title;
    private List<String> tags;
    private Map<String, Object> metadata;
    
    public FileUploadRequest() {
    }
    
    public FileUploadRequest(String fileName, InputStream inputStream, long fileSize) {
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.fileSize = fileSize;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
