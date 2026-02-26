package net.ooder.sdk.api.agent;

import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.capability.CapAddress;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SceneAgent 接口
 *
 * <p>遵循 v0.8.0 架构 "Scene = Agent" 原则,作为场景执行的核心单元</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface SceneAgent extends Agent {

    /**
     * 获取场景ID
     */
    String getSceneId();

    /**
     * 获取域ID
     */
    String getDomainId();

    /**
     * 获取 CAP 注册表
     */
    CapRegistry getCapRegistry();

    /**
     * 获取场景上下文
     */
    SceneContext getContext();

    /**
     * 调用能力 (同步)
     *
     * @param capId 能力ID
     * @param params 参数
     * @return 调用结果
     */
    Object invokeCapability(String capId, Map<String, Object> params);

    /**
     * 调用能力 (异步)
     *
     * @param capId 能力ID
     * @param params 参数
     * @return CompletableFuture
     */
    CompletableFuture<Object> invokeCapabilityAsync(String capId, Map<String, Object> params);

    /**
     * 按地址调用能力
     *
     * @param address 能力地址
     * @param params 参数
     * @return 调用结果
     */
    Object invokeByAddress(CapAddress address, Map<String, Object> params);

    /**
     * 注册能力
     *
     * @param capability 能力定义
     */
    void registerCapability(Capability capability);

    /**
     * 注销能力
     *
     * @param capId 能力ID
     */
    void unregisterCapability(String capId);

    /**
     * 检查 Agent 是否运行中
     */
    boolean isRunning();

    /**
     * 获取 Agent 状态
     */
    AgentStatus getAgentStatus();

    /**
     * Agent 状态 (SceneAgent 专用)
     */
    enum AgentStatus {
        CREATED,
        INITIALIZING,
        RUNNING,
        PAUSED,
        STOPPING,
        STOPPED,
        FAILED
    }
}
