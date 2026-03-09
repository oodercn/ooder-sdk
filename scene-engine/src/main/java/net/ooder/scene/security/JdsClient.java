package net.ooder.scene.security;

import net.ooder.scene.session.SessionInfo;

/**
 * JDS客户端基础类
 *
 * <p>所有场景引擎客户端的基类，包含会话信息和基础安全校验。</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>强制登录：必须通过有效会话创建</li>
 *   <li>状态保持：维护用户会话状态</li>
 *   <li>安全基础：提供基础安全校验方法</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public abstract class JdsClient {

    protected final SessionInfo sessionInfo;
    protected final String sessionId;
    protected final String userId;

    /**
     * 构造JDS客户端
     *
     * <p>必须通过有效会话创建，强制登录验证。</p>
     *
     * @param sessionInfo 会话信息（必须通过登录获取）
     * @throws IllegalArgumentException 如果会话信息无效
     */
    protected JdsClient(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            throw new IllegalArgumentException("会话信息不能为空，请先登录");
        }
        if (sessionInfo.getSessionId() == null || sessionInfo.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("会话无效或已过期，请重新登录");
        }
        this.sessionInfo = sessionInfo;
        this.sessionId = sessionInfo.getSessionId();
        this.userId = sessionInfo.getUserId();
    }

    /**
     * 获取会话ID
     *
     * @return 会话ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取会话信息
     *
     * @return 会话信息
     */
    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * 验证会话是否有效
     *
     * @return true 有效，false 无效
     */
    public boolean isSessionValid() {
        return sessionInfo != null && sessionInfo.getSessionId() != null && !sessionInfo.getSessionId().isEmpty();
    }

    /**
     * 检查权限（子类可重写）
     *
     * @param permission 权限标识
     * @return true 有权限，false 无权限
     */
    protected boolean hasPermission(String permission) {
        // 基础实现，子类可重写
        return isSessionValid();
    }
}
