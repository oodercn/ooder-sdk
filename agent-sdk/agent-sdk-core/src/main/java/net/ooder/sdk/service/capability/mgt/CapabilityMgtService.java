package net.ooder.sdk.service.capability.mgt;

import net.ooder.sdk.api.capability.Capability;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CapabilityMgtService {
    
    CompletableFuture<Capability> register(Capability capability);
    
    CompletableFuture<Void> unregister(String capabilityId);
    
    CompletableFuture<Capability> getCapability(String capabilityId);
    
    CompletableFuture<List<Capability>> listCapabilities(String query);
    
    CompletableFuture<Void> enableCapability(String capabilityId);
    
    CompletableFuture<Void> disableCapability(String capabilityId);
    
    CompletableFuture<Capability> updateCapability(String capabilityId, Capability capability);
    
    CompletableFuture<String> getVersion(String capabilityId, String version);
    
    CompletableFuture<List<String>> listVersions(String capabilityId);
    
    CompletableFuture<Void> rollbackVersion(String capabilityId, String version);
}
