package net.ooder.scene.session.unified;

import java.util.List;
import java.util.Map;

/**
 * 统一会话管理器接口
 *
 * <p>提供统一的会话管理能力，支持多种会话类型：</p>
 * <ul>
 *   <li>USER - 用户会话</li>
 *   <li>AGENT - Agent会话</li>
 *   <li>SCENE - 场景会话</li>
 *   <li>CONVERSATION - 对话会话</li>
 * </ul>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>会话生命周期管理</li>
 *   <li>在线状态管理</li>
 *   <li>会话查询</li>
 *   <li>分布式支持（可选）</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface UnifiedSessionManager {

    UnifiedSession createSession(SessionType type, String ownerId, Map<String, Object> metadata);

    UnifiedSession createSession(SessionType type, String ownerId, String sceneGroupId, Map<String, Object> metadata);

    UnifiedSession getSession(String sessionId);

    List<UnifiedSession> getSessionsByOwner(String ownerId);

    void updateSession(String sessionId, Map<String, Object> updates);

    void invalidateSession(String sessionId);

    boolean isValid(String sessionId);

    void refreshSession(String sessionId);

    void refreshSession(String sessionId, long ttlMs);

    OnlineStatus getOnlineStatus(String ownerId);

    void setOnlineStatus(String ownerId, OnlineStatus status);

    List<UnifiedSession> getActiveSessionsByScene(String sceneGroupId);

    List<UnifiedSession> getActiveSessionsByType(SessionType type);

    void heartbeat(String sessionId);

    void cleanupExpired();

    long getSessionCount();

    long getActiveSessionCount();

    void setDefaultTtl(long ttlMs);

    long getDefaultTtl();
}
