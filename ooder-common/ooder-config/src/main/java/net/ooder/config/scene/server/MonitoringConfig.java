package net.ooder.config.scene.server;

import java.io.Serializable;

public class MonitoringConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = true;
    private long metricsInterval = 60000L;
    private boolean alertEnabled = true;
    private String alertEndpoint;
    
    public MonitoringConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public long getMetricsInterval() {
        return metricsInterval;
    }
    
    public void setMetricsInterval(long metricsInterval) {
        this.metricsInterval = metricsInterval;
    }
    
    public boolean isAlertEnabled() {
        return alertEnabled;
    }
    
    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }
    
    public String getAlertEndpoint() {
        return alertEndpoint;
    }
    
    public void setAlertEndpoint(String alertEndpoint) {
        this.alertEndpoint = alertEndpoint;
    }
}
