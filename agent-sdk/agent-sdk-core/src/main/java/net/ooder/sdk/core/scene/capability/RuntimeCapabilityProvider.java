package net.ooder.sdk.core.scene.capability;

import java.util.List;
import java.util.Map;

public interface RuntimeCapabilityProvider {
    
    String getProviderId();
    
    String getProviderName();
    
    List<RuntimeCapability> getCapabilities();
    
    RuntimeCapability getCapability(String capabilityId);
    
    boolean hasCapability(String capabilityId);
    
    Object invoke(String capabilityId, Map<String, Object> params) throws RuntimeCapabilityException;
    
    void start();
    
    void stop();
    
    boolean isRunning();
}
