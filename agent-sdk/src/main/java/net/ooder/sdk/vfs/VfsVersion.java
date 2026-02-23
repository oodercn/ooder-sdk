package net.ooder.sdk.vfs;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VfsVersion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String versionId;
    private String path;
    private long size;
    private String checksum;
    private long createdTime;
    private String createdBy;
    private boolean isLatest;
    private boolean isDeleted;
    private Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public boolean isLatest() { return isLatest; }
    public void setLatest(boolean latest) { isLatest = latest; }
    
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>(); 
    }
}
