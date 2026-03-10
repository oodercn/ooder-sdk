package net.ooder.skills.config;

import java.util.List;
import java.util.Map;

/**
 * MainFirst 统一配置类
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class MainFirstConfiguration {
    
    private boolean enabled = true;
    private List<SelfCheckConfiguration> selfChecks;
    private List<SelfStartConfiguration> selfStarts;
    private SelfDriveConfiguration selfDrive;
    private List<CollaborationStartConfiguration> collaborationStarts;
    private Map<String, Object> properties;
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<SelfCheckConfiguration> getSelfChecks() { return selfChecks; }
    public void setSelfChecks(List<SelfCheckConfiguration> selfChecks) { this.selfChecks = selfChecks; }
    
    public List<SelfStartConfiguration> getSelfStarts() { return selfStarts; }
    public void setSelfStarts(List<SelfStartConfiguration> selfStarts) { this.selfStarts = selfStarts; }
    
    public SelfDriveConfiguration getSelfDrive() { return selfDrive; }
    public void setSelfDrive(SelfDriveConfiguration selfDrive) { this.selfDrive = selfDrive; }
    
    public List<CollaborationStartConfiguration> getCollaborationStarts() { return collaborationStarts; }
    public void setCollaborationStarts(List<CollaborationStartConfiguration> collaborationStarts) { this.collaborationStarts = collaborationStarts; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    
    /**
     * 自检配置
     */
    public static class SelfCheckConfiguration {
        private String name;
        private String type;
        private Map<String, Object> params;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 自启动配置
     */
    public static class SelfStartConfiguration {
        private String name;
        private String condition;
        private int priority;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
    
    /**
     * 自驱动配置
     */
    public static class SelfDriveConfiguration {
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
    
    /**
     * 协作启动配置
     */
    public static class CollaborationStartConfiguration {
        private String targetId;
        private String protocol;
        private Map<String, Object> params;
        
        public String getTargetId() { return targetId; }
        public void setTargetId(String targetId) { this.targetId = targetId; }
        
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
}
