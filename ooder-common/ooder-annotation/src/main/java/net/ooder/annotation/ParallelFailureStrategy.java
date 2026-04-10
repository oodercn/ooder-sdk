package net.ooder.annotation;

/**
 * 并行失败策略枚举
 */
public enum ParallelFailureStrategy {
    ABORT_ON_FIRST_FAILURE,  // 第一个步骤失败即终止所有
    CONTINUE_ON_ERROR,       // 忽略失败继续执行
    ROLLBACK_ALL,            // 失败时回滚所有已执行步骤
    COMPENSATE_ON_FAILURE    // 失败时执行补偿操作
}