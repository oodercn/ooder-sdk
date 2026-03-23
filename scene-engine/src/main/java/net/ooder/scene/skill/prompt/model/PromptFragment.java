package net.ooder.scene.skill.prompt.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 提示语片段模型
 *
 * <p>从知识库检索到的提示语片段</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class PromptFragment implements Serializable, Comparable<PromptFragment> {

    private static final long serialVersionUID = 1L;

    private String fragmentId;
    private String content;
    private float score;
    private String source;
    private PromptDocument.PromptType type;
    private String skillId;
    private String roleId;
    private Map<String, Object> metadata;

    public PromptFragment() {
        this.metadata = new HashMap<>();
    }

    public PromptFragment(String fragmentId, String content, float score) {
        this();
        this.fragmentId = fragmentId;
        this.content = content;
        this.score = score;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(PromptFragment other) {
        return Float.compare(other.score, this.score);
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public PromptDocument.PromptType getType() {
        return type;
    }

    public void setType(PromptDocument.PromptType type) {
        this.type = type;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Builder 模式
     */
    public static class Builder {
        private final PromptFragment fragment;

        public Builder() {
            fragment = new PromptFragment();
        }

        public Builder fragmentId(String fragmentId) {
            fragment.setFragmentId(fragmentId);
            return this;
        }

        public Builder content(String content) {
            fragment.setContent(content);
            return this;
        }

        public Builder score(float score) {
            fragment.setScore(score);
            return this;
        }

        public Builder source(String source) {
            fragment.setSource(source);
            return this;
        }

        public Builder type(PromptDocument.PromptType type) {
            fragment.setType(type);
            return this;
        }

        public Builder skillId(String skillId) {
            fragment.setSkillId(skillId);
            return this;
        }

        public Builder roleId(String roleId) {
            fragment.setRoleId(roleId);
            return this;
        }

        public Builder metadata(String key, Object value) {
            fragment.getMetadata().put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            if (metadata != null) {
                fragment.getMetadata().putAll(metadata);
            }
            return this;
        }

        public PromptFragment build() {
            return fragment;
        }
    }
}
