package net.ooder.sdk.orchestration;

// Story 类已从外部 llm-sdk 导入
import net.ooder.sdk.story.UserStory;
import net.ooder.sdk.story.StoryStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class CapabilityRouterImpl<P, D> implements CapabilityRouter<P, D> {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityRouterImpl.class);
    
    private final Map<String, CapabilityExecutor<P, D>> executors = new ConcurrentHashMap<>();
    private final Map<String, CapabilityInfo> capabilities = new ConcurrentHashMap<>();
    private final Map<String, List<String>> domainIndex = new ConcurrentHashMap<>();
    
    @Override
    public CompletableFuture<RouteResult<D>> route(String capabilityId, Map<String, P> params) {
        return route(capabilityId, params, new RouteOptions());
    }
    
    @Override
    public CompletableFuture<RouteResult<D>> route(String capabilityId, Map<String, P> params, RouteOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            
            CapabilityExecutor<P, D> executor = executors.get(capabilityId);
            if (executor == null) {
                log.warn("No executor found for capability: {}", capabilityId);
                RouteResult<D> result = RouteResult.failure(capabilityId, "No executor found");
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                return result;
            }
            
            if (!executor.isAvailable()) {
                log.warn("Executor not available for capability: {}", capabilityId);
                RouteResult<D> result = RouteResult.failure(capabilityId, "Executor not available");
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                return result;
            }
            
            try {
                RouteResult<D> result = executor.execute(params).join();
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                result.setExecutorId(executor.getExecutorId());
                
                log.debug("Capability {} executed in {}ms", capabilityId, result.getExecutionTime());
                return result;
            } catch (Exception e) {
                log.error("Capability execution failed: {}", capabilityId, e);
                RouteResult<D> result = RouteResult.failure(capabilityId, "Execution failed: " + e.getMessage());
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                return result;
            }
        });
    }

    @Override
    public boolean canRoute(String capabilityId) {
        CapabilityExecutor<P, D> executor = executors.get(capabilityId);
        return executor != null && executor.isAvailable();
    }
    
    @Override
    public List<String> findCapabilities(String domain, String intent) {
        List<String> results = new ArrayList<>();
        
        String domainKey = domain != null ? domain.toLowerCase() : "default";
        List<String> domainCaps = domainIndex.get(domainKey);
        
        if (domainCaps != null) {
            if (intent != null && !intent.isEmpty()) {
                for (String capId : domainCaps) {
                    CapabilityInfo info = capabilities.get(capId);
                    if (info != null && info.getTags() != null) {
                        if (info.getTags().stream().anyMatch(t -> t.toLowerCase().contains(intent.toLowerCase()))) {
                            results.add(capId);
                        }
                    }
                }
            } else {
                results.addAll(domainCaps);
            }
        }
        
        return results;
    }
    
    @Override
    public CapabilityInfo getCapabilityInfo(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    @Override
    public void registerExecutor(String capabilityId, CapabilityExecutor<P, D> executor) {
        if (capabilityId == null || executor == null) {
            throw new IllegalArgumentException("Capability ID and executor cannot be null");
        }

        executors.put(capabilityId, executor);

        CapabilityInfo info = new CapabilityInfo();
        info.setCapabilityId(capabilityId);
        info.setName(capabilityId);
        capabilities.put(capabilityId, info);

        log.info("Executor registered for capability: {}", capabilityId);
    }

    public void registerCapability(CapabilityInfo info, CapabilityExecutor<P, D> executor) {
        if (info == null || info.getCapabilityId() == null) {
            throw new IllegalArgumentException("Capability info and ID cannot be null");
        }
        
        capabilities.put(info.getCapabilityId(), info);
        
        if (executor != null) {
            executors.put(info.getCapabilityId(), executor);
        }
        
        if (info.getDomain() != null) {
            domainIndex.computeIfAbsent(info.getDomain().toLowerCase(), k -> new CopyOnWriteArrayList<>())
                .add(info.getCapabilityId());
        }
        
        log.info("Capability registered: {} in domain {}", info.getCapabilityId(), info.getDomain());
    }
    
    @Override
    public void unregisterExecutor(String capabilityId) {
        executors.remove(capabilityId);
        log.info("Executor unregistered for capability: {}", capabilityId);
    }
}
