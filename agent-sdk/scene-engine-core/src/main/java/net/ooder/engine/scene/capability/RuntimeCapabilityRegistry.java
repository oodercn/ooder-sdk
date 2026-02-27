package net.ooder.engine.scene.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeCapabilityRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(RuntimeCapabilityRegistry.class);
    
    private final Map<String, RuntimeCapability> capabilities;
    private final Map<String, RuntimeCapabilityProvider> providers;
    private final Map<String, List<RuntimeCapabilityListener>> listeners;
    
    public RuntimeCapabilityRegistry() {
        this.capabilities = new ConcurrentHashMap<>();
        this.providers = new ConcurrentHashMap<>();
        this.listeners = new ConcurrentHashMap<>();
    }
    
    public void registerProvider(RuntimeCapabilityProvider provider) {
        providers.put(provider.getProviderId(), provider);
        
        List<RuntimeCapability> providerCapabilities = provider.getCapabilities();
        for (RuntimeCapability cap : providerCapabilities) {
            registerCapability(cap);
        }
        
        log.info("Registered provider: {} with {} capabilities", 
            provider.getProviderId(), providerCapabilities.size());
    }
    
    public void unregisterProvider(String providerId) {
        RuntimeCapabilityProvider provider = providers.remove(providerId);
        if (provider != null) {
            for (RuntimeCapability cap : provider.getCapabilities()) {
                unregisterCapability(cap.getCapabilityId());
            }
            log.info("Unregistered provider: {}", providerId);
        }
    }
    
    public void registerCapability(RuntimeCapability capability) {
        capabilities.put(capability.getCapabilityId(), capability);
        notifyListeners(capability.getCapabilityId(), "added", capability);
        log.debug("Registered capability: {}", capability.getCapabilityId());
    }
    
    public void unregisterCapability(String capabilityId) {
        RuntimeCapability removed = capabilities.remove(capabilityId);
        if (removed != null) {
            notifyListeners(capabilityId, "removed", null);
            log.debug("Unregistered capability: {}", capabilityId);
        }
    }
    
    public RuntimeCapability getCapability(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    public List<RuntimeCapability> getAllCapabilities() {
        return new ArrayList<>(capabilities.values());
    }
    
    public List<RuntimeCapability> findByName(String name) {
        List<RuntimeCapability> result = new ArrayList<>();
        for (RuntimeCapability cap : capabilities.values()) {
            if (cap.getName().equals(name)) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public List<RuntimeCapability> findByType(RuntimeCapabilityType type) {
        List<RuntimeCapability> result = new ArrayList<>();
        for (RuntimeCapability cap : capabilities.values()) {
            if (cap.getType() == type) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public List<RuntimeCapability> findByTag(String tag) {
        List<RuntimeCapability> result = new ArrayList<>();
        for (RuntimeCapability cap : capabilities.values()) {
            if (cap.getTags().contains(tag)) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public List<RuntimeCapability> findByProvider(String providerId) {
        List<RuntimeCapability> result = new ArrayList<>();
        for (RuntimeCapability cap : capabilities.values()) {
            if (providerId.equals(cap.getProviderId())) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public List<RuntimeCapability> query(String query) {
        List<RuntimeCapability> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (RuntimeCapability cap : capabilities.values()) {
            if (matchesQuery(cap, lowerQuery)) {
                result.add(cap);
            }
        }
        
        return result;
    }
    
    private boolean matchesQuery(RuntimeCapability cap, String query) {
        if (cap.getName().toLowerCase().contains(query)) return true;
        if (cap.getDescription().toLowerCase().contains(query)) return true;
        for (String tag : cap.getTags()) {
            if (tag.toLowerCase().contains(query)) return true;
        }
        return false;
    }
    
    public RuntimeCapabilityProvider getProvider(String providerId) {
        return providers.get(providerId);
    }
    
    public List<RuntimeCapabilityProvider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }
    
    public void subscribe(String capabilityId, RuntimeCapabilityListener listener) {
        listeners.computeIfAbsent(capabilityId, k -> new ArrayList<>()).add(listener);
    }
    
    public void unsubscribe(String capabilityId, RuntimeCapabilityListener listener) {
        List<RuntimeCapabilityListener> list = listeners.get(capabilityId);
        if (list != null) {
            list.remove(listener);
        }
    }
    
    private void notifyListeners(String capabilityId, String event, RuntimeCapability capability) {
        List<RuntimeCapabilityListener> list = listeners.get(capabilityId);
        if (list != null) {
            for (RuntimeCapabilityListener listener : list) {
                try {
                    if ("removed".equals(event)) {
                        listener.onCapabilityUnregistered(capabilityId);
                    } else {
                        listener.onCapabilityStatusChanged(capabilityId, null, capability.getStatus());
                    }
                } catch (Exception e) {
                    log.warn("Listener notification failed", e);
                }
            }
        }
    }
    
    public int getCapabilityCount() {
        return capabilities.size();
    }
    
    public int getProviderCount() {
        return providers.size();
    }
}
