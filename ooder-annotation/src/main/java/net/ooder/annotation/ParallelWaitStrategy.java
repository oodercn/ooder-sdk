package net.ooder.annotation;

/**
 * 并行等待策略枚举
 */
public enum ParallelWaitStrategy {
    ALL_COMPLETED,   // 等待所有步骤完成
    ANY_COMPLETED,   // 任意一个步骤完成即继续
    MAJORITY_COMPLETED // 多数步骤完成(超过50%)
}