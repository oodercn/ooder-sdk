package net.ooder.scene.config;

import java.util.Map;

/**
 * Skill 运行时状态
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillRuntimeStatus {
    
    private String skillId;
    private String status;
    private long startTime;
    private long uptime;
    private Map<String, Object> metrics;
    private String errorMessage;
    
    public SkillRuntimeStatus() {}
    
    public SkillRuntimeStatus(String skillId, String status) {
        this.skillId = skillId;
        this.status = status;
    }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public boolean isRunning() {
        return "running".equalsIgnoreCase(status);
    }
}
