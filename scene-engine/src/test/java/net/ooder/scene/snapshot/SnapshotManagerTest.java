package net.ooder.scene.snapshot;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotManagerTest {

    private SnapshotManager snapshotManager;
    private SceneGroupManager sceneGroupManager;

    @BeforeEach
    void setUp() {
        sceneGroupManager = new SceneGroupManager();
        SceneGroup group = new SceneGroup("scene-001", "template-001", "user-001", SceneGroup.CreatorType.USER);
        sceneGroupManager.createSceneGroup(group);

        snapshotManager = new SnapshotManagerImpl(sceneGroupManager);
    }

    @Test
    void testCreateSnapshot() {
        SceneSnapshot snapshot = snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.MANUAL, "Test Snapshot");

        assertNotNull(snapshot);
        assertEquals("scene-001", snapshot.getSceneGroupId());
        assertEquals(SceneSnapshot.Type.MANUAL, snapshot.getType());
        assertTrue(snapshot.isValid());
    }

    @Test
    void testCreateIncrementalSnapshot() {
        SceneSnapshot full = snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.MANUAL, "Full Snapshot");

        SceneSnapshot incremental = snapshotManager.createIncrementalSnapshot("scene-001", full.getSnapshotId(), "Incremental");

        assertNotNull(incremental);
        assertTrue(incremental.getSceneGroupData().startsWith("incremental:"));
    }

    @Test
    void testGetSnapshotsBySceneGroup() {
        snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.AUTO, "Snapshot 1");
        snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.AUTO, "Snapshot 2");

        List<SceneSnapshot> snapshots = snapshotManager.getSnapshotsBySceneGroup("scene-001");

        assertEquals(2, snapshots.size());
    }

    @Test
    void testGetSnapshotVersions() {
        SceneSnapshot snapshot = snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.MANUAL, "Test");

        List<SnapshotVersion> versions = snapshotManager.getSnapshotVersions(snapshot.getSnapshotId());

        assertEquals(1, versions.size());
        assertEquals(1, versions.get(0).getVersionNumber());
    }

    @Test
    void testDeleteSnapshot() {
        SceneSnapshot snapshot = snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.MANUAL, "Test");

        assertTrue(snapshotManager.deleteSnapshot(snapshot.getSnapshotId()));

        assertFalse(snapshotManager.getSnapshot(snapshot.getSnapshotId()).isPresent());
    }

    @Test
    void testGetSnapshotStats() {
        snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.MANUAL, "Full");
        snapshotManager.createSnapshot("scene-001", SceneSnapshot.Type.MANUAL, "Full 2");

        SnapshotManager.SnapshotStats stats = snapshotManager.getSnapshotStats("scene-001");

        assertEquals(2, stats.getTotalSnapshots());
    }
}
