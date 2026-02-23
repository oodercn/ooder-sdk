package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneConfig {
    private String configId;
    private Map<String, Object> properties;

    public SceneConfig(String configId) {
        this.configId = configId;
        this.properties = new ConcurrentHashMap<>();
    }

    public String getConfigId() {
        return configId;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void merge(SceneConfig other) {
        if (other != null) {
            properties.putAll(other.properties);
        }
    }
}
