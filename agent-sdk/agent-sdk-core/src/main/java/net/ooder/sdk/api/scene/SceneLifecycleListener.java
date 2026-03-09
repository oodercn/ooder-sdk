package net.ooder.sdk.api.scene;

public interface SceneLifecycleListener {
    void onSceneCreated(String sceneId, SceneDefinition definition);
    void onSceneActivated(String sceneId);
    void onSceneDeactivated(String sceneId);
    void onSceneDeleted(String sceneId);
    void onSceneError(String sceneId, Throwable error);
}
