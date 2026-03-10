package net.ooder.scene.skill.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 技能路径
 *
 * <p>表示技能在技能树中的位置，类比文件系统的路径</p>
 *
 * <h3>路径格式：</h3>
 * <ul>
 *   <li>绝对路径：/scene-a/sub-scene-b/skill-c</li>
 *   <li>相对路径：sub-scene-b/skill-c</li>
 *   <li>根路径：/</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public class SkillPath {

    public static final String SEPARATOR = "/";
    public static final SkillPath ROOT = new SkillPath(Collections.emptyList());

    private final List<String> segments;
    private final boolean absolute;

    private SkillPath(List<String> segments) {
        this(segments, true);
    }

    private SkillPath(List<String> segments, boolean absolute) {
        this.segments = Collections.unmodifiableList(segments);
        this.absolute = absolute;
    }

    /**
     * 从字符串解析路径
     *
     * @param path 路径字符串
     * @return SkillPath
     */
    public static SkillPath from(String path) {
        if (path == null || path.isEmpty() || path.equals(SEPARATOR)) {
            return ROOT;
        }

        boolean isAbsolute = path.startsWith(SEPARATOR);
        String normalized = isAbsolute ? path.substring(1) : path;

        List<String> segments = Arrays.stream(normalized.split(SEPARATOR))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return new SkillPath(segments, isAbsolute);
    }

    /**
     * 从段列表创建路径
     */
    public static SkillPath of(String... segments) {
        return new SkillPath(Arrays.asList(segments));
    }

    /**
     * 获取路径段
     */
    public List<String> getSegments() {
        return segments;
    }

    /**
     * 是否为绝对路径
     */
    public boolean isAbsolute() {
        return absolute;
    }

    /**
     * 获取路径深度
     */
    public int getDepth() {
        return segments.size();
    }

    /**
     * 是否为根路径
     */
    public boolean isRoot() {
        return segments.isEmpty();
    }

    /**
     * 获取父路径
     *
     * @return 父路径，根路径返回 empty
     */
    public SkillPath getParent() {
        if (isRoot()) {
            return null;
        }
        return new SkillPath(segments.subList(0, segments.size() - 1), absolute);
    }

    /**
     * 获取最后一段（技能ID）
     */
    public String getLastSegment() {
        if (isRoot()) {
            return "";
        }
        return segments.get(segments.size() - 1);
    }

    /**
     * 获取第一段（根技能ID）
     */
    public String getFirstSegment() {
        if (isRoot()) {
            return "";
        }
        return segments.get(0);
    }

    /**
     * 追加路径
     */
    public SkillPath append(String... segments) {
        List<String> newSegments = Arrays.stream(segments)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<String> combined = new java.util.ArrayList<>(this.segments);
        combined.addAll(newSegments);

        return new SkillPath(combined, absolute);
    }

    /**
     * 追加路径
     */
    public SkillPath append(SkillPath other) {
        List<String> combined = new java.util.ArrayList<>(this.segments);
        combined.addAll(other.segments);
        return new SkillPath(combined, absolute);
    }

    /**
     * 判断是否为后代路径
     *
     * @param ancestor 祖先路径
     * @return true 如果当前路径是 ancestor 的后代
     */
    public boolean isDescendantOf(SkillPath ancestor) {
        if (ancestor.segments.size() >= this.segments.size()) {
            return false;
        }
        return this.segments.subList(0, ancestor.segments.size())
                .equals(ancestor.segments);
    }

    /**
     * 判断是否为祖先路径
     */
    public boolean isAncestorOf(SkillPath descendant) {
        return descendant.isDescendantOf(this);
    }

    /**
     * 获取相对路径
     */
    public SkillPath relativize(SkillPath base) {
        if (!this.isDescendantOf(base) && !base.isDescendantOf(this)) {
            throw new IllegalArgumentException("Paths have no common ancestor");
        }

        if (this.isDescendantOf(base)) {
            List<String> relative = this.segments.subList(base.segments.size(), this.segments.size());
            return new SkillPath(relative, false);
        }

        // base 是当前路径的后代，需要返回 "../" 形式
        int upCount = base.segments.size() - this.segments.size();
        List<String> result = new java.util.ArrayList<>();
        for (int i = 0; i < upCount; i++) {
            result.add("..");
        }
        return new SkillPath(result, false);
    }

    /**
     * 解析子路径
     */
    public SkillPath resolve(String subPath) {
        if (subPath.startsWith(SEPARATOR)) {
            // 绝对路径，直接返回
            return from(subPath);
        }
        return append(subPath);
    }

    @Override
    public String toString() {
        if (isRoot()) {
            return SEPARATOR;
        }
        String path = String.join(SEPARATOR, segments);
        return absolute ? SEPARATOR + path : path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillPath skillPath = (SkillPath) o;
        return absolute == skillPath.absolute &&
                segments.equals(skillPath.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments, absolute);
    }
}
