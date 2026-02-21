package net.ooder.server.session;

import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.conf.OrgConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionManagerImpl implements SessionManager {
    
    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), SessionManagerImpl.class);
    
    private final SessionCacheManager cacheManager;
    private final SessionMetrics metrics;
    private final List<SessionLifecycle> lifecycleListeners;
    private final Object lock = new Object();
    
    public SessionManagerImpl(SessionCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.metrics = new SessionMetricsImpl();
        this.lifecycleListeners = new ArrayList<SessionLifecycle>();
    }
    
    @Override
    public JDSSessionHandle createSession(ConnectInfo info) throws JDSException {
        if (info == null) {
            throw new JDSException("ConnectInfo cannot be null");
        }
        
        String sessionId = generateSessionId();
        JDSSessionHandle handle = new JDSSessionHandle(sessionId);
        
        long startTime = System.currentTimeMillis();
        
        String systemCode = null;
        cacheManager.putSession(handle, info, systemCode);
        
        fireSessionCreated(handle, info);
        
        long duration = System.currentTimeMillis() - startTime;
        metrics.recordLogin(info.getUserID(), duration);
        
        log.info("Session created: " + sessionId + " for user: " + info.getUserID());
        
        return handle;
    }
    
    @Override
    public JDSSessionHandle getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        JDSSessionHandle handle = cacheManager.getSessionHandle(sessionId);
        if (handle != null) {
            fireSessionActivated(handle);
        }
        return handle;
    }
    
    @Override
    public void invalidateSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        JDSSessionHandle handle = cacheManager.getSessionHandle(sessionId);
        if (handle != null) {
            fireSessionDestroyed(handle);
        }
        
        cacheManager.invalidateSession(sessionId);
        metrics.recordLogout(sessionId);
        
        log.info("Session invalidated: " + sessionId);
    }
    
    @Override
    public void keepAlive(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        if (cacheManager.containsSession(sessionId)) {
            cacheManager.updateConnectTime(sessionId);
            log.debug("Session keep-alive: " + sessionId);
        }
    }
    
    @Override
    public boolean isSessionValid(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return cacheManager.containsSession(sessionId);
    }
    
    @Override
    public SessionStats getStats() {
        return metrics.getStats();
    }
    
    @Override
    public void registerLifecycleListener(SessionLifecycle listener) {
        if (listener != null) {
            synchronized (lock) {
                lifecycleListeners.add(listener);
            }
        }
    }
    
    @Override
    public void unregisterLifecycleListener(SessionLifecycle listener) {
        if (listener != null) {
            synchronized (lock) {
                lifecycleListeners.remove(listener);
            }
        }
    }
    
    public void cleanupExpiredSessions() {
        cacheManager.cleanupExpiredSessions();
        
        int activeCount = cacheManager.getSessionCount();
        metrics.recordActiveSessions(activeCount);
    }
    
    public SessionCacheManager getCacheManager() {
        return cacheManager;
    }
    
    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private void fireSessionCreated(JDSSessionHandle handle, ConnectInfo info) {
        List<SessionLifecycle> listeners;
        synchronized (lock) {
            listeners = new ArrayList<SessionLifecycle>(lifecycleListeners);
        }
        
        for (SessionLifecycle listener : listeners) {
            try {
                listener.onCreated(handle, info);
            } catch (Exception e) {
                log.warn("SessionLifecycle listener error onCreated: " + e.getMessage());
            }
        }
    }
    
    private void fireSessionActivated(JDSSessionHandle handle) {
        List<SessionLifecycle> listeners;
        synchronized (lock) {
            listeners = new ArrayList<SessionLifecycle>(lifecycleListeners);
        }
        
        for (SessionLifecycle listener : listeners) {
            try {
                listener.onActivated(handle);
            } catch (Exception e) {
                log.warn("SessionLifecycle listener error onActivated: " + e.getMessage());
            }
        }
    }
    
    private void fireSessionDestroyed(JDSSessionHandle handle) {
        List<SessionLifecycle> listeners;
        synchronized (lock) {
            listeners = new ArrayList<SessionLifecycle>(lifecycleListeners);
        }
        
        for (SessionLifecycle listener : listeners) {
            try {
                listener.onDestroyed(handle);
            } catch (Exception e) {
                log.warn("SessionLifecycle listener error onDestroyed: " + e.getMessage());
            }
        }
    }
}
