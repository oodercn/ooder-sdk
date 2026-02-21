package net.ooder.server.session;

public interface SessionMetrics {
    
    void recordLogin(String userId, long duration);
    
    void recordLogout(String sessionId);
    
    void recordSessionExpired(String sessionId);
    
    void recordActiveSessions(int count);
    
    SessionStats getStats();
    
    void reset();
}
