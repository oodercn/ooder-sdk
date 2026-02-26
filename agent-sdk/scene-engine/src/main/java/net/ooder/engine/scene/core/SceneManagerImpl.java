package net.ooder.engine.scene.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ooder.sdk.api.scene.SceneDefinition;
import net.ooder.sdk.api.scene.SceneManager;
import net.ooder.sdk.api.scene.SceneSnapshot;
import net.ooder.sdk.api.scene.model.SceneConfig;
import net.ooder.sdk.api.scene.model.SceneLifecycleStats;
import net.ooder.sdk.api.scene.model.SceneState;
import net.ooder.sdk.api.capability.Capability;

/**
 * SceneManager 实现类
 */
public class SceneManagerImpl implements SceneManager {

    private static final Logger log = LoggerFactory.getLogger(SceneManagerImpl.class);

    private final Map<String, SceneDefinition> scenes = new ConcurrentHashMap<>();
    private final Map<String, SceneStatusInfo> sceneStatusInfos = new ConcurrentHashMap<>();
    private final List<SceneLifecycleListener> lifecycleListeners = new ArrayList<>();

    public SceneManagerImpl() {
    }

    @Override
    public CompletableFuture<SceneDefinition> create(SceneDefinition definition) {
        return CompletableFuture.supplyAsync(() -> {
            if (definition == null) {
                throw new IllegalArgumentException("SceneDefinition cannot be null");
            }

            String sceneId = definition.getSceneId();
            if (sceneId == null || sceneId.trim().isEmpty()) {
                throw new IllegalArgumentException("SceneId cannot be null or empty");
            }

            if (scenes.containsKey(sceneId)) {
                log.warn("Scene already exists, overwriting: {}", sceneId);
            }

            scenes.put(sceneId, definition);

            SceneStatusInfo status = new SceneStatusInfo();
            status.setSceneId(sceneId);
            status.setActive(false);
            status.setMemberCount(0);
            status.setInstalledSkills(new ArrayList<String>());
            status.setCreateTime(System.currentTimeMillis());
            status.setLastUpdateTime(System.currentTimeMillis());

            sceneStatusInfos.put(sceneId, status);

            log.info("Scene created: {}", sceneId);

            return definition;
        });
    }

    @Override
    public CompletableFuture<Void> delete(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            if (sceneId == null || sceneId.trim().isEmpty()) {
                log.warn("Cannot delete scene with null or empty id");
                return;
            }

            SceneDefinition removed = scenes.remove(sceneId);
            sceneStatusInfos.remove(sceneId);

            if (removed != null) {
                log.info("Scene deleted: {}", sceneId);
            } else {
                log.debug("Scene not found for deletion: {}", sceneId);
            }
        });
    }

    @Override
    public CompletableFuture<SceneDefinition> get(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            if (sceneId == null || sceneId.trim().isEmpty()) {
                return null;
            }
            return scenes.get(sceneId);
        });
    }

    @Override
    public CompletableFuture<List<SceneDefinition>> listAll() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<SceneDefinition>(scenes.values()));
    }

    @Override
    public CompletableFuture<Void> activate(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            if (sceneId == null || sceneId.trim().isEmpty()) {
                log.warn("Cannot activate scene with null or empty id");
                return;
            }

            SceneStatusInfo status = sceneStatusInfos.get(sceneId);
            if (status == null) {
                log.warn("Scene not found for activation: {}", sceneId);
                return;
            }

            if (status.isActive()) {
                log.debug("Scene already active: {}", sceneId);
                return;
            }

            status.setActive(true);
            status.setLastUpdateTime(System.currentTimeMillis());
            log.info("Scene activated: {}", sceneId);
        });
    }

    @Override
    public CompletableFuture<Void> deactivate(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            if (sceneId == null || sceneId.trim().isEmpty()) {
                log.warn("Cannot deactivate scene with null or empty id");
                return;
            }

            SceneStatusInfo status = sceneStatusInfos.get(sceneId);
            if (status == null) {
                log.warn("Scene not found for deactivation: {}", sceneId);
                return;
            }

            if (!status.isActive()) {
                log.debug("Scene already inactive: {}", sceneId);
                return;
            }

            status.setActive(false);
            status.setLastUpdateTime(System.currentTimeMillis());
            log.info("Scene deactivated: {}", sceneId);
        });
    }

    @Override
    public CompletableFuture<SceneState> getState(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneStatusInfo status = sceneStatusInfos.get(sceneId);
            if (status == null) {
                return null;
            }
            // 根据 active 状态返回对应的枚举值
            return status.isActive() ? SceneState.RUNNING : SceneState.STOPPED;
        });
    }

    @Override
    public CompletableFuture<Void> addCapability(String sceneId, Capability capability) {
        return CompletableFuture.runAsync(() -> {
            if (sceneId == null || sceneId.trim().isEmpty()) {
                log.warn("Cannot add capability to scene with null or empty id");
                return;
            }

            if (capability == null) {
                log.warn("Cannot add null capability to scene: {}", sceneId);
                return;
            }

            SceneDefinition definition = scenes.get(sceneId);
            if (definition != null) {
                if (definition.getCapabilities() == null) {
                    definition.setCapabilities(new ArrayList<Capability>());
                }
                definition.getCapabilities().add(capability);
                log.info("Capability {} added to scene {}", capability.getCapId(), sceneId);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeCapability(String sceneId, String capId) {
        return CompletableFuture.runAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            if (definition != null && definition.getCapabilities() != null) {
                definition.getCapabilities().removeIf(c -> capId.equals(c.getCapId()));
                log.info("Capability {} removed from scene {}", capId, sceneId);
            }
        });
    }

    @Override
    public CompletableFuture<List<Capability>> listCapabilities(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            return definition != null && definition.getCapabilities() != null
                    ? new ArrayList<>(definition.getCapabilities())
                    : new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<Capability> getCapability(String sceneId, String capId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            if (definition != null && definition.getCapabilities() != null) {
                for (Capability cap : definition.getCapabilities()) {
                    if (capId.equals(cap.getCapId())) {
                        return cap;
                    }
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> addCollaborativeScene(String sceneId, String collaborativeSceneId) {
        return CompletableFuture.runAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            if (definition != null) {
                if (definition.getCollaborativeScenes() == null) {
                    definition.setCollaborativeScenes(new ArrayList<>());
                }
                if (!definition.getCollaborativeScenes().contains(collaborativeSceneId)) {
                    definition.getCollaborativeScenes().add(collaborativeSceneId);
                    log.info("Collaborative scene {} added to {}", collaborativeSceneId, sceneId);
                }
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeCollaborativeScene(String sceneId, String collaborativeSceneId) {
        return CompletableFuture.runAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            if (definition != null && definition.getCollaborativeScenes() != null) {
                definition.getCollaborativeScenes().remove(collaborativeSceneId);
                log.info("Collaborative scene {} removed from {}", collaborativeSceneId, sceneId);
            }
        });
    }

    @Override
    public CompletableFuture<List<String>> listCollaborativeScenes(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            return definition != null && definition.getCollaborativeScenes() != null
                    ? new ArrayList<>(definition.getCollaborativeScenes())
                    : new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<Void> updateConfig(String sceneId, Map<String, Object> config) {
        return CompletableFuture.runAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            if (definition != null) {
                if (definition.getConfig() == null) {
                    definition.setConfig(new ConcurrentHashMap<>());
                }
                definition.getConfig().putAll(config);

                SceneStatusInfo status = sceneStatusInfos.get(sceneId);
                if (status != null) {
                    status.setLastUpdateTime(System.currentTimeMillis());
                }

                log.info("Config updated for scene {}", sceneId);
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, Object>> getConfig(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            return definition != null ? definition.getConfig() : null;
        });
    }

    @Override
    public CompletableFuture<SceneSnapshot> createSnapshot(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneDefinition definition = scenes.get(sceneId);
            SceneStatusInfo status = sceneStatusInfos.get(sceneId);

            if (definition == null) {
                return null;
            }

            SceneSnapshot snapshot = new SceneSnapshot();
            snapshot.setSceneId(sceneId);
            snapshot.setSnapshotId(java.util.UUID.randomUUID().toString());
            snapshot.setCreateTime(System.currentTimeMillis());
            snapshot.setVersion(definition.getVersion());
            snapshot.setSkills(status != null ? new ArrayList<>(status.getInstalledSkills()) : new ArrayList<>());
            snapshot.setConfig(new ConcurrentHashMap<>(definition.getConfig()));

            log.info("Snapshot created for scene {}: {}", sceneId, snapshot.getSnapshotId());

            return snapshot;
        });
    }

    @Override
    public CompletableFuture<Void> restoreSnapshot(String sceneId, SceneSnapshot snapshot) {
        return CompletableFuture.runAsync(() -> {
            if (snapshot == null || !sceneId.equals(snapshot.getSceneId())) {
                return;
            }

            SceneDefinition definition = scenes.get(sceneId);
            SceneStatusInfo status = sceneStatusInfos.get(sceneId);

            if (definition != null && snapshot.getConfig() != null) {
                definition.setConfig(new ConcurrentHashMap<>(snapshot.getConfig()));
            }

            if (status != null && snapshot.getSkills() != null) {
                status.setInstalledSkills(new ArrayList<>(snapshot.getSkills()));
                status.setLastUpdateTime(System.currentTimeMillis());
            }

            log.info("Snapshot {} restored for scene {}", snapshot.getSnapshotId(), sceneId);
        });
    }

    @Override
    public CompletableFuture<String> startWorkflow(String sceneId, String workflowId) {
        return CompletableFuture.supplyAsync(() -> {
            log.warn("Workflow execution is not supported in SDK SceneManagerImpl. " +
                    "Please use SceneEngine for workflow functionality.");
            throw new UnsupportedOperationException(
                    "Workflow execution moved to scene-engine. Use EngineSceneManager instead.");
        });
    }

    @Override
    public CompletableFuture<Void> stopWorkflow(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            log.warn("Workflow execution is not supported in SDK SceneManagerImpl. " +
                    "Please use SceneEngine for workflow functionality.");
            throw new UnsupportedOperationException(
                    "Workflow execution moved to scene-engine. Use EngineSceneManager instead.");
        });
    }

    @Override
    public CompletableFuture<String> getWorkflowStatus(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneStatusInfo status = sceneStatusInfos.get(sceneId);
            return status != null ? status.getWorkflowStatus() : null;
        });
    }

    // 新增生命周期管理方法
    @Override
    public CompletableFuture<Void> initializeScene(String sceneId, SceneConfig config) {
        return CompletableFuture.runAsync(() -> {
            log.info("Initializing scene: {}", sceneId);
            notifySceneCreated(sceneId);
        });
    }

    @Override
    public CompletableFuture<Void> startScene(String sceneId) {
        return activate(sceneId);
    }

    @Override
    public CompletableFuture<Void> stopScene(String sceneId) {
        return deactivate(sceneId);
    }

    @Override
    public CompletableFuture<Void> pauseScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Pausing scene: {}", sceneId);
        });
    }

    @Override
    public CompletableFuture<Void> resumeScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Resuming scene: {}", sceneId);
        });
    }

    @Override
    public CompletableFuture<Void> destroyScene(String sceneId) {
        return delete(sceneId);
    }

    @Override
    public boolean isSceneActive(String sceneId) {
        SceneStatusInfo status = sceneStatusInfos.get(sceneId);
        return status != null && status.isActive();
    }

    @Override
    public boolean isScenePaused(String sceneId) {
        return false;
    }

    @Override
    public CompletableFuture<Void> reloadScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Reloading scene: {}", sceneId);
        });
    }

    @Override
    public CompletableFuture<Void> restartScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Restarting scene: {}", sceneId);
            deactivate(sceneId).join();
            activate(sceneId).join();
        });
    }

    @Override
    public List<String> getActiveScenes() {
        List<String> active = new ArrayList<>();
        for (Map.Entry<String, SceneStatusInfo> entry : sceneStatusInfos.entrySet()) {
            if (entry.getValue().isActive()) {
                active.add(entry.getKey());
            }
        }
        return active;
    }

    @Override
    public List<String> getPausedScenes() {
        return new ArrayList<>();
    }

    @Override
    public SceneLifecycleStats getStats(String sceneId) {
        return new SceneLifecycleStats();
    }

    @Override
    public void addLifecycleListener(SceneLifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    @Override
    public void removeLifecycleListener(SceneLifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    private void notifySceneCreated(String sceneId) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onSceneCreated(sceneId);
            } catch (Exception e) {
                log.warn("Lifecycle listener error", e);
            }
        }
    }
}
