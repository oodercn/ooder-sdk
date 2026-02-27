package net.ooder.scene.session.impl;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.security.TokenEvent;
import net.ooder.scene.session.TokenInfo;
import net.ooder.scene.session.TokenManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token管理器实现
 *
 * <p>提供Token生成、验证、刷新等功能</p>
 */
public class TokenManagerImpl implements TokenManager {

    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    
    private long tokenExpiration = 3600000L;
    private long refreshTokenExpiration = 604800000L;
    private String secretKey = "ooder-scene-engine-secret-key";
    private SceneEventPublisher eventPublisher;

    public void setTokenExpiration(long tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TokenInfo generateToken(String subject) {
        return generateToken(subject, new HashMap<>());
    }

    @Override
    public TokenInfo generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        
        String tokenId = UUID.randomUUID().toString();
        String token = generateTokenString(subject, tokenId, now, tokenExpiration);
        String refreshToken = generateRefreshTokenString(subject, tokenId, now);
        
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setSubject(subject);
        tokenInfo.setIssuedAt(now);
        tokenInfo.setExpiresAt(now + tokenExpiration);
        tokenInfo.setClaims(claims != null ? claims : new HashMap<>());
        
        tokenStore.put(token, tokenInfo);
        
        publishTokenEvent(TokenEvent.generated(this, tokenId, subject));
        
        return tokenInfo;
    }

    @Override
    public TokenInfo validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        if (revokedTokens.contains(token)) {
            publishTokenEvent(TokenEvent.validationFailed(this, token, "Token has been revoked"));
            return null;
        }
        
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null) {
            publishTokenEvent(TokenEvent.validationFailed(this, token, "Token not found"));
            return null;
        }
        
        if (System.currentTimeMillis() > tokenInfo.getExpiresAt()) {
            tokenStore.remove(token);
            publishTokenEvent(TokenEvent.expired(this, token, tokenInfo.getSubject()));
            return null;
        }
        
        return tokenInfo;
    }

    @Override
    public TokenInfo refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return null;
        }
        
        if (revokedTokens.contains(refreshToken)) {
            return null;
        }
        
        TokenInfo oldTokenInfo = findTokenInfoByRefreshToken(refreshToken);
        if (oldTokenInfo == null) {
            return null;
        }
        
        revokedTokens.add(oldTokenInfo.getToken());
        tokenStore.remove(oldTokenInfo.getToken());
        
        return generateToken(oldTokenInfo.getSubject(), oldTokenInfo.getClaims());
    }

    @Override
    public void revokeToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        
        TokenInfo tokenInfo = tokenStore.get(token);
        String subject = tokenInfo != null ? tokenInfo.getSubject() : null;
        
        revokedTokens.add(token);
        tokenStore.remove(token);
        
        publishTokenEvent(TokenEvent.revoked(this, token, subject));
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return token != null && revokedTokens.contains(token);
    }

    @Override
    public TokenInfo parseToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        return tokenStore.get(token);
    }

    private String generateTokenString(String subject, String tokenId, long issuedAt, long expiration) {
        String data = subject + ":" + tokenId + ":" + issuedAt + ":" + expiration;
        String signature = sign(data);
        return Base64.getEncoder().encodeToString(
            (data + ":" + signature).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String generateRefreshTokenString(String subject, String tokenId, long issuedAt) {
        String data = "refresh:" + subject + ":" + tokenId + ":" + issuedAt;
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

    private TokenInfo findTokenInfoByRefreshToken(String refreshToken) {
        for (TokenInfo tokenInfo : tokenStore.values()) {
            if (refreshToken.equals(tokenInfo.getRefreshToken())) {
                return tokenInfo;
            }
        }
        return null;
    }

    public int getActiveTokenCount() {
        return tokenStore.size();
    }

    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, TokenInfo>> iterator = tokenStore.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, TokenInfo> entry = iterator.next();
            if (entry.getValue().getExpiresAt() < now) {
                publishTokenEvent(TokenEvent.expired(this, entry.getKey(), entry.getValue().getSubject()));
                iterator.remove();
            }
        }
    }
    
    private void publishTokenEvent(TokenEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }
}
