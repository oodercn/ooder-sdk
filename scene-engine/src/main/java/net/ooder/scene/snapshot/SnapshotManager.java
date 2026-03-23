package net.ooder.scene.snapshot;

import java.util.List;
import java.util.Optional;

public interface SnapshotManager {

    SceneSnapshot createSnapshot(String sceneGroupId, SceneSnapshot.Type type, String name);

    SceneSnapshot createIncrementalSnapshot(String sceneGroupId, String baseSnapshotId, String name);

    Optional<SceneSnapshot> getSnapshot(String snapshotId);

    List<SceneSnapshot> getSnapshotsBySceneGroup(String sceneGroupId);

    List<SnapshotVersion> getSnapshotVersions(String snapshotId);

    SnapshotVersion getLatestVersion(String snapshotId);

    boolean restoreSnapshot(String snapshotId);

    boolean restoreToVersion(String snapshotId, int versionNumber);

    boolean deleteSnapshot(String snapshotId);

    boolean deleteVersion(String snapshotId, int versionNumber);

    int cleanupExpiredSnapshots(String sceneGroupId);

    SnapshotStats getSnapshotStats(String sceneGroupId);

    class SnapshotStats {
        private int totalSnapshots;
        private int fullSnapshots;
        private int incrementalSnapshots;
        private long totalSize;
        private int expiredSnapshots;

        public int getTotalSnapshots() { return totalSnapshots; }
        public void setTotalSnapshots(int totalSnapshots) { this.totalSnapshots = totalSnapshots; }

        public int getFullSnapshots() { return fullSnapshots; }
        public void setFullSnapshots(int fullSnapshots) { this.fullSnapshots = fullSnapshots; }

        public int getIncrementalSnapshots() { return incrementalSnapshots; }
        public void setIncrementalSnapshots(int incrementalSnapshots) { this.incrementalSnapshots = incrementalSnapshots; }

        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }

        public int getExpiredSnapshots() { return expiredSnapshots; }
        public void setExpiredSnapshots(int expiredSnapshots) { this.expiredSnapshots = expiredSnapshots; }
    }
}
