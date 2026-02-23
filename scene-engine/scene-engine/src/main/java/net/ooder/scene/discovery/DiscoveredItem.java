package net.ooder.scene.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveredItem {
    private String itemId;
    private String name;
    private DiscoveryType type;
    private String providerName;
    private Map<String, Object> metadata;

    public DiscoveredItem(String itemId, String name, DiscoveryType type) {
        this.itemId = itemId;
        this.name = name;
        this.type = type;
        this.metadata = new ConcurrentHashMap<>();
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public DiscoveryType getType() {
        return type;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DiscoveredItem{" +
                "itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", providerName='" + providerName + '\'' +
                '}';
    }
}
