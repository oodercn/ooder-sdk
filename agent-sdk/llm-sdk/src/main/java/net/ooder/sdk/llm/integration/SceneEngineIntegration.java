package net.ooder.sdk.llm.integration;

import net.ooder.sdk.llm.model.AgentInfo;
import net.ooder.sdk.llm.model.SceneMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SceneEngine 集成接口 - llm-sdk 2.3
 * 定义与 scene-engine 的集成契约
 */
public interface SceneEngineIntegration {

    /**
     * 获取场景中的所有智能体
     * @param sceneId 场景ID
     * @return 智能体列表
     */
    List<AgentInfo> getSceneAgents(String sceneId);

    /**
     * 发送消息到场景
     * @param sceneId 场景ID
     * @param message 场景消息
     * @return 消息ID
     */
    String sendMessageToScene(String sceneId, SceneMessage message);

    /**
     * 发送消息到指定智能体
     * @param sceneId 场景ID
     * @param agentId 智能体ID
     * @param message 消息内容
     * @return 消息ID
     */
    String sendMessageToAgent(String sceneId, String agentId, String message);

    /**
     * 获取场景消息历史
     * @param sceneId 场景ID
     * @param limit 限制条数
     * @return 消息列表
     */
    List<SceneMessage> getMessageHistory(String sceneId, int limit);

    /**
     * 订阅场景消息
     * @param sceneId 场景ID
     * @param listener 消息监听器
     */
    void subscribeToScene(String sceneId, SceneMessageListener listener);

    /**
     * 取消订阅场景消息
     * @param sceneId 场景ID
     * @param listener 消息监听器
     */
    void unsubscribeFromScene(String sceneId, SceneMessageListener listener);

    /**
     * 注册智能体
     * @param sceneId 场景ID
     * @param agentInfo 智能体信息
     */
    void registerAgent(String sceneId, AgentInfo agentInfo);

    /**
     * 注销智能体
     * @param sceneId 场景ID
     * @param agentId 智能体ID
     */
    void unregisterAgent(String sceneId, String agentId);

    /**
     * 检查场景是否存在
     * @param sceneId 场景ID
     * @return 是否存在
     */
    boolean sceneExists(String sceneId);

    /**
     * 创建场景
     * @param sceneId 场景ID
     * @param config 场景配置
     * @return 是否创建成功
     */
    boolean createScene(String sceneId, SceneConfig config);

    /**
     * 关闭场景
     * @param sceneId 场景ID
     */
    void closeScene(String sceneId);

    /**
     * 场景消息监听器
     */
    interface SceneMessageListener {
        /**
         * 收到消息
         */
        void onMessage(SceneMessage message);

        /**
         * 智能体加入
         */
        void onAgentJoined(String agentId, AgentInfo agentInfo);

        /**
         * 智能体离开
         */
        void onAgentLeft(String agentId);
    }

    /**
     * 场景配置
     */
    class SceneConfig {
        private String name;
        private String description;
        private List<String> initialAgents;
        private boolean enablePersistence;
        private long messageRetentionTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getInitialAgents() {
            return initialAgents;
        }

        public void setInitialAgents(List<String> initialAgents) {
            this.initialAgents = initialAgents;
        }

        public boolean isEnablePersistence() {
            return enablePersistence;
        }

        public void setEnablePersistence(boolean enablePersistence) {
            this.enablePersistence = enablePersistence;
        }

        public long getMessageRetentionTime() {
            return messageRetentionTime;
        }

        public void setMessageRetentionTime(long messageRetentionTime) {
            this.messageRetentionTime = messageRetentionTime;
        }
    }
}
