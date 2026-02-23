package net.ooder.sdk;

import net.ooder.sdk.api.skill.SkillPackageManager;
import net.ooder.sdk.config.InterfaceConfigManager;
import net.ooder.sdk.driver.DriverLoader;
import net.ooder.sdk.driver.discovery.DriverDiscovery;
import net.ooder.sdk.fallback.FallbackStrategy;
import net.ooder.sdk.interfaceRegistry.InterfaceRegistry;
import net.ooder.sdk.resolver.InterfaceResolver;
import net.ooder.sdk.scene.SceneInterfaceManager;
import net.ooder.sdk.scene.SceneLifecycleManager;

import java.util.Map;
import java.util.Optional;

public interface OoderSdk {
    
    void initialize();
    
    void initialize(OoderSdkConfig config);
    
    void shutdown();
    
    boolean isInitialized();
    
    String getVersion();
    
    String getSdkId();
    
    InterfaceRegistry getInterfaceRegistry();
    
    DriverLoader getDriverLoader();
    
    InterfaceResolver getInterfaceResolver();
    
    InterfaceConfigManager getConfigManager();
    
    SkillPackageManager getSkillPackageManager();
    
    SceneInterfaceManager getSceneInterfaceManager();
    
    SceneLifecycleManager getSceneLifecycleManager();
    
    FallbackStrategy getFallbackStrategy();
    
    DriverDiscovery getDriverDiscovery();
    
    <T> Optional<T> getInterface(String interfaceId, Class<T> type);
    
    <T> Optional<T> getInterface(String interfaceId, String skillId, Class<T> type);
    
    <T> Optional<T> getSceneInterface(String sceneId, String interfaceId, Class<T> type);
    
    void registerDriver(String interfaceId, String skillId, Object driver);
    
    void unregisterDriver(String interfaceId, String skillId);
    
    void registerInterface(net.ooder.sdk.api.skill.InterfaceDefinition definition);
    
    void unregisterInterface(String interfaceId);
    
    String createScene(String sceneId);
    
    String createScene(String sceneId, Map<String, String> interfaceBindings);
    
    void destroyScene(String sceneId);
    
    void startScene(String sceneId);
    
    void stopScene(String sceneId);
    
    OoderSdkStats getStats();
    
    class OoderSdkConfig {
        private String sdkId;
        private boolean autoDiscoverDrivers = true;
        private boolean autoStartScenes = true;
        private String configPath;
        private String driverScanPackage = "net.ooder.sdk.drivers";
        private int maxThreads = 20;
        private long defaultTimeout = 30000;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getSdkId() { return sdkId; }
        public void setSdkId(String sdkId) { this.sdkId = sdkId; }
        
        public boolean isAutoDiscoverDrivers() { return autoDiscoverDrivers; }
        public void setAutoDiscoverDrivers(boolean autoDiscoverDrivers) { this.autoDiscoverDrivers = autoDiscoverDrivers; }
        
        public boolean isAutoStartScenes() { return autoStartScenes; }
        public void setAutoStartScenes(boolean autoStartScenes) { this.autoStartScenes = autoStartScenes; }
        
        public String getConfigPath() { return configPath; }
        public void setConfigPath(String configPath) { this.configPath = configPath; }
        
        public String getDriverScanPackage() { return driverScanPackage; }
        public void setDriverScanPackage(String driverScanPackage) { this.driverScanPackage = driverScanPackage; }
        
        public int getMaxThreads() { return maxThreads; }
        public void setMaxThreads(int maxThreads) { this.maxThreads = maxThreads; }
        
        public long getDefaultTimeout() { return defaultTimeout; }
        public void setDefaultTimeout(long defaultTimeout) { this.defaultTimeout = defaultTimeout; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        
        public static OoderSdkConfig defaultConfig() {
            return new OoderSdkConfig();
        }
    }
    
    class OoderSdkStats {
        private String sdkId;
        private boolean initialized;
        private long startTime;
        private long uptime;
        private int interfaceCount;
        private int driverCount;
        private int sceneCount;
        private int activeSceneCount;
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        
        public String getSdkId() { return sdkId; }
        public void setSdkId(String sdkId) { this.sdkId = sdkId; }
        
        public boolean isInitialized() { return initialized; }
        public void setInitialized(boolean initialized) { this.initialized = initialized; }
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getUptime() { return uptime; }
        public void setUptime(long uptime) { this.uptime = uptime; }
        
        public int getInterfaceCount() { return interfaceCount; }
        public void setInterfaceCount(int interfaceCount) { this.interfaceCount = interfaceCount; }
        
        public int getDriverCount() { return driverCount; }
        public void setDriverCount(int driverCount) { this.driverCount = driverCount; }
        
        public int getSceneCount() { return sceneCount; }
        public void setSceneCount(int sceneCount) { this.sceneCount = sceneCount; }
        
        public int getActiveSceneCount() { return activeSceneCount; }
        public void setActiveSceneCount(int activeSceneCount) { this.activeSceneCount = activeSceneCount; }
        
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        
        public long getSuccessfulRequests() { return successfulRequests; }
        public void setSuccessfulRequests(long successfulRequests) { this.successfulRequests = successfulRequests; }
        
        public long getFailedRequests() { return failedRequests; }
        public void setFailedRequests(long failedRequests) { this.failedRequests = failedRequests; }
        
        public double getSuccessRate() {
            return totalRequests > 0 ? (double) successfulRequests / totalRequests : 0;
        }
    }
}
