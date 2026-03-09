package net.ooder.sdk.agent.llm.impl;

import net.ooder.sdk.agent.llm.LlmDiscoveryApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LlmDiscoveryApi 实现类（简化版）
 */
public class LlmDiscoveryApiImpl implements LlmDiscoveryApi {

    private final Map<String, LlmProviderInfo> providers = new ConcurrentHashMap<>();
    private final Map<String, LlmEndpoint> endpoints = new ConcurrentHashMap<>();
    private final Map<String, EndpointHealth> healthStatus = new ConcurrentHashMap<>();

    public LlmDiscoveryApiImpl() {
        // 初始化一些默认的Provider
        initDefaultProviders();
    }

    private void initDefaultProviders() {
        LlmProviderInfo openai = new LlmProviderInfo();
        openai.setId("openai");
        openai.setName("OpenAI");
        openai.setType("cloud");
        openai.setVersion("v1");
        openai.setSupportedModels(Arrays.asList("gpt-4", "gpt-3.5-turbo"));
        providers.put("openai", openai);

        LlmEndpoint openaiEndpoint = new LlmEndpoint();
        openaiEndpoint.setId("openai_endpoint_1");
        openaiEndpoint.setProviderId("openai");
        openaiEndpoint.setUrl("https://api.openai.com/v1");
        openaiEndpoint.setProtocol("https");
        openaiEndpoint.setPriority(1);
        endpoints.put("openai_endpoint_1", openaiEndpoint);
    }

    @Override
    public CompletableFuture<List<LlmProviderInfo>> discoverProviders() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>(providers.values());
        });
    }

    @Override
    public CompletableFuture<LlmEndpoint> getProviderEndpoint(String providerId) {
        return CompletableFuture.supplyAsync(() -> {
            // 返回该Provider的第一个端点
            for (LlmEndpoint endpoint : endpoints.values()) {
                if (endpoint.getProviderId().equals(providerId)) {
                    return endpoint;
                }
            }
            // 如果没有找到端点，抛出异常而不是返回null
            throw new IllegalArgumentException("No endpoint found for provider: " + providerId);
        });
    }

    @Override
    public CompletableFuture<LlmEndpoint> selectBestEndpoint(String providerId, EndpointSelectionCriteria criteria) {
        return CompletableFuture.supplyAsync(() -> {
            // 简化实现：返回优先级最高的端点
            LlmEndpoint bestEndpoint = null;
            int bestPriority = Integer.MAX_VALUE;
            
            for (LlmEndpoint endpoint : endpoints.values()) {
                if (endpoint.getProviderId().equals(providerId) && endpoint.getPriority() < bestPriority) {
                    bestPriority = endpoint.getPriority();
                    bestEndpoint = endpoint;
                }
            }
            return bestEndpoint;
        });
    }

    @Override
    public CompletableFuture<EndpointHealth> checkEndpointHealth(String endpointId) {
        return CompletableFuture.supplyAsync(() -> {
            EndpointHealth health = healthStatus.get(endpointId);
            if (health == null) {
                health = new EndpointHealth();
                health.setEndpointId(endpointId);
                health.setStatus("HEALTHY");
                health.setLatency(100);
                health.setAvailability(0.99);
                health.setLastChecked(System.currentTimeMillis());
            }
            return health;
        });
    }

    @Override
    public CompletableFuture<List<String>> getProviderCapabilities(String providerId) {
        return CompletableFuture.supplyAsync(() -> {
            LlmProviderInfo provider = providers.get(providerId);
            if (provider != null) {
                return provider.getSupportedModels();
            }
            return new ArrayList<>();
        });
    }
}
