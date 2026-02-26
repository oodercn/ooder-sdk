package net.ooder.sdk.impl;

import net.ooder.sdk.OoderSdk;
import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.api.skill.SkillPackageManager;
import net.ooder.sdk.infra.config.interfaceconf.InterfaceConfigManager;
import net.ooder.sdk.infra.config.interfaceconf.impl.InterfaceConfigManagerImpl;
import net.ooder.sdk.core.driver.loader.InterfaceDriverLoader;
import net.ooder.sdk.core.driver.loader.impl.InterfaceDriverLoaderImpl;
import net.ooder.sdk.core.driver.discovery.DriverDiscovery;
import net.ooder.sdk.core.fallback.FallbackStrategy;
import net.ooder.sdk.core.fallback.impl.DefaultFallbackStrategy;
import net.ooder.sdk.core.registry.InterfaceRegistry;
import net.ooder.sdk.core.registry.impl.InterfaceRegistryImpl;
import net.ooder.sdk.resolver.InterfaceResolver;
import net.ooder.sdk.resolver.impl.InterfaceResolverImpl;
import net.ooder.sdk.api.scene.SceneInterfaceManager;
import net.ooder.sdk.api.scene.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class OoderSdkImpl implements OoderSdk {
    
    private static final Logger log = LoggerFactory.getLogger(OoderSdkImpl.class);
    private static final String VERSION = "0.8.3";
    
    private final String sdkId;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Map<String, Object> components = new ConcurrentHashMap<>();
    private final Map<String, Object> drivers = new ConcurrentHashMap<>();
    private final AtomicLong requestCounter = new AtomicLong(0);
    private final AtomicLong successCounter = new AtomicLong(0);
    private final AtomicLong failureCounter = new AtomicLong(0);
    
    private long startTime;
    private InterfaceRegistry interfaceRegistry;
    private InterfaceDriverLoader driverLoader;
    private InterfaceResolver interfaceResolver;
    private InterfaceConfigManager configManager;
    private SkillPackageManager skillPackageManager;
    private SceneInterfaceManager sceneInterfaceManager;
    private SceneManager sceneManager;
    private FallbackStrategy fallbackStrategy;
    private DriverDiscovery driverDiscovery;

    public OoderSdkImpl() {
        this("ooder-sdk-" + System.currentTimeMillis());
    }
    
    public OoderSdkImpl(String sdkId) {
        this.sdkId = sdkId != null ? sdkId : "ooder-sdk-" + System.currentTimeMillis();
    }
    
    @Override
    public void initialize() {
        if (initialized.compareAndSet(false, true)) {
            startTime = System.currentTimeMillis();
            
            interfaceRegistry = new InterfaceRegistryImpl();
            driverLoader = new InterfaceDriverLoaderImpl();
            interfaceResolver = new InterfaceResolverImpl(interfaceRegistry, driverLoader);
            configManager = new InterfaceConfigManagerImpl();
            fallbackStrategy = new DefaultFallbackStrategy();
            
            log.info("OODER SDK initialized: {}", sdkId);
        }
    }
    
    @Override
    public void initialize(OoderSdkConfig config) {
        initialize();
    }
    
    @Override
    public void shutdown() {
        if (initialized.compareAndSet(true, false)) {
            log.info("OODER SDK shutdown: {}", sdkId);
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized.get();
    }
    
    @Override
    public String getVersion() {
        return VERSION;
    }
    
    @Override
    public String getSdkId() {
        return sdkId;
    }
    
    @Override
    public InterfaceRegistry getInterfaceRegistry() {
        return interfaceRegistry;
    }
    
    @Override
    public InterfaceDriverLoader getDriverLoader() {
        return driverLoader;
    }
    
    @Override
    public InterfaceResolver getInterfaceResolver() {
        return interfaceResolver;
    }
    
    @Override
    public InterfaceConfigManager getConfigManager() {
        return configManager;
    }
    
    @Override
    public SkillPackageManager getSkillPackageManager() {
        return skillPackageManager;
    }
    
    @Override
    public SceneInterfaceManager getSceneInterfaceManager() {
        return sceneInterfaceManager;
    }
    
    @Override
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    @Override
    public FallbackStrategy getFallbackStrategy() {
        return fallbackStrategy;
    }
    
    @Override
    public DriverDiscovery getDriverDiscovery() {
        return driverDiscovery;
    }
    
    @Override
    public <T> Optional<T> getInterface(String interfaceId, Class<T> type) {
        return Optional.empty();
    }
    
    @Override
    public <T> Optional<T> getInterface(String interfaceId, String skillId, Class<T> type) {
        return Optional.empty();
    }
    
    @Override
    public <T> Optional<T> getSceneInterface(String sceneId, String interfaceId, Class<T> type) {
        return Optional.empty();
    }
    
    @Override
    public void registerDriver(String interfaceId, String skillId, Object driver) {
        drivers.put(interfaceId + ":" + skillId, driver);
    }
    
    @Override
    public void unregisterDriver(String interfaceId, String skillId) {
        drivers.remove(interfaceId + ":" + skillId);
    }
    
    @Override
    public void registerInterface(InterfaceDefinition definition) {
        if (interfaceRegistry != null && definition != null) {
            interfaceRegistry.register(definition);
        }
    }
    
    @Override
    public void unregisterInterface(String interfaceId) {
        if (interfaceRegistry != null) {
            interfaceRegistry.unregister(interfaceId);
        }
    }

    @Override
    public void startScene(String sceneId) {
        if (sceneManager != null) {
            sceneManager.startScene(sceneId);
        }
    }

    @Override
    public void stopScene(String sceneId) {
        if (sceneManager != null) {
            sceneManager.stopScene(sceneId);
        }
    }

    @Override
    public void destroyScene(String sceneId) {
        if (sceneManager != null) {
            sceneManager.destroyScene(sceneId);
        }
    }

    @Override
    public String createScene(String sceneId) {
        return createScene(sceneId, null);
    }

    @Override
    public String createScene(String sceneId, java.util.Map<String, String> config) {
        if (sceneManager != null) {
            net.ooder.sdk.api.scene.SceneDefinition definition = new net.ooder.sdk.api.scene.SceneDefinition();
            definition.setSceneId(sceneId);
            if (config != null) {
                definition.setName(config.get("name"));
                definition.setDescription(config.get("description"));
            }
            sceneManager.create(definition);
        }
        return sceneId;
    }

    @Override
    public OoderSdkStats getStats() {
        OoderSdkStats stats = new OoderSdkStats();
        stats.setSdkId(sdkId);
        stats.setInitialized(initialized.get());
        stats.setStartTime(startTime);
        stats.setUptime(System.currentTimeMillis() - startTime);
        stats.setTotalRequests(requestCounter.get());
        stats.setSuccessfulRequests(successCounter.get());
        stats.setFailedRequests(failureCounter.get());
        stats.setInterfaceCount(interfaceRegistry != null ? interfaceRegistry.getAllInterfaces().size() : 0);
        stats.setDriverCount(drivers.size());
        return stats;
    }
}
