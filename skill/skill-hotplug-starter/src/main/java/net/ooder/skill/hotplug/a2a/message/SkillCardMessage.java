package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Skill卡片消息
 * 对应Ooder-A2A规范v1.0 skill_card类型
 */
public class SkillCardMessage extends A2AMessage {

    /**
     * Skill名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * Skill描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * Skill版本
     */
    @JsonProperty("version")
    private String version;

    /**
     * 能力列表
     */
    @JsonProperty("capabilities")
    private List<String> capabilities;

    /**
     * 输入格式
     */
    @JsonProperty("inputFormats")
    private List<String> inputFormats;

    /**
     * 输出格式
     */
    @JsonProperty("outputFormats")
    private List<String> outputFormats;

    /**
     * 认证方式
     */
    @JsonProperty("authTypes")
    private List<String> authTypes;

    /**
     * 扩展信息
     */
    @JsonProperty("extensions")
    private Map<String, Object> extensions;

    public SkillCardMessage() {
        super(MessageType.SKILL_CARD);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public List<String> getInputFormats() {
        return inputFormats;
    }

    public void setInputFormats(List<String> inputFormats) {
        this.inputFormats = inputFormats;
    }

    public List<String> getOutputFormats() {
        return outputFormats;
    }

    public void setOutputFormats(List<String> outputFormats) {
        this.outputFormats = outputFormats;
    }

    public List<String> getAuthTypes() {
        return authTypes;
    }

    public void setAuthTypes(List<String> authTypes) {
        this.authTypes = authTypes;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
}
