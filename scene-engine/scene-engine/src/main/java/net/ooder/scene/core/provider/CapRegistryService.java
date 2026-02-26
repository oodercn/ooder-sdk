package net.ooder.scene.core.provider;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.capability.CapabilityEvent;
import net.ooder.scene.core.CapabilityInfo;
import net.ooder.scene.core.CapRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapRegistryService {
    private CapRegistry registry;
    private Map<String, CapVersionManager> versionManagers;
    private SceneEventPublisher eventPublisher;

    public CapRegistryService() {
        this.registry = new CapRegistry();
        this.versionManagers = new ConcurrentHashMap<>();
    }
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void registerCapability(CapabilityInfo info) {
        registry.registerCapability(info.getCapId(), info);

        if (!versionManagers.containsKey(info.getCapId())) {
            versionManagers.put(info.getCapId(), new CapVersionManager(info.getCapId()));
        }

        CapVersionManager manager = versionManagers.get(info.getCapId());
        manager.addVersion(info.getVersion(), info);
        
        publishCapabilityEvent(CapabilityEvent.registered(this, info.getCapId(), 
            info.getName(), info.getSkillId()));
    }

    public CapabilityInfo getCapability(String capId) {
        return registry.getCapability(capId);
    }

    public CapabilityInfo getCapability(String capId, String version) {
        CapVersionManager manager = versionManagers.get(capId);
        if (manager != null) {
            return manager.getVersion(version);
        }
        return null;
    }

    public void unregisterCapability(String capId) {
        CapabilityInfo info = registry.getCapability(capId);
        String capName = info != null ? info.getName() : null;
        
        registry.unregisterCapability(capId);
        versionManagers.remove(capId);
        
        publishCapabilityEvent(CapabilityEvent.unregistered(this, capId, capName));
    }

    public boolean hasCapability(String capId) {
        return registry.hasCapability(capId);
    }

    public Map<String, CapabilityInfo> getAllCapabilities() {
        return registry.getAllCapabilities();
    }
    
    private void publishCapabilityEvent(CapabilityEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    private static class CapVersionManager {
        private String capId;
        private Map<String, CapabilityInfo> versions;

        public CapVersionManager(String capId) {
            this.capId = capId;
            this.versions = new ConcurrentHashMap<>();
        }

        public void addVersion(String version, CapabilityInfo info) {
            versions.put(version, info);
        }

        public CapabilityInfo getVersion(String version) {
            return versions.get(version);
        }

        public Map<String, CapabilityInfo> getAllVersions() {
            return versions;
        }
    }
}
