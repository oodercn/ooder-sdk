package net.ooder.scene.websocket.auth;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.security.TokenEvent;
import net.ooder.scene.session.AuthManager;
import net.ooder.scene.session.TokenInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * WebSocket认证服务实现
 *
 * <p>提供WebSocket连接的认证和授权能力</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(WebSocketAuthService.class)
public class WebSocketAuthServiceImpl implements WebSocketAuthService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthServiceImpl.class);

    private static final int DEFAULT_MAX_CONNECTIONS_PER_USER = 5;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_SCENE = 100;
    private static final long DEFAULT_IDLE_TIMEOUT = 300000L;

    private final Map<String, WebSocketToken> tokenStore = new ConcurrentHashMap<>();
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    private final Map<String, ConnectionInfo> connectionStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userConnections = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sceneGroupConnections = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userTokens = new ConcurrentHashMap<>();

    private final AtomicInteger tokenGeneratedCount = new AtomicInteger(0);
    private final AtomicInteger tokenRevokedCount = new AtomicInteger(0);
    private final AtomicLong connectionCreatedCount = new AtomicLong(0);

    private String secretKey = "ooder-websocket-auth-secret-key";
    private long defaultTokenExpiration = 3600000L;
    private int maxConnectionsPerUser = DEFAULT_MAX_CONNECTIONS_PER_USER;
    private int maxConnectionsPerScene = DEFAULT_MAX_CONNECTIONS_PER_SCENE;
    private long idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private SceneEventPublisher eventPublisher;
    private AuthManager authManager;

    public WebSocketAuthServiceImpl() {
    }

    public WebSocketAuthServiceImpl(AuthManager authManager) {
        this.authManager = authManager;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setDefaultTokenExpiration(long defaultTokenExpiration) {
        this.defaultTokenExpiration = defaultTokenExpiration;
    }

    public void setMaxConnectionsPerUser(int maxConnectionsPerUser) {
        this.maxConnectionsPerUser = maxConnectionsPerUser;
    }

    public void setMaxConnectionsPerScene(int maxConnectionsPerScene) {
        this.maxConnectionsPerScene = maxConnectionsPerScene;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public WebSocketToken generateToken(String userId, String sceneGroupId, long expireSeconds) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId is required");
        }
        if (sceneGroupId == null || sceneGroupId.isEmpty()) {
            throw new IllegalArgumentException("SceneGroupId is required");
        }

        long now = System.currentTimeMillis();
        long expireAt = now + (expireSeconds * 1000);

        WebSocketToken wsToken = WebSocketToken.builder()
                .userId(userId)
                .sceneGroupId(sceneGroupId)
                .createdAt(now)
                .expireAt(expireAt)
                .build();

        String tokenString = generateTokenString(wsToken);
        wsToken.setToken(tokenString);

        tokenStore.put(tokenString, wsToken);
        userTokens.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(tokenString);

        tokenGeneratedCount.incrementAndGet();

        publishTokenEvent(TokenEvent.generated(this, wsToken.getTokenId(), userId));

        log.debug("WebSocket token generated: tokenId={}, userId={}, sceneGroupId={}", 
                wsToken.getTokenId(), userId, sceneGroupId);

        return wsToken;
    }

    @Override
    public TokenValidationResult validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return TokenValidationResult.invalid();
        }

        if (revokedTokens.contains(token)) {
            return TokenValidationResult.revoked();
        }

        WebSocketToken wsToken = tokenStore.get(token);
        if (wsToken == null) {
            return TokenValidationResult.notFound();
        }

        if (wsToken.isExpired()) {
            tokenStore.remove(token);
            removeUserToken(wsToken.getUserId(), token);
            publishTokenEvent(TokenEvent.expired(this, token, wsToken.getUserId()));
            return TokenValidationResult.expired();
        }

        return TokenValidationResult.success(wsToken);
    }

    @Override
    public WebSocketToken refreshToken(String token) {
        TokenValidationResult result = validateToken(token);
        if (!result.isValid()) {
            return null;
        }

        WebSocketToken oldToken = result.getToken();
        revokeToken(token);

        return generateToken(oldToken.getUserId(), oldToken.getSceneGroupId(), 
                (oldToken.getExpireAt() - System.currentTimeMillis()) / 1000);
    }

    @Override
    public void revokeToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        WebSocketToken wsToken = tokenStore.remove(token);
        if (wsToken != null) {
            revokedTokens.add(token);
            removeUserToken(wsToken.getUserId(), token);
            tokenRevokedCount.incrementAndGet();
            publishTokenEvent(TokenEvent.revoked(this, token, wsToken.getUserId()));
            log.debug("WebSocket token revoked: tokenId={}", wsToken.getTokenId());
        }
    }

    @Override
    public void revokeUserTokens(String userId) {
        if (userId == null) {
            return;
        }

        Set<String> tokens = userTokens.remove(userId);
        if (tokens != null) {
            for (String token : tokens) {
                WebSocketToken wsToken = tokenStore.remove(token);
                if (wsToken != null) {
                    revokedTokens.add(token);
                    tokenRevokedCount.incrementAndGet();
                }
            }
            log.info("All tokens revoked for user: userId={}, count={}", userId, tokens.size());
        }
    }

    @Override
    public boolean checkConnectionPermission(String userId, String sceneGroupId) {
        if (userId == null || sceneGroupId == null) {
            return false;
        }

        int userConnCount = getUserConnectionCount(userId);
        if (userConnCount >= maxConnectionsPerUser) {
            log.warn("User connection limit reached: userId={}, current={}, max={}", 
                    userId, userConnCount, maxConnectionsPerUser);
            return false;
        }

        int sceneConnCount = getSceneGroupConnectionCount(sceneGroupId);
        if (sceneConnCount >= maxConnectionsPerScene) {
            log.warn("Scene group connection limit reached: sceneGroupId={}, current={}, max={}", 
                    sceneGroupId, sceneConnCount, maxConnectionsPerScene);
            return false;
        }

        return true;
    }

    @Override
    public List<String> getAuthorizedSceneGroups(String userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        Set<String> sceneGroups = new HashSet<>();
        for (WebSocketToken token : tokenStore.values()) {
            if (userId.equals(token.getUserId()) && !token.isExpired()) {
                sceneGroups.add(token.getSceneGroupId());
            }
        }
        return new ArrayList<>(sceneGroups);
    }

    @Override
    public void registerConnection(String sessionId, String userId, String sceneGroupId) {
        ConnectionInfo connectionInfo = ConnectionInfo.builder()
                .sessionId(sessionId)
                .userId(userId)
                .sceneGroupId(sceneGroupId)
                .build();
        registerConnection(connectionInfo);
    }

    @Override
    public void registerConnection(ConnectionInfo connectionInfo) {
        if (connectionInfo == null || connectionInfo.getSessionId() == null) {
            return;
        }

        String sessionId = connectionInfo.getSessionId();
        connectionStore.put(sessionId, connectionInfo);

        String userId = connectionInfo.getUserId();
        if (userId != null) {
            userConnections.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        }

        String sceneGroupId = connectionInfo.getSceneGroupId();
        if (sceneGroupId != null) {
            sceneGroupConnections.computeIfAbsent(sceneGroupId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        }

        connectionCreatedCount.incrementAndGet();

        log.info("Connection registered: sessionId={}, userId={}, sceneGroupId={}", 
                sessionId, userId, sceneGroupId);
    }

    @Override
    public void unregisterConnection(String sessionId) {
        if (sessionId == null) {
            return;
        }

        ConnectionInfo connectionInfo = connectionStore.remove(sessionId);
        if (connectionInfo == null) {
            return;
        }

        String userId = connectionInfo.getUserId();
        if (userId != null) {
            Set<String> conns = userConnections.get(userId);
            if (conns != null) {
                conns.remove(sessionId);
                if (conns.isEmpty()) {
                    userConnections.remove(userId);
                }
            }
        }

        String sceneGroupId = connectionInfo.getSceneGroupId();
        if (sceneGroupId != null) {
            Set<String> conns = sceneGroupConnections.get(sceneGroupId);
            if (conns != null) {
                conns.remove(sessionId);
                if (conns.isEmpty()) {
                    sceneGroupConnections.remove(sceneGroupId);
                }
            }
        }

        log.info("Connection unregistered: sessionId={}, userId={}, sceneGroupId={}", 
                sessionId, userId, sceneGroupId);
    }

    @Override
    public void touchConnection(String sessionId) {
        if (sessionId == null) {
            return;
        }

        ConnectionInfo connectionInfo = connectionStore.get(sessionId);
        if (connectionInfo != null) {
            connectionInfo.touch();
        }
    }

    @Override
    public List<ConnectionInfo> getUserConnections(String userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        Set<String> sessionIds = userConnections.get(userId);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return new ArrayList<>();
        }

        return sessionIds.stream()
                .map(connectionStore::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConnectionInfo> getSceneGroupConnections(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        Set<String> sessionIds = sceneGroupConnections.get(sceneGroupId);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return new ArrayList<>();
        }

        return sessionIds.stream()
                .map(connectionStore::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ConnectionInfo getConnection(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return connectionStore.get(sessionId);
    }

    @Override
    public boolean isUserOnline(String userId) {
        if (userId == null) {
            return false;
        }
        Set<String> conns = userConnections.get(userId);
        return conns != null && !conns.isEmpty();
    }

    @Override
    public boolean isUserOnlineInScene(String userId, String sceneGroupId) {
        if (userId == null || sceneGroupId == null) {
            return false;
        }

        Set<String> userSessionIds = userConnections.get(userId);
        if (userSessionIds == null || userSessionIds.isEmpty()) {
            return false;
        }

        for (String sessionId : userSessionIds) {
            ConnectionInfo conn = connectionStore.get(sessionId);
            if (conn != null && sceneGroupId.equals(conn.getSceneGroupId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getUserConnectionCount(String userId) {
        if (userId == null) {
            return 0;
        }
        Set<String> conns = userConnections.get(userId);
        return conns != null ? conns.size() : 0;
    }

    @Override
    public int getSceneGroupConnectionCount(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }
        Set<String> conns = sceneGroupConnections.get(sceneGroupId);
        return conns != null ? conns.size() : 0;
    }

    @Override
    public int getTotalConnectionCount() {
        return connectionStore.size();
    }

    @Override
    public int cleanupExpiredConnections() {
        int count = 0;
        List<String> toRemove = new ArrayList<>();

        for (ConnectionInfo conn : connectionStore.values()) {
            if (conn.isIdle(idleTimeout)) {
                toRemove.add(conn.getSessionId());
            }
        }

        for (String sessionId : toRemove) {
            unregisterConnection(sessionId);
            count++;
        }

        if (count > 0) {
            log.info("Cleaned up {} expired connections", count);
        }

        return count;
    }

    @Override
    public WebSocketAuthStats getStats() {
        WebSocketAuthStats stats = new WebSocketAuthStats();
        stats.setActiveConnections(connectionStore.size());
        stats.setActiveUsers(userConnections.size());
        stats.setActiveSceneGroups(sceneGroupConnections.size());
        stats.setTotalTokensGenerated(tokenGeneratedCount.get());
        stats.setTotalTokensRevoked(tokenRevokedCount.get());
        stats.setTotalConnectionsCreated(connectionCreatedCount.get());
        return stats;
    }

    private String generateTokenString(WebSocketToken wsToken) {
        String data = wsToken.getUserId() + ":" + wsToken.getSceneGroupId() + ":" + 
                wsToken.getTokenId() + ":" + wsToken.getExpireAt();
        String signature = sign(data);
        return Base64.getEncoder().encodeToString(
                (data + ":" + signature).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String sign(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((data + secretKey).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString();
        }
    }

    private void removeUserToken(String userId, String token) {
        if (userId == null) {
            return;
        }
        Set<String> tokens = userTokens.get(userId);
        if (tokens != null) {
            tokens.remove(token);
            if (tokens.isEmpty()) {
                userTokens.remove(userId);
            }
        }
    }

    private void publishTokenEvent(TokenEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, WebSocketToken>> iterator = tokenStore.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, WebSocketToken> entry = iterator.next();
            WebSocketToken token = entry.getValue();
            if (token.getExpireAt() < now) {
                iterator.remove();
                removeUserToken(token.getUserId(), entry.getKey());
                publishTokenEvent(TokenEvent.expired(this, entry.getKey(), token.getUserId()));
            }
        }
    }
}
