package net.ooder.server;

import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.scene.SceneConfig;
import net.ooder.config.scene.SceneFactory;
import net.ooder.config.scene.OrgSceneConfig;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.session.*;
import net.ooder.server.session.admin.SessionAdminService;
import net.ooder.server.session.admin.SessionAdminServiceImpl;
import net.ooder.server.session.admin.SessionHealthCheck;
import net.ooder.server.session.admin.SessionInfo;
import net.ooder.server.session.admin.SessionOperationResult;

import java.util.List;
import java.util.Map;

public class JDSServerSupport {
    
    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), JDSServerSupport.class);
    
    private static volatile JDSServerSupport instance;
    private static volatile boolean initialized = false;
    private static volatile Throwable initializationError = null;
    private static final Object LOCK = new Object();
    
    private final SessionCacheManager cacheManager;
    private final SessionManager sessionManager;
    private final SessionAdminService adminService;
    private final SceneConfig sceneConfig;
    private final long expireTime;
    private final long startTime;
    
    private JDSServerSupport() throws JDSException {
        this.startTime = System.currentTimeMillis();
        this.sceneConfig = loadSceneConfig();
        this.expireTime = getExpireTime();
        this.cacheManager = createCacheManager();
        this.sessionManager = createSessionManager();
        this.adminService = createAdminService();
    }
    
    public static JDSServerSupport getInstance() throws JDSException {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    try {
                        instance = new JDSServerSupport();
                        initialized = true;
                    } catch (Throwable e) {
                        initializationError = e;
                        throw new JDSException("JDSServerSupport initialization failed: " + e.getMessage(), e);
                    }
                }
            }
        }
        return instance;
    }
    
    public static boolean isAvailable() {
        return initialized && instance != null;
    }
    
    public static JDSServerSupport getInstanceOrNull() {
        try {
            if (isAvailable()) {
                return instance;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Throwable getInitializationError() {
        return initializationError;
    }
    
    public static void reset() {
        synchronized (LOCK) {
            if (instance != null) {
                instance.cacheManager.clear();
                instance = null;
                initialized = false;
                initializationError = null;
            }
        }
    }
    
    private SceneConfig loadSceneConfig() {
        try {
            SceneFactory factory = SceneFactory.create();
            return factory.getSceneConfig();
        } catch (Exception e) {
            log.warn("Failed to load scene config: " + e.getMessage());
            return null;
        }
    }
    
    private SessionCacheManager createCacheManager() {
        long expireTime = getExpireTime();
        return new SessionCacheManagerImpl(expireTime);
    }
    
    private SessionManager createSessionManager() {
        return new SessionManagerImpl(cacheManager);
    }
    
    private SessionAdminService createAdminService() {
        return new SessionAdminServiceImpl(cacheManager, sessionManager, expireTime);
    }
    
    private long getExpireTime() {
        if (sceneConfig != null && sceneConfig.getOrg() != null) {
            OrgSceneConfig orgConfig = sceneConfig.getOrg();
            if (orgConfig.getCacheExpireTime() > 0) {
                return orgConfig.getCacheExpireTime();
            }
        }
        return 30 * 60 * 1000L;
    }
    
    public SessionManager getSessionManager() {
        return sessionManager;
    }
    
    public SessionCacheManager getCacheManager() {
        return cacheManager;
    }
    
    public SessionAdminService getAdminService() {
        return adminService;
    }
    
    public SceneConfig getSceneConfig() {
        return sceneConfig;
    }
    
    public SessionStats getSessionStats() {
        return sessionManager.getStats();
    }
    
    public CacheStats getCacheStats() {
        return cacheManager.getStats();
    }
    
    public void cleanupExpiredSessions() {
        if (sessionManager instanceof SessionManagerImpl) {
            ((SessionManagerImpl) sessionManager).cleanupExpiredSessions();
        }
    }
    
    public SessionInfo getSessionInfo(String sessionId) {
        return adminService.getSessionInfo(sessionId);
    }
    
    public List<SessionInfo> getAllSessions() {
        return adminService.getAllSessions();
    }
    
    public SessionOperationResult invalidateSession(String sessionId) {
        return adminService.invalidateSession(sessionId);
    }
    
    public SessionOperationResult invalidateSessionsByUserId(String userId) {
        return adminService.invalidateSessionsByUserId(userId);
    }
    
    public SessionOperationResult invalidateAllSessions() {
        return adminService.invalidateAllSessions();
    }
    
    public SessionHealthCheck healthCheck() {
        return adminService.healthCheck();
    }
    
    public SessionHealthCheck detailedHealthCheck() {
        return adminService.detailedHealthCheck();
    }
    
    public Map<String, Object> getStatistics() {
        return adminService.getStatistics();
    }
    
    public int getActiveSessionCount() {
        return adminService.getActiveSessionCount();
    }
    
    public long getUptime() {
        return System.currentTimeMillis() - startTime;
    }
}
