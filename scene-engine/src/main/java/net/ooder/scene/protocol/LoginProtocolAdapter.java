package net.ooder.scene.protocol;

import java.util.concurrent.CompletableFuture;

/**
 * 登录协议适配器接口
 *
 * <p>定义登录认证相关的操作接口，由Engine Core实现</p>
 */
public interface LoginProtocolAdapter {

    /**
     * 登录
     *
     * @param request 登录请求
     * @return 登录结果
     */
    CompletableFuture<LoginResult> login(LoginRequest request);

    /**
     * 登出
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    CompletableFuture<Void> logout(String sessionId);

    /**
     * 获取会话
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    CompletableFuture<Session> getSession(String sessionId);

    /**
     * 验证会话
     *
     * @param sessionId 会话ID
     * @return 是否有效
     */
    CompletableFuture<Boolean> validateSession(String sessionId);

    /**
     * 刷新会话
     *
     * @param sessionId 会话ID
     * @return 新会话信息
     */
    CompletableFuture<Session> refreshSession(String sessionId);

    /**
     * 获取当前用户ID
     *
     * @param sessionId 会话ID
     * @return 用户ID
     */
    CompletableFuture<String> getCurrentUserId(String sessionId);
}
