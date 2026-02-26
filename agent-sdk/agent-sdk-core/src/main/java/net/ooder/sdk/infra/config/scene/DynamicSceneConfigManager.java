package net.ooder.sdk.infra.config.scene;

import java.nio.file.Path;
import java.util.List;

/**
 * 动态场景配置管理器接口
 */
public interface DynamicSceneConfigManager {

    /**
     * 启用文件监听
     */
    void enableFileWatching(Path configDir);

    /**
     * 禁用文件监听
     */
    void disableFileWatching();

    /**
     * 加载场景配置
     */
    void loadSceneConfig(String sceneId);

    /**
     * 重新加载所有配置
     */
    void reloadAllConfigs();

    /**
     * 添加 Skill 到场景
     */
    void addSkillToScene(String sceneId, String skillId);

    /**
     * 从场景移除 Skill
     */
    void removeSkillFromScene(String sceneId, String skillId);

    /**
     * 获取场景中的 Skills
     */
    List<String> getSceneSkills(String sceneId);

    /**
     * 更新场景配置
     */
    void updateSceneConfig(String sceneId, SceneConfiguration config);

    /**
     * 获取场景配置
     */
    SceneConfiguration getSceneConfig(String sceneId);

    /**
     * 添加配置变更监听器
     */
    void addConfigListener(ConfigChangeListener listener);

    /**
     * 移除配置变更监听器
     */
    void removeConfigListener(ConfigChangeListener listener);

    /**
     * 配置变更监听器接口
     */
    interface ConfigChangeListener {
        void onConfigChanged(String sceneId, SceneConfiguration config);
        void onSkillAdded(String sceneId, String skillId);
        void onSkillRemoved(String sceneId, String skillId);
    }
}
