package net.ooder.sdk.api.capability;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 能力注册表
 * 用于动态注册和管理能力提供者
 */
public class CapabilityRegistry<T> {

    private final Map<String, CapabilityProvider<T>> providers = new ConcurrentHashMap<>();
    private final String capabilityType;

    public CapabilityRegistry(String capabilityType) {
        this.capabilityType = capabilityType;
    }

    /**
     * 注册能力提供者
     */
    public void registerProvider(CapabilityProvider<T> provider) {
        providers.put(provider.getProviderType(), provider);
    }

    /**
     * 注销能力提供者
     */
    public void unregisterProvider(String providerType) {
        providers.remove(providerType);
    }

    /**
     * 获取能力提供者
     */
    public CapabilityProvider<T> getProvider(String providerType) {
        return providers.get(providerType);
    }

    /**
     * 创建能力配置
     */
    public T createCapabilities(String providerType) {
        CapabilityProvider<T> provider = providers.get(providerType);
        if (provider != null) {
            return provider.createCapabilities();
        }
        return null;
    }

    /**
     * 检查是否支持该类型
     */
    public boolean supports(String providerType) {
        return providers.containsKey(providerType);
    }

    /**
     * 获取所有提供者类型
     */
    public Set<String> getProviderTypes() {
        return Collections.unmodifiableSet(providers.keySet());
    }

    /**
     * 获取能力类型
     */
    public String getCapabilityType() {
        return capabilityType;
    }
}
