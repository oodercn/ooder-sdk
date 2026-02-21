package net.ooder.scene.provider.model.config;

import java.util.Map;

public class ServiceConfig {
    
    private boolean autoStart;
    private int healthCheckInterval;
    private int restartDelay;
    private int maxRestarts;
    private String logPath;
    private Map<String, Object> extra;
    
    public boolean isAutoStart() {
        return autoStart;
    }
    
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
    
    public int getHealthCheckInterval() {
        return healthCheckInterval;
    }
    
    public void setHealthCheckInterval(int healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }
    
    public int getRestartDelay() {
        return restartDelay;
    }
    
    public void setRestartDelay(int restartDelay) {
        this.restartDelay = restartDelay;
    }
    
    public int getMaxRestarts() {
        return maxRestarts;
    }
    
    public void setMaxRestarts(int maxRestarts) {
        this.maxRestarts = maxRestarts;
    }
    
    public String getLogPath() {
        return logPath;
    }
    
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
