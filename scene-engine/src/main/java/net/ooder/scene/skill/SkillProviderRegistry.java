package net.ooder.scene.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Skill Provider 注册器
 *
 * <p>通过 ServiceLoader 发现并注册 Provider 实现</p>
 */
public class SkillProviderRegistry {

    private static final SkillProviderRegistry INSTANCE = new SkillProviderRegistry();

    private Map<String, LlmProvider> llmProviders = new HashMap<String, LlmProvider>();
    private Map<String, StorageProvider> storageProviders = new HashMap<String, StorageProvider>();
    private Map<String, SchedulerProvider> schedulerProviders = new HashMap<String, SchedulerProvider>();
    private Map<String, HttpClientProvider> httpClientProviders = new HashMap<String, HttpClientProvider>();

    private LlmProvider defaultLlmProvider;
    private StorageProvider defaultStorageProvider;
    private SchedulerProvider defaultSchedulerProvider;
    private HttpClientProvider defaultHttpClientProvider;

    private SkillProviderRegistry() {
        loadProviders();
    }

    public static SkillProviderRegistry getInstance() {
        return INSTANCE;
    }

    private void loadProviders() {
        ServiceLoader<LlmProvider> llmLoader = ServiceLoader.load(LlmProvider.class);
        for (LlmProvider provider : llmLoader) {
            llmProviders.put(provider.getProviderType(), provider);
            if (defaultLlmProvider == null) {
                defaultLlmProvider = provider;
            }
        }

        ServiceLoader<StorageProvider> storageLoader = ServiceLoader.load(StorageProvider.class);
        for (StorageProvider provider : storageLoader) {
            storageProviders.put(provider.getProviderType(), provider);
            if (defaultStorageProvider == null) {
                defaultStorageProvider = provider;
            }
        }

        ServiceLoader<SchedulerProvider> schedulerLoader = ServiceLoader.load(SchedulerProvider.class);
        for (SchedulerProvider provider : schedulerLoader) {
            schedulerProviders.put(provider.getProviderType(), provider);
            if (defaultSchedulerProvider == null) {
                defaultSchedulerProvider = provider;
            }
        }

        ServiceLoader<HttpClientProvider> httpLoader = ServiceLoader.load(HttpClientProvider.class);
        for (HttpClientProvider provider : httpLoader) {
            httpClientProviders.put(provider.getProviderType(), provider);
            if (defaultHttpClientProvider == null) {
                defaultHttpClientProvider = provider;
            }
        }

        if (llmProviders.isEmpty()) {
            defaultLlmProvider = new MockLlmProvider();
            llmProviders.put("mock", defaultLlmProvider);
        }
        if (storageProviders.isEmpty()) {
            defaultStorageProvider = new MockStorageProvider();
            storageProviders.put("mock", defaultStorageProvider);
        }
        if (schedulerProviders.isEmpty()) {
            defaultSchedulerProvider = new MockSchedulerProvider();
            schedulerProviders.put("mock", defaultSchedulerProvider);
        }
        if (httpClientProviders.isEmpty()) {
            defaultHttpClientProvider = new MockHttpClientProvider();
            httpClientProviders.put("mock", defaultHttpClientProvider);
        }
    }

    public LlmProvider getLlmProvider() {
        return defaultLlmProvider;
    }

    public LlmProvider getLlmProvider(String type) {
        return llmProviders.getOrDefault(type, defaultLlmProvider);
    }

    public StorageProvider getStorageProvider() {
        return defaultStorageProvider;
    }

    public StorageProvider getStorageProvider(String type) {
        return storageProviders.getOrDefault(type, defaultStorageProvider);
    }

    public SchedulerProvider getSchedulerProvider() {
        return defaultSchedulerProvider;
    }

    public SchedulerProvider getSchedulerProvider(String type) {
        return schedulerProviders.getOrDefault(type, defaultSchedulerProvider);
    }

    public HttpClientProvider getHttpClientProvider() {
        return defaultHttpClientProvider;
    }

    public HttpClientProvider getHttpClientProvider(String type) {
        return httpClientProviders.getOrDefault(type, defaultHttpClientProvider);
    }

    public List<String> getAvailableLlmProviders() {
        return new ArrayList<String>(llmProviders.keySet());
    }

    public List<String> getAvailableStorageProviders() {
        return new ArrayList<String>(storageProviders.keySet());
    }

    public List<String> getAvailableSchedulerProviders() {
        return new ArrayList<String>(schedulerProviders.keySet());
    }

    public List<String> getAvailableHttpClientProviders() {
        return new ArrayList<String>(httpClientProviders.keySet());
    }

    public void reload() {
        llmProviders.clear();
        storageProviders.clear();
        schedulerProviders.clear();
        httpClientProviders.clear();
        defaultLlmProvider = null;
        defaultStorageProvider = null;
        defaultSchedulerProvider = null;
        defaultHttpClientProvider = null;
        loadProviders();
    }
}
