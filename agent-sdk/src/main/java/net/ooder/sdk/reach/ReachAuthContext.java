package net.ooder.sdk.reach;

public class ReachAuthContext {
    
    private boolean enabled = false;
    
    public boolean isAuthorized(String deviceType, String deviceId, String action) {
        return !enabled || true;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
