package net.ooder.skills.config;

/**
 * SDK 配置存储接口
 *
 * <p>定义配置存储的基本操作，支持系统配置、Profile、Skill配置、场景配置等多层级配置</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface SdkConfigStorage {

    /**
     * 加载系统配置
     *
     * @return 系统配置节点
     */
    ConfigNode loadSystemConfig();

    /**
     * 加载 Profile 配置
     *
     * @param profileName Profile 名称
     * @return Profile 配置节点
     */
    ConfigNode loadProfile(String profileName);

    /**
     * 加载 Skill 配置
     *
     * @param skillId Skill ID
     * @return Skill 配置节点
     */
    ConfigNode loadSkillConfig(String skillId);

    /**
     * 加载场景配置
     *
     * @param sceneId 场景 ID
     * @return 场景配置节点
     */
    ConfigNode loadSceneConfig(String sceneId);

    /**
     * 加载内部 Skill 配置
     *
     * @param sceneId 场景 ID
     * @param skillId Skill ID
     * @return 内部 Skill 配置节点
     */
    ConfigNode loadInternalSkillConfig(String sceneId, String skillId);

    /**
     * 保存系统配置
     *
     * @param config 配置节点
     */
    void saveSystemConfig(ConfigNode config);

    /**
     * 保存 Skill 配置
     *
     * @param skillId Skill ID
     * @param config  配置节点
     */
    void saveSkillConfig(String skillId, ConfigNode config);

    /**
     * 保存场景配置
     *
     * @param sceneId 场景 ID
     * @param config  配置节点
     */
    void saveSceneConfig(String sceneId, ConfigNode config);

    /**
     * 保存内部 Skill 配置
     *
     * @param sceneId 场景 ID
     * @param skillId Skill ID
     * @param config  配置节点
     */
    void saveInternalSkillConfig(String sceneId, String skillId, ConfigNode config);

    /**
     * 删除配置
     *
     * @param targetType 目标类型 (system, skill, scene, internal_skill)
     * @param targetId   目标 ID
     */
    void deleteConfig(String targetType, String targetId);

    /**
     * 检查配置是否存在
     *
     * @param targetType 目标类型
     * @param targetId   目标 ID
     * @return 是否存在
     */
    boolean exists(String targetType, String targetId);
}
