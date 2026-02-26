package net.ooder.sdk.llm.chat;

import net.ooder.sdk.llm.integration.SceneEngineIntegration;

/**
 * SceneChat 工厂
 */
public interface SceneChatFactory {

    /**
     * 创建场景对话实例
     * @param sceneId 场景ID
     * @return SceneChat 实例
     */
    SceneChat createSceneChat(String sceneId);

    /**
     * 创建场景对话实例（带配置）
     * @param sceneId 场景ID
     * @param config 配置
     * @return SceneChat 实例
     */
    SceneChat createSceneChat(String sceneId, SceneChatConfig config);

    /**
     * 设置场景引擎集成
     */
    void setSceneEngineIntegration(SceneEngineIntegration integration);

    /**
     * SceneChat 配置
     */
    class SceneChatConfig {
        private boolean enableStreaming;
        private long defaultTimeout;
        private int maxHistorySize;
        private boolean enableCollaboration;

        public boolean isEnableStreaming() {
            return enableStreaming;
        }

        public void setEnableStreaming(boolean enableStreaming) {
            this.enableStreaming = enableStreaming;
        }

        public long getDefaultTimeout() {
            return defaultTimeout;
        }

        public void setDefaultTimeout(long defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
        }

        public int getMaxHistorySize() {
            return maxHistorySize;
        }

        public void setMaxHistorySize(int maxHistorySize) {
            this.maxHistorySize = maxHistorySize;
        }

        public boolean isEnableCollaboration() {
            return enableCollaboration;
        }

        public void setEnableCollaboration(boolean enableCollaboration) {
            this.enableCollaboration = enableCollaboration;
        }
    }
}
