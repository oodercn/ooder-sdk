package net.ooder.config.scene;

import net.ooder.config.scene.enums.CapabilityType;
import net.ooder.config.scene.enums.SceneEnvironment;
import net.ooder.config.scene.enums.ServiceType;
import net.ooder.config.scene.extension.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SceneFactory {
    
    private static final Map<String, SceneFactory> factoryCache = new ConcurrentHashMap<String, SceneFactory>();
    
    private final SceneConfig sceneConfig;
    private boolean initialized = false;
    
    private final List<SceneLifecycleListener> lifecycleListeners = new ArrayList<SceneLifecycleListener>();
    private CapabilityStatusProvider statusProvider;
    private ConfigValidator configValidator;
    
    private SceneFactory(SceneConfig sceneConfig) {
        this.sceneConfig = sceneConfig;
    }
    
    public static SceneFactory create() {
        return create(SceneLoader.load());
    }
    
    public static SceneFactory create(SceneEnvironment env) {
        return create(SceneLoader.loadByEnv(env));
    }
    
    public static SceneFactory create(String env) {
        if (env.endsWith(".yaml") || env.endsWith(".yml")) {
            return create(SceneLoader.load(env));
        }
        return create(SceneLoader.loadByEnv(env));
    }
    
    public static SceneFactory create(SceneConfig config) {
        if (config == null) {
            throw new SceneConfigException("SceneConfig cannot be null");
        }
        
        String cacheKey = config.getId();
        if (cacheKey == null) {
            cacheKey = "default";
        }
        
        return factoryCache.computeIfAbsent(cacheKey, k -> {
            SceneFactory factory = new SceneFactory(config);
            factory.initialize();
            return factory;
        });
    }
    
    public static SceneFactory fromYaml(String yamlContent) {
        SceneConfig config = SceneLoader.loadFromYaml(yamlContent);
        return create(config);
    }
    
    private void initialize() {
        fireSceneInitializing(sceneConfig);
        
        try {
            initialized = true;
            fireSceneInitialized(sceneConfig);
        } catch (Exception e) {
            fireSceneError(sceneConfig, e);
            throw new SceneConfigException("Failed to initialize scene: " + e.getMessage(), e);
        }
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public SceneConfig getSceneConfig() {
        return sceneConfig;
    }
    
    public OrgSceneConfig getOrgConfig() {
        return sceneConfig != null ? sceneConfig.getOrg() : null;
    }
    
    public VfsSceneConfig getVfsConfig() {
        return sceneConfig != null ? sceneConfig.getVfs() : null;
    }
    
    public MsgSceneConfig getMsgConfig() {
        return sceneConfig != null ? sceneConfig.getMsg() : null;
    }
    
    public CapabilityConfig getCapability(String serviceCode, String capabilityCode) {
        ServiceType serviceType = ServiceType.fromCode(serviceCode);
        return getCapability(serviceType, capabilityCode);
    }
    
    public CapabilityConfig getCapability(ServiceType serviceType, String capabilityCode) {
        if (serviceType == null) {
            return null;
        }
        
        switch (serviceType) {
            case ORG:
                OrgSceneConfig orgConfig = getOrgConfig();
                return orgConfig != null ? orgConfig.getCapabilities().get(capabilityCode) : null;
            case VFS:
                VfsSceneConfig vfsConfig = getVfsConfig();
                return vfsConfig != null ? vfsConfig.getCapabilities().get(capabilityCode) : null;
            case MSG:
                MsgSceneConfig msgConfig = getMsgConfig();
                return msgConfig != null ? msgConfig.getCapabilities().get(capabilityCode) : null;
            default:
                return null;
        }
    }
    
    public CapabilityConfig getCapability(CapabilityType capabilityType) {
        if (capabilityType == null) {
            return null;
        }
        return getCapability(capabilityType.getServiceType(), capabilityType.getCode());
    }
    
    public String getSceneId() {
        return sceneConfig != null ? sceneConfig.getId() : null;
    }
    
    public String getSceneName() {
        return sceneConfig != null ? sceneConfig.getName() : null;
    }
    
    public void refresh() {
        initialize();
    }
    
    public void close() {
        fireSceneClosing(sceneConfig);
        
        String cacheKey = sceneConfig != null ? sceneConfig.getId() : "default";
        factoryCache.remove(cacheKey);
        initialized = false;
        
        fireSceneClosed(sceneConfig);
    }
    
    public static void clearCache() {
        factoryCache.clear();
    }
    
    public static Set<String> getCacheKeys() {
        return factoryCache.keySet();
    }
    
    public static SceneFactory getDefault() {
        if (factoryCache.isEmpty()) {
            return create();
        }
        return factoryCache.values().iterator().next();
    }
    
    // ========== 扩展接口：生命周期监听 ==========
    
    public void addLifecycleListener(SceneLifecycleListener listener) {
        if (listener != null && !lifecycleListeners.contains(listener)) {
            lifecycleListeners.add(listener);
        }
    }
    
    public void removeLifecycleListener(SceneLifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }
    
    public void clearLifecycleListeners() {
        lifecycleListeners.clear();
    }
    
    private void fireSceneInitializing(SceneConfig config) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onSceneInitializing(config);
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    private void fireSceneInitialized(SceneConfig config) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onSceneInitialized(config);
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    private void fireSceneError(SceneConfig config, Exception error) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onSceneError(config, error);
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    private void fireSceneClosing(SceneConfig config) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onSceneClosing(config);
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    private void fireSceneClosed(SceneConfig config) {
        for (SceneLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onSceneClosed(config);
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    // ========== 扩展接口：能力状态查询 ==========
    
    public void setCapabilityStatusProvider(CapabilityStatusProvider provider) {
        this.statusProvider = provider;
    }
    
    public CapabilityStatusProvider getCapabilityStatusProvider() {
        return statusProvider;
    }
    
    public CapabilityStatus getCapabilityStatus(String capabilityCode) {
        if (statusProvider != null) {
            return statusProvider.getCapabilityStatus(capabilityCode);
        }
        return null;
    }
    
    public Map<String, CapabilityStatus> getAllCapabilityStatus() {
        if (statusProvider != null) {
            return statusProvider.getAllCapabilityStatus();
        }
        return new ConcurrentHashMap<String, CapabilityStatus>();
    }
    
    public void updateCapabilityStatus(String capabilityCode, CapabilityStatus status) {
        if (statusProvider != null) {
            statusProvider.updateCapabilityStatus(capabilityCode, status);
        }
    }
    
    // ========== 扩展接口：配置验证 ==========
    
    public void setConfigValidator(ConfigValidator validator) {
        this.configValidator = validator;
    }
    
    public ConfigValidator getConfigValidator() {
        return configValidator;
    }
    
    public ValidationResult validate() {
        if (configValidator != null) {
            return configValidator.validate(sceneConfig);
        }
        return ValidationResult.success();
    }
    
    public ValidationResult validateOrg() {
        if (configValidator != null && getOrgConfig() != null) {
            return configValidator.validate(getOrgConfig());
        }
        return ValidationResult.success("OrgSceneConfig");
    }
    
    public ValidationResult validateVfs() {
        if (configValidator != null && getVfsConfig() != null) {
            return configValidator.validate(getVfsConfig());
        }
        return ValidationResult.success("VfsSceneConfig");
    }
    
    public ValidationResult validateMsg() {
        if (configValidator != null && getMsgConfig() != null) {
            return configValidator.validate(getMsgConfig());
        }
        return ValidationResult.success("MsgSceneConfig");
    }
}
