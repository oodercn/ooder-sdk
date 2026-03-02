package net.ooder.scene.monitor;

import net.ooder.sdk.api.connection.ConnectionTestService;

import java.util.concurrent.CompletableFuture;

/**
 * 场景监控接口
 * 统一暴露场景监控相关功能
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneMonitor {
    
    /**
     * 获取连接测试服务
     * @return 连接测试服务
     */
    ConnectionTestService getConnectionTestService();
    
    /**
     * 获取性能监控器
     * @return 性能监控器
     */
    PerformanceMonitor getPerformanceMonitor();
    
    /**
     * 获取流程管理器
     * @return 流程管理器
     */
    SceneFlowManager getFlowManager();
    
    /**
     * 获取配置管理器
     * @return 配置管理器
     */
    SceneConfigManager getConfigManager();
    
    /**
     * 获取事件管理器
     * @return 事件管理器
     */
    SceneEventManager getEventManager();
    
    /**
     * 获取日志管理器
     * @return 日志管理器
     */
    SceneLogManager getLogManager();
    
    /**
     * 获取服务健康监控器
     * @return 服务健康监控器
     */
    ServiceHealthMonitor getServiceHealthMonitor();
    
    /**
     * 获取能力状态监控器
     * @return 能力状态监控器
     */
    CapabilityStatusMonitor getCapabilityStatusMonitor();
}
