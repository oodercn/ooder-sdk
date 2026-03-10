package net.ooder.scene.core;

import net.ooder.scene.skill.model.SkillCategory;
import net.ooder.scene.skill.model.SkillForm;
import net.ooder.scene.skill.model.SceneType;

/**
 * Skill 查询条件 (v3.0)
 *
 * <p>支持按形态、分类、场景类型过滤</p>
 *
 * @author Ooder Team
 * @version 3.0
 */
public class SkillQuery {

    private String keyword;
    private SkillForm form;
    private SkillCategory category;
    private SceneType sceneType;
    private String status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SkillForm getForm() {
        return form;
    }

    public void setForm(SkillForm form) {
        this.form = form;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public SceneType getSceneType() {
        return sceneType;
    }

    public void setSceneType(SceneType sceneType) {
        this.sceneType = sceneType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasForm() {
        return form != null;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasSceneType() {
        return sceneType != null;
    }
}
