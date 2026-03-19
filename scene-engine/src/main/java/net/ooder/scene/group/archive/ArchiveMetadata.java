package net.ooder.scene.group.archive;

import java.time.LocalDateTime;
import java.util.Map;

public class ArchiveMetadata {
    
    private String archiveId;
    private String sceneGroupId;
    private LocalDateTime archiveTime;
    private ArchiveType archiveType;
    private String description;
    private long dataSize;
    private Map<String, Object> runtimeData;
    
    public enum ArchiveType {
        USER_INITIATED,
        SYSTEM_AUTO,
        MIGRATION
    }
    
    public String getArchiveId() { return archiveId; }
    public void setArchiveId(String archiveId) { this.archiveId = archiveId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public LocalDateTime getArchiveTime() { return archiveTime; }
    public void setArchiveTime(LocalDateTime archiveTime) { this.archiveTime = archiveTime; }
    public ArchiveType getArchiveType() { return archiveType; }
    public void setArchiveType(ArchiveType archiveType) { this.archiveType = archiveType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getDataSize() { return dataSize; }
    public void setDataSize(long dataSize) { this.dataSize = dataSize; }
    public Map<String, Object> getRuntimeData() { return runtimeData; }
    public void setRuntimeData(Map<String, Object> runtimeData) { this.runtimeData = runtimeData; }
}
