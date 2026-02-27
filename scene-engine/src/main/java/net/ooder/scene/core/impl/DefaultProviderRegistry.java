package net.ooder.scene.core.impl;

import net.ooder.scene.core.ProviderRegistry;
import net.ooder.scene.provider.BaseProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provider注册中心默认实现
 */
public class DefaultProviderRegistry implements ProviderRegistry {

    private final Map<Class<? extends BaseProvider>, BaseProvider> providers = new ConcurrentHashMap<>();
    private final Map<Class<? extends BaseProvider>, Integer> priorities = new ConcurrentHashMap<>();

    @Override
    public <T extends BaseProvider> void register(Class<T> providerType, T provider) {
        if (providerType == null || provider == null) {
            throw new IllegalArgumentException("Provider type and instance cannot be null");
        }

        Integer existingPriority = priorities.get(providerType);
        int newPriority = provider.getPriority();

        if (existingPriority == null || newPriority > existingPriority) {
            providers.put(providerType, provider);
            priorities.put(providerType, newPriority);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseProvider> T getProvider(Class<T> providerType) {
        BaseProvider provider = providers.get(providerType);
        if (provider != null && providerType.isInstance(provider)) {
            return (T) provider;
        }
        return null;
    }

    @Override
    public boolean hasProvider(Class<? extends BaseProvider> providerType) {
        return providers.containsKey(providerType);
    }

    @Override
    public Set<Class<? extends BaseProvider>> getProviderTypes() {
        return Collections.unmodifiableSet(providers.keySet());
    }

    @Override
    public void startAll() {
        for (BaseProvider provider : providers.values()) {
            if (provider.isInitialized() && !provider.isRunning()) {
                provider.start();
            }
        }
    }

    @Override
    public void stopAll() {
        for (BaseProvider provider : providers.values()) {
            if (provider.isRunning()) {
                provider.stop();
            }
        }
    }

    @Override
    public int getProviderCount() {
        return providers.size();
    }
}
