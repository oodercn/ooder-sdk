package net.ooder.scene.snapshot;

/**
 * 快照触发方式枚举
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public enum SnapshotTrigger {
    /** 手动触发 */
    MANUAL,
    /** 定时触发 */
    SCHEDULED,
    /** 更新前自动触发 */
    BEFORE_UPDATE,
    /** 删除前自动触发 */
    BEFORE_DELETE,
    /** 能力变更时触发 */
    CAPABILITY_CHANGE
}
