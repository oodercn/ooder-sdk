package net.ooder.sdk.agent.capability.impl;

import net.ooder.sdk.agent.capability.CapabilityInvocationApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CapabilityInvocationApi 实现类（简化版）
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CapabilityInvocationApiImpl implements CapabilityInvocationApi {

    private final Map<String, Map<String, CapabilityInfo>> sceneCapabilities = new ConcurrentHashMap<>();
    private final Map<String, CapabilityStatus> capabilityStatuses = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<List<CapabilityInfo>> discoverCapabilities(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, CapabilityInfo> capabilities = sceneCapabilities.get(sceneId);
            return capabilities != null ? new ArrayList<>(capabilities.values()) : new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<CapabilityResult> invokeCapability(String sceneId, String capabilityId, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            CapabilityResult result = new CapabilityResult();
            result.setSuccess(true);
            result.setData("Capability " + capabilityId + " invoked with params: " + params);
            result.setExecutionTime(100);
            return result;
        });
    }

    @Override
    public CompletableFuture<String> invokeCapabilityAsync(String sceneId, String capabilityId, Map<String, Object> params, CapabilityCallback callback) {
        return CompletableFuture.supplyAsync(() -> {
            String taskId = "task_" + System.currentTimeMillis();
            // 异步执行
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                    CapabilityResult result = new CapabilityResult();
                    result.setSuccess(true);
                    result.setData("Async result for " + capabilityId);
                    callback.onComplete(result);
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            });
            return taskId;
        });
    }

    @Override
    public CompletableFuture<List<CapabilityResult>> invokeCapabilitiesBatch(String sceneId, List<CapabilityInvocation> invocations) {
        return CompletableFuture.supplyAsync(() -> {
            List<CapabilityResult> results = new ArrayList<>();
            for (CapabilityInvocation invocation : invocations) {
                CapabilityResult result = new CapabilityResult();
                result.setSuccess(true);
                result.setData("Batch result for " + invocation.getCapabilityId());
                results.add(result);
            }
            return results;
        });
    }

    @Override
    public CompletableFuture<CapabilityStatus> getCapabilityStatus(String sceneId, String capabilityId) {
        return CompletableFuture.supplyAsync(() -> {
            String key = sceneId + ":" + capabilityId;
            return capabilityStatuses.getOrDefault(key, createDefaultStatus(capabilityId));
        });
    }

    @Override
    public CompletableFuture<Boolean> registerCapability(String sceneId, CapabilityInfo capability) {
        return CompletableFuture.supplyAsync(() -> {
            sceneCapabilities.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>())
                .put(capability.getId(), capability);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> unregisterCapability(String sceneId, String capabilityId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, CapabilityInfo> capabilities = sceneCapabilities.get(sceneId);
            if (capabilities != null) {
                capabilities.remove(capabilityId);
            }
            return true;
        });
    }

    private CapabilityStatus createDefaultStatus(String capabilityId) {
        CapabilityStatus status = new CapabilityStatus();
        status.setId(capabilityId);
        status.setState("IDLE");
        status.setActiveInvocations(0);
        status.setLastUsed(System.currentTimeMillis());
        return status;
    }
}
