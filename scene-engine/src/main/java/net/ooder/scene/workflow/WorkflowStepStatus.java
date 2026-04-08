package net.ooder.scene.workflow;

/**
 * 工作流步骤状态枚举
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public enum WorkflowStepStatus {
    /** 等待中 */
    PENDING,
    /** 执行中 */
    RUNNING,
    /** 已完成 */
    COMPLETED,
    /** 失败 */
    FAILED,
    /** 已跳过 */
    SKIPPED,
    /** 已取消 */
    CANCELLED
}
