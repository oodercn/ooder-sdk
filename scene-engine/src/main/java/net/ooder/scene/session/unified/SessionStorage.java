package net.ooder.scene.session.unified;

import java.util.List;
import java.util.Optional;

/**
 * 会话存储接口
 *
 * <p>抽象会话存储，支持多种后端实现。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface SessionStorage {
    
    String save(UnifiedSession session);
    
    Optional<UnifiedSession> load(String sessionId);
    
    void update(UnifiedSession session);
    
    void delete(String sessionId);
    
    List<UnifiedSession> findByOwner(String ownerId);
    
    List<UnifiedSession> findBySceneGroup(String sceneGroupId);
    
    List<UnifiedSession> findByType(SessionType type);
    
    List<UnifiedSession> findActiveSessions();
    
    long count();
    
    void cleanupExpired();
    
    void clear();
}
