package net.ooder.sdk.service.capability;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.service.capability.spec.CapabilitySpecService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class CapabilitySpecServiceImpl implements CapabilitySpecService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilitySpecServiceImpl.class);
    
    private final Map<String, Capability> specs = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public CompletableFuture<Capability> registerSpec(Capability spec) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Registering spec: {}", spec.getName());
            spec.setCapabilityId("spec-" + UUID.randomUUID().toString().substring(0, 8));
            specs.put(spec.getCapabilityId(), spec);
            log.info("Spec registered: {}", spec.getCapabilityId());
            return spec;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Capability> getSpec(String specId) {
        return CompletableFuture.supplyAsync(() -> specs.get(specId), executor);
    }
    
    @Override
    public CompletableFuture<Capability> getSpecByName(String name, String version) {
        return CompletableFuture.supplyAsync(() -> {
            for (Capability spec : specs.values()) {
                if (spec.getName().equals(name) && spec.getVersion().equals(version)) {
                    return spec;
                }
            }
            return null;
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Capability>> listSpecs(String query) {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(specs.values()), executor);
    }
    
    @Override
    public CompletableFuture<Capability> updateSpec(String specId, Capability spec) {
        return CompletableFuture.supplyAsync(() -> {
            Capability existing = specs.get(specId);
            if (existing != null) {
                spec.setCapabilityId(specId);
                specs.put(specId, spec);
                log.info("Spec updated: {}", specId);
            }
            return spec;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> deleteSpec(String specId) {
        return CompletableFuture.runAsync(() -> {
            specs.remove(specId);
            log.info("Spec deleted: {}", specId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Boolean> validateSpec(Capability spec) {
        return CompletableFuture.supplyAsync(() -> {
            return spec.getName() != null && !spec.getName().isEmpty() &&
                   spec.getVersion() != null && !spec.getVersion().isEmpty();
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Capability>> searchSpecs(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            List<Capability> result = new ArrayList<>();
            for (Capability spec : specs.values()) {
                if (spec.getName().contains(keyword) || 
                    (spec.getDescription() != null && spec.getDescription().contains(keyword))) {
                    result.add(spec);
                }
            }
            return result;
        }, executor);
    }
}
