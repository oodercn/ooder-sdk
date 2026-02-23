package net.ooder.sdk.scene;

public interface SceneLifecycleListener {
    
    void onSceneCreated(String sceneId);
    
    void onSceneInitializing(String sceneId);
    
    void onSceneInitialized(String sceneId);
    
    void onSceneStarting(String sceneId);
    
    void onSceneStarted(String sceneId);
    
    void onScenePausing(String sceneId);
    
    void onScenePaused(String sceneId);
    
    void onSceneResuming(String sceneId);
    
    void onSceneResumed(String sceneId);
    
    void onSceneStopping(String sceneId);
    
    void onSceneStopped(String sceneId);
    
    void onSceneDestroying(String sceneId);
    
    void onSceneDestroyed(String sceneId);
    
    void onSceneError(String sceneId, String error);
}
