package net.ooder.scene.skill.importer;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 压缩包导入请求
 *
 * @author ooder
 * @since 2.3
 */
public class ArchiveImportRequest {
    
    private InputStream inputStream;
    private String fileName;
    private long fileSize;
    private ArchiveType archiveType;
    private List<String> tags;
    private Map<String, Object> metadata;
    private boolean flattenDirectories = true;
    private List<String> includePatterns;
    private List<String> excludePatterns;
    private long maxFileSize = 10 * 1024 * 1024;
    private int maxFileCount = 100;
    
    public ArchiveImportRequest() {
    }
    
    public ArchiveImportRequest(InputStream inputStream, String fileName, long fileSize) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.archiveType = detectArchiveType(fileName);
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public ArchiveType getArchiveType() {
        return archiveType;
    }
    
    public void setArchiveType(ArchiveType archiveType) {
        this.archiveType = archiveType;
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
    
    public boolean isFlattenDirectories() {
        return flattenDirectories;
    }
    
    public void setFlattenDirectories(boolean flattenDirectories) {
        this.flattenDirectories = flattenDirectories;
    }
    
    public List<String> getIncludePatterns() {
        return includePatterns;
    }
    
    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }
    
    public List<String> getExcludePatterns() {
        return excludePatterns;
    }
    
    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public int getMaxFileCount() {
        return maxFileCount;
    }
    
    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }
    
    private ArchiveType detectArchiveType(String fileName) {
        if (fileName == null) {
            return ArchiveType.ZIP;
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".zip")) {
            return ArchiveType.ZIP;
        } else if (lower.endsWith(".tar") || lower.endsWith(".tar.gz") || lower.endsWith(".tgz")) {
            return ArchiveType.TAR;
        } else if (lower.endsWith(".gz")) {
            return ArchiveType.GZIP;
        }
        return ArchiveType.ZIP;
    }
}
