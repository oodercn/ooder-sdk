package net.ooder.sdk.scene.impl;

import net.ooder.sdk.scene.SceneInterfaceManager;
import net.ooder.sdk.scene.SceneLifecycleListener;
import net.ooder.sdk.scene.SceneLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SceneLifecycleManagerImpl implements SceneLifecycleManager {
    
    private static final Logger log = LoggerFactory.getLogger(SceneLifecycleManagerImpl.class);
    
    private final SceneInterfaceManager interfaceManager;
    private final Map<String, SceneState> sceneStates = new ConcurrentHashMap<>();
    private final Map<String, SceneConfig> sceneConfigs = new ConcurrentHashMap<>();
    private final Map<String, SceneLifecycleStats> sceneStats = new ConcurrentHashMap<>();
    private final List<SceneLifecycleListener> listeners = new CopyOnWriteArrayList<>();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public SceneLifecycleManagerImpl(SceneInterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
    }
    
    @Override
    public CompletableFuture<Void> initializeScene(String sceneId, SceneConfig config) {
        return CompletableFuture.runAsync(() -> {
            setState(sceneId, SceneState.INITIALIZING);
            
            try {
                interfaceManager.initialize(sceneId);
                
                if (config != null && config.getInterfaceBindings() != null) {
                    interfaceManager.bindInterfaces(sceneId, config.getInterfaceBindings()).join();
                }
                
                sceneConfigs.put(sceneId, config);
                
                SceneLifecycleStats stats = new SceneLifecycleStats();
                stats.setSceneId(sceneId);
                stats.setState(SceneState.INITIALIZED);
                stats.setCreatedTime(System.currentTimeMillis());
                stats.setLastStateChange(System.currentTimeMillis());
                sceneStats.put(sceneId, stats);
                
                setState(sceneId, SceneState.INITIALIZED);
                
                log.info("Scene initialized: {}", sceneId);
                
                if (config != null && config.isAutoStart()) {
                    startScene(sceneId).join();
                }
                
            } catch (Exception e) {
                setState(sceneId, SceneState.ERROR);
                updateErrorStats(sceneId, e.getMessage());
                throw new RuntimeException("Failed to initialize scene: " + sceneId, e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> startScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            SceneState currentState = sceneStates.get(sceneId);
            if (currentState != SceneState.INITIALIZED && currentState != SceneState.STOPPED) {
                throw new IllegalStateException("Cannot start scene in state: " + currentState);
            }
            
            setState(sceneId, SceneState.STARTING);
            
            try {
                setState(sceneId, SceneState.RUNNING);
                
                SceneLifecycleStats stats = sceneStats.get(sceneId);
                if (stats != null) {
                    stats.setStartedTime(System.currentTimeMillis());
                }
                
                log.info("Scene started: {}", sceneId);
                
            } catch (Exception e) {
                setState(sceneId, SceneState.ERROR);
                updateErrorStats(sceneId, e.getMessage());
                throw new RuntimeException("Failed to start scene: " + sceneId, e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> stopScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            SceneState currentState = sceneStates.get(sceneId);
            if (currentState != SceneState.RUNNING && currentState != SceneState.PAUSED) {
                return;
            }
            
            setState(sceneId, SceneState.STOPPING);
            
            try {
                setState(sceneId, SceneState.STOPPED);
                log.info("Scene stopped: {}", sceneId);
                
            } catch (Exception e) {
                setState(sceneId, SceneState.ERROR);
                updateErrorStats(sceneId, e.getMessage());
                throw new RuntimeException("Failed to stop scene: " + sceneId, e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> pauseScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            if (sceneStates.get(sceneId) != SceneState.RUNNING) {
                return;
            }
            
            setState(sceneId, SceneState.PAUSING);
            
            try {
                setState(sceneId, SceneState.PAUSED);
                log.info("Scene paused: {}", sceneId);
                
            } catch (Exception e) {
                setState(sceneId, SceneState.ERROR);
                updateErrorStats(sceneId, e.getMessage());
                throw new RuntimeException("Failed to pause scene: " + sceneId, e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> resumeScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            if (sceneStates.get(sceneId) != SceneState.PAUSED) {
                return;
            }
            
            setState(sceneId, SceneState.RESUMING);
            
            try {
                setState(sceneId, SceneState.RUNNING);
                log.info("Scene resumed: {}", sceneId);
                
            } catch (Exception e) {
                setState(sceneId, SceneState.ERROR);
                updateErrorStats(sceneId, e.getMessage());
                throw new RuntimeException("Failed to resume scene: " + sceneId, e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> destroyScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            setState(sceneId, SceneState.DESTROYING);
            
            try {
                interfaceManager.clearScene(sceneId);
                
                sceneConfigs.remove(sceneId);
                sceneStates.remove(sceneId);
                sceneStats.remove(sceneId);
                
                setState(sceneId, SceneState.DESTROYED);
                log.info("Scene destroyed: {}", sceneId);
                
            } catch (Exception e) {
                setState(sceneId, SceneState.ERROR);
                updateErrorStats(sceneId, e.getMessage());
                throw new RuntimeException("Failed to destroy scene: " + sceneId, e);
            }
        }, executor);
    }
    
    @Override
    public SceneState getSceneState(String sceneId) {
        return sceneStates.get(sceneId);
    }
    
    @Override
    public boolean isSceneActive(String sceneId) {
        SceneState state = sceneStates.get(sceneId);
        return state == SceneState.RUNNING || state == SceneState.PAUSED;
    }
    
    @Override
    public boolean isScenePaused(String sceneId) {
        return sceneStates.get(sceneId) == SceneState.PAUSED;
    }
    
    @Override
    public void addLifecycleListener(SceneLifecycleListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeLifecycleListener(SceneLifecycleListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public CompletableFuture<Void> reloadScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            SceneConfig config = sceneConfigs.get(sceneId);
            if (config == null) {
                throw new RuntimeException("Scene config not found: " + sceneId);
            }
            
            stopScene(sceneId).join();
            interfaceManager.clearScene(sceneId);
            initializeScene(sceneId, config).join();
            
            log.info("Scene reloaded: {}", sceneId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> restartScene(String sceneId) {
        return CompletableFuture.runAsync(() -> {
            stopScene(sceneId).join();
            startScene(sceneId).join();
            
            SceneLifecycleStats stats = sceneStats.get(sceneId);
            if (stats != null) {
                stats.setRestartCount(stats.getRestartCount() + 1);
            }
            
            log.info("Scene restarted: {}", sceneId);
        }, executor);
    }
    
    @Override
    public List<String> getActiveScenes() {
        List<String> active = new ArrayList<>();
        for (Map.Entry<String, SceneState> entry : sceneStates.entrySet()) {
            if (entry.getValue() == SceneState.RUNNING) {
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
        return sceneStats.get(sceneId);
    }
    
    private void setState(String sceneId, SceneState newState) {
        SceneState oldState = sceneStates.put(sceneId, newState);
        
        SceneLifecycleStats stats = sceneStats.get(sceneId);
        if (stats != null) {
            stats.setState(newState);
            stats.setLastStateChange(System.currentTimeMillis());
        }
        
        notifyStateChange(sceneId, oldState, newState);
    }
    
    private void notifyStateChange(String sceneId, SceneState oldState, SceneState newState) {
        for (SceneLifecycleListener listener : listeners) {
            try {
                switch (newState) {
                    case CREATED: listener.onSceneCreated(sceneId); break;
                    case INITIALIZING: listener.onSceneInitializing(sceneId); break;
                    case INITIALIZED: listener.onSceneInitialized(sceneId); break;
                    case STARTING: listener.onSceneStarting(sceneId); break;
                    case RUNNING: listener.onSceneStarted(sceneId); break;
                    case PAUSING: listener.onScenePausing(sceneId); break;
                    case PAUSED: listener.onScenePaused(sceneId); break;
                    case RESUMING: listener.onSceneResuming(sceneId); break;
                    case STOPPING: listener.onSceneStopping(sceneId); break;
                    case STOPPED: listener.onSceneStopped(sceneId); break;
                    case DESTROYING: listener.onSceneDestroying(sceneId); break;
                    case DESTROYED: listener.onSceneDestroyed(sceneId); break;
                    case ERROR: listener.onSceneError(sceneId, "State error"); break;
                }
            } catch (Exception e) {
                log.warn("Listener error for scene {}", sceneId, e);
            }
        }
    }
    
    private void updateErrorStats(String sceneId, String error) {
        SceneLifecycleStats stats = sceneStats.get(sceneId);
        if (stats != null) {
            stats.setErrorCount(stats.getErrorCount() + 1);
            stats.setLastError(error);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
        for (String sceneId : new ArrayList<>(sceneStates.keySet())) {
            try {
                destroyScene(sceneId).join();
            } catch (Exception e) {
                log.warn("Failed to destroy scene during shutdown: {}", sceneId, e);
            }
        }
        log.info("Scene lifecycle manager shutdown");
    }
}
