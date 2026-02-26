package net.ooder.sdk.service.capability;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.service.capability.dist.CapabilityDistService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class CapabilityDistServiceImpl implements CapabilityDistService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityDistServiceImpl.class);
    
    private final Map<String, String> distStatuses = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public CompletableFuture<Boolean> distribute(Capability capability, List<String> targetNodes) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Distributing capability: {} to {} nodes", capability.getCapabilityId(), targetNodes.size());
            String distId = "dist-" + UUID.randomUUID().toString().substring(0, 8);
            distStatuses.put(distId, "COMPLETED");
            log.info("Distribution completed: {}", distId);
            return true;
        }, executor);
    }
    
    @Override
    public CompletableFuture<String> getDistStatus(String distId) {
        return CompletableFuture.supplyAsync(() -> distStatuses.getOrDefault(distId, "UNKNOWN"), executor);
    }
    
    @Override
    public CompletableFuture<Void> cancelDist(String distId) {
        return CompletableFuture.runAsync(() -> {
            distStatuses.put(distId, "CANCELLED");
            log.info("Distribution cancelled: {}", distId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<String>> listPendingDists() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> pending = new ArrayList<>();
            for (Map.Entry<String, String> entry : distStatuses.entrySet()) {
                if ("PENDING".equals(entry.getValue()) || "RUNNING".equals(entry.getValue())) {
                    pending.add(entry.getKey());
                }
            }
            return pending;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> confirmReceipt(String distId, String nodeId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Receipt confirmed: distId={}, nodeId={}", distId, nodeId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<String>> getDistTargets(String specId) {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(), executor);
    }
}
