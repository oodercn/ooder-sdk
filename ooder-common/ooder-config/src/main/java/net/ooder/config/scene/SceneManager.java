package net.ooder.config.scene;

import net.ooder.config.scene.enums.SceneEnvironment;
import net.ooder.config.scene.extension.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SceneManager {
    
    private static final SceneManager INSTANCE = new SceneManager();
    
    private final Map<String, SceneFactory> factories = new ConcurrentHashMap<String, SceneFactory>();
    private final List<ConfigChangeListener> configChangeListeners = new ArrayList<ConfigChangeListener>();
    private String activeSceneId;
    
    private SceneManager() {
    }
    
    public static SceneManager getInstance() {
        return INSTANCE;
    }
    
    public void register(SceneConfig config) {
        SceneFactory factory = SceneFactory.create(config);
        factories.put(config.getId(), factory);
    }
    
    public void register(String configFile) {
        SceneConfig config = SceneLoader.load(configFile);
        register(config);
    }
    
    public void register(SceneEnvironment env) {
        SceneFactory factory = SceneFactory.create(env);
        SceneConfig config = factory.getSceneConfig();
        if (config != null && config.getId() != null) {
            factories.put(config.getId(), factory);
        }
    }
    
    public void setActiveScene(String sceneId) {
        if (!factories.containsKey(sceneId)) {
            throw new SceneConfigException("Scene not found: " + sceneId);
        }
        this.activeSceneId = sceneId;
    }
    
    public SceneFactory getActiveFactory() {
        if (activeSceneId == null) {
            throw new SceneConfigException("No active scene set");
        }
        return factories.get(activeSceneId);
    }
    
    public SceneFactory getFactory(String sceneId) {
        return factories.get(sceneId);
    }
    
    public SceneConfig getActiveSceneConfig() {
        SceneFactory factory = getActiveFactory();
        return factory != null ? factory.getSceneConfig() : null;
    }
    
    public Set<String> getSceneIds() {
        return factories.keySet();
    }
    
    public int getSceneCount() {
        return factories.size();
    }
    
    public boolean hasScene(String sceneId) {
        return factories.containsKey(sceneId);
    }
    
    public void unregister(String sceneId) {
        SceneFactory factory = factories.remove(sceneId);
        if (factory != null) {
            factory.close();
        }
        if (activeSceneId != null && activeSceneId.equals(sceneId)) {
            activeSceneId = null;
        }
    }
    
    public void clear() {
        for (SceneFactory factory : factories.values()) {
            factory.close();
        }
        factories.clear();
        activeSceneId = null;
    }
    
    public String getActiveSceneId() {
        return activeSceneId;
    }
    
    public boolean hasActiveScene() {
        return activeSceneId != null;
    }
    
    // ========== 扩展接口：配置变更通知 ==========
    // 注意：以下方法已弃用，建议使用 SDK 7.3 中的 ConfigObserver 替代
    
    /**
     * 添加配置变更监听器
     * @param listener 监听器实例
     * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigObserver} 替代。
     *             该方法将在未来版本中移除。
     * @see net.ooder.sdk.infra.observer.ConfigObserver
     */
    @Deprecated
    public void addConfigChangeListener(ConfigChangeListener listener) {
        if (listener != null && !configChangeListeners.contains(listener)) {
            configChangeListeners.add(listener);
            Collections.sort(configChangeListeners, new Comparator<ConfigChangeListener>() {
                @Override
                public int compare(ConfigChangeListener l1, ConfigChangeListener l2) {
                    return Integer.compare(l1.getOrder(), l2.getOrder());
                }
            });
        }
    }
    
    /**
     * 移除配置变更监听器
     * @param listener 监听器实例
     * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigObserver} 替代。
     *             该方法将在未来版本中移除。
     * @see net.ooder.sdk.infra.observer.ConfigObserver
     */
    @Deprecated
    public void removeConfigChangeListener(ConfigChangeListener listener) {
        configChangeListeners.remove(listener);
    }
    
    /**
     * 清除所有配置变更监听器
     * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigObserver} 替代。
     *             该方法将在未来版本中移除。
     * @see net.ooder.sdk.infra.observer.ConfigObserver
     */
    @Deprecated
    public void clearConfigChangeListeners() {
        configChangeListeners.clear();
    }
    
    /**
     * 通知配置变更
     * @param sceneId 场景ID
     * @param configPath 配置路径
     * @param oldValue 旧值
     * @param newValue 新值
     * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigObserver} 替代。
     *             该方法将在未来版本中移除。
     * @see net.ooder.sdk.infra.observer.ConfigObserver
     */
    @Deprecated
    public void notifyConfigChange(String sceneId, String configPath, Object oldValue, Object newValue) {
        ConfigChangeEvent event = new ConfigChangeEvent(sceneId, configPath, oldValue, newValue);
        notifyConfigChange(event);
    }
    
    /**
     * 通知配置变更
     * @param event 配置变更事件
     * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigObserver} 替代。
     *             该方法将在未来版本中移除。
     * @see net.ooder.sdk.infra.observer.ConfigObserver
     */
    @Deprecated
    public void notifyConfigChange(ConfigChangeEvent event) {
        if (event == null) {
            return;
        }
        
        for (ConfigChangeListener listener : configChangeListeners) {
            try {
                listener.onConfigChanged(event);
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    // ========== 扩展接口：全局能力状态查询 ==========
    
    public Map<String, CapabilityStatus> getAllCapabilityStatus() {
        Map<String, CapabilityStatus> result = new ConcurrentHashMap<String, CapabilityStatus>();
        
        for (SceneFactory factory : factories.values()) {
            Map<String, CapabilityStatus> factoryStatus = factory.getAllCapabilityStatus();
            if (factoryStatus != null) {
                result.putAll(factoryStatus);
            }
        }
        
        return result;
    }
    
    public CapabilityStatus getCapabilityStatus(String sceneId, String capabilityCode) {
        SceneFactory factory = factories.get(sceneId);
        if (factory != null) {
            return factory.getCapabilityStatus(capabilityCode);
        }
        return null;
    }
    
    // ========== 扩展接口：全局配置验证 ==========
    
    public Map<String, ValidationResult> validateAll() {
        Map<String, ValidationResult> results = new ConcurrentHashMap<String, ValidationResult>();
        
        for (Map.Entry<String, SceneFactory> entry : factories.entrySet()) {
            ValidationResult result = entry.getValue().validate();
            results.put(entry.getKey(), result);
        }
        
        return results;
    }
    
    public ValidationResult validateScene(String sceneId) {
        SceneFactory factory = factories.get(sceneId);
        if (factory != null) {
            return factory.validate();
        }
        return ValidationResult.error("Scene not found: " + sceneId);
    }
    
    // ========== 扩展接口：场景状态管理 ==========
    
    public List<String> getInitializedScenes() {
        List<String> initialized = new ArrayList<String>();
        for (Map.Entry<String, SceneFactory> entry : factories.entrySet()) {
            if (entry.getValue().isInitialized()) {
                initialized.add(entry.getKey());
            }
        }
        return initialized;
    }
    
    public boolean isSceneInitialized(String sceneId) {
        SceneFactory factory = factories.get(sceneId);
        return factory != null && factory.isInitialized();
    }
    
    public void refreshScene(String sceneId) {
        SceneFactory factory = factories.get(sceneId);
        if (factory != null) {
            factory.refresh();
        }
    }
    
    public void refreshAll() {
        for (SceneFactory factory : factories.values()) {
            factory.refresh();
        }
    }
}
