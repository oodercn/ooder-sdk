package net.ooder.skills.config;

import java.util.Map;

/**
 * 场景统一配置类
 * 用于 Skills Framework 模块
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneConfiguration {

    private String sceneId;
    private String sceneName;
    private String sceneType;
    private String description;
    private boolean autoCreate;
    private boolean mainFirst;
    private Map<String, Object> properties;

    // Getters and Setters
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAutoCreate() { return autoCreate; }
    public void setAutoCreate(boolean autoCreate) { this.autoCreate = autoCreate; }

    public boolean isMainFirst() { return mainFirst; }
    public void setMainFirst(boolean mainFirst) { this.mainFirst = mainFirst; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}
