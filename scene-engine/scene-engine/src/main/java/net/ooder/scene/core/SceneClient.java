package net.ooder.scene.core;

import net.ooder.sdk.api.capability.Capability;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SceneClient 场景客户端接口
 * 
 * <p>提供用户与场景服务交互的统一入口，包括技能管理、场景组操作、能力调用等功能。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface SceneClient {

    String getSessionId();

    String getUserId();

    String getUsername();

    String getToken();

    SkillInfo findSkill(String skillId);

    List<SkillInfo> searchSkills(SkillQuery query);

    List<InstalledSkillInfo> listMySkills();

    SkillInstallResult installSkill(String skillId);

    SkillInstallResult installSkill(String skillId, Map<String, Object> config);

    SkillUninstallResult uninstallSkill(String skillId);

    SkillInstallProgress getInstallProgress(String installId);

    List<SceneInfo> listAvailableScenes();

    SceneGroupInfo joinSceneGroup(String sceneId);

    SceneGroupInfo joinSceneGroup(String sceneId, String inviteCode);

    void leaveSceneGroup(String groupId);

    List<SceneGroupInfo> listMySceneGroups();

    SceneGroupInfo getSceneGroup(String groupId);

    Object invokeCapability(String skillId, String capability, Map<String, Object> params);

    List<Capability> listCapabilities(String skillId);

    UserSettings getSettings();

    void updateSettings(UserSettings settings);

    IdentityInfo getIdentity();

    /**
     * 启动场景组心跳
     * @param groupId 场景组ID
     * @return 心跳结果
     */
    CompletableFuture<HeartbeatResult> startHeartbeat(String groupId);
    
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
    HeartbeatStatus getHeartbeatStatus(String groupId);

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
    SceneInfo getScene(String sceneId);
}
