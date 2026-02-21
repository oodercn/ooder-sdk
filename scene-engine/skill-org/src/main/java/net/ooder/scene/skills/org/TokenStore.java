package net.ooder.scene.skills.org;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * TokenStore 令牌存储
 * 
 * <p>简单的内存令牌存储实现，生产环境应使用持久化存储。</p>
 */
@Component
public class TokenStore {

    private static final long TOKEN_EXPIRE_TIME = 2 * 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private final Map<String, TokenInfo> tokenMap = new ConcurrentHashMap<>();
    private final Map<String, String> refreshTokenMap = new ConcurrentHashMap<>();

    public String createToken(String userId, String username) {
        String token = generateToken(userId);
        String refreshToken = generateToken(userId + "_refresh");
        
        TokenInfo info = new TokenInfo();
        info.setUserId(userId);
        info.setUsername(username);
        info.setToken(token);
        info.setRefreshToken(refreshToken);
        info.setCreateTime(System.currentTimeMillis());
        info.setExpireTime(System.currentTimeMillis() + TOKEN_EXPIRE_TIME);
        info.setRefreshExpireTime(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME);
        
        tokenMap.put(token, info);
        refreshTokenMap.put(refreshToken, token);
        
        return token;
    }

    public boolean validateToken(String token) {
        TokenInfo info = tokenMap.get(token);
        if (info == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > info.getExpireTime()) {
            tokenMap.remove(token);
            refreshTokenMap.remove(info.getRefreshToken());
            return false;
        }
        
        return true;
    }

    public String getUserId(String token) {
        TokenInfo info = tokenMap.get(token);
        if (info == null || System.currentTimeMillis() > info.getExpireTime()) {
            return null;
        }
        return info.getUserId();
    }

    public String getUserIdByRefreshToken(String refreshToken) {
        String token = refreshTokenMap.get(refreshToken);
        if (token == null) {
            return null;
        }
        
        TokenInfo info = tokenMap.get(token);
        if (info == null || System.currentTimeMillis() > info.getRefreshExpireTime()) {
            refreshTokenMap.remove(refreshToken);
            tokenMap.remove(token);
            return null;
        }
        
        return info.getUserId();
    }

    public String refreshToken(String refreshToken) {
        String oldToken = refreshTokenMap.get(refreshToken);
        if (oldToken == null) {
            return null;
        }
        
        TokenInfo oldInfo = tokenMap.get(oldToken);
        if (oldInfo == null || System.currentTimeMillis() > oldInfo.getRefreshExpireTime()) {
            refreshTokenMap.remove(refreshToken);
            tokenMap.remove(oldToken);
            return null;
        }
        
        tokenMap.remove(oldToken);
        refreshTokenMap.remove(refreshToken);
        
        return createToken(oldInfo.getUserId(), oldInfo.getUsername());
    }

    public void removeToken(String token) {
        TokenInfo info = tokenMap.remove(token);
        if (info != null) {
            refreshTokenMap.remove(info.getRefreshToken());
        }
    }

    private String generateToken(String seed) {
        try {
            String source = seed + UUID.randomUUID().toString() + System.currentTimeMillis();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    private static class TokenInfo {
        private String userId;
        private String username;
        private String token;
        private String refreshToken;
        private long createTime;
        private long expireTime;
        private long refreshExpireTime;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        public long getRefreshExpireTime() { return refreshExpireTime; }
        public void setRefreshExpireTime(long refreshExpireTime) { this.refreshExpireTime = refreshExpireTime; }
    }
}
