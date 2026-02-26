package net.ooder.sdk.core.scene.capability;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RuntimeCapabilityConsumer {
    
    String getConsumerId();
    
    List<RuntimeCapability> discoverCapabilities(String query);
    
    RuntimeCapability getCapability(String capabilityId);
    
    Object invoke(String capabilityId, Map<String, Object> params) throws RuntimeCapabilityException;
    
    CompletableFuture<Object> invokeAsync(String capabilityId, Map<String, Object> params);
    
    void subscribe(String capabilityId, RuntimeCapabilityListener listener);
    
    void unsubscribe(String capabilityId, RuntimeCapabilityListener listener);
    
    interface RuntimeCapabilityListener {
        void onCapabilityChanged(RuntimeCapability capability);
        void onCapabilityRemoved(String capabilityId);
    }
}
