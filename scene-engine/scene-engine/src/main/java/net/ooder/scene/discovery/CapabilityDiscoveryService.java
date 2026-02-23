package net.ooder.scene.discovery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CapabilityDiscoveryService {
    CompletableFuture<SyncResult> syncAllIndexes();
    CompletableFuture<List<DiscoveredItem>> listScenes(String category);
    CompletableFuture<List<DiscoveredItem>> searchScenes(String query);
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);
    CompletableFuture<List<DiscoveredItem>> getAvailableSkills(String sceneId);
    CompletableFuture<List<DiscoveredItem>> listCapabilities(String category);
    CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query);
    CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId);
    CompletableFuture<List<DiscoveredItem>> getAvailableSkillsForCapability(String capId);
    void registerProvider(DiscoveryProvider provider);
    void unregisterProvider(String providerName);
    void setDiscoveryScope(DiscoveryScope scope);
    DiscoveryScope getDiscoveryScope();
}
