package net.ooder.sdk.api.agent.support;

import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.SceneContext;
import net.ooder.sdk.api.capability.CapAddress;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.CapRegistryException;
import net.ooder.sdk.api.capability.CapRegistryListener;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.capability.CapabilityStatus;
import net.ooder.sdk.api.capability.CapabilityType;
import net.ooder.sdk.common.enums.AgentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SceneAgent 抽象基类
 * 提供场景上下文、能力注册等通用实现
 *
 * @version 3.0.1
 * @since 3.0.0
 */
@PublicAPI
public abstract class AbstractSceneAgent extends AbstractAgent implements SceneAgent {

    protected final String sceneId;
    protected final String domainId;
    protected final CapRegistry capRegistry;
    protected volatile AgentStatus agentStatus = AgentStatus.CREATED;
    protected SceneContext context;

    public AbstractSceneAgent(String sceneId, String agentName, String domainId) {
        super(generateSceneAgentId(sceneId, agentName), agentName, AgentType.SCENE);
        this.sceneId = sceneId;
        this.domainId = domainId;
        this.capRegistry = createCapRegistry();
        this.context = createSceneContext(sceneId, domainId);
    }

    private static String generateSceneAgentId(String sceneId, String name) {
        return "scene-" + sceneId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected CapRegistry createCapRegistry() {
        return new InMemoryCapRegistry();
    }

    protected SceneContext createSceneContext(String sceneId, String domainId) {
        return new SceneContext(UUID.randomUUID().toString(), sceneId, domainId);
    }

    @Override
    public String getSceneId() {
        return sceneId;
    }

    @Override
    public String getDomainId() {
        return domainId;
    }

    @Override
    public CapRegistry getCapRegistry() {
        return capRegistry;
    }

    @Override
    public SceneContext getContext() {
        return context;
    }

    @Override
    public boolean isRunning() {
        return getState() == Agent.AgentState.RUNNING && agentStatus == AgentStatus.RUNNING;
    }

    @Override
    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    @Override
    public void registerCapability(Capability capability) {
        try {
            capRegistry.register(capability);
        } catch (CapRegistryException e) {
            throw new RuntimeException("Failed to register capability", e);
        }
    }

    @Override
    public void unregisterCapability(String capId) {
        try {
            capRegistry.unregister(capId);
        } catch (CapRegistryException e) {
            throw new RuntimeException("Failed to unregister capability", e);
        }
    }

    @Override
    public Object invokeCapability(String capId, Map<String, Object> params) {
        Capability capability = capRegistry.findById(capId);
        if (capability == null) {
            throw new RuntimeException("Capability not found: " + capId);
        }
        return invokeCapabilityInternal(capability, params);
    }

    @Override
    public CompletableFuture<Object> invokeCapabilityAsync(String capId, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> invokeCapability(capId, params));
    }

    @Override
    public Object invokeByAddress(CapAddress address, Map<String, Object> params) {
        Capability capability = capRegistry.findByAddress(address);
        if (capability == null) {
            throw new RuntimeException("Capability not found at address: " + address);
        }
        return invokeCapability(capability.getCapId(), params);
    }

    protected abstract Object invokeCapabilityInternal(Capability capability, Map<String, Object> params);

    protected void setAgentStatus(AgentStatus status) {
        this.agentStatus = status;
    }

    protected static class InMemoryCapRegistry implements CapRegistry {
        private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();
        private final List<CapRegistryListener> listeners = new CopyOnWriteArrayList<>();

        @Override
        public void register(Capability capability) throws CapRegistryException {
            capabilities.put(capability.getCapId(), capability);
        }

        @Override
        public void unregister(String capId) throws CapRegistryException {
            capabilities.remove(capId);
        }

        @Override
        public Capability findById(String capId) {
            return capabilities.get(capId);
        }

        @Override
        public Capability findByAddress(CapAddress address) {
            return capabilities.values().stream()
                    .filter(cap -> cap.getAddress() != null && cap.getAddress().equals(address))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Capability> findByDomain(String domainId) {
            List<Capability> result = new ArrayList<>();
            for (Capability cap : capabilities.values()) {
                if (cap.getAddress() != null && domainId.equals(cap.getAddress().getDomainId())) {
                    result.add(cap);
                }
            }
            return result;
        }

        @Override
        public List<Capability> findByType(CapabilityType type) {
            List<Capability> result = new ArrayList<>();
            for (Capability cap : capabilities.values()) {
                if (cap.getType() != null && cap.getType().equals(type.name())) {
                    result.add(cap);
                }
            }
            return result;
        }

        @Override
        public List<Capability> findBySkill(String skillId) {
            List<Capability> result = new ArrayList<>();
            for (Capability cap : capabilities.values()) {
                if (skillId.equals(cap.getSkillId())) {
                    result.add(cap);
                }
            }
            return result;
        }

        @Override
        public List<Capability> findByTag(String tag) {
            List<Capability> result = new ArrayList<>();
            for (Capability cap : capabilities.values()) {
                if (cap.getTags() != null && cap.getTags().contains(tag)) {
                    result.add(cap);
                }
            }
            return result;
        }

        @Override
        public List<Capability> findAll() {
            return new ArrayList<>(capabilities.values());
        }

        @Override
        public boolean hasCapability(String capId) {
            return capabilities.containsKey(capId);
        }

        @Override
        public boolean isAddressOccupied(CapAddress address) {
            return findByAddress(address) != null;
        }

        @Override
        public void updateStatus(String capId, CapabilityStatus status) throws CapRegistryException {
            Capability cap = capabilities.get(capId);
            if (cap != null) {
                cap.setStatus(status);
            }
        }

        @Override
        public CapabilityStatus getStatus(String capId) {
            Capability cap = capabilities.get(capId);
            return cap != null ? cap.getStatus() : null;
        }

        @Override
        public CapAddress allocateAddress(String domainId) throws CapRegistryException {
            return CapAddress.ofZone(CapAddress.AddressZone.EXTENSION, domainId);
        }

        @Override
        public void releaseAddress(CapAddress address) {
        }

        @Override
        public DomainStats getDomainStats(String domainId) {
            DomainStats stats = new DomainStats();
            stats.setDomainId(domainId);
            stats.setTotalCapabilities(findByDomain(domainId).size());
            return stats;
        }

        @Override
        public List<String> getAllDomains() {
            return Collections.emptyList();
        }

        @Override
        public void addListener(CapRegistryListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removeListener(CapRegistryListener listener) {
            listeners.remove(listener);
        }

        @Override
        public void clear() {
            capabilities.clear();
        }

        @Override
        public int size() {
            return capabilities.size();
        }
    }
}
