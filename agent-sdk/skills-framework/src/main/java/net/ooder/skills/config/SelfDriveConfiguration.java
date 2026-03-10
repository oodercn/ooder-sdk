package net.ooder.skills.config;

import java.util.Map;

/**
 * 自驱动统一配置类
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SelfDriveConfiguration {

    private boolean enabled;
    private String mode;
    private Map<String, Object> params;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
