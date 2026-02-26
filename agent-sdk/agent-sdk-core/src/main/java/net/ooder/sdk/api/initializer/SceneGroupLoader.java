package net.ooder.sdk.api.initializer;

import java.util.List;

/**
 * 场景组加载器接口
 * 用于从配置文件或数据库加载场景组配置，避免硬编码
 */
public interface SceneGroupLoader {

    /**
     * 加载所有可用的场景组
     */
    List<NexusInitializer.SceneGroupInfo> loadSceneGroups();

    /**
     * 根据 ID 加载场景组
     */
    NexusInitializer.SceneGroupInfo loadSceneGroup(String sceneGroupId);

    /**
     * 重新加载场景组配置
     */
    void reload();

    /**
     * 注册场景组
     */
    void registerSceneGroup(NexusInitializer.SceneGroupInfo groupInfo);

    /**
     * 注销场景组
     */
    void unregisterSceneGroup(String sceneGroupId);
}
