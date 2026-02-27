package net.ooder.scene.core;

import net.ooder.scene.provider.BaseProvider;

/**
 * Provider注册中心
 *
 * <p>管理所有Provider的注册、获取和生命周期</p>
 */
public interface ProviderRegistry {

    /**
     * 注册Provider
     *
     * @param providerType Provider类型
     * @param provider Provider实例
     * @param <T> Provider类型
     */
    <T extends BaseProvider> void register(Class<T> providerType, T provider);

    /**
     * 获取Provider
     *
     * @param providerType Provider类型
     * @param <T> Provider类型
     * @return Provider实例，如果不存在返回null
     */
    <T extends BaseProvider> T getProvider(Class<T> providerType);

    /**
     * 检查Provider是否存在
     *
     * @param providerType Provider类型
     * @return true存在，false不存在
     */
    boolean hasProvider(Class<? extends BaseProvider> providerType);

    /**
     * 获取所有已注册的Provider类型
     *
     * @return Provider类型集合
     */
    java.util.Set<Class<? extends BaseProvider>> getProviderTypes();

    /**
     * 启动所有Provider
     */
    void startAll();

    /**
     * 停止所有Provider
     */
    void stopAll();

    /**
     * 获取Provider数量
     *
     * @return Provider数量
     */
    int getProviderCount();
}
