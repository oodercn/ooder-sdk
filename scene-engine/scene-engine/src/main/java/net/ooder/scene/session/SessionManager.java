package net.ooder.scene.session;

/**
 * Session管理器接口
 */
public interface SessionManager {

    /**
     * 创建Session
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return Session信息
     */
    SessionInfo createSession(String userId, String username, String clientIp, String userAgent);

    /**
     * 获取Session
     * 
     * @param sessionId 会话ID
     * @return Session信息
     */
    SessionInfo getSession(String sessionId);

    /**
     * 验证Session
     * 
     * @param sessionId 会话ID
     * @return true 有效，false 无效
     */
    boolean validateSession(String sessionId);

    /**
     * 刷新Session
     * 
     * @param sessionId 会话ID
     * @return 新的Session信息
     */
    SessionInfo refreshSession(String sessionId);

    /**
     * 销毁Session
     * 
     * @param sessionId 会话ID
     */
    void destroySession(String sessionId);

    /**
     * 更新Session活跃时间
     * 
     * @param sessionId 会话ID
     */
    void touchSession(String sessionId);

    /**
     * 获取用户的所有活跃Session
     * 
     * @param userId 用户ID
     * @return Session列表
     */
    java.util.List<SessionInfo> getActiveSessions(String userId);

    /**
     * 销毁用户的所有Session
     * 
     * @param userId 用户ID
     */
    void destroyUserSessions(String userId);
}
