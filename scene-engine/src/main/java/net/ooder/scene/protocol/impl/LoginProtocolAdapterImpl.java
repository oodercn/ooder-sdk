package net.ooder.scene.protocol.impl;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.security.LoginEvent;
import net.ooder.scene.event.security.LogoutEvent;
import net.ooder.scene.protocol.*;
import net.ooder.scene.session.SessionInfo;
import net.ooder.scene.session.SessionManager;
import net.ooder.scene.session.TokenManager;
import net.ooder.scene.session.TokenInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录协议适配器实现
 *
 * <p>提供登录认证、会话管理等功能</p>
 */
public class LoginProtocolAdapterImpl implements LoginProtocolAdapter {

    private final SessionManager sessionManager;
    private final TokenManager tokenManager;
    private final Map<String, Session> sessionCache = new ConcurrentHashMap<>();
    private final Map<String, String> tokenToSessionMap = new ConcurrentHashMap<>();

    private long sessionTimeout = 1800000L;
    private SceneEventPublisher eventPublisher;

    public LoginProtocolAdapterImpl(SessionManager sessionManager, TokenManager tokenManager) {
        this.sessionManager = sessionManager;
        this.tokenManager = tokenManager;
    }
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public CompletableFuture<LoginResult> login(LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            LoginResult result = new LoginResult();
            String username = request.getUsername();
            
            try {
                String password = request.getPassword();
                
                if (username == null || username.isEmpty()) {
                    result.setSuccess(false);
                    result.setMessage("Username is required");
                    result.setErrorCode("USERNAME_REQUIRED");
                    publishLoginFailed(username, request.getClientIp(), "Username is required");
                    return result;
                }
                
                if (password == null || password.isEmpty()) {
                    result.setSuccess(false);
                    result.setMessage("Password is required");
                    result.setErrorCode("PASSWORD_REQUIRED");
                    publishLoginFailed(username, request.getClientIp(), "Password is required");
                    return result;
                }
                
                String userId = authenticateUser(username, password);
                if (userId == null) {
                    result.setSuccess(false);
                    result.setMessage("Invalid username or password");
                    result.setErrorCode("AUTH_FAILED");
                    publishLoginFailed(username, request.getClientIp(), "Invalid username or password");
                    return result;
                }
                
                SessionInfo sessionInfo = sessionManager.createSession(
                    userId, 
                    username, 
                    request.getClientIp(), 
                    request.getUserAgent()
                );
                
                TokenInfo tokenInfo = tokenManager.generateToken(userId, createClaims(userId, username));
                
                Session session = convertToSession(sessionInfo, tokenInfo);
                sessionCache.put(session.getSessionId(), session);
                tokenToSessionMap.put(tokenInfo.getToken(), session.getSessionId());
                
                result.setSuccess(true);
                result.setMessage("Login successful");
                result.setSession(session);
                
                publishLoginSuccess(username, userId, request.getClientIp());
                
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("Login failed: " + e.getMessage());
                result.setErrorCode("SYSTEM_ERROR");
                publishLoginFailed(username, request.getClientIp(), e.getMessage());
            }
            
            return result;
        });
    }

    @Override
    public CompletableFuture<Void> logout(String sessionId) {
        return CompletableFuture.runAsync(() -> {
            if (sessionId == null || sessionId.isEmpty()) {
                return;
            }
            
            Session session = sessionCache.remove(sessionId);
            if (session != null) {
                sessionManager.destroySession(sessionId);
                publishLogout(session.getUserId(), session.getUsername(), sessionId);
            }
        });
    }

    @Override
    public CompletableFuture<Session> getSession(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            if (sessionId == null || sessionId.isEmpty()) {
                return null;
            }
            
            Session session = sessionCache.get(sessionId);
            if (session == null) {
                SessionInfo sessionInfo = sessionManager.getSession(sessionId);
                if (sessionInfo != null) {
                    session = convertFromSessionInfo(sessionInfo);
                    sessionCache.put(sessionId, session);
                }
            }
            
            if (session != null && session.isExpired()) {
                sessionCache.remove(sessionId);
                sessionManager.destroySession(sessionId);
                return null;
            }
            
            return session;
        });
    }

    @Override
    public CompletableFuture<Boolean> validateSession(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            if (sessionId == null || sessionId.isEmpty()) {
                return false;
            }
            
            Session session = sessionCache.get(sessionId);
            if (session == null) {
                return sessionManager.validateSession(sessionId);
            }
            
            return session.isActive();
        });
    }

    @Override
    public CompletableFuture<Session> refreshSession(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            if (sessionId == null || sessionId.isEmpty()) {
                return null;
            }
            
            Session session = sessionCache.get(sessionId);
            if (session == null) {
                SessionInfo sessionInfo = sessionManager.refreshSession(sessionId);
                if (sessionInfo != null) {
                    session = convertFromSessionInfo(sessionInfo);
                    sessionCache.put(sessionId, session);
                }
            } else {
                session.setLastActiveAt(System.currentTimeMillis());
                session.setExpiresAt(System.currentTimeMillis() + sessionTimeout);
                sessionManager.touchSession(sessionId);
            }
            
            return session;
        });
    }

    @Override
    public CompletableFuture<String> getCurrentUserId(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = sessionCache.get(sessionId);
            if (session == null) {
                SessionInfo sessionInfo = sessionManager.getSession(sessionId);
                if (sessionInfo != null) {
                    return sessionInfo.getUserId();
                }
                return null;
            }
            return session.getUserId();
        });
    }

    private String authenticateUser(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            return "user-admin";
        }
        if ("test".equals(username) && "test123".equals(password)) {
            return "user-test";
        }
        return null;
    }

    private Map<String, Object> createClaims(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("loginTime", System.currentTimeMillis());
        return claims;
    }

    private Session convertToSession(SessionInfo sessionInfo, TokenInfo tokenInfo) {
        Session session = new Session();
        session.setSessionId(sessionInfo.getSessionId());
        session.setUserId(sessionInfo.getUserId());
        session.setUsername(sessionInfo.getUsername());
        session.setDeviceId(sessionInfo.getDomain());
        session.setClientIp(sessionInfo.getClientIp());
        session.setCreatedAt(sessionInfo.getCreatedAt());
        session.setExpiresAt(sessionInfo.getExpiresAt());
        session.setLastActiveAt(sessionInfo.getLastActiveAt());
        session.setStatus(sessionInfo.getStatus());
        return session;
    }

    private Session convertFromSessionInfo(SessionInfo sessionInfo) {
        Session session = new Session();
        session.setSessionId(sessionInfo.getSessionId());
        session.setUserId(sessionInfo.getUserId());
        session.setUsername(sessionInfo.getUsername());
        session.setDeviceId(sessionInfo.getDomain());
        session.setClientIp(sessionInfo.getClientIp());
        session.setCreatedAt(sessionInfo.getCreatedAt());
        session.setExpiresAt(sessionInfo.getExpiresAt());
        session.setLastActiveAt(sessionInfo.getLastActiveAt());
        session.setStatus(sessionInfo.getStatus());
        return session;
    }
    
    private void publishLoginSuccess(String username, String userId, String ipAddress) {
        if (eventPublisher != null) {
            eventPublisher.publish(LoginEvent.success(this, username, userId, ipAddress));
        }
    }
    
    private void publishLoginFailed(String username, String ipAddress, String reason) {
        if (eventPublisher != null) {
            eventPublisher.publish(LoginEvent.failed(this, username, ipAddress, reason));
        }
    }
    
    private void publishLogout(String userId, String username, String sessionId) {
        if (eventPublisher != null) {
            eventPublisher.publish(new LogoutEvent(this, userId, username, sessionId));
        }
    }
}
