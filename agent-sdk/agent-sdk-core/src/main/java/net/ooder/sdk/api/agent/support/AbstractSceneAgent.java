package net.ooder.sdk.api.agent.support;

import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.SceneContext;
import net.ooder.sdk.api.capability.CapAddress;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.common.enums.AgentType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * SceneAgent 抽象基类
 * 提供场景上下文、能力注册等通用实现
 *
 * @version 3.0.0
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

    private static String generateSceneAgentId(String sceneId, String agentName) {
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
        return getState() == AgentState.RUNNING && agentStatus == AgentStatus.RUNNING;
    }

    @Override
    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    @Override
    public void registerCapability(Capability capability) {
        capRegistry.register(capability);
    }

    @Override
    public void unregisterCapability(String capId) {
        capRegistry.unregister(capId);
    }

    @Override
    public Object invokeCapability(String capId, Map<String, Object> params) {
        Capability capability = capRegistry.findById(capId);
        if (capability == null) {
            throw new RuntimeException("Capability not found: " + capId);
        }
        if (!capability.isAvailable()) {
            throw new RuntimeException("Capability not available: " + capId);
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
        private final Map<String, Capability> capabilities = new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        public void register(Capability capability) {
            capabilities.put(capability.getCapId(), capability);
        }

        @Override
        public void unregister(String capId) {
            capabilities.remove(capId);
        }

        @Override
        public Capability findById(String capId) {
            return capabilities.get(capId);
        }

        @Override
        public Capability findByAddress(CapAddress address) {
            return capabilities.values().stream()
                    .filter(cap -> cap.getAddress().equals(address))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public java.util.List<Capability> findAll() {
            return new java.util.ArrayList<>(capabilities.values());
        }
    }
}
