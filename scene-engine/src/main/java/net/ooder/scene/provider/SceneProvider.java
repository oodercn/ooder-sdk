package net.ooder.scene.provider;

import java.util.List;

/**
 * 场景提供者接口
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public interface SceneProvider {

    /**
     * 列出可用场景
     * @return 场景列表
     */
    List<Object> listAvailableScenes();

    /**
     * 加入场景组
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return 加入结果
     */
    boolean joinSceneGroup(String sceneId, String userId);

    /**
     * 加入场景组（带邀请码）
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @param inviteCode 邀请码
     * @return 加入结果
     */
    boolean joinSceneGroup(String sceneId, String userId, String inviteCode);

    /**
     * 离开场景组
     * @param groupId 组ID
     * @param userId 用户ID
     * @return 离开结果
     */
    boolean leaveSceneGroup(String groupId, String userId);

    /**
     * 获取用户的场景组列表
     * @param userId 用户ID
     * @return 场景组列表
     */
    List<Object> listMySceneGroups(String userId);

    /**
     * 获取场景组信息
     * @param groupId 组ID
     * @return 场景组信息
     */
    Object getSceneGroup(String groupId);

    /**
     * 获取场景状态
     * @param sceneId 场景ID
     * @return 场景状态
     */
    Object getSceneStatus(String sceneId);

    /**
     * 更新场景状态
     * @param sceneId 场景ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateSceneStatus(String sceneId, String status);

    /**
     * 激活场景
     * @param sceneId 场景ID
     * @return 激活结果
     */
    boolean activateScene(String sceneId);

    /**
     * 停用场景
     * @param sceneId 场景ID
     * @return 停用结果
     */
    boolean deactivateScene(String sceneId);

    /**
     * 获取场景
     * @param sceneId 场景ID
     * @return 场景信息
     */
    Object getScene(String sceneId);
}
