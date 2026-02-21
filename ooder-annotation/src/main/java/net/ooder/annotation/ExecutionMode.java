package net.ooder.annotation;

/**
 * AI任务执行模式枚举
 */
public enum ExecutionMode {
    /** 同步执行 - 立即返回结果 */
    SYNC,
    /** 异步执行 - 通过回调或轮询获取结果 */
    ASYNC,
    /** 流式执行 - 分批次返回结果 */
    STREAMING,
    /** 批处理模式 - 处理多个任务项 */
    BATCH
}