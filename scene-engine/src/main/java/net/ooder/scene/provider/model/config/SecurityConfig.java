package net.ooder.scene.provider.model.config;

import java.util.Map;

public class SecurityConfig {
    
    private boolean enabled;
    private boolean authRequired;
    private int sessionTimeout;
    private int maxLoginAttempts;
    private String passwordPolicy;
    private boolean twoFactorEnabled;
    private Map<String, Object> extra;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isAuthRequired() {
        return authRequired;
    }
    
    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }
    
    public int getSessionTimeout() {
        return sessionTimeout;
    }
    
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    public int getMaxLoginAttempts() {
        return maxLoginAttempts;
    }
    
    public void setMaxLoginAttempts(int maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
    }
    
    public String getPasswordPolicy() {
        return passwordPolicy;
    }
    
    public void setPasswordPolicy(String passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }
    
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
