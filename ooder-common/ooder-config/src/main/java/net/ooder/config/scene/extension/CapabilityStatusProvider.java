package net.ooder.config.scene.extension;

import java.util.Map;

public interface CapabilityStatusProvider {
    
    CapabilityStatus getCapabilityStatus(String capabilityCode);
    
    Map<String, CapabilityStatus> getAllCapabilityStatus();
    
    void updateCapabilityStatus(String capabilityCode, CapabilityStatus status);
    
    void resetCapabilityStatus(String capabilityCode);
}
