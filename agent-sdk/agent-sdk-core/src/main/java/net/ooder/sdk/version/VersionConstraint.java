package net.ooder.sdk.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 版本约束解析器
 *
 * <p>支持语义化版本约束表达式，包括：</p>
 * <ul>
 *   <li>精确版本: 1.2.3</li>
 *   <li>兼容版本: ^1.2.3 (允许不修改最左边非零数字的更新)</li>
 *   <li>近似版本: ~1.2.3 (允许补丁版本更新)</li>
 *   <li>范围: >=1.0.0, <2.0.0, 1.0.0 < 2.0.0</li>
 *   <li>通配: * (任意版本)</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class VersionConstraint {

    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(
        "^(\\^|~|>=|<=|>|<)?\\s*(\\d+(?:\\.\\d+)?(?:\\.\\d+)?)?(?:\\s+(<)\\s*(\\d+(?:\\.\\d+)?(?:\\.\\d+)?))?"
    );

    /**
     * 约束操作符类型
     */
    public enum Operator {
        EXACT,
        CARET,
        TILDE,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        RANGE
    }

    private final Operator operator;
    private final SemanticVersion version;
    private final SemanticVersion upperBound;

    private VersionConstraint(Operator operator, SemanticVersion version) {
        this(operator, version, null);
    }

    private VersionConstraint(Operator operator, SemanticVersion version, SemanticVersion upperBound) {
        this.operator = operator;
        this.version = version;
        this.upperBound = upperBound;
    }

    /**
     * 解析版本约束表达式
     *
     * @param constraint 约束表达式，如 "^1.2.3", ">=1.0.0", "*"
     * @return 版本约束对象
     * @throws VersionParseException 解析失败时抛出
     */
    public static VersionConstraint parse(String constraint) throws VersionParseException {
        if (constraint == null || constraint.trim().isEmpty() || "*".equals(constraint.trim())) {
            return new VersionConstraint(Operator.GREATER_THAN_OR_EQUAL, new SemanticVersion(0, 0, 0));
        }

        constraint = constraint.trim();

        String[] parts = constraint.split("\\s+");
        if (parts.length == 3 && "<".equals(parts[1])) {
            SemanticVersion lower = SemanticVersion.parse(parts[0]);
            SemanticVersion upper = SemanticVersion.parse(parts[2]);
            return new VersionConstraint(Operator.RANGE, lower, upper);
        }

        Matcher matcher = CONSTRAINT_PATTERN.matcher(constraint);
        if (!matcher.matches()) {
            return new VersionConstraint(Operator.EXACT, SemanticVersion.parse(constraint));
        }

        String op = matcher.group(1);
        String verStr = matcher.group(2);

        if (verStr == null || verStr.isEmpty()) {
            return new VersionConstraint(Operator.GREATER_THAN_OR_EQUAL, new SemanticVersion(0, 0, 0));
        }

        SemanticVersion version = SemanticVersion.parse(verStr);

        if (op == null || op.isEmpty()) {
            return new VersionConstraint(Operator.EXACT, version);
        }

        switch (op) {
            case "^":
                return createCaretConstraint(version);
            case "~":
                return createTildeConstraint(version);
            case ">=":
                return new VersionConstraint(Operator.GREATER_THAN_OR_EQUAL, version);
            case ">":
                return new VersionConstraint(Operator.GREATER_THAN, version);
            case "<=":
                return new VersionConstraint(Operator.LESS_THAN_OR_EQUAL, version);
            case "<":
                return new VersionConstraint(Operator.LESS_THAN, version);
            default:
                return new VersionConstraint(Operator.EXACT, version);
        }
    }

    /**
     * 尝试解析版本约束表达式，失败时返回null
     *
     * @param constraint 约束表达式
     * @return 版本约束对象，解析失败返回null
     */
    public static VersionConstraint tryParse(String constraint) {
        try {
            return parse(constraint);
        } catch (VersionParseException e) {
            return null;
        }
    }

    /**
     * 创建兼容版本约束 (^)
     * ^1.2.3 表示 >=1.2.3 <2.0.0
     * ^0.2.3 表示 >=0.2.3 <0.3.0
     * ^0.0.3 表示 >=0.0.3 <0.0.4
     */
    private static VersionConstraint createCaretConstraint(SemanticVersion version) {
        SemanticVersion upperBound;
        if (version.getMajor() == 0) {
            if (version.getMinor() == 0) {
                upperBound = new SemanticVersion(0, 0, version.getPatch() + 1);
            } else {
                upperBound = new SemanticVersion(0, version.getMinor() + 1, 0);
            }
        } else {
            upperBound = new SemanticVersion(version.getMajor() + 1, 0, 0);
        }
        return new VersionConstraint(Operator.RANGE, version, upperBound);
    }

    /**
     * 创建近似版本约束 (~)
     * ~1.2.3 表示 >=1.2.3 <1.3.0
     */
    private static VersionConstraint createTildeConstraint(SemanticVersion version) {
        SemanticVersion upperBound = new SemanticVersion(version.getMajor(), version.getMinor() + 1, 0);
        return new VersionConstraint(Operator.RANGE, version, upperBound);
    }

    /**
     * 检查版本是否满足约束条件
     *
     * @param version 要检查的版本
     * @return 是否满足约束
     */
    public boolean satisfies(SemanticVersion version) {
        if (version == null) {
            return false;
        }

        switch (operator) {
            case EXACT:
                return this.version.equals(version);
            case CARET:
            case TILDE:
            case RANGE:
                return version.compareTo(this.version) >= 0
                    && (upperBound == null || version.compareTo(upperBound) < 0);
            case GREATER_THAN:
                return version.compareTo(this.version) > 0;
            case GREATER_THAN_OR_EQUAL:
                return version.compareTo(this.version) >= 0;
            case LESS_THAN:
                return version.compareTo(this.version) < 0;
            case LESS_THAN_OR_EQUAL:
                return version.compareTo(this.version) <= 0;
            default:
                return false;
        }
    }

    public Operator getOperator() {
        return operator;
    }

    public SemanticVersion getVersion() {
        return version;
    }

    public SemanticVersion getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        switch (operator) {
            case EXACT:
                return version.toString();
            case RANGE:
                return version + " < " + upperBound;
            case CARET:
                return "^" + version;
            case TILDE:
                return "~" + version;
            case GREATER_THAN:
                return ">" + version;
            case GREATER_THAN_OR_EQUAL:
                return ">=" + version;
            case LESS_THAN:
                return "<" + version;
            case LESS_THAN_OR_EQUAL:
                return "<=" + version;
            default:
                return version.toString();
        }
    }
}
