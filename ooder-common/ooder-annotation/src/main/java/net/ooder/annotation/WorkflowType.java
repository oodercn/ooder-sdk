package net.ooder.annotation;

/** 工作流类型枚举 */
public enum WorkflowType {
    SEQUENTIAL,    // 顺序执行
    STATE_MACHINE, // 状态机模式
    EVENT_DRIVEN,  // 事件驱动
    RULE_BASED     // 规则驱动
}