package net.ooder.scene.snapshot;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class SnapshotManagerImpl implements SnapshotManager {

    private static final Logger log = LoggerFactory.getLogger(SnapshotManagerImpl.class);

    private final SceneGroupManager sceneGroupManager;
    private final Map<String, SceneSnapshot> snapshotStore = new ConcurrentHashMap<>();
    private final Map<String, List<SnapshotVersion>> versionStore = new ConcurrentHashMap<>();
    private final Map<String, IncrementalSnapshot> deltaStore = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> versionCounters = new ConcurrentHashMap<>();

    public SnapshotManagerImpl(SceneGroupManager sceneGroupManager) {
        this.sceneGroupManager = sceneGroupManager;
    }

    @Override
    public SceneSnapshot createSnapshot(String sceneGroupId, SceneSnapshot.Type type, String name) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }

        String snapshotId = generateSnapshotId();
        SceneSnapshot snapshot = new SceneSnapshot(snapshotId, sceneGroupId, type);
        snapshot.setName(name);
        snapshot.setParticipants(sceneGroup.getAllParticipants());
        snapshot.setCapabilityBindings(sceneGroup.getAllCapabilityBindings());
        snapshot.setKnowledgeBindings(sceneGroup.getAllKnowledgeBindings());
        snapshot.setConfigData(sceneGroup.getAllConfig());
        snapshot.estimateSize();

        snapshotStore.put(snapshotId, snapshot);

        SnapshotVersion version = createVersion(snapshotId, sceneGroupId, null, false);
        versionStore.computeIfAbsent(snapshotId, k -> new ArrayList<>()).add(version);

        log.info("Snapshot created: snapshotId={}, sceneGroupId={}, type={}, size={}",
                snapshotId, sceneGroupId, type, snapshot.getSize());

        return snapshot;
    }

    @Override
    public SceneSnapshot createIncrementalSnapshot(String sceneGroupId, String baseSnapshotId, String name) {
        SceneSnapshot baseSnapshot = snapshotStore.get(baseSnapshotId);
        if (baseSnapshot == null) {
            throw new IllegalArgumentException("Base snapshot not found: " + baseSnapshotId);
        }

        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }

        String snapshotId = generateSnapshotId();
        SceneSnapshot snapshot = new SceneSnapshot(snapshotId, sceneGroupId, SceneSnapshot.Type.AUTO);
        snapshot.setName(name);

        IncrementalSnapshot delta = computeDelta(baseSnapshot, sceneGroup);
        delta.setBaseSnapshotId(baseSnapshotId);
        delta.setDeltaId(snapshotId + "-delta");
        delta.calculateDeltaSize();

        deltaStore.put(snapshotId, delta);

        snapshot.setSceneGroupData("incremental:" + delta.getDeltaId());
        snapshot.setSize(delta.getDeltaSize());

        snapshotStore.put(snapshotId, snapshot);

        SnapshotVersion baseVersion = getLatestVersion(baseSnapshotId);
        SnapshotVersion version = createVersion(snapshotId, sceneGroupId,
                baseVersion != null ? baseVersion.getVersionId() : null, true);
        versionStore.computeIfAbsent(snapshotId, k -> new ArrayList<>()).add(version);

        log.info("Incremental snapshot created: snapshotId={}, baseSnapshotId={}, deltaSize={}",
                snapshotId, baseSnapshotId, delta.getDeltaSize());

        return snapshot;
    }

    @Override
    public Optional<SceneSnapshot> getSnapshot(String snapshotId) {
        return Optional.ofNullable(snapshotStore.get(snapshotId));
    }

    @Override
    public List<SceneSnapshot> getSnapshotsBySceneGroup(String sceneGroupId) {
        return snapshotStore.values().stream()
                .filter(s -> s.getSceneGroupId().equals(sceneGroupId))
                .sorted(Comparator.comparing(SceneSnapshot::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<SnapshotVersion> getSnapshotVersions(String snapshotId) {
        List<SnapshotVersion> versions = versionStore.get(snapshotId);
        return versions != null ? new ArrayList<>(versions) : new ArrayList<>();
    }

    @Override
    public SnapshotVersion getLatestVersion(String snapshotId) {
        List<SnapshotVersion> versions = versionStore.get(snapshotId);
        if (versions == null || versions.isEmpty()) {
            return null;
        }
        return versions.get(versions.size() - 1);
    }

    @Override
    public boolean restoreSnapshot(String snapshotId) {
        SceneSnapshot snapshot = snapshotStore.get(snapshotId);
        if (snapshot == null || !snapshot.isValid()) {
            log.warn("Cannot restore snapshot: snapshotId={}, status={}",
                    snapshotId, snapshot != null ? snapshot.getStatus() : "not found");
            return false;
        }

        snapshot.markRestoring();

        try {
            SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(snapshot.getSceneGroupId());
            if (sceneGroup == null) {
                log.error("SceneGroup not found for snapshot restore: {}", snapshot.getSceneGroupId());
                snapshot.setStatus(SceneSnapshot.Status.ACTIVE);
                return false;
            }

            IncrementalSnapshot delta = deltaStore.get(snapshotId);
            if (delta != null) {
                applyDelta(sceneGroup, delta);
            } else {
                sceneGroup.getAllParticipants().clear();
                sceneGroup.getAllParticipants().addAll(snapshot.getParticipants());
            }

            snapshot.markRestored();
            log.info("Snapshot restored: snapshotId={}, sceneGroupId={}",
                    snapshotId, snapshot.getSceneGroupId());
            return true;

        } catch (Exception e) {
            log.error("Failed to restore snapshot: snapshotId={}", snapshotId, e);
            snapshot.setStatus(SceneSnapshot.Status.ACTIVE);
            return false;
        }
    }

    @Override
    public boolean restoreToVersion(String snapshotId, int versionNumber) {
        List<SnapshotVersion> versions = versionStore.get(snapshotId);
        if (versions == null || versionNumber < 1 || versionNumber > versions.size()) {
            return false;
        }

        return restoreSnapshot(snapshotId);
    }

    @Override
    public boolean deleteSnapshot(String snapshotId) {
        SceneSnapshot snapshot = snapshotStore.remove(snapshotId);
        if (snapshot != null) {
            snapshot.markDeleted();
            versionStore.remove(snapshotId);
            deltaStore.remove(snapshotId);
            log.info("Snapshot deleted: snapshotId={}", snapshotId);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteVersion(String snapshotId, int versionNumber) {
        List<SnapshotVersion> versions = versionStore.get(snapshotId);
        if (versions == null || versionNumber < 1 || versionNumber > versions.size()) {
            return false;
        }

        versions.remove(versionNumber - 1);
        log.info("Snapshot version deleted: snapshotId={}, versionNumber={}", snapshotId, versionNumber);
        return true;
    }

    @Override
    public int cleanupExpiredSnapshots(String sceneGroupId) {
        int count = 0;
        Iterator<Map.Entry<String, SceneSnapshot>> iterator = snapshotStore.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, SceneSnapshot> entry = iterator.next();
            SceneSnapshot snapshot = entry.getValue();

            if (snapshot.getSceneGroupId().equals(sceneGroupId) && snapshot.isExpired()) {
                iterator.remove();
                versionStore.remove(entry.getKey());
                deltaStore.remove(entry.getKey());
                snapshot.markDeleted();
                count++;
            }
        }

        if (count > 0) {
            log.info("Cleaned up expired snapshots: sceneGroupId={}, count={}", sceneGroupId, count);
        }

        return count;
    }

    @Override
    public SnapshotStats getSnapshotStats(String sceneGroupId) {
        SnapshotStats stats = new SnapshotStats();

        List<SceneSnapshot> snapshots = getSnapshotsBySceneGroup(sceneGroupId);
        stats.setTotalSnapshots(snapshots.size());

        int fullCount = 0;
        int incrementalCount = 0;
        long totalSize = 0;
        int expiredCount = 0;

        for (SceneSnapshot snapshot : snapshots) {
            if (deltaStore.containsKey(snapshot.getSnapshotId())) {
                incrementalCount++;
            } else {
                fullCount++;
            }
            totalSize += snapshot.getSize();
            if (snapshot.isExpired()) {
                expiredCount++;
            }
        }

        stats.setFullSnapshots(fullCount);
        stats.setIncrementalSnapshots(incrementalCount);
        stats.setTotalSize(totalSize);
        stats.setExpiredSnapshots(expiredCount);

        return stats;
    }

    private String generateSnapshotId() {
        return "snap-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private SnapshotVersion createVersion(String snapshotId, String sceneGroupId,
                                          String parentVersionId, boolean isIncremental) {
        AtomicInteger counter = versionCounters.computeIfAbsent(snapshotId, k -> new AtomicInteger(0));

        SnapshotVersion version = new SnapshotVersion();
        version.setVersionId("ver-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        version.setSnapshotId(snapshotId);
        version.setSceneGroupId(sceneGroupId);
        version.setVersionNumber(counter.incrementAndGet());
        version.setParentVersionId(parentVersionId);
        version.setIncremental(isIncremental);
        version.setCreateTime(Instant.now());

        return version;
    }

    private IncrementalSnapshot computeDelta(SceneSnapshot base, SceneGroup current) {
        IncrementalSnapshot delta = new IncrementalSnapshot(base.getSnapshotId(), current.getSceneGroupId());

        Map<String, net.ooder.scene.participant.Participant> baseParticipants = new HashMap<>();
        for (net.ooder.scene.participant.Participant p : base.getParticipants()) {
            baseParticipants.put(p.getParticipantId(), p);
        }

        for (net.ooder.scene.participant.Participant p : current.getAllParticipants()) {
            if (!baseParticipants.containsKey(p.getParticipantId())) {
                delta.addAddedData("participant:" + p.getParticipantId(), p.getName());
            }
        }

        for (net.ooder.scene.participant.Participant p : base.getParticipants()) {
            if (current.getParticipant(p.getParticipantId()) == null) {
                delta.addDeletedData("participant:" + p.getParticipantId(), p.getName());
            }
        }

        return delta;
    }

    private void applyDelta(SceneGroup sceneGroup, IncrementalSnapshot delta) {
        log.info("Applying delta to scene group: sceneGroupId={}, changes={}",
                sceneGroup.getSceneGroupId(), delta.getChangeCount());
    }
}
