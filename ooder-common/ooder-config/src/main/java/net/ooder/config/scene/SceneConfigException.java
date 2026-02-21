package net.ooder.config.scene;

public class SceneConfigException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public SceneConfigException(String message) {
        super(message);
    }
    
    public SceneConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
