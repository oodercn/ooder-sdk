package net.ooder.scene.core;

import java.util.List;
import net.ooder.scene.provider.SecurityStatus;
import net.ooder.scene.provider.SecurityPolicy;

/**
 * AdminClient 管理客户端接口
 * 
 * <p>提供系统管理员与场景服务交互的统一入口，包括用户管理、技能管理、场景组管理、审计日志等功能。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface AdminClient {

    String getSessionId();

    String getUserId();

    String getUsername();

    String getToken();

    PageResult<UserInfo> listUsers(PageRequest request);

    UserInfo getUser(String userId);

    UserInfo createUser(UserInfo user);

    UserInfo updateUser(String userId, UserInfo user);

    void deleteUser(String userId);

    void enableUser(String userId);

    void disableUser(String userId, String reason);

    PageResult<SkillInfo> listAllSkills(PageRequest request);

    PageResult<SkillInfo> listPendingSkills(PageRequest request);

    void approveSkill(String skillId);

    void rejectSkill(String skillId, String reason);

    void deleteSkill(String skillId);

    void updateSkillStatus(String skillId, String status);

    PageResult<SceneGroupInfo> listAllSceneGroups(PageRequest request);

    SceneGroupInfo createSceneGroup(SceneGroupInfo group);

    SceneGroupInfo updateSceneGroup(String groupId, SceneGroupInfo group);

    void deleteSceneGroup(String groupId);

    List<SceneMemberInfo> listSceneGroupMembers(String groupId);

    void removeSceneGroupMember(String groupId, String memberId);

    PageResult<AuditLog> listAuditLogs(PageRequest request);

    PageResult<AuditLog> listAuditLogs(PageRequest request, AuditLogFilter filter);

    byte[] exportAuditLogs(AuditLogFilter filter);

    SystemConfig getSystemConfig();

    void updateSystemConfig(SystemConfig config);

    SystemStats getSystemStats();

    SecurityStatus getSecurityStatus();

    PageResult<SecurityPolicy> listSecurityPolicies(PageRequest request);

    SecurityPolicy createSecurityPolicy(SecurityPolicy policy);

    SecurityPolicy updateSecurityPolicy(String policyId, SecurityPolicy policy);

    void deleteSecurityPolicy(String policyId);

    SceneInfo createScene(SceneInfo scene);

    SceneInfo updateScene(String sceneId, SceneInfo scene);

    void deleteScene(String sceneId);

    PageResult<SceneInfo> listAllScenes(PageRequest request);
}
