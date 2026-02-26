package net.ooder.sdk.api.capability;

/**
 * 能力提供者接口
 * 用于动态注册和创建能力配置
 */
public interface CapabilityProvider<T> {

    /**
     * 获取提供者类型
     */
    String getProviderType();

    /**
     * 获取提供者名称
     */
    String getProviderName();

    /**
     * 创建能力配置
     */
    T createCapabilities();

    /**
     * 判断是否支持该类型
     */
    boolean supports(String providerType);
}
