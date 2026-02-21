package net.ooder.server.session;

import net.ooder.common.cache.Cache;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

import java.util.HashSet;
import java.util.Set;

public class TestSessionCacheManager implements SessionCacheManager {
    
    private final Cache<String, ConnectInfo> connectInfoCache = new MockCache<String, ConnectInfo>();
    private final Cache<String, JDSSessionHandle> sessionHandleCache = new MockCache<String, JDSSessionHandle>();
    private final Cache<String, String> systemCodeCache = new MockCache<String, String>();
    private final Cache<String, Long> connectTimeCache = new MockCache<String, Long>();
    
    private final long expireTime;
    private final Object lock = new Object();
    
    public TestSessionCacheManager(long expireTime) {
        this.expireTime = expireTime;
    }
    
    @Override
    public void putSession(JDSSessionHandle handle, ConnectInfo info, String systemCode) {
        if (handle == null || info == null) {
            return;
        }
        
        String sessionId = handle.getSessionID();
        synchronized (lock) {
            connectInfoCache.put(sessionId, info);
            sessionHandleCache.put(sessionId, handle);
            if (systemCode != null) {
                systemCodeCache.put(sessionId, systemCode);
            }
            connectTimeCache.put(sessionId, System.currentTimeMillis());
        }
    }
    
    @Override
    public ConnectInfo getConnectInfo(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return connectInfoCache.get(sessionId);
    }
    
    @Override
    public JDSSessionHandle getSessionHandle(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionHandleCache.get(sessionId);
    }
    
    @Override
    public String getSystemCode(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return systemCodeCache.get(sessionId);
    }
    
    @Override
    public Long getConnectTime(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return connectTimeCache.get(sessionId);
    }
    
    @Override
    public void updateConnectTime(String sessionId) {
        if (sessionId == null) {
            return;
        }
        synchronized (lock) {
            if (connectTimeCache.get(sessionId) != null) {
                connectTimeCache.put(sessionId, System.currentTimeMillis());
            }
        }
    }
    
    @Override
    public void invalidateSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        synchronized (lock) {
            connectInfoCache.remove(sessionId);
            sessionHandleCache.remove(sessionId);
            systemCodeCache.remove(sessionId);
            connectTimeCache.remove(sessionId);
        }
    }
    
    @Override
    public void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        Set<String> sessionIds;
        
        synchronized (lock) {
            sessionIds = new HashSet<String>(connectTimeCache.keySet());
        }
        
        for (String sessionId : sessionIds) {
            Long loginTime = connectTimeCache.get(sessionId);
            if (loginTime != null && (currentTime - loginTime) > expireTime) {
                invalidateSession(sessionId);
            }
        }
    }
    
    @Override
    public boolean containsSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return sessionHandleCache.get(sessionId) != null;
    }
    
    @Override
    public int getSessionCount() {
        return sessionHandleCache.size();
    }
    
    @Override
    public CacheStats getStats() {
        return new CacheStats(
            sessionHandleCache.size(),
            connectInfoCache.size(),
            systemCodeCache.size(),
            connectTimeCache.size()
        );
    }
    
    @Override
    public void clear() {
        synchronized (lock) {
            connectInfoCache.clear();
            sessionHandleCache.clear();
            systemCodeCache.clear();
            connectTimeCache.clear();
        }
    }
    
    @Override
    public Cache<String, ConnectInfo> getConnectInfoCache() {
        return connectInfoCache;
    }
    
    @Override
    public Cache<String, JDSSessionHandle> getSessionHandleCache() {
        return sessionHandleCache;
    }
    
    @Override
    public Cache<String, String> getSystemCodeCache() {
        return systemCodeCache;
    }
    
    @Override
    public Cache<String, Long> getConnectTimeCache() {
        return connectTimeCache;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
}
