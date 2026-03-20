package net.ooder.sdk.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.scene.SceneDefinition;
import net.ooder.sdk.api.scene.SceneGroup;
import net.ooder.sdk.api.scene.SceneManager;
import net.ooder.sdk.api.scene.SceneSnapshot;
import net.ooder.sdk.api.scene.model.SceneConfig;
import net.ooder.sdk.api.scene.model.SceneLifecycleStats;
import net.ooder.sdk.api.scene.model.SceneState;
import net.ooder.skills.sync.UserSceneGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneManagerImpl implements SceneManager {
    
    private static final Logger log = LoggerFactory.getLogger(SceneManagerImpl.class);
    
    private final Map<String, SceneDefinition> sceneDefinitions = new ConcurrentHashMap<>();
    private final Map<String, SceneState> sceneStates = new ConcurrentHashMap<>();
    private final Map<String, SceneGroup> activeSceneGroups = new ConcurrentHashMap<>();
    private final Map<String, List<Capability>> sceneCapabilities = new ConcurrentHashMap<>();
    private final Map<String, List<String>> collaborativeScenes = new ConcurrentHashMap<>();
    private final Map<String, String> workflowInstances = new ConcurrentHashMap<>();
    private final List<SceneLifecycleListener> lifecycleListeners = new ArrayList<>();
    private final Map<String, SceneLifecycleStats> lifecycleStats = new ConcurrentHashMap<>();
    
    private final Map<String, UserSceneGroup> userSceneGroups = new ConcurrentHashMap<>();
    
    public SceneManagerImpl() {
        log.info("SceneManagerImpl initialized");
    }
    
    @Override
    public CompletableFuture<SceneDefinition> create(SceneDefinition definition) {
        return CompletableFuture.supplyAsync(() -> {
            String sceneId = definition.getSceneId();
            if (sceneId == null || sceneId.isEmpty()) {
                sceneId = "scene_" + System.currentTimeMillis();
                definition.setSceneId(sceneId);
            }
            
            final String finalSceneId = sceneId;
            sceneDefinitions.put(finalSceneId, definition);
            sceneStates.put(finalSceneId, SceneState.CREATED);
            
            SceneLifecycleStats stats = new SceneLifecycleStats();
            stats.setSceneId(finalSceneId);
            stats.setCreatedTime(System.currentTimeMillis());
            stats.setState(SceneState.CREATED);
            lifecycleStats.put(finalSceneId, stats);
            
            notifyListeners(l -> l.onSceneCreated(finalSceneId));
            
            log.info("Scene created: {}", finalSceneId);
            return definition;
        });
    }
    
    @Override
    public CompletableFuture<Void> delete(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneDefinitions.remove(sceneId);
            sceneStates.remove(sceneId);
            activeSceneGroups.remove(sceneId);
            sceneCapabilities.remove(sceneId);
            collaborativeScenes.remove(sceneId);
            workflowInstances.remove(sceneId);
            lifecycleStats.remove(sceneId);
            
            notifyListeners(l -> l.onSceneDestroyed(sceneId));
            
            log.info("Scene deleted: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<SceneDefinition> get(String sceneId) {
        return CompletableFuture.completedFuture(sceneDefinitions.get(sceneId));
    }
    
    @Override
    public CompletableFuture<List<SceneDefinition>> listAll() {
        return CompletableFuture.completedFuture(new ArrayList<>(sceneDefinitions.values()));
    }
    
    @Override
    public CompletableFuture<Void> activate(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.ACTIVE);
            updateStats(sceneId, SceneState.ACTIVE);
            notifyListeners(l -> l.onSceneStarted(sceneId));
            log.info("Scene activated: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> deactivate(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.INACTIVE);
            updateStats(sceneId, SceneState.INACTIVE);
            notifyListeners(l -> l.onSceneStopped(sceneId));
            log.info("Scene deactivated: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<SceneState> getState(String sceneId) {
        return CompletableFuture.completedFuture(sceneStates.get(sceneId));
    }
    
    @Override
    public CompletableFuture<Void> addCapability(String sceneId, Capability capability) {
        return CompletableFuture.runAsync(() -> {
            sceneCapabilities.computeIfAbsent(sceneId, k -> new ArrayList<>()).add(capability);
            log.debug("Capability added to scene {}: {}", sceneId, capability.getCapId());
        });
    }
    
    @Override
    public CompletableFuture<Void> removeCapability(String sceneId, String capId) {
        return CompletableFuture.runAsync(() -> {
            List<Capability> caps = sceneCapabilities.get(sceneId);
            if (caps != null) {
                caps.removeIf(c -> capId.equals(c.getCapId()));
            }
            log.debug("Capability removed from scene {}: {}", sceneId, capId);
        });
    }
    
    @Override
    public CompletableFuture<List<Capability>> listCapabilities(String sceneId) {
        return CompletableFuture.completedFuture(
            sceneCapabilities.getOrDefault(sceneId, new ArrayList<>())
        );
    }
    
    @Override
    public CompletableFuture<Capability> getCapability(String sceneId, String capId) {
        List<Capability> caps = sceneCapabilities.get(sceneId);
        if (caps != null) {
            for (Capability cap : caps) {
                if (capId.equals(cap.getCapId())) {
                    return CompletableFuture.completedFuture(cap);
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public CompletableFuture<Void> addCollaborativeScene(String sceneId, String collaborativeSceneId) {
        return CompletableFuture.runAsync(() -> {
            collaborativeScenes.computeIfAbsent(sceneId, k -> new ArrayList<>()).add(collaborativeSceneId);
            log.debug("Collaborative scene added: {} -> {}", sceneId, collaborativeSceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> removeCollaborativeScene(String sceneId, String collaborativeSceneId) {
        return CompletableFuture.runAsync(() -> {
            List<String> scenes = collaborativeScenes.get(sceneId);
            if (scenes != null) {
                scenes.remove(collaborativeSceneId);
            }
        });
    }
    
    @Override
    public CompletableFuture<List<String>> listCollaborativeScenes(String sceneId) {
        return CompletableFuture.completedFuture(
            collaborativeScenes.getOrDefault(sceneId, new ArrayList<>())
        );
    }
    
    @Override
    public CompletableFuture<Void> updateConfig(String sceneId, Map<String, Object> config) {
        return CompletableFuture.runAsync(() -> {
            SceneDefinition def = sceneDefinitions.get(sceneId);
            if (def != null && def.getConfig() != null) {
                def.getConfig().putAll(config);
            }
        });
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> getConfig(String sceneId) {
        SceneDefinition def = sceneDefinitions.get(sceneId);
        return CompletableFuture.completedFuture(def != null ? def.getConfig() : null);
    }
    
    @Override
    public CompletableFuture<SceneSnapshot> createSnapshot(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneSnapshot snapshot = new SceneSnapshot();
            snapshot.setSceneId(sceneId);
            snapshot.setCreateTime(System.currentTimeMillis());
            return snapshot;
        });
    }
    
    @Override
    public CompletableFuture<Void> restoreSnapshot(String sceneId, SceneSnapshot snapshot) {
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public CompletableFuture<String> startWorkflow(String sceneId, String workflowId) {
        return CompletableFuture.supplyAsync(() -> {
            String instanceId = "wf_" + System.currentTimeMillis();
            workflowInstances.put(sceneId, instanceId);
            log.info("Workflow started: {} for scene: {}", workflowId, sceneId);
            return instanceId;
        });
    }
    
    @Override
    public CompletableFuture<Void> stopWorkflow(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            workflowInstances.remove(sceneId);
            log.info("Workflow stopped for scene: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<String> getWorkflowStatus(String sceneId) {
        String instanceId = workflowInstances.get(sceneId);
        return CompletableFuture.completedFuture(instanceId != null ? "RUNNING" : "STOPPED");
    }
    
    @Override
    public CompletableFuture<Void> initializeScene(String sceneId, SceneConfig config) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.INITIALIZED);
            updateStats(sceneId, SceneState.INITIALIZED);
            notifyListeners(l -> l.onSceneInitialized(sceneId));
            log.info("Scene initialized: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> startScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.RUNNING);
            updateStats(sceneId, SceneState.RUNNING);
            notifyListeners(l -> l.onSceneStarted(sceneId));
            log.info("Scene started: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> stopScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.STOPPED);
            updateStats(sceneId, SceneState.STOPPED);
            notifyListeners(l -> l.onSceneStopped(sceneId));
            log.info("Scene stopped: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> pauseScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.PAUSED);
            updateStats(sceneId, SceneState.PAUSED);
            notifyListeners(l -> l.onScenePaused(sceneId));
            log.info("Scene paused: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> resumeScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            sceneStates.put(sceneId, SceneState.RUNNING);
            updateStats(sceneId, SceneState.RUNNING);
            notifyListeners(l -> l.onSceneResumed(sceneId));
            log.info("Scene resumed: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> destroyScene(String sceneId) {
        return delete(sceneId);
    }
    
    @Override
    public boolean isSceneActive(String sceneId) {
        SceneState state = sceneStates.get(sceneId);
        return state == SceneState.ACTIVE || state == SceneState.RUNNING;
    }
    
    @Override
    public boolean isScenePaused(String sceneId) {
        SceneState state = sceneStates.get(sceneId);
        return state == SceneState.PAUSED;
    }
    
    @Override
    public CompletableFuture<Void> reloadScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Scene reloaded: {}", sceneId);
        });
    }
    
    @Override
    public CompletableFuture<Void> restartScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            stopScene(sceneId).join();
            startScene(sceneId).join();
            log.info("Scene restarted: {}", sceneId);
        });
    }
    
    @Override
    public List<String> getActiveScenes() {
        List<String> active = new ArrayList<>();
        for (Map.Entry<String, SceneState> entry : sceneStates.entrySet()) {
            if (entry.getValue() == SceneState.ACTIVE || entry.getValue() == SceneState.RUNNING) {
                active.add(entry.getKey());
            }
        }
        return active;
    }
    
    @Override
    public List<String> getPausedScenes() {
        List<String> paused = new ArrayList<>();
        for (Map.Entry<String, SceneState> entry : sceneStates.entrySet()) {
            if (entry.getValue() == SceneState.PAUSED) {
                paused.add(entry.getKey());
            }
        }
        return paused;
    }
    
    @Override
    public SceneLifecycleStats getStats(String sceneId) {
        return lifecycleStats.get(sceneId);
    }
    
    @Override
    public void addLifecycleListener(SceneLifecycleListener listener) {
        if (listener != null && !lifecycleListeners.contains(listener)) {
            lifecycleListeners.add(listener);
        }
    }
    
    @Override
    public void removeLifecycleListener(SceneLifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }
    
    @Override
    public CompletableFuture<SceneGroup> getSceneGroup(String sceneId) {
        SceneGroup group = activeSceneGroups.get(sceneId);
        if (group == null && isSceneActive(sceneId)) {
            group = createSceneGroup(sceneId);
            activeSceneGroups.put(sceneId, group);
        }
        return CompletableFuture.completedFuture(group);
    }
    
    @Override
    public CompletableFuture<UserSceneGroup> getUserSceneGroup(String sceneGroupId, String userId) {
        String key = sceneGroupId + ":" + userId;
        UserSceneGroup userGroup = userSceneGroups.get(key);
        return CompletableFuture.completedFuture(userGroup);
    }
    
    @Override
    public CompletableFuture<List<UserSceneGroup>> getUserSceneGroups(String userId) {
        List<UserSceneGroup> result = new ArrayList<>();
        for (Map.Entry<String, UserSceneGroup> entry : userSceneGroups.entrySet()) {
            if (entry.getKey().endsWith(":" + userId)) {
                result.add(entry.getValue());
            }
        }
        return CompletableFuture.completedFuture(result);
    }
    
    public void registerUserSceneGroup(String sceneGroupId, String userId, UserSceneGroup userSceneGroup) {
        String key = sceneGroupId + ":" + userId;
        userSceneGroups.put(key, userSceneGroup);
        log.debug("UserSceneGroup registered: {}", key);
    }
    
    public void unregisterUserSceneGroup(String sceneGroupId, String userId) {
        String key = sceneGroupId + ":" + userId;
        userSceneGroups.remove(key);
        log.debug("UserSceneGroup unregistered: {}", key);
    }
    
    private SceneGroup createSceneGroup(String sceneId) {
        SceneGroup group = new SceneGroup();
        group.setSceneId(sceneId);
        group.setSceneGroupId("sg_" + sceneId);
        group.setStatus("ACTIVE");
        group.setCreateTime(System.currentTimeMillis());
        return group;
    }
    
    private void updateStats(String sceneId, SceneState state) {
        SceneLifecycleStats stats = lifecycleStats.get(sceneId);
        if (stats != null) {
            stats.setState(state);
            stats.setLastStateChange(System.currentTimeMillis());
        }
    }
    
    private void notifyListeners(java.util.function.Consumer<SceneLifecycleListener> action) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                log.error("Error notifying listener", e);
            }
        }
    }
}
