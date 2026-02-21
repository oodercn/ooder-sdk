package net.ooder.sdk.nexus.resource.model;

import java.util.List;
import java.util.Map;

public class SkillInfo {
    
    private String skillId;
    private String skillName;
    private String version;
    private String description;
    private SkillStatus status;
    private long installedAt;
    private long lastUsedAt;
    private int useCount;
    private List<String> capabilities;
    
    private Map<String, InterfaceLocation> interfaces;
    private FallbackConfig fallback;
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public SkillStatus getStatus() { return status; }
    public void setStatus(SkillStatus status) { this.status = status; }
    
    public long getInstalledAt() { return installedAt; }
    public void setInstalledAt(long installedAt) { this.installedAt = installedAt; }
    
    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    
    public int getUseCount() { return useCount; }
    public void setUseCount(int useCount) { this.useCount = useCount; }
    
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    
    private String handler;
    
    public String getHandler() { return handler; }
    public void setHandler(String handler) { this.handler = handler; }
    
    public Map<String, InterfaceLocation> getInterfaces() { return interfaces; }
    public void setInterfaces(Map<String, InterfaceLocation> interfaces) { this.interfaces = interfaces; }
    
    public FallbackConfig getFallback() { return fallback; }
    public void setFallback(FallbackConfig fallback) { this.fallback = fallback; }
    
    public static class InterfaceLocation {
        private String type;
        private String location;
        private String path;
        private String url;
        private String checksum;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
    }
    
    public static class FallbackConfig {
        private boolean enabled;
        private String implementation;
        private List<String> limitations;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getImplementation() { return implementation; }
        public void setImplementation(String implementation) { this.implementation = implementation; }
        
        public List<String> getLimitations() { return limitations; }
        public void setLimitations(List<String> limitations) { this.limitations = limitations; }
    }
}
