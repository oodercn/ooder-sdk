package net.ooder.common;

/** 条件操作符枚举 */
public enum ConditionOperator {
    EQUALS,         // 等于
    NOT_EQUALS,     // 不等于
    GREATER_THAN,   // 大于
    LESS_THAN,      // 小于
    CONTAINS,       // 包含
    MATCHES,        // 正则匹配
    EXISTS,         // 存在
    EMPTY           // 为空
}