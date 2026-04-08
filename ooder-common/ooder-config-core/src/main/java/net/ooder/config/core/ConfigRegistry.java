package net.ooder.config.core;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigRegistry {
    
    private static final ConfigRegistry INSTANCE = new ConfigRegistry();
    
    private final Map<String, OoderConfig> configs = new ConcurrentHashMap<String, OoderConfig>();
    private volatile OoderConfig activeConfig;
    private volatile boolean testMode = false;
    private volatile Properties testProperties;
    
    private ConfigRegistry() {
    }
    
    public static ConfigRegistry getInstance() {
        return INSTANCE;
    }
    
    public void register(OoderConfig config) {
        if (config != null && config.getId() != null) {
            configs.put(config.getId(), config);
        }
    }
    
    public void setActiveConfig(String configId) {
        OoderConfig config = configs.get(configId);
        if (config == null) {
            throw new ConfigException("Config not found: " + configId);
        }
        this.activeConfig = config;
    }
    
    public void setActiveConfig(OoderConfig config) {
        if (config == null) {
            throw new ConfigException("Config cannot be null");
        }
        this.activeConfig = config;
        if (config.getId() != null) {
            configs.put(config.getId(), config);
        }
    }
    
    public OoderConfig getActiveConfig() {
        return activeConfig;
    }
    
    public String getValue(String key) {
        if (testMode && testProperties != null) {
            String value = testProperties.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        if (activeConfig != null) {
            return activeConfig.getValue(key);
        }
        return null;
    }
    
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    
    public boolean isTestMode() {
        return testMode;
    }
    
    public void setTestProperties(Properties props) {
        this.testProperties = props;
        this.testMode = true;
    }
    
    public void clearTestProperties() {
        this.testProperties = null;
        this.testMode = false;
    }
    
    public void reset() {
        configs.clear();
        activeConfig = null;
        testMode = false;
        testProperties = null;
    }
    
    public Properties toProperties() {
        Properties props = new Properties();
        if (activeConfig != null) {
            props.putAll(activeConfig.toProperties());
        }
        if (testMode && testProperties != null) {
            props.putAll(testProperties);
        }
        return props;
    }
}
