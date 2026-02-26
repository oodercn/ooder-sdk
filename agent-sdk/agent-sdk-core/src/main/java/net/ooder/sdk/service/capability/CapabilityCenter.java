package net.ooder.sdk.service.capability;

import net.ooder.sdk.service.capability.spec.CapabilitySpecService;
import net.ooder.sdk.service.capability.dist.CapabilityDistService;
import net.ooder.sdk.service.capability.mgt.CapabilityMgtService;
import net.ooder.sdk.service.capability.mon.CapabilityMonService;
import net.ooder.sdk.service.capability.coop.CapabilityCoopService;

public interface CapabilityCenter {
    
    CapabilitySpecService getSpecService();
    
    CapabilityDistService getDistService();
    
    CapabilityMgtService getMgtService();
    
    CapabilityMonService getMonService();
    
    CapabilityCoopService getCoopService();
    
    void initialize();
    
    void shutdown();
    
    boolean isInitialized();
}
