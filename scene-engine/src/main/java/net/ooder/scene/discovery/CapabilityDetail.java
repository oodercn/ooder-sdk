package net.ooder.scene.discovery;

import net.ooder.scene.skill.model.SkillCategory;
import net.ooder.scene.skill.model.SkillForm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 能力详情 (v3.0)
 *
 * <p>使用v3.0枚举类型替代String类型分类</p>
 */
public class CapabilityDetail {
    private String capabilityId;
    private String name;
    private String description;
    private SkillCategory category;
    private SkillForm form;
    private String version;
    private String provider;
    private String status;
    private Map<String, Object> parameters;
    private Map<String, Object> metadata;

    public CapabilityDetail() {
        this.metadata = new ConcurrentHashMap<>();
    }

    public CapabilityDetail(String capabilityId, String name) {
        this.capabilityId = capabilityId;
        this.name = name;
        this.metadata = new ConcurrentHashMap<>();
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
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

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public void setCategory(String category) {
        this.category = SkillCategory.fromCode(category);
    }

    public SkillForm getForm() {
        return form;
    }

    public void setForm(SkillForm form) {
        this.form = form;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean isSceneCapability() {
        return form == SkillForm.SCENE;
    }
}
