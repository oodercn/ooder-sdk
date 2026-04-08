package net.ooder.config.core;

import java.io.Serializable;

public class SessionConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = true;
    private long expireTime = 30L;
    private long checkInterval = 5L;
    private boolean singleLogin = true;
    
    public SessionConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public long getCheckInterval() {
        return checkInterval;
    }
    
    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }
    
    public boolean isSingleLogin() {
        return singleLogin;
    }
    
    public void setSingleLogin(boolean singleLogin) {
        this.singleLogin = singleLogin;
    }
    
    public String getValue(String key) {
        switch (key) {
            case "enabled":
                return String.valueOf(enabled);
            case "ExpireTime":
                return String.valueOf(expireTime);
            case "CheckInterval":
                return String.valueOf(checkInterval);
            case "singleLogin":
                return String.valueOf(singleLogin);
            default:
                return null;
        }
    }
}
