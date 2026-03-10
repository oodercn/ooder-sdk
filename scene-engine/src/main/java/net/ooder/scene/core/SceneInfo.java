package net.ooder.scene.core;

import net.ooder.scene.skill.model.SkillCategory;
import net.ooder.scene.skill.model.SkillForm;
import net.ooder.scene.skill.model.SceneType;

import java.util.List;
import java.util.Optional;

/**
 * 场景信息 (v3.0)
 *
 * <p>使用v3.0枚举类型替代String类型分类</p>
 */
public class SceneInfo {
    private String sceneId;
    private String name;
    private String description;
    private SkillCategory category;
    private SkillForm form;
    private SceneType sceneType;
    private String status;
    private String owner;
    private List<String> requiredCapabilities;
    private long createdAt;
    private long updatedAt;

    public SceneInfo() {}

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }

    public SkillForm getForm() { return form; }
    public void setForm(SkillForm form) { this.form = form; }

    public SceneType getSceneType() { return sceneType; }
    public void setSceneType(SceneType sceneType) { this.sceneType = sceneType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public List<String> getRequiredCapabilities() { return requiredCapabilities; }
    public void setRequiredCapabilities(List<String> requiredCapabilities) { this.requiredCapabilities = requiredCapabilities; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public boolean isScene() {
        return form == SkillForm.SCENE;
    }

    public boolean canSelfDrive() {
        return sceneType != null && sceneType.canSelfDrive();
    }

    public Optional<SkillCategory> getCategoryOptional() {
        return Optional.ofNullable(category);
    }

    public Optional<SceneType> getSceneTypeOptional() {
        return Optional.ofNullable(sceneType);
    }
}
