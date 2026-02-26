package net.ooder.sdk.api.capability;

import java.util.List;
import java.util.Map;

public interface Capability {
    // Basic info
    String getCapId();
    void setCapId(String capId);
    
    String getName();
    void setName(String name);
    
    String getType();
    void setType(String type);
    
    String getVersion();
    void setVersion(String version);
    
    String getDescription();
    void setDescription(String description);
    
    // Status
    CapabilityStatus getStatus();
    void setStatus(CapabilityStatus status);
    void setStatus(String status);
    
    // Configuration
    Map<String, Object> getConfig();
    void setConfig(Map<String, Object> config);
    
    // Extended properties
    String getCapabilityId();
    void setCapabilityId(String capabilityId);
    
    String getSkillId();
    void setSkillId(String skillId);
    
    CapAddress getAddress();
    void setAddress(CapAddress address);
    
    List<String> getTags();
    void setTags(List<String> tags);
    
    long getRegisteredTime();
    void setRegisteredTime(long registeredTime);
    
    long getLastHeartbeat();
    void setLastHeartbeat(long lastHeartbeat);
    
    boolean isAvailable();
    void setAvailable(boolean available);
}
