package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapRegistry {
    private Map<String, CapabilityInfo> capabilities;

    public CapRegistry() {
        this.capabilities = new ConcurrentHashMap<>();
    }

    public void registerCapability(String capId, CapabilityInfo info) {
        capabilities.put(capId, info);
    }

    public CapabilityInfo getCapability(String capId) {
        return capabilities.get(capId);
    }

    public void unregisterCapability(String capId) {
        capabilities.remove(capId);
    }

    public boolean hasCapability(String capId) {
        return capabilities.containsKey(capId);
    }

    public Map<String, CapabilityInfo> getAllCapabilities() {
        return capabilities;
    }

    public Map<String, CapabilityInfo> getCapabilitiesByCategory(String category) {
        Map<String, CapabilityInfo> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, CapabilityInfo> entry : capabilities.entrySet()) {
            if (entry.getValue().getCategory().equals(category)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
