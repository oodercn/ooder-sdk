package net.ooder.sdk.core.driver;

import net.ooder.sdk.core.InterfaceDefinition;

/**
 * Driver 接口
 * 从 scene-engine 迁移到 agent-sdk，作为核心抽象
 */
public interface Driver {
    
    /**
     * 获取驱动类别
     * @return 类别
     */
    String getCategory();
    
    /**
     * 获取版本
     * @return 版本号
     */
    String getVersion();
    
    /**
     * 初始化驱动
     * @param context 驱动上下文
     */
    void initialize(DriverContext context);
    
    /**
     * 关闭驱动
     */
    void shutdown();
    
    /**
     * 获取 Skill 实例
     * @return Skill
     */
    Object getSkill();
    
    /**
     * 获取能力定义
     * @return 能力
     */
    Object getCapabilities();
    
    /**
     * 获取降级实现
     * @return Fallback
     */
    Object getFallback();
    
    /**
     * 是否有降级实现
     * @return 是否有 Fallback
     */
    boolean hasFallback();
    
    /**
     * 获取接口定义
     * @return 接口定义
     */
    InterfaceDefinition getInterfaceDefinition();
    
    /**
     * 获取健康状态
     * @return 健康状态
     */
    HealthStatus getHealthStatus();
}
