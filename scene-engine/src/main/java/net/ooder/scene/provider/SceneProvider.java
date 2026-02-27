package net.ooder.scene.provider;

import net.ooder.scene.core.SceneGroupInfo;
import net.ooder.scene.core.SceneInfo;

import java.util.List;

/**
 * 场景提供者接口
 * 管理场景和场景组
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneProvider extends BaseProvider {

    /**
     * 获取所有可用场景
     * @return 场景列表
     */
    List<SceneInfo> listAvailableScenes();

    /**
     * 获取场景信息
     * @param sceneId 场景ID
     * @return 场景信息
     */
    SceneInfo getScene(String sceneId);

    /**
     * 获取场景状态
     * @param sceneId 场景ID
     * @return 场景状态
     */
    String getSceneState(String sceneId);

    /**
     * 激活场景
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean activateScene(String sceneId);

    /**
     * 停用场景
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean deactivateScene(String sceneId);

    /**
     * 加入场景组
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @return 场景组信息
     */
    SceneGroupInfo joinSceneGroup(String userId, String sceneId);

    /**
     * 通过邀请码加入场景组
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @param inviteCode 邀请码
     * @return 场景组信息
     */
    SceneGroupInfo joinSceneGroup(String userId, String sceneId, String inviteCode);

    /**
     * 离开场景组
     * @param userId 用户ID
     * @param groupId 场景组ID
     */
    void leaveSceneGroup(String userId, String groupId);

    /**
     * 获取用户的场景组列表
     * @param userId 用户ID
     * @return 场景组列表
     */
    List<SceneGroupInfo> listMySceneGroups(String userId);

    /**
     * 获取场景组信息
     * @param groupId 场景组ID
     * @return 场景组信息
     */
    SceneGroupInfo getSceneGroup(String groupId);
}
