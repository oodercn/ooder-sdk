package net.ooder.server.session.admin;

import java.util.List;
import java.util.Map;

public interface SessionAdminService {
    
    SessionInfo getSessionInfo(String sessionId);
    
    List<SessionInfo> getAllSessions();
    
    List<SessionInfo> getSessionsByUserId(String userId);
    
    List<SessionInfo> getSessionsBySystemCode(String systemCode);
    
    List<SessionInfo> getSessionsByAccount(String account);
    
    List<SessionInfo> getExpiredSessions();
    
    List<SessionInfo> getSessions(int page, int pageSize);
    
    SessionOperationResult invalidateSession(String sessionId);
    
    SessionOperationResult invalidateSessionsByUserId(String userId);
    
    SessionOperationResult invalidateSessionsByAccount(String account);
    
    SessionOperationResult invalidateAllSessions();
    
    SessionOperationResult invalidateExpiredSessions();
    
    SessionOperationResult keepAlive(String sessionId);
    
    SessionOperationResult cleanupExpiredSessions();
    
    SessionHealthCheck healthCheck();
    
    SessionHealthCheck detailedHealthCheck();
    
    Map<String, Object> getStatistics();
    
    Map<String, Object> getCacheStatistics();
    
    SessionOperationResult setSessionAttribute(String sessionId, String key, Object value);
    
    Object getSessionAttribute(String sessionId, String key);
    
    Map<String, Object> getSessionAttributes(String sessionId);
    
    int getActiveSessionCount();
    
    int getTotalSessionCount();
    
    long getAverageSessionDuration();
    
    boolean isSessionValid(String sessionId);
    
    SessionOperationResult forceLogout(String sessionId, String reason);
    
    SessionOperationResult broadcastMessage(String message);
    
    List<String> getActiveUserIds();
    
    Map<String, Integer> getSessionCountBySystemCode();
    
    Map<String, Integer> getSessionCountByHour();
}
