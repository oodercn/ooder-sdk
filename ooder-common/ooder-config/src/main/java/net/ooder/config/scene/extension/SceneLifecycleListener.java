package net.ooder.config.scene.extension;

import net.ooder.config.scene.SceneConfig;

public interface SceneLifecycleListener {
    
    void onSceneInitializing(SceneConfig config);
    
    void onSceneInitialized(SceneConfig config);
    
    void onSceneError(SceneConfig config, Exception error);
    
    void onSceneClosing(SceneConfig config);
    
    void onSceneClosed(SceneConfig config);
}
