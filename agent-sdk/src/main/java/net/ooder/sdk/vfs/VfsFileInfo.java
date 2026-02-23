package net.ooder.sdk.vfs;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VfsFileInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String path;
    private String name;
    private boolean directory;
    private long size;
    private String contentType;
    private String checksum;
    private long createdTime;
    private long modifiedTime;
    private String createdBy;
    private String modifiedBy;
    private Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isDirectory() { return directory; }
    public void setDirectory(boolean directory) { this.directory = directory; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    
    public long getModifiedTime() { return modifiedTime; }
    public void setModifiedTime(long modifiedTime) { this.modifiedTime = modifiedTime; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>(); 
    }
    
    public boolean isFile() {
        return !directory;
    }
    
    public String getExtension() {
        if (name == null || !name.contains(".")) {
            return null;
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }
}
