package net.ooder.skills.container.api.config;

import java.util.Map;

/**
 * Skill 配置管理器
 *
 * 支持运行时配置获取和更新
 */
public interface SkillConfigManager {

    /**
     * 获取 Skill 配置
     *
     * @param skillId Skill ID
     * @return 配置 Map
     */
    Map<String, Object> getConfig(String skillId);

    /**
     * 获取指定配置项
     *
     * @param skillId Skill ID
     * @param key 配置键
     * @return 配置值
     */
    Object getConfigValue(String skillId, String key);

    /**
     * 获取指定配置项（带默认值）
     *
     * @param skillId Skill ID
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    <T> T getConfigValue(String skillId, String key, T defaultValue);

    /**
     * 更新配置（热更新）
     *
     * @param skillId Skill ID
     * @param config 新配置
     */
    void updateConfig(String skillId, Map<String, Object> config);

    /**
     * 更新单个配置项
     *
     * @param skillId Skill ID
     * @param key 配置键
     * @param value 配置值
     */
    void updateConfigValue(String skillId, String key, Object value);

    /**
     * 添加配置变更监听器
     *
     * @param skillId Skill ID
     * @param listener 监听器
     */
    void addConfigChangeListener(String skillId, ConfigChangeListener listener);

    /**
     * 移除配置变更监听器
     *
     * @param skillId Skill ID
     * @param listener 监听器
     */
    void removeConfigChangeListener(String skillId, ConfigChangeListener listener);

    /**
     * 重新加载配置
     *
     * @param skillId Skill ID
     */
    void reloadConfig(String skillId);

    /**
     * 配置变更监听器
     */
    @FunctionalInterface
    interface ConfigChangeListener {
        void onConfigChange(String skillId, String key, Object oldValue, Object newValue);
    }
}
