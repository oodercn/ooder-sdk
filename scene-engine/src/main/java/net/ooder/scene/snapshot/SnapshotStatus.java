package net.ooder.scene.snapshot;

/**
 * 快照状态枚举
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public enum SnapshotStatus {
    /** 创建中 */
    CREATING,
    /** 活跃可用 */
    ACTIVE,
    /** 恢复中 */
    RESTORING,
    /** 删除中 */
    DELETING,
    /** 错误状态 */
    ERROR
}
