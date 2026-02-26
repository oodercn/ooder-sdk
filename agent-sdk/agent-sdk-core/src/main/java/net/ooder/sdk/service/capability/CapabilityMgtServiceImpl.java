package net.ooder.sdk.service.capability;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.capability.CapabilityStatus;
import net.ooder.sdk.service.capability.mgt.CapabilityMgtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class CapabilityMgtServiceImpl implements CapabilityMgtService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityMgtServiceImpl.class);
    
    private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public CompletableFuture<Capability> register(Capability capability) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Registering capability: {}", capability.getName());
            capability.setCapabilityId("cap-" + UUID.randomUUID().toString().substring(0, 8));
            capability.setStatus(CapabilityStatus.REGISTERED);
            capability.setRegisteredTime(System.currentTimeMillis());
            capabilities.put(capability.getCapabilityId(), capability);
            log.info("Capability registered: {}", capability.getCapabilityId());
            return capability;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> unregister(String capabilityId) {
        return CompletableFuture.runAsync(() -> {
            capabilities.remove(capabilityId);
            log.info("Capability unregistered: {}", capabilityId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Capability> getCapability(String capabilityId) {
        return CompletableFuture.supplyAsync(() -> capabilities.get(capabilityId), executor);
    }
    
    @Override
    public CompletableFuture<List<Capability>> listCapabilities(String query) {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(capabilities.values()), executor);
    }
    
    @Override
    public CompletableFuture<Void> enableCapability(String capabilityId) {
        return CompletableFuture.runAsync(() -> {
            Capability cap = capabilities.get(capabilityId);
            if (cap != null) {
                cap.setStatus(CapabilityStatus.ENABLED);
                log.info("Capability enabled: {}", capabilityId);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> disableCapability(String capabilityId) {
        return CompletableFuture.runAsync(() -> {
            Capability cap = capabilities.get(capabilityId);
            if (cap != null) {
                cap.setStatus(CapabilityStatus.DISABLED);
                log.info("Capability disabled: {}", capabilityId);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Capability> updateCapability(String capabilityId, Capability capability) {
        return CompletableFuture.supplyAsync(() -> {
            Capability existing = capabilities.get(capabilityId);
            if (existing != null) {
                capability.setCapabilityId(capabilityId);
                capabilities.put(capabilityId, capability);
                log.info("Capability updated: {}", capabilityId);
            }
            return capability;
        }, executor);
    }
    
    @Override
    public CompletableFuture<String> getVersion(String capabilityId, String version) {
        return CompletableFuture.supplyAsync(() -> version, executor);
    }
    
    @Override
    public CompletableFuture<List<String>> listVersions(String capabilityId) {
        return CompletableFuture.supplyAsync(() -> Arrays.asList("1.0.0"), executor);
    }
    
    @Override
    public CompletableFuture<Void> rollbackVersion(String capabilityId, String version) {
        return CompletableFuture.runAsync(() -> {
            log.info("Capability rolled back: {} to version {}", capabilityId, version);
        }, executor);
    }
}
