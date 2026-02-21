package net.ooder.server.session.admin;

import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.server.session.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TestSessionAdminService implements SessionAdminService {
    
    private final SessionCacheManager cacheManager;
    private final SessionManager sessionManager;
    private final SessionMetrics metrics;
    private final Map<String, Map<String, Object>> sessionAttributes;
    private final long expireTime;
    private final long startTime;
    
    public TestSessionAdminService(SessionCacheManager cacheManager, SessionManager sessionManager, long expireTime) {
        this.cacheManager = cacheManager;
        this.sessionManager = sessionManager;
        this.metrics = new SessionMetricsImpl();
        this.sessionAttributes = new ConcurrentHashMap<String, Map<String, Object>>();
        this.expireTime = expireTime;
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
    public SessionInfo getSessionInfo(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        JDSSessionHandle handle = cacheManager.getSessionHandle(sessionId);
        ConnectInfo connectInfo = cacheManager.getConnectInfo(sessionId);
        
        if (handle == null || connectInfo == null) {
            return null;
        }
        
        return buildSessionInfo(sessionId, handle, connectInfo);
    }
    
    @Override
    public List<SessionInfo> getAllSessions() {
        List<SessionInfo> result = new ArrayList<SessionInfo>();
        
        Set<String> sessionIds = new HashSet<String>();
        sessionIds.addAll(cacheManager.getSessionHandleCache().keySet());
        
        for (String sessionId : sessionIds) {
            SessionInfo info = getSessionInfo(sessionId);
            if (info != null) {
                result.add(info);
            }
        }
        
        return result;
    }
    
    @Override
    public List<SessionInfo> getSessionsByUserId(String userId) {
        List<SessionInfo> result = new ArrayList<SessionInfo>();
        
        if (userId == null) {
            return result;
        }
        
        for (SessionInfo info : getAllSessions()) {
            if (userId.equals(info.getUserId())) {
                result.add(info);
            }
        }
        
        return result;
    }
    
    @Override
    public List<SessionInfo> getSessionsBySystemCode(String systemCode) {
        List<SessionInfo> result = new ArrayList<SessionInfo>();
        
        if (systemCode == null) {
            return result;
        }
        
        for (SessionInfo info : getAllSessions()) {
            if (systemCode.equals(info.getSystemCode())) {
                result.add(info);
            }
        }
        
        return result;
    }
    
    @Override
    public List<SessionInfo> getSessionsByAccount(String account) {
        List<SessionInfo> result = new ArrayList<SessionInfo>();
        
        if (account == null) {
            return result;
        }
        
        for (SessionInfo info : getAllSessions()) {
            if (account.equals(info.getAccount())) {
                result.add(info);
            }
        }
        
        return result;
    }
    
    @Override
    public List<SessionInfo> getExpiredSessions() {
        List<SessionInfo> result = new ArrayList<SessionInfo>();
        
        for (SessionInfo info : getAllSessions()) {
            if (info.isExpired(expireTime)) {
                info.setStatus("EXPIRED");
                result.add(info);
            }
        }
        
        return result;
    }
    
    @Override
    public List<SessionInfo> getSessions(int page, int pageSize) {
        List<SessionInfo> allSessions = getAllSessions();
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allSessions.size());
        
        if (start >= allSessions.size()) {
            return new ArrayList<SessionInfo>();
        }
        
        return allSessions.subList(start, end);
    }
    
    @Override
    public SessionOperationResult invalidateSession(String sessionId) {
        if (sessionId == null) {
            return SessionOperationResult.failure("INVALIDATE", null, "INVALID_PARAM", "Session ID is null");
        }
        
        try {
            SessionInfo info = getSessionInfo(sessionId);
            if (info == null) {
                return SessionOperationResult.failure("INVALIDATE", sessionId, "NOT_FOUND", "Session not found");
            }
            
            sessionManager.invalidateSession(sessionId);
            sessionAttributes.remove(sessionId);
            
            return SessionOperationResult.success("INVALIDATE", sessionId, "Session invalidated successfully");
        } catch (Exception e) {
            return SessionOperationResult.failure("INVALIDATE", sessionId, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult invalidateSessionsByUserId(String userId) {
        if (userId == null) {
            return SessionOperationResult.failure("INVALIDATE_BY_USERID", null, "INVALID_PARAM", "User ID is null");
        }
        
        try {
            List<SessionInfo> sessions = getSessionsByUserId(userId);
            int count = 0;
            
            for (SessionInfo info : sessions) {
                sessionManager.invalidateSession(info.getSessionId());
                sessionAttributes.remove(info.getSessionId());
                count++;
            }
            
            return SessionOperationResult.success("INVALIDATE_BY_USERID", userId, 
                "Invalidated " + count + " sessions");
        } catch (Exception e) {
            return SessionOperationResult.failure("INVALIDATE_BY_USERID", userId, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult invalidateSessionsByAccount(String account) {
        if (account == null) {
            return SessionOperationResult.failure("INVALIDATE_BY_ACCOUNT", null, "INVALID_PARAM", "Account is null");
        }
        
        try {
            List<SessionInfo> sessions = getSessionsByAccount(account);
            int count = 0;
            
            for (SessionInfo info : sessions) {
                sessionManager.invalidateSession(info.getSessionId());
                sessionAttributes.remove(info.getSessionId());
                count++;
            }
            
            return SessionOperationResult.success("INVALIDATE_BY_ACCOUNT", account, 
                "Invalidated " + count + " sessions");
        } catch (Exception e) {
            return SessionOperationResult.failure("INVALIDATE_BY_ACCOUNT", account, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult invalidateAllSessions() {
        try {
            List<SessionInfo> sessions = getAllSessions();
            int count = sessions.size();
            
            cacheManager.clear();
            sessionAttributes.clear();
            
            return SessionOperationResult.success("INVALIDATE_ALL", null, 
                "Invalidated " + count + " sessions");
        } catch (Exception e) {
            return SessionOperationResult.failure("INVALIDATE_ALL", null, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult invalidateExpiredSessions() {
        try {
            List<SessionInfo> expiredSessions = getExpiredSessions();
            int count = 0;
            
            for (SessionInfo info : expiredSessions) {
                sessionManager.invalidateSession(info.getSessionId());
                sessionAttributes.remove(info.getSessionId());
                count++;
            }
            
            return SessionOperationResult.success("INVALIDATE_EXPIRED", null, 
                "Invalidated " + count + " expired sessions");
        } catch (Exception e) {
            return SessionOperationResult.failure("INVALIDATE_EXPIRED", null, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult keepAlive(String sessionId) {
        if (sessionId == null) {
            return SessionOperationResult.failure("KEEPALIVE", null, "INVALID_PARAM", "Session ID is null");
        }
        
        try {
            if (!sessionManager.isSessionValid(sessionId)) {
                return SessionOperationResult.failure("KEEPALIVE", sessionId, "NOT_FOUND", "Session not found");
            }
            
            sessionManager.keepAlive(sessionId);
            return SessionOperationResult.success("KEEPALIVE", sessionId, "Session keep-alive successful");
        } catch (Exception e) {
            return SessionOperationResult.failure("KEEPALIVE", sessionId, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult cleanupExpiredSessions() {
        try {
            cacheManager.cleanupExpiredSessions();
            return SessionOperationResult.success("CLEANUP", null, "Expired sessions cleaned up");
        } catch (Exception e) {
            return SessionOperationResult.failure("CLEANUP", null, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionHealthCheck healthCheck() {
        try {
            int activeCount = cacheManager.getSessionCount();
            CacheStats stats = cacheManager.getStats();
            
            if (stats.isConsistent()) {
                return SessionHealthCheck.healthy(activeCount, stats.getSessionCount());
            } else {
                return SessionHealthCheck.degraded(activeCount, "Cache statistics inconsistent");
            }
        } catch (Exception e) {
            return SessionHealthCheck.unhealthy(e.getMessage());
        }
    }
    
    @Override
    public SessionHealthCheck detailedHealthCheck() {
        SessionHealthCheck health = healthCheck();
        
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        long memoryMax = runtime.maxMemory();
        
        health.setMemoryUsed(memoryUsed);
        health.setMemoryMax(memoryMax);
        health.setMemoryUsagePercent((double) memoryUsed / memoryMax * 100);
        health.setUptime(System.currentTimeMillis() - startTime);
        
        SessionStats stats = sessionManager.getStats();
        health.setExpiredSessionCount(stats.getExpiredSessions());
        
        health.addDetail("totalLogins", stats.getTotalLogins());
        health.addDetail("totalLogouts", stats.getTotalLogouts());
        health.addDetail("avgLoginTime", stats.getAvgLoginTime());
        
        return health;
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<String, Object>();
        
        SessionStats sessionStats = sessionManager.getStats();
        stats.put("activeSessions", sessionStats.getActiveSessions());
        stats.put("totalLogins", sessionStats.getTotalLogins());
        stats.put("totalLogouts", sessionStats.getTotalLogouts());
        stats.put("expiredSessions", sessionStats.getExpiredSessions());
        stats.put("avgLoginTime", sessionStats.getAvgLoginTime());
        stats.put("logoutRate", sessionStats.getLogoutRate());
        stats.put("expirationRate", sessionStats.getExpirationRate());
        
        stats.put("uptime", System.currentTimeMillis() - startTime);
        stats.put("expireTime", expireTime);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new LinkedHashMap<String, Object>();
        
        CacheStats cacheStats = cacheManager.getStats();
        stats.put("sessionCount", cacheStats.getSessionCount());
        stats.put("connectInfoCount", cacheStats.getConnectInfoCount());
        stats.put("systemCodeCount", cacheStats.getSystemCodeCount());
        stats.put("connectTimeCount", cacheStats.getConnectTimeCount());
        stats.put("consistent", cacheStats.isConsistent());
        
        return stats;
    }
    
    @Override
    public SessionOperationResult setSessionAttribute(String sessionId, String key, Object value) {
        if (sessionId == null || key == null) {
            return SessionOperationResult.failure("SET_ATTR", sessionId, "INVALID_PARAM", "Session ID or key is null");
        }
        
        if (!sessionManager.isSessionValid(sessionId)) {
            return SessionOperationResult.failure("SET_ATTR", sessionId, "NOT_FOUND", "Session not found");
        }
        
        Map<String, Object> attrs = sessionAttributes.get(sessionId);
        if (attrs == null) {
            attrs = new ConcurrentHashMap<String, Object>();
            sessionAttributes.put(sessionId, attrs);
        }
        
        attrs.put(key, value);
        return SessionOperationResult.success("SET_ATTR", sessionId, "Attribute set successfully");
    }
    
    @Override
    public Object getSessionAttribute(String sessionId, String key) {
        if (sessionId == null || key == null) {
            return null;
        }
        
        Map<String, Object> attrs = sessionAttributes.get(sessionId);
        return attrs != null ? attrs.get(key) : null;
    }
    
    @Override
    public Map<String, Object> getSessionAttributes(String sessionId) {
        if (sessionId == null) {
            return new HashMap<String, Object>();
        }
        
        Map<String, Object> attrs = sessionAttributes.get(sessionId);
        return attrs != null ? new HashMap<String, Object>(attrs) : new HashMap<String, Object>();
    }
    
    @Override
    public int getActiveSessionCount() {
        return cacheManager.getSessionCount();
    }
    
    @Override
    public int getTotalSessionCount() {
        return sessionManager.getStats().getTotalLogins();
    }
    
    @Override
    public long getAverageSessionDuration() {
        return sessionManager.getStats().getAvgLoginTime();
    }
    
    @Override
    public boolean isSessionValid(String sessionId) {
        return sessionManager.isSessionValid(sessionId);
    }
    
    @Override
    public SessionOperationResult forceLogout(String sessionId, String reason) {
        if (sessionId == null) {
            return SessionOperationResult.failure("FORCE_LOGOUT", null, "INVALID_PARAM", "Session ID is null");
        }
        
        try {
            SessionInfo info = getSessionInfo(sessionId);
            if (info == null) {
                return SessionOperationResult.failure("FORCE_LOGOUT", sessionId, "NOT_FOUND", "Session not found");
            }
            
            sessionManager.invalidateSession(sessionId);
            sessionAttributes.remove(sessionId);
            
            return SessionOperationResult.success("FORCE_LOGOUT", sessionId, 
                "Force logout successful. Reason: " + reason);
        } catch (Exception e) {
            return SessionOperationResult.failure("FORCE_LOGOUT", sessionId, "ERROR", e.getMessage());
        }
    }
    
    @Override
    public SessionOperationResult broadcastMessage(String message) {
        return SessionOperationResult.failure("BROADCAST", null, "NOT_IMPLEMENTED", 
            "Broadcast not implemented in this version");
    }
    
    @Override
    public List<String> getActiveUserIds() {
        Set<String> userIds = new HashSet<String>();
        
        for (SessionInfo info : getAllSessions()) {
            if (info.getUserId() != null) {
                userIds.add(info.getUserId());
            }
        }
        
        return new ArrayList<String>(userIds);
    }
    
    @Override
    public Map<String, Integer> getSessionCountBySystemCode() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        
        for (SessionInfo info : getAllSessions()) {
            String code = info.getSystemCode() != null ? info.getSystemCode() : "unknown";
            result.put(code, result.getOrDefault(code, 0) + 1);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Integer> getSessionCountByHour() {
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        
        for (SessionInfo info : getAllSessions()) {
            if (info.getConnectTime() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(info.getConnectTime());
                String hour = String.format("%02d:00", cal.get(Calendar.HOUR_OF_DAY));
                result.put(hour, result.getOrDefault(hour, 0) + 1);
            }
        }
        
        return result;
    }
    
    private SessionInfo buildSessionInfo(String sessionId, JDSSessionHandle handle, ConnectInfo connectInfo) {
        SessionInfo info = new SessionInfo();
        
        info.setSessionId(sessionId);
        info.setUserId(connectInfo.getUserID());
        info.setAccount(connectInfo.getLoginName());
        
        String systemCode = cacheManager.getSystemCode(sessionId);
        info.setSystemCode(systemCode);
        
        Long connectTime = cacheManager.getConnectTime(sessionId);
        if (connectTime != null) {
            info.setConnectTime(new Date(connectTime));
            info.setLastActiveTime(new Date(connectTime));
            
            long duration = System.currentTimeMillis() - connectTime;
            info.setDuration(duration);
        }
        
        info.setStatus(info.isExpired(expireTime) ? "EXPIRED" : "ACTIVE");
        
        return info;
    }
}
