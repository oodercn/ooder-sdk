package net.ooder.scene.llm.session;

import java.util.List;

/**
 * LLM会话消息存储接口
 * 
 * <p>只负责消息的持久化存储，会话管理由 UnifiedSessionManager 负责。</p>
 *
 * @author ooder Team
 * @since 3.0.1
 */
public interface LlmSessionMessageRepository {
    
    LlmSessionMessage saveMessage(LlmSessionMessage message);
    
    List<LlmSessionMessage> findMessagesBySessionId(String sessionId);
    
    List<LlmSessionMessage> findMessagesBySessionId(String sessionId, int limit);
    
    List<LlmSessionMessage> findMessagesBefore(String sessionId, long timestamp, int limit);
    
    boolean deleteMessagesBySessionId(String sessionId);
    
    int countMessagesBySessionId(String sessionId);
    
    void initialize();
    
    void close();
}
