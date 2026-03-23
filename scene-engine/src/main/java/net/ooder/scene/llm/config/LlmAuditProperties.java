package net.ooder.scene.llm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LLM 审计配置属性
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@ConfigurationProperties(prefix = "scene.engine.llm.audit")
public class LlmAuditProperties {
    
    private boolean enabled = true;
    
    private String dataPath = "data/llm-audit";
    
    private int maxLogSize = 10000;
    
    private boolean statsEnabled = true;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getDataPath() {
        return dataPath;
    }
    
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    
    public int getMaxLogSize() {
        return maxLogSize;
    }
    
    public void setMaxLogSize(int maxLogSize) {
        this.maxLogSize = maxLogSize;
    }
    
    public boolean isStatsEnabled() {
        return statsEnabled;
    }
    
    public void setStatsEnabled(boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
    }
}
