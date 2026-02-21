package net.ooder.config.scene.server;

import java.io.Serializable;

public class SecurityPolicy implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean authRequired = true;
    private long tokenExpireTime = 7200000L;
    private boolean refreshTokenEnabled = true;
    private boolean encryptData = false;
    private boolean auditLog = true;
    
    public SecurityPolicy() {
    }
    
    public boolean isAuthRequired() {
        return authRequired;
    }
    
    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }
    
    public long getTokenExpireTime() {
        return tokenExpireTime;
    }
    
    public void setTokenExpireTime(long tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }
    
    public boolean isRefreshTokenEnabled() {
        return refreshTokenEnabled;
    }
    
    public void setRefreshTokenEnabled(boolean refreshTokenEnabled) {
        this.refreshTokenEnabled = refreshTokenEnabled;
    }
    
    public boolean isEncryptData() {
        return encryptData;
    }
    
    public void setEncryptData(boolean encryptData) {
        this.encryptData = encryptData;
    }
    
    public boolean isAuditLog() {
        return auditLog;
    }
    
    public void setAuditLog(boolean auditLog) {
        this.auditLog = auditLog;
    }
}
