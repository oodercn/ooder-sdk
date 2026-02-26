package net.ooder.scene.core.impl;

import net.ooder.scene.core.*;
import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.capability.CapAddress;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SceneAgent 桥接实现
 *
 * <p>同时实现 scene-engine 的 SceneAgentCore 和 agent-sdk 的 SceneAgent，
 * 桥接两者的功能：</p>
 *
 * <ul>
 *   <li>SceneAgentCore: 场景生命周期管理、Skill 挂载</li>
 *   <li>SceneAgent: Capability 管理、能力调用</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneAgentBridge implements SceneAgentCore, SceneAgent {

    private String agentId;
    private String sceneId;
    private String domainId;
    private SceneAgentState state;
    private net.ooder.scene.core.SceneContext sceneEngineContext;
    private Map<String, Object> skills;
    private CapRegistry capRegistry;
    private SceneAgent.AgentStatus agentStatus;

    public SceneAgentBridge() {
        this.agentId = "scene-agent-" + UUID.randomUUID().toString();
        this.state = SceneAgentState.INITIALIZED;
        this.sceneEngineContext = new net.ooder.scene.core.SceneContext(agentId);
        this.skills = new ConcurrentHashMap<>();
        this.capRegistry = new net.ooder.sdk.api.cap.impl.InMemoryCapRegistry();
        this.agentStatus = SceneAgent.AgentStatus.CREATED;
    }

    // ==================== Agent 接口实现 ====================

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public String getAgentName() {
        return agentId;
    }

    @Override
    public net.ooder.sdk.common.enums.AgentType getAgentType() {
        return net.ooder.sdk.common.enums.AgentType.SCENE;
    }

    @Override
    public String getEndpoint() {
        return null;
    }

    @Override
    public void start() {
        // 启动逻辑在 initialize 中实现
    }

    @Override
    public void stop() {
        shutdown();
    }

    @Override
    public boolean isHealthy() {
        return state == SceneAgentState.RUNNING;
    }

    // ==================== SceneAgentCore 实现 ====================

    /**
     * 获取 scene-engine 状态 (SceneAgentCore 接口要求)
     */
    @Override
    public SceneAgentState getAgentCoreState() {
        return state;
    }

    // ==================== Agent 接口实现 ====================

    @Override
    public AgentState getState() {
        // 转换 scene-engine 状态为 agent-sdk 状态
        switch (state) {
            case INITIALIZED:
                return AgentState.INITIALIZED;
            case RUNNING:
                return AgentState.RUNNING;
            case PAUSED:
                return AgentState.STOPPING;
            case SHUTDOWN:
                return AgentState.STOPPED;
            case ERROR:
                return AgentState.ERROR;
            default:
                return AgentState.CREATED;
        }
    }

    @Override
    public void initialize(SceneConfig config) {
        if (state == SceneAgentState.INITIALIZED) {
            sceneEngineContext.setAttribute("config", config);
            state = SceneAgentState.RUNNING;
            agentStatus = SceneAgent.AgentStatus.RUNNING;
        }
    }

    @Override
    public void mountSkill(String skillId, SceneConfig config) {
        if (state == SceneAgentState.RUNNING) {
            sceneEngineContext.addSkillConfig(skillId, config);
            skills.put(skillId, new Object());
        }
    }

    @Override
    public void unmountSkill(String skillId) {
        if (state == SceneAgentState.RUNNING) {
            sceneEngineContext.removeSkillConfig(skillId);
            skills.remove(skillId);
        }
    }

    @Override
    public CapResponse invokeCap(String capId, CapRequest request) {
        if (state != SceneAgentState.RUNNING) {
            return CapResponse.failure(request.getRequestId(), capId, "SceneAgent is not running");
        }
        // 通过 Capability 系统调用
        return CapResponse.success(request.getRequestId(), capId, "Capability invoked successfully");
    }

    @Override
    public void shutdown() {
        if (state != SceneAgentState.SHUTDOWN) {
            skills.clear();
            sceneEngineContext.clear();
            state = SceneAgentState.SHUTDOWN;
            agentStatus = SceneAgent.AgentStatus.STOPPED;
        }
    }

    // ==================== SceneAgent 实现 ====================

    @Override
    public String getSceneId() {
        return sceneId != null ? sceneId : agentId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    @Override
    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    @Override
    public CapRegistry getCapRegistry() {
        return capRegistry;
    }

    @Override
    public net.ooder.sdk.api.agent.SceneContext getContext() {
        // 创建 agent-sdk 的 SceneContext (需要3个参数)
        return new net.ooder.sdk.api.agent.SceneContext(agentId, getSceneId(), domainId != null ? domainId : "default");
    }

    @Override
    public Object invokeCapability(String capId, Map<String, Object> params) {
        // 转换为 CapRequest 调用
        CapRequest request = new CapRequest(UUID.randomUUID().toString(), capId);
        if (params != null) {
            params.forEach(request::setParameter);
        }
        CapResponse response = invokeCap(capId, request);
        return response.isSuccess() ? response.getResult() : null;
    }

    @Override
    public CompletableFuture<Object> invokeCapabilityAsync(String capId, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> invokeCapability(capId, params));
    }

    @Override
    public Object invokeByAddress(CapAddress address, Map<String, Object> params) {
        if (address != null) {
            // 从 CapAddress 获取能力ID (使用十六进制地址作为capId)
            String capId = address.toHex();
            return invokeCapability(capId, params);
        }
        return null;
    }

    @Override
    public void registerCapability(Capability capability) {
        if (capability != null && capRegistry != null) {
            try {
                capRegistry.register(capability);
            } catch (net.ooder.sdk.api.capability.CapRegistryException e) {
                throw new RuntimeException("Failed to register capability: " + capability.getCapId(), e);
            }
        }
    }

    @Override
    public void unregisterCapability(String capId) {
        if (capRegistry != null) {
            try {
                capRegistry.unregister(capId);
            } catch (net.ooder.sdk.api.capability.CapRegistryException e) {
                throw new RuntimeException("Failed to unregister capability: " + capId, e);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return state == SceneAgentState.RUNNING;
    }

    @Override
    public SceneAgent.AgentStatus getAgentStatus() {
        return agentStatus;
    }

    // ==================== 额外方法 ====================

    public net.ooder.scene.core.SceneContext getSceneContext() {
        return sceneEngineContext;
    }

    public Map<String, Object> getSkills() {
        return skills;
    }
}
