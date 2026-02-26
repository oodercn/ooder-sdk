package net.ooder.sdk.migration;

import java.util.List;

/**
 * 迁移检测器接口
 */
public interface MigrationDetector {

    /**
     * 检测所有迁移机会
     */
    List<MigrationOpportunity> detectAll();

    /**
     * 检测指定 Skill 的迁移机会
     */
    MigrationOpportunity detectForSkill(String skillId);

    /**
     * 添加迁移规则
     */
    void addMigrationRule(MigrationRule rule);

    /**
     * 移除迁移规则
     */
    void removeMigrationRule(String ruleId);

    /**
     * 启用/禁用规则
     */
    void setRuleEnabled(String ruleId, boolean enabled);

    /**
     * 评估迁移
     */
    MigrationAssessment assessMigration(String skillId, String fromVersion, String toVersion);

    /**
     * 检查是否允许迁移
     */
    boolean isMigrationAllowed(MigrationOpportunity opportunity);

    /**
     * 获取所有规则
     */
    List<MigrationRule> getRules();

    /**
     * 添加监听器
     */
    void addListener(MigrationListener listener);

    /**
     * 移除监听器
     */
    void removeListener(MigrationListener listener);

    /**
     * 迁移监听器接口
     */
    interface MigrationListener {
        void onOpportunityDetected(MigrationOpportunity opportunity);
        void onRuleMatched(MigrationOpportunity opportunity, MigrationRule rule);
    }
}
