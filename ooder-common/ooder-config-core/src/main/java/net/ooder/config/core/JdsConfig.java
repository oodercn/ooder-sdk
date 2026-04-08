package net.ooder.config.core;

import java.io.Serializable;

public class JdsConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String home;
    private String configName;
    
    public JdsConfig() {
    }
    
    public String getHome() {
        return home;
    }
    
    public void setHome(String home) {
        this.home = home;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public String getValue(String key) {
        switch (key) {
            case "home":
                return home;
            case "configName":
                return configName;
            default:
                return null;
        }
    }
}
