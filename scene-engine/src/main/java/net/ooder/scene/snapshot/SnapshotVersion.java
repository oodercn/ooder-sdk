package net.ooder.scene.snapshot;

import java.time.Instant;

public class SnapshotVersion {

    private String versionId;
    private String snapshotId;
    private String sceneGroupId;
    private int versionNumber;
    private String parentVersionId;
    private boolean isIncremental;
    private long size;
    private Instant createTime;
    private String createdBy;
    private String description;
    private String checksum;

    public SnapshotVersion() {
        this.createTime = Instant.now();
    }

    public SnapshotVersion(String snapshotId, int versionNumber) {
        this();
        this.snapshotId = snapshotId;
        this.versionNumber = versionNumber;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getParentVersionId() {
        return parentVersionId;
    }

    public void setParentVersionId(String parentVersionId) {
        this.parentVersionId = parentVersionId;
    }

    public boolean isIncremental() {
        return isIncremental;
    }

    public void setIncremental(boolean incremental) {
        isIncremental = incremental;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public boolean isFullSnapshot() {
        return !isIncremental;
    }

    @Override
    public String toString() {
        return "SnapshotVersion{" +
                "versionId='" + versionId + '\'' +
                ", versionNumber=" + versionNumber +
                ", isIncremental=" + isIncremental +
                ", size=" + size +
                '}';
    }
}
