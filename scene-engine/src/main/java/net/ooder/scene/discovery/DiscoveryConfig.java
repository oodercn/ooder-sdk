package net.ooder.scene.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryConfig {
    private String configId;
    private DiscoveryScope scope;
    private int timeout;
    private int retryCount;
    private Map<String, Object> properties;

    public DiscoveryConfig(String configId) {
        this.configId = configId;
        this.scope = DiscoveryScope.PERSONAL;
        this.timeout = 5000;
        this.retryCount = 3;
        this.properties = new ConcurrentHashMap<>();
    }

    public String getConfigId() {
        return configId;
    }

    public DiscoveryScope getScope() {
        return scope;
    }

    public void setScope(DiscoveryScope scope) {
        this.scope = scope;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
