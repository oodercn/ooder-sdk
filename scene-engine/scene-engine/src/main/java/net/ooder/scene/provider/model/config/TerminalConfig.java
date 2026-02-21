package net.ooder.scene.provider.model.config;

import java.util.Map;

public class TerminalConfig {
    
    private int maxSessions;
    private int sessionTimeout;
    private String defaultShell;
    private int scrollbackLines;
    private boolean enableColors;
    private Map<String, Object> extra;
    
    public int getMaxSessions() {
        return maxSessions;
    }
    
    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }
    
    public int getSessionTimeout() {
        return sessionTimeout;
    }
    
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    public String getDefaultShell() {
        return defaultShell;
    }
    
    public void setDefaultShell(String defaultShell) {
        this.defaultShell = defaultShell;
    }
    
    public int getScrollbackLines() {
        return scrollbackLines;
    }
    
    public void setScrollbackLines(int scrollbackLines) {
        this.scrollbackLines = scrollbackLines;
    }
    
    public boolean isEnableColors() {
        return enableColors;
    }
    
    public void setEnableColors(boolean enableColors) {
        this.enableColors = enableColors;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
