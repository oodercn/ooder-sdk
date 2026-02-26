package net.ooder.scene.core;

/**
 * SceneAgent 核心接口 (scene-engine 内部使用)
 *
 * <p>注意: 此接口是 scene-engine 内部使用的 SceneAgent 定义，
 * 与 agent-sdk 的 {@link net.ooder.sdk.api.agent.SceneAgent} 不同。</p>
 *
 * <p>scene-engine 的 SceneAgent 专注于场景生命周期管理和 Skill 挂载，
 * 而 agent-sdk 的 SceneAgent 专注于 Capability 管理。</p>
 *
 * <p>DefaultSceneAgent 同时实现了两个接口，桥接两者功能。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 * @deprecated 使用 {@link net.ooder.sdk.api.agent.SceneAgent} 替代
 */
@Deprecated
public interface SceneAgentCore {

    /**
     * 获取 Agent ID
     */
    String getAgentId();

    /**
     * 获取当前状态
     */
    SceneAgentState getAgentCoreState();

    /**
     * 初始化
     */
    void initialize(SceneConfig config);

    /**
     * 挂载 Skill
     */
    void mountSkill(String skillId, SceneConfig config);

    /**
     * 卸载 Skill
     */
    void unmountSkill(String skillId);

    /**
     * 调用能力
     */
    CapResponse invokeCap(String capId, CapRequest request);

    /**
     * 关闭
     */
    void shutdown();
}
