package net.ooder.scene.discovery;

import net.ooder.scene.skill.model.SkillCategory;
import net.ooder.scene.skill.model.SkillForm;
import net.ooder.scene.skill.model.SceneType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景详情 (v3.0)
 *
 * <p>使用v3.0枚举类型替代String类型分类</p>
 */
public class SceneDetail {
    private String sceneId;
    private String name;
    private String description;
    private SkillCategory category;
    private SkillForm form;
    private SceneType sceneType;
    private List<String> requiredCapabilities;
    private List<String> optionalCapabilities;
    private List<DiscoveredItem> availableSkills;
    private Map<String, Object> metadata;

    public SceneDetail(String sceneId, String name) {
        this.sceneId = sceneId;
        this.name = name;
        this.metadata = new ConcurrentHashMap<>();
    }

    public String getSceneId() {
        return sceneId;
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

    public SceneType getSceneType() {
        return sceneType;
    }

    public void setSceneType(SceneType sceneType) {
        this.sceneType = sceneType;
    }

    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }

    public List<String> getOptionalCapabilities() {
        return optionalCapabilities;
    }

    public void setOptionalCapabilities(List<String> optionalCapabilities) {
        this.optionalCapabilities = optionalCapabilities;
    }

    public List<DiscoveredItem> getAvailableSkills() {
        return availableSkills;
    }

    public void setAvailableSkills(List<DiscoveredItem> availableSkills) {
        this.availableSkills = availableSkills;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean isScene() {
        return form == SkillForm.SCENE;
    }

    public boolean canSelfDrive() {
        return sceneType != null && sceneType.canSelfDrive();
    }
}
