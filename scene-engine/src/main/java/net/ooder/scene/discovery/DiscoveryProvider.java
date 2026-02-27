package net.ooder.scene.discovery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DiscoveryProvider {
    String getProviderName();
    void initialize(DiscoveryConfig config);
    void start();
    void stop();
    boolean isRunning();
    CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query);
    int getPriority();
    boolean isApplicable(DiscoveryScope scope);
}
