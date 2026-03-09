package net.ooder.sdk.api.scene.model;

import java.util.Map;

/**
 * 场景配置（泛型版本）
 *
 * @param <P> 属性类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class SceneConfig<P> {
    private String sceneId;
    private String sceneName;
    private String configId;
    private Map<String, String> interfaceBindings;
    /** 场景配置属性 */
    private Map<String, P> properties;
    private boolean autoStart;
    private long startupTimeout;
    private long shutdownTimeout;
    
    /**
     * 创建通用配置（向后兼容）
     */
    public static SceneConfig<Object> createGeneric() {
        return new SceneConfig<>();
    }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getConfigId() { return configId; }
    public void setConfigId(String configId) { this.configId = configId; }
    
    public Map<String, String> getInterfaceBindings() { return interfaceBindings; }
    public void setInterfaceBindings(Map<String, String> interfaceBindings) { this.interfaceBindings = interfaceBindings; }
    
    /**
     * 获取场景配置属性
     * @return 属性映射
     */
    public Map<String, P> getProperties() { return properties; }
    /**
     * 设置场景配置属性
     * @param properties 属性映射
     */
    public void setProperties(Map<String, P> properties) { this.properties = properties; }
    
    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
    
    public long getStartupTimeout() { return startupTimeout; }
    public void setStartupTimeout(long startupTimeout) { this.startupTimeout = startupTimeout; }
    
    public long getShutdownTimeout() { return shutdownTimeout; }
    public void setShutdownTimeout(long shutdownTimeout) { this.shutdownTimeout = shutdownTimeout; }
}
