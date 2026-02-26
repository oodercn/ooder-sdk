package net.ooder.sdk.version;

/**
 * 版本兼容性检查器
 *
 * <p>提供版本兼容性检查、升级/降级决策等功能</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class VersionCompatibilityChecker {

    /**
     * 兼容性级别
     */
    public enum CompatibilityLevel {
        /**
         * 完全兼容
         */
        FULLY_COMPATIBLE,
        /**
         * 向后兼容（新增功能）
         */
        BACKWARD_COMPATIBLE,
        /**
         * 可能不兼容（功能降级）
         */
        POTENTIALLY_INCOMPATIBLE,
        /**
         * 不兼容
         */
        INCOMPATIBLE
    }

    /**
     * 升级决策
     */
    public enum UpgradeDecision {
        /**
         * 自动升级
         */
        AUTO,
        /**
         * 允许升级
         */
        ALLOWED,
        /**
         * 需要确认
         */
        CONFIRMATION_REQUIRED,
        /**
         * 禁止升级
         */
        BLOCKED
    }

    /**
     * 检查版本兼容性
     *
     * @param current 当前版本
     * @param target  目标版本
     * @return 兼容性结果
     */
    public CompatibilityResult checkCompatibility(SemanticVersion current, SemanticVersion target) {
        if (current.equals(target)) {
            return new CompatibilityResult(CompatibilityLevel.FULLY_COMPATIBLE,
                "版本相同，完全兼容");
        }

        if (current.getMajor() != target.getMajor()) {
            return new CompatibilityResult(CompatibilityLevel.INCOMPATIBLE,
                "主版本号不同，API可能存在破坏性变更");
        }

        if (current.getMinor() != target.getMinor()) {
            if (target.getMinor() > current.getMinor()) {
                return new CompatibilityResult(CompatibilityLevel.BACKWARD_COMPATIBLE,
                    "次版本号升级，向后兼容，新增功能");
            } else {
                return new CompatibilityResult(CompatibilityLevel.POTENTIALLY_INCOMPATIBLE,
                    "次版本号降级，可能丢失功能");
            }
        }

        if (target.getPatch() > current.getPatch()) {
            return new CompatibilityResult(CompatibilityLevel.FULLY_COMPATIBLE,
                "补丁版本升级，仅bug修复");
        } else {
            return new CompatibilityResult(CompatibilityLevel.FULLY_COMPATIBLE,
                "补丁版本降级，回滚bug修复");
        }
    }

    /**
     * 判断是否允许升级
     *
     * @param current 当前版本
     * @param target  目标版本
     * @return 升级决策
     */
    public UpgradeDecision canUpgrade(SemanticVersion current, SemanticVersion target) {
        if (current.equals(target)) {
            return UpgradeDecision.AUTO;
        }

        if (current.getMajor() != target.getMajor()) {
            return UpgradeDecision.BLOCKED;
        }

        if (current.getMinor() == target.getMinor()) {
            return UpgradeDecision.AUTO;
        }

        return UpgradeDecision.ALLOWED;
    }

    /**
     * 判断是否允许降级
     *
     * @param current 当前版本
     * @param target  目标版本
     * @return 升级决策
     */
    public UpgradeDecision canDowngrade(SemanticVersion current, SemanticVersion target) {
        if (current.equals(target)) {
            return UpgradeDecision.AUTO;
        }

        if (current.getMajor() != target.getMajor()) {
            return UpgradeDecision.BLOCKED;
        }

        if (current.getMinor() == target.getMinor()) {
            return UpgradeDecision.ALLOWED;
        }

        return UpgradeDecision.CONFIRMATION_REQUIRED;
    }

    /**
     * 判断是否为破坏性变更
     *
     * @param current 当前版本
     * @param target  目标版本
     * @return 是否为破坏性变更
     */
    public boolean isBreakingChange(SemanticVersion current, SemanticVersion target) {
        return current.getMajor() != target.getMajor();
    }

    /**
     * 判断是否为功能变更
     *
     * @param current 当前版本
     * @param target  目标版本
     * @return 是否为功能变更
     */
    public boolean isFeatureChange(SemanticVersion current, SemanticVersion target) {
        return current.getMajor() == target.getMajor()
            && current.getMinor() != target.getMinor();
    }

    /**
     * 判断是否为补丁变更
     *
     * @param current 当前版本
     * @param target  目标版本
     * @return 是否为补丁变更
     */
    public boolean isPatchChange(SemanticVersion current, SemanticVersion target) {
        return current.getMajor() == target.getMajor()
            && current.getMinor() == target.getMinor()
            && current.getPatch() != target.getPatch();
    }

    /**
     * 兼容性检查结果
     */
    public static class CompatibilityResult {
        private final CompatibilityLevel level;
        private final String message;

        public CompatibilityResult(CompatibilityLevel level, String message) {
            this.level = level;
            this.message = message;
        }

        public CompatibilityLevel getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        /**
         * 是否兼容
         */
        public boolean isCompatible() {
            return level != CompatibilityLevel.INCOMPATIBLE;
        }

        /**
         * 是否需要确认
         */
        public boolean requiresConfirmation() {
            return level == CompatibilityLevel.POTENTIALLY_INCOMPATIBLE;
        }
    }
}
