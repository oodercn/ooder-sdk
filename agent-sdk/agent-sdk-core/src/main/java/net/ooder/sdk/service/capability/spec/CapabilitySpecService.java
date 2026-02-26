package net.ooder.sdk.service.capability.spec;

import net.ooder.sdk.api.capability.Capability;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CapabilitySpecService {
    
    CompletableFuture<Capability> registerSpec(Capability spec);
    
    CompletableFuture<Capability> getSpec(String specId);
    
    CompletableFuture<Capability> getSpecByName(String name, String version);
    
    CompletableFuture<List<Capability>> listSpecs(String query);
    
    CompletableFuture<Capability> updateSpec(String specId, Capability spec);
    
    CompletableFuture<Void> deleteSpec(String specId);
    
    CompletableFuture<Boolean> validateSpec(Capability spec);
    
    CompletableFuture<List<Capability>> searchSpecs(String keyword);
}
