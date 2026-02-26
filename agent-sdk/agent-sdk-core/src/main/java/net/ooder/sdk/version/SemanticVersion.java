package net.ooder.sdk.version;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 语义化版本 (Semantic Versioning)
 *
 * <p>遵循SemVer 2.0.0规范: https://semver.org/</p>
 *
 * <p>版本格式: MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]</p>
 * <ul>
 *   <li>MAJOR: 主版本号，不兼容的API修改</li>
 *   <li>MINOR: 次版本号，向下兼容的功能新增</li>
 *   <li>PATCH: 修订号，向下兼容的问题修复</li>
 *   <li>PRERELEASE: 预发布版本标识 (可选)</li>
 *   <li>BUILD: 构建元数据 (可选)</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SemanticVersion implements Comparable<SemanticVersion> {

    /**
     * SemVer正则表达式
     */
    private static final Pattern SEMVER_PATTERN = Pattern.compile(
            "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
            "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
            "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );

    private final int major;
    private final int minor;
    private final int patch;
    private final String prerelease;
    private final String build;

    /**
     * 创建语义化版本
     *
     * @param major 主版本号
     * @param minor 次版本号
     * @param patch 修订号
     */
    public SemanticVersion(int major, int minor, int patch) {
        this(major, minor, patch, null, null);
    }

    /**
     * 创建语义化版本
     *
     * @param major 主版本号
     * @param minor 次版本号
     * @param patch 修订号
     * @param prerelease 预发布版本标识
     */
    public SemanticVersion(int major, int minor, int patch, String prerelease) {
        this(major, minor, patch, prerelease, null);
    }

    /**
     * 创建语义化版本
     *
     * @param major 主版本号
     * @param minor 次版本号
     * @param patch 修订号
     * @param prerelease 预发布版本标识
     * @param build 构建元数据
     */
    public SemanticVersion(int major, int minor, int patch, String prerelease, String build) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version components must be non-negative");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prerelease = prerelease;
        this.build = build;
    }

    // ==================== 解析方法 ====================

    /**
     * 从字符串解析语义化版本
     *
     * @param version 版本字符串
     * @return SemanticVersion对象
     * @throws VersionParseException 解析失败时抛出
     */
    public static SemanticVersion parse(String version) throws VersionParseException {
        if (version == null || version.trim().isEmpty()) {
            throw new VersionParseException("Version string cannot be null or empty");
        }

        Matcher matcher = SEMVER_PATTERN.matcher(version.trim());
        if (!matcher.matches()) {
            throw new VersionParseException("Invalid semantic version format: " + version);
        }

        try {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));
            String prerelease = matcher.group(4);
            String build = matcher.group(5);

            return new SemanticVersion(major, minor, patch, prerelease, build);
        } catch (NumberFormatException e) {
            throw new VersionParseException("Failed to parse version numbers", e);
        }
    }

    /**
     * 尝试从字符串解析，失败返回null
     *
     * @param version 版本字符串
     * @return SemanticVersion对象或null
     */
    public static SemanticVersion tryParse(String version) {
        try {
            return parse(version);
        } catch (VersionParseException e) {
            return null;
        }
    }

    // ==================== 版本比较 ====================

    @Override
    public int compareTo(SemanticVersion other) {
        if (other == null) {
            return 1;
        }

        // 比较主版本号
        int result = Integer.compare(this.major, other.major);
        if (result != 0) return result;

        // 比较次版本号
        result = Integer.compare(this.minor, other.minor);
        if (result != 0) return result;

        // 比较修订号
        result = Integer.compare(this.patch, other.patch);
        if (result != 0) return result;

        // 比较预发布版本
        return comparePrerelease(this.prerelease, other.prerelease);
    }

    /**
     * 比较预发布版本标识
     */
    private int comparePrerelease(String pre1, String pre2) {
        // 没有预发布版本 > 有预发布版本
        if (pre1 == null && pre2 == null) return 0;
        if (pre1 == null) return 1;
        if (pre2 == null) return -1;

        String[] parts1 = pre1.split("\\.");
        String[] parts2 = pre2.split("\\.");

        int minLength = Math.min(parts1.length, parts2.length);
        for (int i = 0; i < minLength; i++) {
            int result = comparePrereleasePart(parts1[i], parts2[i]);
            if (result != 0) return result;
        }

        return Integer.compare(parts1.length, parts2.length);
    }

    /**
     * 比较单个预发布版本部分
     */
    private int comparePrereleasePart(String part1, String part2) {
        boolean isNum1 = part1.matches("\\d+");
        boolean isNum2 = part2.matches("\\d+");

        // 数字标识符 < 字母标识符
        if (isNum1 && !isNum2) return -1;
        if (!isNum1 && isNum2) return 1;

        // 都是数字，按数值比较
        if (isNum1 && isNum2) {
            return Integer.compare(Integer.parseInt(part1), Integer.parseInt(part2));
        }

        // 都是字母，按字典序比较
        return part1.compareTo(part2);
    }

    // ==================== 兼容性检查 ====================

    /**
     * 检查是否与指定版本兼容
     *
     * <p>兼容性规则: 主版本号相同即兼容</p>
     *
     * @param other 其他版本
     * @return true如果兼容
     */
    public boolean isCompatibleWith(SemanticVersion other) {
        if (other == null) return false;
        return this.major == other.major;
    }

    /**
     * 检查是否满足版本范围
     *
     * @param range 版本范围表达式
     * @return true如果满足
     */
    public boolean satisfies(String range) {
        if (range == null || range.trim().isEmpty()) {
            return false;
        }

        VersionRange versionRange = VersionRange.parse(range);
        return versionRange.includes(this);
    }

    // ==================== 版本判断 ====================

    /**
     * 是否为预发布版本
     */
    public boolean isPrerelease() {
        return prerelease != null && !prerelease.isEmpty();
    }

    /**
     * 是否为初始开发版本 (0.x.x)
     */
    public boolean isInitialDevelopment() {
        return major == 0;
    }

    /**
     * 是否为稳定版本 (>= 1.0.0)
     */
    public boolean isStable() {
        return major >= 1 && !isPrerelease();
    }

    // ==================== 版本升级 ====================

    /**
     * 增加主版本号 (MAJOR + 1, MINOR=0, PATCH=0)
     */
    public SemanticVersion incrementMajor() {
        return new SemanticVersion(major + 1, 0, 0);
    }

    /**
     * 增加次版本号 (MINOR + 1, PATCH=0)
     */
    public SemanticVersion incrementMinor() {
        return new SemanticVersion(major, minor + 1, 0);
    }

    /**
     * 增加修订号 (PATCH + 1)
     */
    public SemanticVersion incrementPatch() {
        return new SemanticVersion(major, minor, patch + 1);
    }

    // ==================== Getters ====================

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getPrerelease() {
        return prerelease;
    }

    public String getBuild() {
        return build;
    }

    // ==================== Object方法 ====================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append('.').append(minor).append('.').append(patch);
        if (prerelease != null && !prerelease.isEmpty()) {
            sb.append('-').append(prerelease);
        }
        if (build != null && !build.isEmpty()) {
            sb.append('+').append(build);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticVersion that = (SemanticVersion) o;
        return major == that.major &&
               minor == that.minor &&
               patch == that.patch &&
               Objects.equals(prerelease, that.prerelease);
        // build不参与相等性比较
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, prerelease);
    }
}
