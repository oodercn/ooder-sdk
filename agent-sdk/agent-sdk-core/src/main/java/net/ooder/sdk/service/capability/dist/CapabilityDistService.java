package net.ooder.sdk.service.capability.dist;

import net.ooder.sdk.api.capability.Capability;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CapabilityDistService {
    
    CompletableFuture<Boolean> distribute(Capability capability, List<String> targetNodes);
    
    CompletableFuture<String> getDistStatus(String distId);
    
    CompletableFuture<Void> cancelDist(String distId);
    
    CompletableFuture<List<String>> listPendingDists();
    
    CompletableFuture<Void> confirmReceipt(String distId, String nodeId);
    
    CompletableFuture<List<String>> getDistTargets(String specId);
}
