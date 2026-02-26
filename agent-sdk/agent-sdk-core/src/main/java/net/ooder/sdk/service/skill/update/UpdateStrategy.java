package net.ooder.sdk.service.skill.update;

/**
 * 更新策略
 */
public enum UpdateStrategy {
    /**
     * 自动更新所有类型
     */
    AUTO,

    /**
     * 仅自动更新 PATCH 版本
     */
    PATCH_ONLY,

    /**
     * 自动更新 PATCH 和 MINOR 版本
     */
    PATCH_AND_MINOR,

    /**
     * 手动更新
     */
    MANUAL
}
