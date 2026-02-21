package net.ooder.server.session;

import net.ooder.common.cache.Cache;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

public interface SessionCacheManager {
    
    void putSession(JDSSessionHandle handle, ConnectInfo info, String systemCode);
    
    ConnectInfo getConnectInfo(String sessionId);
    
    JDSSessionHandle getSessionHandle(String sessionId);
    
    String getSystemCode(String sessionId);
    
    Long getConnectTime(String sessionId);
    
    void updateConnectTime(String sessionId);
    
    void invalidateSession(String sessionId);
    
    void cleanupExpiredSessions();
    
    boolean containsSession(String sessionId);
    
    int getSessionCount();
    
    CacheStats getStats();
    
    void clear();
    
    Cache<String, ConnectInfo> getConnectInfoCache();
    
    Cache<String, JDSSessionHandle> getSessionHandleCache();
    
    Cache<String, String> getSystemCodeCache();
    
    Cache<String, Long> getConnectTimeCache();
}
