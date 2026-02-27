package net.ooder.sdk.llm.chat;

import net.ooder.sdk.llm.model.SceneMessage;

/**
 * 场景消息监听器
 */
public interface SceneMessageListener {

    /**
     * 收到消息时触发
     */
    void onMessage(SceneMessage message);

    /**
     * 智能体加入场景时触发
     */
    void onAgentJoined(String agentId);

    /**
     * 智能体离开场景时触发
     */
    void onAgentLeft(String agentId);

    /**
     * 场景关闭时触发
     */
    void onSceneClosed();
}
