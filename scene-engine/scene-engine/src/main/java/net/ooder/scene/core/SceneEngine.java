package net.ooder.scene.core;

import net.ooder.scene.session.SessionInfo;

/**
 * SceneEngine 核心入口接口
 * 
 * <p>SceneEngine 是场景引擎的核心入口，封装agentSDK 提供统一的安全、审计、状态管理，
 * 上层工程（ooder-Nexus、ooder-Nexus-Enterprise、agent-skillcenter）通过此接口访问所有功能。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>统一连接状态管理- Session、ConnectInfo 管理</li>
 *   <li>统一安全 - Token 令牌控制</li>
 *   <li>统一日志审计 - 所有操作记录审计日志</li>
 *   <li>用户故事推进 - 状态管理</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface SceneEngine {

    /**
     * 用户登录（用户名密码方式）
     * 
     * @param username 用户名
     * @param password 密码
     * @return SceneClient 用户客户端实现
     * @throws AuthenticationException 认证失败
     */
    SceneClient login(String username, String password);

    /**
     * 用户登录（Token方式）
     * 
     * @param token 访问令牌
     * @return SceneClient 用户客户端实现
     * @throws AuthenticationException 认证失败
     */
    SceneClient login(String token);

    /**
     * 管理员登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return AdminClient 管理客户端实现
     * @throws AuthenticationException 认证失败
     * @throws AuthorizationException 无管理员权限
     */
    AdminClient adminLogin(String username, String password);

    /**
     * 登出
     * 
     * @param sessionId 会话ID
     */
    void logout(String sessionId);

    /**
     * 获取会话信息
     * 
     * @param sessionId 会话ID
     * @return SessionInfo 会话信息
     */
    SessionInfo getSession(String sessionId);

    /**
     * 验证会话有效性
     * 
     * @param sessionId 会话ID
     * @return true 有效，false 无效
     */
    boolean validateSession(String sessionId);

    /**
     * 刷新会话
     * 
     * @param sessionId 会话ID
     * @return 新的会话信息
     */
    SessionInfo refreshSession(String sessionId);

    /**
     * 获取引擎状态
     * 
     * @return EngineStatus 引擎状态
     */
    EngineStatus getStatus();

    /**
     * 启动引擎
     */
    void start();

    /**
     * 停止引擎
     */
    void stop();

    /**
     * 获取引擎名称
     * 
     * @return 引擎名称
     */
    String getName();

    /**
     * 获取引擎版本
     * 
     * @return 引擎版本
     */
    String getVersion();
}
