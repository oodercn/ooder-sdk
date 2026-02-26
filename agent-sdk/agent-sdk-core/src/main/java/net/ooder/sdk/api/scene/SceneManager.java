package net.ooder.sdk.api.scene;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.ooder.sdk.api.scene.model.SceneConfig;
import net.ooder.sdk.api.scene.model.SceneLifecycleStats;
import net.ooder.sdk.api.scene.model.SceneState;
import net.ooder.sdk.api.skill.Capability;

public interface SceneManager {
    
    CompletableFuture<SceneDefinition> create(SceneDefinition definition);
    
    CompletableFuture<Void> delete(String sceneId);
    
    CompletableFuture<SceneDefinition> get(String sceneId);
    
    CompletableFuture<List<SceneDefinition>> listAll();
    
    CompletableFuture<Void> activate(String sceneId);
    
    CompletableFuture<Void> deactivate(String sceneId);
    
    CompletableFuture<SceneState> getState(String sceneId);
    
    CompletableFuture<Void> addCapability(String sceneId, Capability capability);
    
    CompletableFuture<Void> removeCapability(String sceneId, String capId);
    
    CompletableFuture<List<Capability>> listCapabilities(String sceneId);
    
    CompletableFuture<Capability> getCapability(String sceneId, String capId);
    
    CompletableFuture<Void> addCollaborativeScene(String sceneId, String collaborativeSceneId);
    
    CompletableFuture<Void> removeCollaborativeScene(String sceneId, String collaborativeSceneId);
    
    CompletableFuture<List<String>> listCollaborativeScenes(String sceneId);
    
    CompletableFuture<Void> updateConfig(String sceneId, Map<String, Object> config);
    
    CompletableFuture<Map<String, Object>> getConfig(String sceneId);
    
    CompletableFuture<SceneSnapshot> createSnapshot(String sceneId);
    
    CompletableFuture<Void> restoreSnapshot(String sceneId, SceneSnapshot snapshot);
    
    CompletableFuture<String> startWorkflow(String sceneId, String workflowId);
    
    CompletableFuture<Void> stopWorkflow(String sceneId);
    
    CompletableFuture<String> getWorkflowStatus(String sceneId);
    
    CompletableFuture<Void> initializeScene(String sceneId, SceneConfig config);
    
    CompletableFuture<Void> startScene(String sceneId);
    
    CompletableFuture<Void> stopScene(String sceneId);
    
    CompletableFuture<Void> pauseScene(String sceneId);
    
    CompletableFuture<Void> resumeScene(String sceneId);
    
    CompletableFuture<Void> destroyScene(String sceneId);
    
    boolean isSceneActive(String sceneId);
    
    boolean isScenePaused(String sceneId);
    
    CompletableFuture<Void> reloadScene(String sceneId);
    
    CompletableFuture<Void> restartScene(String sceneId);
    
    List<String> getActiveScenes();
    
    List<String> getPausedScenes();
    
    SceneLifecycleStats getStats(String sceneId);
    
    void addLifecycleListener(SceneLifecycleListener listener);
    
    void removeLifecycleListener(SceneLifecycleListener listener);
    
    interface SceneLifecycleListener {
        void onSceneCreated(String sceneId);
        void onSceneInitialized(String sceneId);
        void onSceneStarted(String sceneId);
        void onScenePaused(String sceneId);
        void onSceneResumed(String sceneId);
        void onSceneStopped(String sceneId);
        void onSceneDestroyed(String sceneId);
        void onSceneError(String sceneId, Throwable error);
    }
    
    class SceneStatusInfo {
        private String sceneId;
        private boolean active;
        private int memberCount;
        private List<String> installedSkills;
        private long createTime;
        private long lastUpdateTime;
        private String currentWorkflowId;
        private String workflowStatus;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public int getMemberCount() { return memberCount; }
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
        public List<String> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
        public String getCurrentWorkflowId() { return currentWorkflowId; }
        public void setCurrentWorkflowId(String currentWorkflowId) { this.currentWorkflowId = currentWorkflowId; }
        public String getWorkflowStatus() { return workflowStatus; }
        public void setWorkflowStatus(String workflowStatus) { this.workflowStatus = workflowStatus; }
    }
}
