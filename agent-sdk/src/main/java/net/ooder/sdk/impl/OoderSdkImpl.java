package net.ooder.sdk.impl;

import net.ooder.sdk.OoderSdk;
import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.api.skill.SkillPackageManager;
import net.ooder.sdk.config.InterfaceConfigManager;
import net.ooder.sdk.config.impl.InterfaceConfigManagerImpl;
import net.ooder.sdk.driver.DriverLoader;
import net.ooder.sdk.driver.discovery.DriverDiscovery;
import net.ooder.sdk.driver.discovery.impl.DriverDiscoveryImpl;
import net.ooder.sdk.driver.impl.DriverLoaderImpl;
import net.ooder.sdk.fallback.FallbackStrategy;
import net.ooder.sdk.fallback.impl.DefaultFallbackStrategy;
import net.ooder.sdk.interfaceRegistry.InterfaceRegistry;
import net.ooder.sdk.interfaceRegistry.impl.InterfaceRegistryImpl;
import net.ooder.sdk.resolver.InterfaceResolver;
import net.ooder.sdk.resolver.impl.InterfaceResolverImpl;
import net.ooder.sdk.scene.SceneInterfaceManager;
import net.ooder.sdk.scene.SceneLifecycleManager;
import net.ooder.sdk.scene.impl.SceneInterfaceManagerImpl;
import net.ooder.sdk.scene.impl.SceneLifecycleManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class OoderSdkImpl implements OoderSdk {
    
    private static final Logger log = LoggerFactory.getLogger(OoderSdkImpl.class);
    private static final String VERSION = "0.8.3";
    
    private final String sdkId;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Map<String, Object> components = new ConcurrentHashMap<>();
    private final Map<String, Object> drivers = new ConcurrentHashMap<>();
    private final Map<String, SceneLifecycleManager> scenes = new ConcurrentHashMap<>();
    private final AtomicLong requestCounter = new AtomicLong(0);
    private final AtomicLong successCounter = new AtomicLong(0);
    private final AtomicLong failureCounter = new AtomicLong(0);
    
    private long startTime;
    private OoderSdkConfig config;
    private InterfaceRegistry interfaceRegistry;
    private DriverLoader driverLoader;
    private InterfaceResolver interfaceResolver;
    private InterfaceConfigManager configManager;
    private SkillPackageManager skillPackageManager;
    private SceneInterfaceManager sceneInterfaceManager;
    private SceneLifecycleManager sceneLifecycleManager;
    private FallbackStrategy fallbackStrategy;
    private DriverDiscovery driverDiscovery;
    private ExecutorService executor;

    public OoderSdkImpl() {
        this("ooder-sdk-" + System.currentTimeMillis());
    }
    
    public OoderSdkImpl(String sdkId) {
        this.sdkId = sdkId != null ? sdkId : "ooder-sdk-" + System.currentTimeMillis();
    }
    
    @Override
    public void initialize() {
        initialize(OoderSdkConfig.defaultConfig());
    }
    
    @Override
    public void initialize(OoderSdkConfig config) {
        if (initialized.get()) {
            throw new IllegalStateException("SDK already initialized");
        }
        
        this.config = config != null ? config : OoderSdkConfig.defaultConfig();
        this.startTime = System.currentTimeMillis();
        
        log.info("Initializing OoderSdk: {}", sdkId);
        
        this.executor = Executors.newFixedThreadPool(this.config.getMaxThreads());
        
        this.interfaceRegistry = new InterfaceRegistryImpl();
        this.driverLoader = new DriverLoaderImpl(this.interfaceRegistry);
        this.fallbackStrategy = new DefaultFallbackStrategy();
        this.interfaceResolver = new InterfaceResolverImpl(this.interfaceRegistry, this.driverLoader);
        this.configManager = new InterfaceConfigManagerImpl();
        this.driverDiscovery = new DriverDiscoveryImpl();
        
        this.sceneInterfaceManager = new SceneInterfaceManagerImpl(
            this.interfaceRegistry, this.driverLoader, this.interfaceResolver);
        this.sceneLifecycleManager = new SceneLifecycleManagerImpl(this.sceneInterfaceManager);
        
        if (this.config.isAutoDiscoverDrivers()) {
            discoverDrivers();
        }
        
        if (this.config.getConfigPath() != null) {
            loadConfig(this.config.getConfigPath());
        }
        
        initialized.set(true);
        log.info("OoderSdk initialized successfully: {}", sdkId);
    }
    
    private void discoverDrivers() {
        String basePackage = this.config.getDriverScanPackage();
        if (basePackage != null) {
            List<DriverDiscovery.DiscoveredDriver> discovered = driverDiscovery.discover(basePackage);
            for (DriverDiscovery.DiscoveredDriver driver : discovered) {
                try {
                    Object instance = driver.getDriverClass().newInstance();
                    driverLoader.registerDriver(driver.getInterfaceId(), driver.getSkillId(), instance);
                    log.info("Auto-discovered and registered driver: {} -> {}", 
                        driver.getInterfaceId(), driver.getSkillId());
                } catch (Exception e) {
                    log.warn("Failed to instantiate driver: {}", driver.getClassName(), e);
                }
            }
        }
    }
    
    private void loadConfig(String configPath) {
        File file = new File(configPath);
        if (!file.exists()) {
            log.info("Configuration file not found: {}", configPath);
            return;
        }
        
        try {
            configManager.loadConfigs(configPath);
            log.info("Loaded configuration from: {}", configPath);
        } catch (Exception e) {
            log.error("Failed to load configuration: {}", configPath, e);
        }
    }
    
    @Override
    public void shutdown() {
        if (!initialized.get()) {
            return;
        }
        
        log.info("Shutting down OoderSdk: {}", sdkId);
        
        for (String sceneId : new ArrayList<>(scenes.keySet())) {
            try {
                destroyScene(sceneId);
            } catch (Exception e) {
                log.warn("Failed to destroy scene during shutdown: {}", sceneId, e);
            }
        }
        
        if (sceneLifecycleManager != null && sceneLifecycleManager instanceof SceneLifecycleManagerImpl) {
            ((SceneLifecycleManagerImpl) sceneLifecycleManager).shutdown();
        }
        
        if (driverLoader != null) {
            driverLoader.clear();
        }
        
        if (interfaceRegistry != null) {
            interfaceRegistry.clear();
        }
        
        if (executor != null) {
            executor.shutdown();
        }
        
        drivers.clear();
        components.clear();
        initialized.set(false);
        
        log.info("OoderSdk shutdown complete: {}", sdkId);
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
    public DriverLoader getDriverLoader() {
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
    public SceneLifecycleManager getSceneLifecycleManager() {
        return sceneLifecycleManager;
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
        return driverLoader.load(interfaceId, type);
    }
    
    @Override
    public <T> Optional<T> getInterface(String interfaceId, String skillId, Class<T> type) {
        return driverLoader.load(interfaceId, skillId, type);
    }
    
    @Override
    public <T> Optional<T> getSceneInterface(String sceneId, String interfaceId, Class<T> type) {
        return sceneInterfaceManager.getInterface(sceneId, interfaceId, type);
    }
    
    @Override
    public void registerDriver(String interfaceId, String skillId, Object driver) {
        driverLoader.registerDriver(interfaceId, skillId, driver);
    }
    
    @Override
    public void unregisterDriver(String interfaceId, String skillId) {
        driverLoader.unregisterDriver(interfaceId, skillId);
    }
    
    @Override
    public void registerInterface(InterfaceDefinition definition) {
        interfaceRegistry.register(definition);
    }
    
    @Override
    public void unregisterInterface(String interfaceId) {
        interfaceRegistry.unregister(interfaceId);
    }
    
    @Override
    public String createScene(String sceneId) {
        return createScene(sceneId, null);
    }
    
    @Override
    public String createScene(String sceneId, Map<String, String> interfaceBindings) {
        if (!initialized.get()) {
            throw new IllegalStateException("SDK not initialized");
        }
        
        SceneLifecycleManager.SceneConfig sceneConfig = new SceneLifecycleManager.SceneConfig();
        sceneConfig.setSceneId(sceneId);
        sceneConfig.setInterfaceBindings(interfaceBindings);
        sceneConfig.setAutoStart(this.config.isAutoStartScenes());
        
        sceneLifecycleManager.initializeScene(sceneId, sceneConfig).join();
        scenes.put(sceneId, sceneLifecycleManager);
        
        log.info("Created scene: {}", sceneId);
        return sceneId;
    }
    
    @Override
    public void destroyScene(String sceneId) {
        SceneLifecycleManager manager = scenes.remove(sceneId);
        if (manager != null) {
            sceneLifecycleManager.destroyScene(sceneId).join();
            log.info("Destroyed scene: {}", sceneId);
        }
    }
    
    @Override
    public void startScene(String sceneId) {
        sceneLifecycleManager.startScene(sceneId).join();
        log.info("Started scene: {}", sceneId);
    }
    
    @Override
    public void stopScene(String sceneId) {
        sceneLifecycleManager.stopScene(sceneId).join();
        log.info("Stopped scene: {}", sceneId);
    }
    
    @Override
    public OoderSdkStats getStats() {
        OoderSdkStats stats = new OoderSdkStats();
        stats.setSdkId(sdkId);
        stats.setInitialized(initialized.get());
        stats.setStartTime(startTime);
        stats.setUptime(System.currentTimeMillis() - startTime);
        stats.setInterfaceCount(interfaceRegistry != null ? interfaceRegistry.getInterfaceCount() : 0);
        stats.setDriverCount(driverLoader != null ? driverLoader.getDriverCount() : 0);
        stats.setSceneCount(scenes.size());
        stats.setActiveSceneCount(sceneLifecycleManager != null ? sceneLifecycleManager.getActiveScenes().size() : 0);
        stats.setTotalRequests(requestCounter.get());
        stats.setSuccessfulRequests(successCounter.get());
        stats.setFailedRequests(failureCounter.get());
        return stats;
    }
}
