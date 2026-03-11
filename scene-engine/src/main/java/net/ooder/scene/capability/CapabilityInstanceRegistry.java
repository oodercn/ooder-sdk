package net.ooder.scene.capability;

import net.ooder.skills.capability.CapabilityAddress;
import net.ooder.skills.capability.CapabilityCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityInstanceRegistry {

    private static final Logger log = LoggerFactory.getLogger(CapabilityInstanceRegistry.class);

    private final Map<String, CapabilityInstance> instanceById = new ConcurrentHashMap<>();
    private final Map<Integer, Map<String, CapabilityInstance>> instancesByAddress = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, CapabilityInstance>> instancesByContext = new ConcurrentHashMap<>();

    public CapabilityInstanceRegistry() {
    }

    public CapabilityInstance register(CapabilityAddress address, String providerId, String contextId) {
        String instanceId = generateInstanceId(address, contextId);
        
        CapabilityInstance instance = new CapabilityInstance();
        instance.setInstanceId(instanceId);
        instance.setAddress(address);
        instance.setProviderId(providerId);
        instance.setContextId(contextId);
        instance.setCreatedAt(System.currentTimeMillis());
        instance.setState(InstanceState.CREATED);
        
        instanceById.put(instanceId, instance);
        
        instancesByAddress.computeIfAbsent(address.getAddress(), k -> new ConcurrentHashMap<>())
            .put(instanceId, instance);
        
        if (contextId != null) {
            instancesByContext.computeIfAbsent(contextId, k -> new ConcurrentHashMap<>())
                .put(address.getAddress(), instance);
        }
        
        log.info("Registered capability instance: {} for address: {}", instanceId, address);
        return instance;
    }

    public void unregister(String instanceId) {
        CapabilityInstance instance = instanceById.remove(instanceId);
        if (instance != null) {
            Map<String, CapabilityInstance> addressInstances = instancesByAddress.get(instance.getAddress().getAddress());
            if (addressInstances != null) {
                addressInstances.remove(instanceId);
            }
            
            if (instance.getContextId() != null) {
                Map<Integer, CapabilityInstance> contextInstances = instancesByContext.get(instance.getContextId());
                if (contextInstances != null) {
                    contextInstances.remove(instance.getAddress().getAddress());
                }
            }
            
            log.info("Unregistered capability instance: {}", instanceId);
        }
    }

    public CapabilityInstance getInstance(String instanceId) {
        return instanceById.get(instanceId);
    }

    public CapabilityInstance getInstance(CapabilityAddress address, String contextId) {
        if (contextId == null) {
            Map<String, CapabilityInstance> addressInstances = instancesByAddress.get(address.getAddress());
            if (addressInstances != null && !addressInstances.isEmpty()) {
                return addressInstances.values().iterator().next();
            }
            return null;
        }
        
        Map<Integer, CapabilityInstance> contextInstances = instancesByContext.get(contextId);
        if (contextInstances != null) {
            return contextInstances.get(address.getAddress());
        }
        return null;
    }

    public List<CapabilityInstance> getInstancesByAddress(CapabilityAddress address) {
        Map<String, CapabilityInstance> addressInstances = instancesByAddress.get(address.getAddress());
        if (addressInstances == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(addressInstances.values());
    }

    public List<CapabilityInstance> getInstancesByContext(String contextId) {
        Map<Integer, CapabilityInstance> contextInstances = instancesByContext.get(contextId);
        if (contextInstances == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(contextInstances.values());
    }

    public Set<String> getAllInstanceIds() {
        return instanceById.keySet();
    }

    public int getInstanceCount() {
        return instanceById.size();
    }

    public void updateState(String instanceId, InstanceState state) {
        CapabilityInstance instance = instanceById.get(instanceId);
        if (instance != null) {
            instance.setState(state);
            instance.setLastAccessedAt(System.currentTimeMillis());
            log.debug("Updated instance {} state to {}", instanceId, state);
        }
    }

    public void clear() {
        instanceById.clear();
        instancesByAddress.clear();
        instancesByContext.clear();
        log.info("Cleared all capability instances");
    }

    private String generateInstanceId(CapabilityAddress address, String contextId) {
        return "cap-" + address.getCode().toLowerCase() + "-" + 
               (contextId != null ? contextId.substring(0, Math.min(8, contextId.length())) : "default") + 
               "-" + System.currentTimeMillis() % 10000;
    }

    public static class CapabilityInstance {
        private String instanceId;
        private CapabilityAddress address;
        private String providerId;
        private String contextId;
        private InstanceState state;
        private long createdAt;
        private long lastAccessedAt;
        private Map<String, Object> config;

        public String getInstanceId() { return instanceId; }
        public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
        
        public CapabilityAddress getAddress() { return address; }
        public void setAddress(CapabilityAddress address) { this.address = address; }
        
        public String getProviderId() { return providerId; }
        public void setProviderId(String providerId) { this.providerId = providerId; }
        
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        
        public InstanceState getState() { return state; }
        public void setState(InstanceState state) { this.state = state; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        
        public long getLastAccessedAt() { return lastAccessedAt; }
        public void setLastAccessedAt(long lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
        
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    public enum InstanceState {
        CREATED,
        INITIALIZING,
        ACTIVE,
        PAUSED,
        ERROR,
        DESTROYED
    }
}
