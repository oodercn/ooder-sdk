package net.ooder.sdk.core.agent.impl;

import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.SceneContext;
import net.ooder.sdk.api.capability.CapAddress;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.core.capability.impl.InMemoryCapRegistry;
import net.ooder.sdk.api.skill.SkillInvoker;
import net.ooder.sdk.common.enums.AgentType;
import net.ooder.sdk.core.event.EventBean;
import net.ooder.sdk.core.event.agent.AgentStateChangedEvent;
import net.ooder.sdk.core.event.skill.SkillInvocationCompletedEvent;
import net.ooder.sdk.core.event.skill.SkillInvocationStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SceneAgent 实现
 *
 * <p>遵循 v0.8.0 架构 "Scene = Agent" 原则</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public class SceneAgentImpl implements SceneAgent {

    private static final Logger log = LoggerFactory.getLogger(SceneAgentImpl.class);

    private final String agentId;
    private final String agentName;
    private final String sceneId;
    private final String domainId;
    private final AgentType agentType;
    private final String endpoint;

    private final AtomicReference<AgentState> state = new AtomicReference<>(AgentState.CREATED);
    private final AtomicReference<AgentStatus> agentStatus = new AtomicReference<>(AgentStatus.CREATED);

    private final CapRegistry capRegistry;
    private final SceneContext context;

    // Skill 调用器 - 用于真实调用 Skill 能力
    private SkillInvoker skillInvoker;

    public SceneAgentImpl(String sceneId, String agentName) {
        this(sceneId, agentName, "default");
    }

    public SceneAgentImpl(String sceneId, String agentName, String domainId) {
        this.sceneId = sceneId;
        this.agentName = agentName;
        this.domainId = domainId;
        this.agentId = generateAgentId(sceneId, agentName);
        this.agentType = AgentType.SCENE;
        this.endpoint = "scene://" + sceneId + "/" + agentId;

        this.capRegistry = new InMemoryCapRegistry();
        this.context = new SceneContext(UUID.randomUUID().toString(), sceneId, domainId);

        log.info("SceneAgent created: {} for scene {} in domain {}", agentId, sceneId, domainId);
    }

    private String generateAgentId(String sceneId, String agentName) {
        return "scene-" + sceneId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 设置 Skill 调用器
     *
     * @param skillInvoker Skill 调用器
     */
    public void setSkillInvoker(SkillInvoker skillInvoker) {
        this.skillInvoker = skillInvoker;
        log.info("SkillInvoker set for SceneAgent: {}", agentId);
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public AgentType getAgentType() {
        return agentType;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
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
    public Object invokeCapability(String capId, Map<String, Object> params) {
        log.debug("Invoking capability: {} with params: {}", capId, params);

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

    @Override
    public void registerCapability(Capability capability) {
        try {
            capRegistry.register(capability);
            log.info("Capability registered: {} at address {}", capability.getCapId(), capability.getAddress());
        } catch (Exception e) {
            log.error("Failed to register capability: {}", capability.getCapId(), e);
            throw new RuntimeException("Failed to register capability", e);
        }
    }

    @Override
    public void unregisterCapability(String capId) {
        try {
            capRegistry.unregister(capId);
            log.info("Capability unregistered: {}", capId);
        } catch (Exception e) {
            log.error("Failed to unregister capability: {}", capId, e);
            throw new RuntimeException("Failed to unregister capability", e);
        }
    }

    @Override
    public void start() {
        log.info("Starting SceneAgent: {}", agentId);

        if (!transitionState(AgentState.CREATED, AgentState.INITIALIZING) &&
            !transitionState(AgentState.STOPPED, AgentState.INITIALIZING)) {
            log.warn("Cannot start agent from state: {}", state.get());
            return;
        }

        try {
            transitionState(AgentState.INITIALIZING, AgentState.STARTING);
            transitionState(AgentState.STARTING, AgentState.RUNNING);
            agentStatus.set(AgentStatus.RUNNING);

            log.info("SceneAgent started successfully: {}", agentId);
        } catch (Exception e) {
            state.set(AgentState.ERROR);
            agentStatus.set(AgentStatus.FAILED);
            log.error("Failed to start SceneAgent: {}", agentId, e);
            throw new RuntimeException("Failed to start agent", e);
        }
    }

    @Override
    public void stop() {
        log.info("Stopping SceneAgent: {}", agentId);

        if (!transitionState(AgentState.RUNNING, AgentState.STOPPING)) {
            log.warn("Cannot stop agent from state: {}", state.get());
            return;
        }

        try {
            transitionState(AgentState.STOPPING, AgentState.STOPPED);
            agentStatus.set(AgentStatus.STOPPED);

            log.info("SceneAgent stopped successfully: {}", agentId);
        } catch (Exception e) {
            state.set(AgentState.ERROR);
            agentStatus.set(AgentStatus.FAILED);
            log.error("Failed to stop SceneAgent: {}", agentId, e);
            throw new RuntimeException("Failed to stop agent", e);
        }
    }

    @Override
    public boolean isRunning() {
        return state.get() == AgentState.RUNNING && agentStatus.get() == AgentStatus.RUNNING;
    }

    @Override
    public boolean isHealthy() {
        return isRunning();
    }

    @Override
    public AgentState getState() {
        return state.get();
    }

    @Override
    public AgentStatus getAgentStatus() {
        return agentStatus.get();
    }

    private boolean transitionState(AgentState from, AgentState to) {
        boolean success = state.compareAndSet(from, to);
        if (success) {
            // 发布 Core 层 Agent 状态变更事件
            try {
                EventBean.getInstance().publish(
                    new AgentStateChangedEvent(agentId, agentName, from, to)
                );
            } catch (Exception e) {
                log.error("Failed to publish AgentStateChangedEvent for agent: {}", agentId, e);
            }
        }
        return success;
    }

    /**
     * 内部方法：调用能力
     *
     * <p>优先使用 SkillInvoker 进行真实调用，如果没有设置则使用模拟实现</p>
     */
    private Object invokeCapabilityInternal(Capability capability, Map<String, Object> params) {
        String skillId = capability.getSkillId();
        String capId = capability.getCapId();
        long startTime = System.currentTimeMillis();

        // 发布 Core 层调用开始事件
        try {
            EventBean.getInstance().publish(
                new SkillInvocationStartedEvent(skillId, capId, params)
            );
        } catch (Exception e) {
            log.error("Failed to publish SkillInvocationStartedEvent", e);
        }

        try {
            Object result;

            // 如果有 SkillInvoker，使用真实调用
            if (skillInvoker != null && skillInvoker.isSkillAvailable(skillId)) {
                log.debug("Invoking skill {} capability {} via SkillInvoker", skillId, capId);
                result = skillInvoker.invoke(skillId, capId, params);
            } else {
                // 如果没有 SkillInvoker 或 Skill 不可用，使用模拟实现（向后兼容）
                log.warn("No SkillInvoker available or skill {} not available, using mock implementation", skillId);
                result = invokeCapabilityMock(capability, params);
            }

            long duration = System.currentTimeMillis() - startTime;

            // 发布 Core 层调用完成事件（成功）
            try {
                EventBean.getInstance().publish(
                    new SkillInvocationCompletedEvent(skillId, capId, true, duration, 
                        summarizeResult(result), null)
                );
            } catch (Exception e) {
                log.error("Failed to publish SkillInvocationCompletedEvent", e);
            }

            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            // 发布 Core 层调用完成事件（失败）
            try {
                EventBean.getInstance().publish(
                    new SkillInvocationCompletedEvent(skillId, capId, false, duration, 
                        null, e.getMessage())
                );
            } catch (Exception ex) {
                log.error("Failed to publish SkillInvocationCompletedEvent", ex);
            }

            log.error("Failed to invoke skill {} capability {}", skillId, capId, e);
            throw new RuntimeException("Skill invocation failed", e);
        }
    }

    /**
     * 摘要结果（用于事件，避免大对象）
     */
    private Object summarizeResult(Object result) {
        if (result == null) {
            return null;
        }
        // 简单摘要，避免大对象
        String resultStr = result.toString();
        if (resultStr.length() > 100) {
            return resultStr.substring(0, 100) + "...";
        }
        return resultStr;
    }

    /**
     * Mock 实现 - 用于向后兼容和测试
     */
    private Object invokeCapabilityMock(Capability capability, Map<String, Object> params) {
        log.debug("Executing capability (mock): {} with params: {}", capability.getCapId(), params);

        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("capId", capability.getCapId());
        result.put("status", "success");
        result.put("params", params);
        result.put("mock", true);

        return result;
    }
}
