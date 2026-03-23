package net.ooder.scene.session;

import java.util.List;

/**
 * Session 管理器接口
 *
 * <p>管理用户会话的生命周期，包括创建、验证、刷新和销毁。</p>
 *
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>会话创建 - 为新登录用户创建会话</li>
 *   <li>会话验证 - 验证会话有效性</li>
 *   <li>会话刷新 - 延长会话有效期</li>
 *   <li>会话销毁 - 用户登出或超时时销毁会话</li>
 *   <li>活跃管理 - 跟踪用户活跃会话</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 创建会话
 * SessionInfo session = sessionManager.createSession(
 *     "user123", "张三", "192.168.1.1", "Chrome/120.0");
 *
 * // 验证会话
 * if (sessionManager.validateSession(session.getSessionId())) {
 *     // 会话有效
 * }
 *
 * // 刷新会话
 * SessionInfo refreshed = sessionManager.refreshSession(session.getSessionId());
 *
 * // 销毁会话
 * sessionManager.destroySession(session.getSessionId());
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see SessionInfo
 * @see AuthManager
 */
public interface SessionManager {

    /**
     * 创建 Session
     *
     * <p>为新登录用户创建会话，生成唯一的会话ID。</p>
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return Session 信息
     */
    SessionInfo createSession(String userId, String username, String clientIp, String userAgent);

    /**
     * 获取 Session
     *
     * <p>根据会话ID获取会话信息。</p>
     *
     * @param sessionId 会话ID
     * @return Session 信息，不存在返回 null
     */
    SessionInfo getSession(String sessionId);

    /**
     * 验证 Session
     *
     * <p>检查会话是否存在且未过期。</p>
     *
     * @param sessionId 会话ID
     * @return true 有效，false 无效或已过期
     */
    boolean validateSession(String sessionId);

    /**
     * 刷新 Session
     *
     * <p>延长会话有效期，更新最后访问时间。</p>
     *
     * @param sessionId 会话ID
     * @return 新的 Session 信息
     */
    SessionInfo refreshSession(String sessionId);

    /**
     * 销毁 Session
     *
     * <p>用户登出或主动销毁会话。</p>
     *
     * @param sessionId 会话ID
     */
    void destroySession(String sessionId);

    /**
     * 更新 Session 活跃时间
     *
     * <p>更新会话的最后访问时间，防止过期。</p>
     *
     * @param sessionId 会话ID
     */
    void touchSession(String sessionId);

    /**
     * 获取用户的所有活跃 Session
     *
     * <p>获取指定用户的所有未过期会话。</p>
     *
     * @param userId 用户ID
     * @return Session 列表
     */
    List<SessionInfo> getActiveSessions(String userId);

    /**
     * 销毁用户的所有 Session
     *
     * <p>用户密码修改或账号禁用时调用。</p>
     *
     * @param userId 用户ID
     */
    void destroyUserSessions(String userId);
}
