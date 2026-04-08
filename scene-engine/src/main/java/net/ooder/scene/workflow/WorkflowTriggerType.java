package net.ooder.scene.workflow;

/**
 * 工作流触发类型枚举
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public enum WorkflowTriggerType {
    /** 手动触发 */
    MANUAL,
    /** 定时触发 */
    SCHEDULED,
    /** 事件触发 */
    EVENT,
    /** 条件触发 */
    CONDITION,
    /** API触发 */
    API,
    /** 自动触发 */
    AUTO
}
