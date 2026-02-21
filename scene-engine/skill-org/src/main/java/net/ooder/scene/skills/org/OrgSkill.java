package net.ooder.scene.skills.org;

import net.ooder.scene.core.PageRequest;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * OrgSkill 组织技能接口
 * 
 * <p>提供用户认证、组织管理、角色管理等能力，封装 ooder-org-web 的功能。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface OrgSkill {

    String getSkillId();

    String getSkillName();

    String getSkillVersion();

    List<String> getCapabilities();

    // ==================== 用户认证 ====================

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @param clientIp 客户端IP
     * @return 用户信息（含Token）
     */
    UserInfo login(String username, String password, String clientIp);

    /**
     * 用户登出
     * 
     * @param token 令牌
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 验证令牌
     * 
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 刷新令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的用户信息
     */
    UserInfo refreshToken(String refreshToken);

    // ==================== 用户管理 ====================

    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getUser(String userId);

    /**
     * 根据账号获取用户
     * 
     * @param account 账号
     * @return 用户信息
     */
    UserInfo getUserByAccount(String account);

    /**
     * 注册用户
     * 
     * @param userInfo 用户信息
     * @return 创建的用户信息
     */
    UserInfo registerUser(UserInfo userInfo);

    /**
     * 更新用户
     * 
     * @param userId 用户ID
     * @param userInfo 用户信息
     * @return 更新后的用户信息
     */
    UserInfo updateUser(String userId, UserInfo userInfo);

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(String userId);

    /**
     * 获取用户列表
     * 
     * @param request 分页请求
     * @return 用户列表
     */
    PageResult<UserInfo> listUsers(PageRequest request);

    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(String userId);

    // ==================== 组织管理 ====================

    /**
     * 获取组织树
     * 
     * @return 组织树
     */
    List<OrgInfo> getOrgTree();

    /**
     * 获取组织信息
     * 
     * @param orgId 组织ID
     * @return 组织信息
     */
    OrgInfo getOrg(String orgId);

    /**
     * 获取组织下的用户
     * 
     * @param orgId 组织ID
     * @return 用户列表
     */
    List<UserInfo> getOrgUsers(String orgId);

    // ==================== 组织同步 (SE-002 扩展) ====================

    /**
     * 同步组织架构
     * 
     * @param orgData 组织数据
     * @return 同步结果
     */
    Map<String, Object> syncOrganization(Map<String, Object> orgData);

    /**
     * 创建部门
     * 
     * @param params 部门参数
     * @return 创建的部门信息
     */
    OrgInfo createDepartment(Map<String, Object> params);

    /**
     * 更新部门
     * 
     * @param orgId 部门ID
     * @param params 更新参数
     * @return 更新后的部门信息
     */
    OrgInfo updateDepartment(String orgId, Map<String, Object> params);

    /**
     * 删除部门
     * 
     * @param orgId 部门ID
     * @return 是否成功
     */
    boolean deleteDepartment(String orgId);

    // ==================== 用户创建 (SE-002 扩展) ====================

    /**
     * 创建用户
     * 
     * @param params 用户参数
     * @return 创建的用户信息
     */
    Map<String, Object> createUser(Map<String, Object> params);

    /**
     * 批量创建用户
     * 
     * @param users 用户列表
     * @return 创建结果
     */
    Map<String, Object> batchCreateUsers(List<Map<String, Object>> users);

    /**
     * 同步用户
     * 
     * @param userData 用户数据
     * @return 同步结果
     */
    Map<String, Object> syncUsers(Map<String, Object> userData);

    // ==================== 能力调用 ====================

    /**
     * 调用能力
     * 
     * @param capability 能力名称
     * @param params 参数
     * @return 调用结果
     */
    Object invoke(String capability, Map<String, Object> params);
}
