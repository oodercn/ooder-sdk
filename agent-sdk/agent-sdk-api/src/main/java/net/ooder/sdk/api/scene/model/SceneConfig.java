package net.ooder.sdk.api.scene.model;

import java.util.Map;

public class SceneConfig {
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
