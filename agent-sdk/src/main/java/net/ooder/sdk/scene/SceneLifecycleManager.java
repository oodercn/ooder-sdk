package net.ooder.sdk.scene;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SceneLifecycleManager {
    
    CompletableFuture<Void> initializeScene(String sceneId, SceneConfig config);
    
    CompletableFuture<Void> startScene(String sceneId);
    
    CompletableFuture<Void> stopScene(String sceneId);
    
    CompletableFuture<Void> pauseScene(String sceneId);
    
    CompletableFuture<Void> resumeScene(String sceneId);
    
    CompletableFuture<Void> destroyScene(String sceneId);
    
    SceneState getSceneState(String sceneId);
    
    boolean isSceneActive(String sceneId);
    
    boolean isScenePaused(String sceneId);
    
    void addLifecycleListener(SceneLifecycleListener listener);
    
    void removeLifecycleListener(SceneLifecycleListener listener);
    
    CompletableFuture<Void> reloadScene(String sceneId);
    
    CompletableFuture<Void> restartScene(String sceneId);
    
    List<String> getActiveScenes();
    
    List<String> getPausedScenes();
    
    SceneLifecycleStats getStats(String sceneId);
    
    enum SceneState {
        CREATED,
        INITIALIZING,
        INITIALIZED,
        STARTING,
        RUNNING,
        PAUSING,
        PAUSED,
        RESUMING,
        STOPPING,
        STOPPED,
        DESTROYING,
        DESTROYED,
        ERROR
    }
    
    class SceneConfig {
        private String sceneId;
        private String sceneName;
        private Map<String, String> interfaceBindings;
        private Map<String, Object> properties;
        private boolean autoStart;
        private long startupTimeout;
        private long shutdownTimeout;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        
        public String getSceneName() { return sceneName; }
        public void setSceneName(String sceneName) { this.sceneName = sceneName; }
        
        public Map<String, String> getInterfaceBindings() { return interfaceBindings; }
        public void setInterfaceBindings(Map<String, String> interfaceBindings) { this.interfaceBindings = interfaceBindings; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        
        public boolean isAutoStart() { return autoStart; }
        public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
        
        public long getStartupTimeout() { return startupTimeout; }
        public void setStartupTimeout(long startupTimeout) { this.startupTimeout = startupTimeout; }
        
        public long getShutdownTimeout() { return shutdownTimeout; }
        public void setShutdownTimeout(long shutdownTimeout) { this.shutdownTimeout = shutdownTimeout; }
    }
    
    class SceneLifecycleStats {
        private String sceneId;
        private SceneState state;
        private long createdTime;
        private long startedTime;
        private long lastStateChange;
        private long totalUptime;
        private int restartCount;
        private int errorCount;
        private String lastError;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        
        public SceneState getState() { return state; }
        public void setState(SceneState state) { this.state = state; }
        
        public long getCreatedTime() { return createdTime; }
        public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
        
        public long getStartedTime() { return startedTime; }
        public void setStartedTime(long startedTime) { this.startedTime = startedTime; }
        
        public long getLastStateChange() { return lastStateChange; }
        public void setLastStateChange(long lastStateChange) { this.lastStateChange = lastStateChange; }
        
        public long getTotalUptime() { return totalUptime; }
        public void setTotalUptime(long totalUptime) { this.totalUptime = totalUptime; }
        
        public int getRestartCount() { return restartCount; }
        public void setRestartCount(int restartCount) { this.restartCount = restartCount; }
        
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
        
        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
    }
}
