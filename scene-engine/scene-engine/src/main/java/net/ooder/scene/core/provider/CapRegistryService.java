package net.ooder.scene.core.provider;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.capability.CapabilityEvent;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.capability.CapabilityStatus;
import net.ooder.sdk.core.capability.impl.InMemoryCapRegistry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapRegistryService {
    private CapRegistry registry;
    private Map<String, CapVersionManager> versionManagers;
    private SceneEventPublisher eventPublisher;

    public CapRegistryService() {
        this.registry = new InMemoryCapRegistry();
        this.versionManagers = new ConcurrentHashMap<>();
    }
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void registerCapability(Capability capability) {
        try {
            registry.register(capability);
            
            String capId = capability.getCapabilityId();
            if (!versionManagers.containsKey(capId)) {
                versionManagers.put(capId, new CapVersionManager(capId));
            }

            CapVersionManager manager = versionManagers.get(capId);
            manager.addVersion(capability.getVersion(), capability);
            
            publishCapabilityEvent(CapabilityEvent.registered(this, capId, 
                capability.getName(), capability.getSkillId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to register capability", e);
        }
    }

    public Capability getCapability(String capId) {
        return registry.findById(capId);
    }

    public Capability getCapability(String capId, String version) {
        CapVersionManager manager = versionManagers.get(capId);
        if (manager != null) {
            return manager.getVersion(version);
        }
        return null;
    }

    public void unregisterCapability(String capId) {
        try {
            Capability capability = registry.findById(capId);
            String capName = capability != null ? capability.getName() : null;
            
            registry.unregister(capId);
            versionManagers.remove(capId);
            
            publishCapabilityEvent(CapabilityEvent.unregistered(this, capId, capName));
        } catch (Exception e) {
            throw new RuntimeException("Failed to unregister capability", e);
        }
    }

    public boolean hasCapability(String capId) {
        return registry.hasCapability(capId);
    }

    public List<Capability> getAllCapabilities() {
        return registry.findAll();
    }
    
    private void publishCapabilityEvent(CapabilityEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    private static class CapVersionManager {
        private String capId;
        private Map<String, Capability> versions;

        public CapVersionManager(String capId) {
            this.capId = capId;
            this.versions = new ConcurrentHashMap<>();
        }

        public void addVersion(String version, Capability capability) {
            versions.put(version, capability);
        }

        public Capability getVersion(String version) {
            return versions.get(version);
        }

        public Map<String, Capability> getAllVersions() {
            return versions;
        }
    }
}
