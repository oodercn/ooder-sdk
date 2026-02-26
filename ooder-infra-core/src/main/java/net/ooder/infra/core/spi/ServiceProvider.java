package net.ooder.infra.core.spi;

/**
 * 服务提供者接口
 * SPI 机制基础接口
 */
public interface ServiceProvider {
    
    /**
     * 获取服务类型
     * @return 服务类型标识
     */
    String getServiceType();
    
    /**
     * 获取服务优先级
     * @return 优先级，数值越小优先级越高
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * 是否可用
     * @return 是否可用
     */
    default boolean isAvailable() {
        return true;
    }
}
