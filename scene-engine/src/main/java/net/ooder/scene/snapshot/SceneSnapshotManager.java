package net.ooder.scene.snapshot;

import java.util.List;

/**
 * 场景组快照管理器接口
 *
 * <p>管理场景组快照的生命周期，包括创建、恢复、删除和查询。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface SceneSnapshotManager {

    /**
     * 创建快照
     *
     * @param sceneGroupId 场景组ID
     * @param name 快照名称
     * @param description 快照描述
     * @return 创建的快照
     */
    SceneSnapshot createSnapshot(String sceneGroupId, String name, String description);

    /**
     * 创建快照（带触发方式）
     *
     * @param sceneGroupId 场景组ID
     * @param name 快照名称
     * @param description 快照描述
     * @param trigger 触发方式
     * @return 创建的快照
     */
    SceneSnapshot createSnapshot(String sceneGroupId, String name, String description, SnapshotTrigger trigger);

    /**
     * 获取快照
     *
     * @param sceneGroupId 场景组ID
     * @param snapshotId 快照ID
     * @return 快照，不存在返回null
     */
    SceneSnapshot getSnapshot(String sceneGroupId, String snapshotId);

    /**
     * 获取场景组的所有快照
     *
     * @param sceneGroupId 场景组ID
     * @return 快照列表
     */
    List<SceneSnapshot> listSnapshots(String sceneGroupId);

    /**
     * 获取场景组的活跃快照
     *
     * @param sceneGroupId 场景组ID
     * @return 活跃快照列表
     */
    List<SceneSnapshot> listActiveSnapshots(String sceneGroupId);

    /**
     * 恢复快照
     *
     * @param sceneGroupId 场景组ID
     * @param snapshotId 快照ID
     * @return 是否成功
     */
    boolean restoreSnapshot(String sceneGroupId, String snapshotId);

    /**
     * 删除快照
     *
     * @param sceneGroupId 场景组ID
     * @param snapshotId 快照ID
     * @return 是否成功
     */
    boolean deleteSnapshot(String sceneGroupId, String snapshotId);

    /**
     * 删除场景组的所有快照
     *
     * @param sceneGroupId 场景组ID
     * @return 删除的快照数量
     */
    int deleteAllSnapshots(String sceneGroupId);

    /**
     * 对比两个快照
     *
     * @param sceneGroupId 场景组ID
     * @param snapshotId1 快照1 ID
     * @param snapshotId2 快照2 ID
     * @return 快照差异
     */
    SnapshotDiff compareSnapshots(String sceneGroupId, String snapshotId1, String snapshotId2);

    /**
     * 设置自动快照策略
     *
     * @param sceneGroupId 场景组ID
     * @param trigger 触发方式
     * @param enabled 是否启用
     */
    void setAutoSnapshot(String sceneGroupId, SnapshotTrigger trigger, boolean enabled);

    /**
     * 设置快照保留策略
     *
     * @param sceneGroupId 场景组ID
     * @param maxSnapshots 最大快照数
     * @param retentionDays 保留天数
     */
    void setSnapshotRetention(String sceneGroupId, int maxSnapshots, int retentionDays);

    /**
     * 获取快照数量
     *
     * @param sceneGroupId 场景组ID
     * @return 快照数量
     */
    int getSnapshotCount(String sceneGroupId);

    /**
     * 检查快照是否存在
     *
     * @param sceneGroupId 场景组ID
     * @param snapshotId 快照ID
     * @return 是否存在
     */
    boolean exists(String sceneGroupId, String snapshotId);
}
