package net.ooder.infra.driver.provider;

import java.util.Map;
import java.util.Set;

/**
 * 能力提供者接口
 * 统一的能力暴露接口
 */
public interface CapabilityProvider {
    
    /**
     * 获取提供者ID
     * @return 提供者ID
     */
    String getProviderId();
    
    /**
     * 获取能力ID列表
     * @return 能力ID集合
     */
    Set<String> getCapabilityIds();
    
    /**
     * 调用能力
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 结果
     */
    Object invoke(String capabilityId, Map<String, Object> params);
    
    /**
     * 是否支持发现能力
     * @return 是否支持
     */
    default boolean supportsDiscovery() {
        return false;
    }
    
    /**
     * 是否支持可视化
     * @return 是否支持
     */
    default boolean supportsVisualization() {
        return false;
    }
    
    /**
     * 是否支持协议
     * @return 是否支持
     */
    default boolean supportsProtocol() {
        return false;
    }
}
