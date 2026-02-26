package net.ooder.sdk.service.skill.update;

import java.util.List;

/**
 * Skill 自动更新器接口
 */
public interface SkillUpdater {

    /**
     * 启用/禁用自动更新
     */
    void enableAutoUpdate(boolean enabled);

    /**
     * 检查是否启用了自动更新
     */
    boolean isAutoUpdateEnabled();

    /**
     * 设置更新策略
     */
    void setUpdateStrategy(UpdateStrategy strategy);

    /**
     * 获取当前更新策略
     */
    UpdateStrategy getUpdateStrategy();

    /**
     * 检查所有 Skill 的更新
     */
    List<UpdateInfo> checkForUpdates();

    /**
     * 检查指定 Skill 的更新
     */
    UpdateInfo checkForUpdate(String skillId);

    /**
     * 执行更新
     */
    UpdateResult performUpdate(String skillId, String newVersion);

    /**
     * 执行更新（指定策略）
     */
    UpdateResult performUpdate(String skillId, String newVersion, UpdateStrategy strategy);

    /**
     * 判断是否为补丁更新
     */
    boolean isPatchUpdate(String currentVersion, String newVersion);

    /**
     * 判断是否为次要版本更新
     */
    boolean isMinorUpdate(String currentVersion, String newVersion);

    /**
     * 判断为主要版本更新
     */
    boolean isMajorUpdate(String currentVersion, String newVersion);

    /**
     * 获取更新历史
     */
    List<UpdateResult> getUpdateHistory(String skillId);

    /**
     * 回滚到上一个版本
     */
    UpdateResult rollback(String skillId);

    /**
     * 设置更新检查间隔（分钟）
     */
    void setCheckInterval(long minutes);

    /**
     * 获取更新检查间隔
     */
    long getCheckInterval();

    /**
     * 添加更新监听器
     */
    void addUpdateListener(UpdateListener listener);

    /**
     * 移除更新监听器
     */
    void removeUpdateListener(UpdateListener listener);

    /**
     * 更新监听器接口
     */
    interface UpdateListener {
        void onUpdateAvailable(UpdateInfo updateInfo);
        void onUpdateStarted(String skillId, String newVersion);
        void onUpdateCompleted(UpdateResult result);
        void onUpdateFailed(UpdateResult result);
    }
}
