package net.ooder.config.scene.capability;

import java.io.Serializable;
import java.util.Map;

public interface Capability extends Serializable {
    
    String getCapabilityId();
    
    String getCapabilityName();
    
    CapabilityType getType();
    
    CapabilityResult execute(CapabilityContext context);
    
    boolean validate(CapabilityRequest request);
    
    CapabilityMetadata getMetadata();
    
    public enum CapabilityType {
        QUERY,
        COMMAND,
        EVENT,
        SYNC,
        ADMIN
    }
}
