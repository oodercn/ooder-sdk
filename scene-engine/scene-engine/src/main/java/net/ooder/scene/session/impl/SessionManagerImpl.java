package net.ooder.scene.session.impl;

import net.ooder.scene.session.SessionInfo;
import net.ooder.scene.session.SessionManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session管理器实现
 */
public class SessionManagerImpl implements SessionManager {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userSessions = new ConcurrentHashMap<>();
    
    private long sessionTimeout = 1800000L;
    private int maxSessionsPerUser = 10;

    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setMaxSessionsPerUser(int maxSessionsPerUser) {
        this.maxSessionsPerUser = maxSessionsPerUser;
    }

    @Override
    public SessionInfo createSession(String userId, String username, String clientIp, String userAgent) {
        String sessionId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        
        SessionInfo session = new SessionInfo();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setUsername(username);
        session.setClientIp(clientIp);
        session.setUserAgent(userAgent);
        session.setCreatedAt(now);
        session.setExpiresAt(now + sessionTimeout);
        session.setLastActiveAt(now);
        session.setStatus("ACTIVE");
        
        sessions.put(sessionId, session);
        
        List<String> userSessionList = userSessions.computeIfAbsent(userId, k -> new ArrayList<>());
        userSessionList.add(sessionId);
        
        if (userSessionList.size() > maxSessionsPerUser) {
            String oldestSessionId = userSessionList.remove(0);
            sessions.remove(oldestSessionId);
        }
        
        return session;
    }

    @Override
    public SessionInfo getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        
        SessionInfo session = sessions.get(sessionId);
        if (session != null && session.isExpired()) {
            destroySession(sessionId);
            return null;
        }
        
        return session;
    }

    @Override
    public boolean validateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }
        
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        if (session.isExpired()) {
            destroySession(sessionId);
            return false;
        }
        
        return "ACTIVE".equals(session.getStatus());
    }

    @Override
    public SessionInfo refreshSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        if (session.isExpired()) {
            destroySession(sessionId);
            return null;
        }
        
        long now = System.currentTimeMillis();
        session.setExpiresAt(now + sessionTimeout);
        session.setLastActiveAt(now);
        
        return session;
    }

    @Override
    public void destroySession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return;
        }
        
        SessionInfo session = sessions.remove(sessionId);
        if (session != null) {
            String userId = session.getUserId();
            List<String> userSessionList = userSessions.get(userId);
            if (userSessionList != null) {
                userSessionList.remove(sessionId);
                if (userSessionList.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
    }

    @Override
    public void touchSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return;
        }
        
        SessionInfo session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.setLastActiveAt(System.currentTimeMillis());
        }
    }

    @Override
    public List<SessionInfo> getActiveSessions(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> sessionIds = userSessions.get(userId);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SessionInfo> activeSessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            SessionInfo session = sessions.get(sessionId);
            if (session != null && session.isActive()) {
                activeSessions.add(session);
            }
        }
        
        return activeSessions;
    }

    @Override
    public void destroyUserSessions(String userId) {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        
        List<String> sessionIds = userSessions.remove(userId);
        if (sessionIds != null) {
            for (String sessionId : sessionIds) {
                sessions.remove(sessionId);
            }
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }

    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, SessionInfo>> iterator = sessions.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, SessionInfo> entry = iterator.next();
            SessionInfo session = entry.getValue();
            
            if (session.isExpired()) {
                iterator.remove();
                
                String userId = session.getUserId();
                List<String> userSessionList = userSessions.get(userId);
                if (userSessionList != null) {
                    userSessionList.remove(entry.getKey());
                    if (userSessionList.isEmpty()) {
                        userSessions.remove(userId);
                    }
                }
            }
        }
    }
}
