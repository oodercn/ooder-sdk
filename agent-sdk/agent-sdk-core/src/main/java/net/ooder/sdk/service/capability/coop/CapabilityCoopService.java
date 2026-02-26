package net.ooder.sdk.service.capability.coop;

import net.ooder.sdk.api.capability.Capability;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CapabilityCoopService {
    
    CompletableFuture<String> createOrchestration(Map<String, Object> config);
    
    CompletableFuture<Void> deleteOrchestration(String orchestrationId);
    
    CompletableFuture<Map<String, Object>> getOrchestration(String orchestrationId);
    
    CompletableFuture<List<Map<String, Object>>> listOrchestrations();
    
    CompletableFuture<Map<String, Object>> executeOrchestration(String orchestrationId, Map<String, Object> input);
    
    CompletableFuture<String> createSceneGroup(Map<String, Object> config);
    
    CompletableFuture<Void> deleteSceneGroup(String sceneGroupId);
    
    CompletableFuture<Map<String, Object>> getSceneGroup(String sceneGroupId);
    
    CompletableFuture<List<Map<String, Object>>> listSceneGroups();
    
    CompletableFuture<String> createChain(Map<String, Object> config);
    
    CompletableFuture<Void> deleteChain(String chainId);
    
    CompletableFuture<Map<String, Object>> executeChain(String chainId, Map<String, Object> input);
}
