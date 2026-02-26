package net.ooder.sdk;

import net.ooder.sdk.impl.OoderSdkImpl;

import java.util.Map;

public class OoderSdkBuilder {
    
    private String sdkId;
    private boolean autoDiscoverDrivers = true;
    private boolean autoStartScenes = true;
    private String configPath;
    private String driverScanPackage = "net.ooder.sdk.drivers";
    private int maxThreads = 20;
    private long defaultTimeout = 30000;
    private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
    private Map<String, String> defaultInterfaceBindings = new java.util.concurrent.ConcurrentHashMap<>();
    
    public OoderSdkBuilder sdkId(String sdkId) {
        this.sdkId = sdkId;
        return this;
    }
    
    public OoderSdkBuilder autoDiscoverDrivers(boolean autoDiscover) {
        this.autoDiscoverDrivers = autoDiscover;
        return this;
    }
    
    public OoderSdkBuilder autoStartScenes(boolean autoStart) {
        this.autoStartScenes = autoStart;
        return this;
    }
    
    public OoderSdkBuilder configPath(String configPath) {
        this.configPath = configPath;
        return this;
    }
    
    public OoderSdkBuilder driverScanPackage(String packageName) {
        this.driverScanPackage = packageName;
        return this;
    }
    
    public OoderSdkBuilder maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }
    
    public OoderSdkBuilder defaultTimeout(long timeout) {
        this.defaultTimeout = timeout;
        return this;
    }
    
    public OoderSdkBuilder property(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }
    
    public OoderSdkBuilder properties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }
    
    public OoderSdkBuilder defaultInterfaceBinding(String interfaceId, String skillId) {
        this.defaultInterfaceBindings.put(interfaceId, skillId);
        return this;
    }
    
    public OoderSdkBuilder defaultInterfaceBindings(Map<String, String> bindings) {
        this.defaultInterfaceBindings.putAll(bindings);
        return this;
    }
    
    public OoderSdk build() {
        OoderSdkImpl sdk = new OoderSdkImpl(sdkId);
        
        OoderSdk.OoderSdkConfig config = new OoderSdk.OoderSdkConfig();
        config.setSdkId(sdkId);
        config.setAutoDiscoverDrivers(autoDiscoverDrivers);
        config.setAutoStartScenes(autoStartScenes);
        config.setConfigPath(configPath);
        config.setDriverScanPackage(driverScanPackage);
        config.setMaxThreads(maxThreads);
        config.setDefaultTimeout(defaultTimeout);
        config.setProperties(properties);
        
        sdk.initialize(config);
        
        return sdk;
    }
    
    public static OoderSdkBuilder create() {
        return new OoderSdkBuilder();
    }
    
    public static OoderSdk createDefault() {
        return create().build();
    }
}
