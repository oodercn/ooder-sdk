package net.ooder.scene.snapshot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IncrementalSnapshot {

    private String deltaId;
    private String baseSnapshotId;
    private String sceneGroupId;
    private Map<String, Object> addedData;
    private Map<String, Object> modifiedData;
    private Map<String, Object> deletedData;
    private long deltaSize;
    private Instant createTime;
    private String checksum;

    public IncrementalSnapshot() {
        this.deltaId = UUID.randomUUID().toString().replace("-", "");
        this.createTime = Instant.now();
        this.addedData = new HashMap<>();
        this.modifiedData = new HashMap<>();
        this.deletedData = new HashMap<>();
    }

    public IncrementalSnapshot(String baseSnapshotId, String sceneGroupId) {
        this();
        this.baseSnapshotId = baseSnapshotId;
        this.sceneGroupId = sceneGroupId;
    }

    public String getDeltaId() {
        return deltaId;
    }

    public void setDeltaId(String deltaId) {
        this.deltaId = deltaId;
    }

    public String getBaseSnapshotId() {
        return baseSnapshotId;
    }

    public void setBaseSnapshotId(String baseSnapshotId) {
        this.baseSnapshotId = baseSnapshotId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public Map<String, Object> getAddedData() {
        return addedData;
    }

    public void setAddedData(Map<String, Object> addedData) {
        this.addedData = addedData;
    }

    public void addAddedData(String key, Object value) {
        this.addedData.put(key, value);
    }

    public Map<String, Object> getModifiedData() {
        return modifiedData;
    }

    public void setModifiedData(Map<String, Object> modifiedData) {
        this.modifiedData = modifiedData;
    }

    public void addModifiedData(String key, Object oldValue, Object newValue) {
        Map<String, Object> change = new HashMap<>();
        change.put("old", oldValue);
        change.put("new", newValue);
        this.modifiedData.put(key, change);
    }

    public Map<String, Object> getDeletedData() {
        return deletedData;
    }

    public void setDeletedData(Map<String, Object> deletedData) {
        this.deletedData = deletedData;
    }

    public void addDeletedData(String key, Object value) {
        this.deletedData.put(key, value);
    }

    public long getDeltaSize() {
        return deltaSize;
    }

    public void setDeltaSize(long deltaSize) {
        this.deltaSize = deltaSize;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public boolean isEmpty() {
        return addedData.isEmpty() && modifiedData.isEmpty() && deletedData.isEmpty();
    }

    public int getChangeCount() {
        return addedData.size() + modifiedData.size() + deletedData.size();
    }

    public void calculateDeltaSize() {
        long size = 0;
        size += estimateMapSize(addedData);
        size += estimateMapSize(modifiedData);
        size += estimateMapSize(deletedData);
        this.deltaSize = size;
    }

    private long estimateMapSize(Map<String, Object> map) {
        long size = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            size += entry.getKey().getBytes().length;
            if (entry.getValue() != null) {
                size += entry.getValue().toString().getBytes().length;
            }
        }
        return size;
    }

    @Override
    public String toString() {
        return "IncrementalSnapshot{" +
                "deltaId='" + deltaId + '\'' +
                ", baseSnapshotId='" + baseSnapshotId + '\'' +
                ", changeCount=" + getChangeCount() +
                ", deltaSize=" + deltaSize +
                '}';
    }
}
