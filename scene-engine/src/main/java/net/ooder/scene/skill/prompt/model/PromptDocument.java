package net.ooder.scene.skill.prompt.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 提示语文档模型
 *
 * <p>用于存储到知识库的提示语文档结构</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class PromptDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    private String docId;
    private String skillId;
    private PromptType type;
    private String roleId;
    private String content;
    private String language;
    private int version;
    private Map<String, Object> metadata;

    public PromptDocument() {
        this.metadata = new HashMap<>();
        this.version = 1;
        this.language = "zh-CN";
    }

    public PromptDocument(String docId, String skillId, PromptType type, String content) {
        this();
        this.docId = docId;
        this.skillId = skillId;
        this.type = type;
        this.content = content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public PromptType getType() {
        return type;
    }

    public void setType(PromptType type) {
        this.type = type;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * 提示语类型枚举
     */
    public enum PromptType {
        SYSTEM("system", "系统提示词"),
        ROLE("role", "角色提示词"),
        CONTEXT("context", "上下文提示词"),
        INSTRUCTION("instruction", "指令提示词"),
        EXAMPLE("example", "示例提示词");

        private final String code;
        private final String description;

        PromptType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Builder 模式
     */
    public static class Builder {
        private final PromptDocument document;

        public Builder() {
            document = new PromptDocument();
        }

        public Builder docId(String docId) {
            document.setDocId(docId);
            return this;
        }

        public Builder skillId(String skillId) {
            document.setSkillId(skillId);
            return this;
        }

        public Builder type(PromptType type) {
            document.setType(type);
            return this;
        }

        public Builder roleId(String roleId) {
            document.setRoleId(roleId);
            return this;
        }

        public Builder content(String content) {
            document.setContent(content);
            return this;
        }

        public Builder language(String language) {
            document.setLanguage(language);
            return this;
        }

        public Builder version(int version) {
            document.setVersion(version);
            return this;
        }

        public Builder metadata(String key, Object value) {
            document.getMetadata().put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            document.setMetadata(metadata);
            return this;
        }

        public PromptDocument build() {
            return document;
        }
    }
}
