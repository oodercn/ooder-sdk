package net.ooder.scene.workflow;

/**
 * 工作流状态枚举
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public enum WorkflowStatus {
    /** 草稿 */
    DRAFT,
    /** 激活 */
    ACTIVE,
    /** 暂停 */
    PAUSED,
    /** 执行中 */
    RUNNING,
    /** 已完成 */
    COMPLETED,
    /** 已取消 */
    CANCELLED,
    /** 错误 */
    ERROR,
    /** 已归档 */
    ARCHIVED
}
