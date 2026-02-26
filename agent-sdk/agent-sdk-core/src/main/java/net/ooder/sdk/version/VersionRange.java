package net.ooder.sdk.version;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本范围
 *
 * <p>支持多种版本范围表达式:</p>
 * <ul>
 *   <li>1.0.0 - 精确版本</li>
 *   <li>&gt;=1.0.0 - 大于等于</li>
 *   <li>&gt;1.0.0 - 大于</li>
 *   <li>&lt;=1.0.0 - 小于等于</li>
 *   <li>&lt;1.0.0 - 小于</li>
 *   <li>^1.0.0 - 兼容版本 (1.x.x)</li>
 *   <li>~1.0.0 - 近似版本 (1.0.x)</li>
 *   <li>1.0.0 - 2.0.0 - 范围</li>
 *   <li>1.0.0 || 2.0.0 - 或条件</li>
 *   <li>* - 任意版本</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class VersionRange {

    private final List<RangeCondition> conditions;

    private VersionRange(List<RangeCondition> conditions) {
        this.conditions = conditions;
    }

    /**
     * 解析版本范围表达式
     *
     * @param range 范围表达式
     * @return VersionRange对象
     */
    public static VersionRange parse(String range) {
        if (range == null || range.trim().isEmpty()) {
            throw new IllegalArgumentException("Range cannot be null or empty");
        }

        List<RangeCondition> conditions = new ArrayList<>();
        String[] orParts = range.split("\\s*\\|\\|\\s*");

        for (String part : orParts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            RangeCondition condition = parseCondition(part);
            conditions.add(condition);
        }

        return new VersionRange(conditions);
    }

    /**
     * 解析单个条件
     */
    private static RangeCondition parseCondition(String condition) {
        condition = condition.trim();

        // 任意版本
        if ("*".equals(condition) || "x".equals(condition) || "X".equals(condition)) {
            return new RangeCondition(null, null, true, true, true);
        }

        // 处理范围: 1.0.0 - 2.0.0
        if (condition.contains(" - ")) {
            String[] parts = condition.split("\\s+-\\s+");
            if (parts.length == 2) {
                SemanticVersion min = SemanticVersion.tryParse(parts[0]);
                SemanticVersion max = SemanticVersion.tryParse(parts[1]);
                return new RangeCondition(min, max, true, true, false);
            }
        }

        // 处理操作符
        if (condition.startsWith(">=")) {
            SemanticVersion version = SemanticVersion.tryParse(condition.substring(2).trim());
            return new RangeCondition(version, null, true, false, false);
        } else if (condition.startsWith(">")) {
            SemanticVersion version = SemanticVersion.tryParse(condition.substring(1).trim());
            return new RangeCondition(version, null, false, false, false);
        } else if (condition.startsWith("<=")) {
            SemanticVersion version = SemanticVersion.tryParse(condition.substring(2).trim());
            return new RangeCondition(null, version, false, true, false);
        } else if (condition.startsWith("<")) {
            SemanticVersion version = SemanticVersion.tryParse(condition.substring(1).trim());
            return new RangeCondition(null, version, false, false, false);
        } else if (condition.startsWith("^")) {
            // 兼容版本: ^1.2.3 = >=1.2.3 <2.0.0
            SemanticVersion version = SemanticVersion.tryParse(condition.substring(1).trim());
            if (version != null) {
                SemanticVersion max = new SemanticVersion(version.getMajor() + 1, 0, 0);
                return new RangeCondition(version, max, true, false, false);
            }
        } else if (condition.startsWith("~")) {
            // 近似版本: ~1.2.3 = >=1.2.3 <1.3.0
            SemanticVersion version = SemanticVersion.tryParse(condition.substring(1).trim());
            if (version != null) {
                SemanticVersion max = new SemanticVersion(version.getMajor(), version.getMinor() + 1, 0);
                return new RangeCondition(version, max, true, false, false);
            }
        } else {
            // 精确版本
            SemanticVersion version = SemanticVersion.tryParse(condition);
            if (version != null) {
                return new RangeCondition(version, version, true, true, false);
            }
        }

        throw new IllegalArgumentException("Invalid version range: " + condition);
    }

    /**
     * 检查版本是否在范围内
     *
     * @param version 要检查的版本
     * @return true如果在范围内
     */
    public boolean includes(SemanticVersion version) {
        if (version == null) return false;

        // 任一条件满足即可 (OR逻辑)
        for (RangeCondition condition : conditions) {
            if (condition.satisfies(version)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查版本是否在范围内
     *
     * @param versionString 版本字符串
     * @return true如果在范围内
     */
    public boolean includes(String versionString) {
        SemanticVersion version = SemanticVersion.tryParse(versionString);
        return version != null && includes(version);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) sb.append(" || ");
            sb.append(conditions.get(i).toString());
        }
        return sb.toString();
    }

    // ==================== 内部类 ====================

    /**
     * 范围条件
     */
    private static class RangeCondition {
        private final SemanticVersion minVersion;
        private final SemanticVersion maxVersion;
        private final boolean includeMin;
        private final boolean includeMax;
        private final boolean anyVersion;

        RangeCondition(SemanticVersion minVersion, SemanticVersion maxVersion,
                       boolean includeMin, boolean includeMax, boolean anyVersion) {
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
            this.includeMin = includeMin;
            this.includeMax = includeMax;
            this.anyVersion = anyVersion;
        }

        boolean satisfies(SemanticVersion version) {
            if (anyVersion) return true;

            // 检查最小版本
            if (minVersion != null) {
                int cmp = version.compareTo(minVersion);
                if (includeMin ? cmp < 0 : cmp <= 0) {
                    return false;
                }
            }

            // 检查最大版本
            if (maxVersion != null) {
                int cmp = version.compareTo(maxVersion);
                if (includeMax ? cmp > 0 : cmp >= 0) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String toString() {
            if (anyVersion) return "*";
            if (minVersion == null && maxVersion == null) return "*";
            if (minVersion != null && maxVersion == null) {
                return (includeMin ? ">=" : ">") + minVersion;
            }
            if (minVersion == null && maxVersion != null) {
                return (includeMax ? "<=" : "<") + maxVersion;
            }
            if (minVersion.equals(maxVersion)) {
                return minVersion.toString();
            }
            return minVersion + " - " + maxVersion;
        }
    }
}
