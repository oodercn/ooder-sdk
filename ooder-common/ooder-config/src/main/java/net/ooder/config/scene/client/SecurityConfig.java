package net.ooder.config.scene.client;

import java.io.Serializable;

public class SecurityConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean tokenEnabled = true;
    private String tokenHeader = "Authorization";
    private String tokenPrefix = "Bearer";
    private long tokenExpireTime = 7200000L;
    
    private boolean encryptEnabled = false;
    private String encryptAlgorithm = "AES-256";
    
    private boolean refreshTokenEnabled = true;
    
    public SecurityConfig() {
    }
    
    public static SecurityConfigBuilder builder() {
        return new SecurityConfigBuilder();
    }
    
    public boolean isTokenEnabled() {
        return tokenEnabled;
    }
    
    public void setTokenEnabled(boolean tokenEnabled) {
        this.tokenEnabled = tokenEnabled;
    }
    
    public String getTokenHeader() {
        return tokenHeader;
    }
    
    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }
    
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
    
    public long getTokenExpireTime() {
        return tokenExpireTime;
    }
    
    public void setTokenExpireTime(long tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }
    
    public boolean isEncryptEnabled() {
        return encryptEnabled;
    }
    
    public void setEncryptEnabled(boolean encryptEnabled) {
        this.encryptEnabled = encryptEnabled;
    }
    
    public String getEncryptAlgorithm() {
        return encryptAlgorithm;
    }
    
    public void setEncryptAlgorithm(String encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }
    
    public boolean isRefreshTokenEnabled() {
        return refreshTokenEnabled;
    }
    
    public void setRefreshTokenEnabled(boolean refreshTokenEnabled) {
        this.refreshTokenEnabled = refreshTokenEnabled;
    }
    
    public static class SecurityConfigBuilder {
        private SecurityConfig config = new SecurityConfig();
        
        public SecurityConfigBuilder tokenEnabled(boolean tokenEnabled) {
            config.tokenEnabled = tokenEnabled;
            return this;
        }
        
        public SecurityConfigBuilder tokenHeader(String tokenHeader) {
            config.tokenHeader = tokenHeader;
            return this;
        }
        
        public SecurityConfigBuilder tokenPrefix(String tokenPrefix) {
            config.tokenPrefix = tokenPrefix;
            return this;
        }
        
        public SecurityConfigBuilder tokenExpireTime(long tokenExpireTime) {
            config.tokenExpireTime = tokenExpireTime;
            return this;
        }
        
        public SecurityConfigBuilder encryptEnabled(boolean encryptEnabled) {
            config.encryptEnabled = encryptEnabled;
            return this;
        }
        
        public SecurityConfigBuilder encryptAlgorithm(String encryptAlgorithm) {
            config.encryptAlgorithm = encryptAlgorithm;
            return this;
        }
        
        public SecurityConfigBuilder refreshTokenEnabled(boolean refreshTokenEnabled) {
            config.refreshTokenEnabled = refreshTokenEnabled;
            return this;
        }
        
        public SecurityConfig build() {
            return config;
        }
    }
}
