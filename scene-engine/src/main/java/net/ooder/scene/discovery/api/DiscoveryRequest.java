package net.ooder.scene.discovery.api;

import net.ooder.scene.skill.model.SkillCategory;
import net.ooder.scene.skill.model.SkillForm;
import net.ooder.scene.skill.model.SceneType;

import java.util.List;
import java.util.Map;

/**
 * 发现请求 (v3.0)
 *
 * <p>使用v3.0枚举类型替代String类型分类</p>
 *
 * @author ooder Team
 * @since 2.3
 */
public class DiscoveryRequest {
    
    private String source;
    private String sceneId;
    private SkillCategory category;
    private SkillForm form;
    private SceneType sceneType;
    private List<String> tags;
    private String keyword;
    private String version;
    private Map<String, String> filters;
    private boolean includeInstalled;
    private boolean includeCached;
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }

    public SkillForm getForm() { return form; }
    public void setForm(SkillForm form) { this.form = form; }

    public SceneType getSceneType() { return sceneType; }
    public void setSceneType(SceneType sceneType) { this.sceneType = sceneType; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public Map<String, String> getFilters() { return filters; }
    public void setFilters(Map<String, String> filters) { this.filters = filters; }
    
    public boolean isIncludeInstalled() { return includeInstalled; }
    public void setIncludeInstalled(boolean includeInstalled) { this.includeInstalled = includeInstalled; }
    
    public boolean isIncludeCached() { return includeCached; }
    public void setIncludeCached(boolean includeCached) { this.includeCached = includeCached; }

    public boolean hasForm() {
        return form != null;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasSceneType() {
        return sceneType != null;
    }

    public boolean isSceneRequest() {
        return form == SkillForm.SCENE;
    }
}
