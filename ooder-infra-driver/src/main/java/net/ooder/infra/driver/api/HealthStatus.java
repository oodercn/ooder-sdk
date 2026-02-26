package net.ooder.infra.driver.api;

/**
 * 健康状态
 */
public enum HealthStatus {
    
    /**
     * 健康
     */
    HEALTHY,
    
    /**
     * 不健康
     */
    UNHEALTHY,
    
    /**
     * 未知
     */
    UNKNOWN,
    
    /**
     * 正在启动
     */
    STARTING,
    
    /**
     * 正在停止
     */
    STOPPING
}
