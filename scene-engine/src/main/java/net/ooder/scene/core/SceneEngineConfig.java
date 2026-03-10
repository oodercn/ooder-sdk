package net.ooder.scene.core;

import java.util.Map;

/**
 * SceneEngine 配置
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class SceneEngineConfig {

    private String name = "scene-engine";
    private String version = "2.3.1";
    private String dataPath = "./data";
    private boolean cacheEnabled = true;
    private Map<String, Object> securityConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public Map<String, Object> getSecurityConfig() {
        return securityConfig;
    }

    public void setSecurityConfig(Map<String, Object> securityConfig) {
        this.securityConfig = securityConfig;
    }
}
