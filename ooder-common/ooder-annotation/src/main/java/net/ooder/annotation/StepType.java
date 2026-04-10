package net.ooder.annotation;

/** 步骤类型枚举 */
public enum StepType {
    ACTION,        // 普通动作步骤
    DECISION,      // 决策步骤
    SUB_WORKFLOW,  // 子工作流步骤
    AI_TASK,       // AI任务步骤
    MCP_CALL,      // MCP服务调用步骤
    VALIDATION     // 数据验证步骤
}