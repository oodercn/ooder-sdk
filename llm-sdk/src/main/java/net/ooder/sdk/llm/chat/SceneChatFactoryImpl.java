package net.ooder.sdk.llm.chat;

import net.ooder.sdk.llm.integration.SceneEngineIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SceneChat 工厂实现类
 */
public class SceneChatFactoryImpl implements SceneChatFactory {

    private static final Logger log = LoggerFactory.getLogger(SceneChatFactoryImpl.class);

    private final Map<String, SceneChat> sceneChats;
    private SceneEngineIntegration sceneEngineIntegration;

    public SceneChatFactoryImpl() {
        this.sceneChats = new ConcurrentHashMap<>();
    }

    @Override
    public SceneChat createSceneChat(String sceneId) {
        return createSceneChat(sceneId, new SceneChatConfig());
    }

    @Override
    public SceneChat createSceneChat(String sceneId, SceneChatConfig config) {
        if (sceneId == null || sceneId.trim().isEmpty()) {
            throw new IllegalArgumentException("Scene ID cannot be null or empty");
        }

        // 检查是否已存在
        SceneChat existing = sceneChats.get(sceneId);
        if (existing != null) {
            log.warn("SceneChat already exists for scene: {}, returning existing instance", sceneId);
            return existing;
        }

        // 创建新的 SceneChat 实例
        SceneChatImpl sceneChat = new SceneChatImpl(sceneId);

        // 应用配置
        if (config != null) {
            applyConfig(sceneChat, config);
        }

        // 注册到 SceneEngine（如果集成存在）
        if (sceneEngineIntegration != null) {
            try {
                SceneEngineIntegration.SceneConfig sceneConfig = new SceneEngineIntegration.SceneConfig();
                sceneConfig.setName(sceneId);
                sceneEngineIntegration.createScene(sceneId, sceneConfig);
                log.debug("Registered scene {} with SceneEngine", sceneId);
            } catch (Exception e) {
                log.warn("Failed to register scene with SceneEngine: {}", e.getMessage());
            }
        }

        sceneChats.put(sceneId, sceneChat);
        log.info("Created SceneChat for scene: {}", sceneId);

        return sceneChat;
    }

    @Override
    public void setSceneEngineIntegration(SceneEngineIntegration integration) {
        this.sceneEngineIntegration = integration;
        log.info("SceneEngine integration set");
    }

    /**
     * 获取已创建的 SceneChat
     * @param sceneId 场景ID
     * @return SceneChat 实例，如果不存在返回 null
     */
    public SceneChat getSceneChat(String sceneId) {
        return sceneChats.get(sceneId);
    }

    /**
     * 关闭并移除 SceneChat
     * @param sceneId 场景ID
     * @return 是否成功移除
     */
    public boolean closeSceneChat(String sceneId) {
        SceneChat sceneChat = sceneChats.remove(sceneId);
        if (sceneChat != null) {
            try {
                sceneChat.close();
                log.info("Closed SceneChat for scene: {}", sceneId);

                // 从 SceneEngine 注销
                if (sceneEngineIntegration != null) {
                    try {
                        sceneEngineIntegration.closeScene(sceneId);
                    } catch (Exception e) {
                        log.warn("Failed to unregister scene from SceneEngine: {}", e.getMessage());
                    }
                }

                return true;
            } catch (Exception e) {
                log.error("Error closing SceneChat for scene: {}", sceneId, e);
            }
        }
        return false;
    }

    /**
     * 关闭所有 SceneChat
     */
    public void closeAll() {
        for (Map.Entry<String, SceneChat> entry : sceneChats.entrySet()) {
            try {
                entry.getValue().close();
                log.debug("Closed SceneChat for scene: {}", entry.getKey());
            } catch (Exception e) {
                log.error("Error closing SceneChat for scene: {}", entry.getKey(), e);
            }
        }
        sceneChats.clear();
        log.info("All SceneChats closed");
    }

    /**
     * 获取活跃的 SceneChat 数量
     */
    public int getActiveSceneCount() {
        return sceneChats.size();
    }

    /**
     * 应用配置到 SceneChat
     */
    private void applyConfig(SceneChatImpl sceneChat, SceneChatConfig config) {
        // 配置应用逻辑（根据实际需求扩展）
        log.debug("Applied config to SceneChat: streaming={}, timeout={}, maxHistory={}, collaboration={}",
            config.isEnableStreaming(),
            config.getDefaultTimeout(),
            config.getMaxHistorySize(),
            config.isEnableCollaboration());
    }
}
