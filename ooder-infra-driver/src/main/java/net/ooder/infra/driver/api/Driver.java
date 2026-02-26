package net.ooder.infra.driver.api;

import net.ooder.api.core.Identifiable;
import net.ooder.api.core.Versioned;

/**
 * 驱动接口
 * 所有基础设施驱动的基础接口
 */
public interface Driver extends Identifiable, Versioned {
    
    /**
     * 初始化驱动
     */
    void initialize();
    
    /**
     * 销毁驱动
     */
    void destroy();
    
    /**
     * 健康检查
     * @return 健康状态
     */
    HealthStatus healthCheck();
    
    /**
     * 是否已初始化
     * @return 是否已初始化
     */
    boolean isInitialized();
    
    /**
     * 获取驱动类型
     * @return 驱动类型
     */
    String getDriverType();
}
