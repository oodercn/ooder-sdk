package net.ooder.scene.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SceneClient 场景客户端接口
 *
 * <p>提供用户与场景服务交互的统一入口，包括技能管理、场景组操作、能力调用等功能。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public interface SceneClient {

    String getSessionId();

    String getUserId();

    String getUsername();

    String getToken();

    Object findSkill(String skillId);

    List<Object> searchSkills(SkillQuery query);

    List<Object> listMySkills();

    Object installSkill(String skillId);

    Object installSkill(String skillId, Map<String, Object> config);

    Object uninstallSkill(String skillId);

    Object getInstallProgress(String installId);

    List<Object> listAvailableScenes();

    Object joinSceneGroup(String sceneId);

    Object joinSceneGroup(String sceneId, String inviteCode);

    void leaveSceneGroup(String groupId);

    List<Object> listMySceneGroups();

    Object getSceneGroup(String groupId);

    Object invokeCapability(String skillId, String capability, Map<String, Object> params);

    List<Object> listCapabilities(String skillId);

    Object getSettings();

    void updateSettings(Object settings);

    Object getIdentity();

    /**
     * 启动场景组心跳
     * @param groupId 场景组ID
     * @return 心跳结果
     */
    CompletableFuture<Object> startHeartbeat(String groupId);

    /**
     * 停止场景组心跳
     * @param groupId 场景组ID
     */
    void stopHeartbeat(String groupId);

    /**
     * 获取场景组心跳状态
     * @param groupId 场景组ID
     * @return 心跳状态
     */
    Object getHeartbeatStatus(String groupId);

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
     * 获取场景状态
     * @param sceneId 场景ID
     * @return 场景状态
     */
    String getSceneState(String sceneId);

    /**
     * 获取场景详情
     * @param sceneId 场景ID
     * @return 场景信息
     */
    Object getScene(String sceneId);
}
