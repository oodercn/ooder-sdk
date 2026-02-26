package net.ooder.sdk.plugin;

/**
 * Skill 依赖
 */
public class SkillDependency {
    private String id;
    private String version;
    private boolean optional;

    public SkillDependency() {
    }

    public SkillDependency(String id) {
        this.id = id;
    }

    public SkillDependency(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * 检查版本是否匹配
     */
    public boolean matchesVersion(String actualVersion) {
        if (version == null || version.isEmpty()) {
            return true; // 没有版本要求，任何版本都匹配
        }
        if (actualVersion == null) {
            return false;
        }
        // 简单版本匹配，实际应该使用语义化版本比较
        return version.equals(actualVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillDependency that = (SkillDependency) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
