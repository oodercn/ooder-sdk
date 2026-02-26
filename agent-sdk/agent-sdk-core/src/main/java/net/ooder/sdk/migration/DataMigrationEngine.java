package net.ooder.sdk.migration;

import java.util.List;

/**
 * 数据迁移引擎接口
 */
public interface DataMigrationEngine {

    /**
     * 执行迁移
     */
    MigrationResult migrate(String skillId, String fromVersion, String toVersion, MigrationContext context);

    /**
     * 检查是否需要迁移
     */
    boolean needsMigration(String skillId, String fromVersion, String toVersion);

    /**
     * 查找迁移脚本
     */
    List<MigrationScript> findMigrationScripts(String skillId, String fromVersion, String toVersion);

    /**
     * 注册迁移脚本
     */
    void registerScript(MigrationScript script);

    /**
     * 执行单个脚本
     */
    boolean executeScript(MigrationScript script, MigrationContext context);

    /**
     * 创建备份
     */
    String createBackup(String skillId);

    /**
     * 从备份恢复
     */
    boolean restoreFromBackup(String skillId, String backupLocation);

    /**
     * 获取迁移历史
     */
    List<MigrationResult> getMigrationHistory(String skillId);

    /**
     * 回滚最后一次迁移
     */
    MigrationResult rollback(String skillId);

    /**
     * 验证脚本
     */
    boolean validateScript(MigrationScript script);
}
