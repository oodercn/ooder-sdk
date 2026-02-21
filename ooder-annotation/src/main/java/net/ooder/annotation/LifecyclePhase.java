package net.ooder.annotation;

/**
 * 定义AI Agent的生命周期阶段枚举
 * 用于{@link AgentLifecycle}注解指定生命周期回调方法的执行阶段
 */
public enum LifecyclePhase {
    /**
     * 初始化阶段 - Agent实例创建后立即执行
     * 用于资源预加载、配置初始化等操作
     */
    INIT,
    
    /**
     * 启动阶段 - Agent初始化完成后执行
     * 用于建立连接、启动后台任务等操作
     */
    START,
    
    /**
     * 销毁阶段 - Agent实例销毁前执行
     * 用于资源释放、连接关闭等清理操作
     */
    DESTROY
}