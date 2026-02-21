package net.ooder.annotation;

/**
 * AI Agent运行状态枚举
 * 用于表示Agent在生命周期中的不同状态
 */
public enum AgentStatus {
    /** 初始化中 - Agent正在进行初始化 */
    INITIALIZING,
    
    /** 运行中 - Agent正常运行 */
    RUNNING,
    
    /** 暂停 - Agent暂时停止服务 */
    PAUSED,
    
    /** 停止 - Agent已停止运行 */
    STOPPED,
    
    /** 错误 - Agent发生错误 */
    ERROR
}