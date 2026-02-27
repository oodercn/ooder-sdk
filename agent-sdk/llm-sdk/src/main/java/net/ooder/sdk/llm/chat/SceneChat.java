package net.ooder.sdk.llm.chat;

import net.ooder.sdk.llm.model.AgentInfo;
import net.ooder.sdk.llm.model.SceneMessage;

import java.util.List;

/**
 * 场景对话接口 - llm-sdk 2.3
 * 支持在场景中与多个智能体对话
 */
public interface SceneChat {

    /**
     * 获取场景ID
     */
    String getSceneId();

    /**
     * 获取场景中的所有智能体
     */
    List<AgentInfo> getAgents();

    /**
     * 发送消息到场景（广播给所有智能体）
     * @param message 用户消息
     * @return 消息ID
     */
    String broadcastMessage(String message);

    /**
     * 发送消息到指定智能体
     * @param agentId 目标智能体ID
     * @param message 用户消息
     * @return 消息ID
     */
    String sendToAgent(String agentId, String message);

    /**
     * 获取对话历史
     */
    List<SceneMessage> getHistory();

    /**
     * 订阅场景消息
     * @param listener 消息监听器
     */
    void subscribe(SceneMessageListener listener);

    /**
     * 取消订阅
     */
    void unsubscribe(SceneMessageListener listener);

    /**
     * 开始协作模式
     * @param task 协作任务描述
     */
    CollaborationSession startCollaboration(String task);

    /**
     * 结束场景对话
     */
    void close();
}
