package net.ooder.engine.scene.core;

public class SceneGroupException extends RuntimeException {
    private final String sceneGroupId;
    
    public SceneGroupException(String sceneGroupId, String message) {
        super(message);
        this.sceneGroupId = sceneGroupId;
    }
    
    public SceneGroupException(String sceneGroupId, String message, Throwable cause) {
        super(message, cause);
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
}
