package net.ooder.skill.hotplug.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Skill元数据
 * 对应skill.yaml中的配置
 */
public class SkillMetadata {

    private static final Logger logger = LoggerFactory.getLogger(SkillMetadata.class);

    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private String type;
    private String form;            // Skill 形态：SCENE、DRIVER、PROVIDER
    private String category;        // 分类
    private String skillCategory;   // 技能分类（枚举类型）
    private String sceneType;       // 场景类型：AUTO、TRIGGER、PRIMARY、COLLABORATIVE
    private List<String> purposes;  // 服务目的列表
    private List<String> dependencies;
    private Map<String, Object> config;
    private Map<String, Object> ui;

    /**
     * 从YAML加载
     */
    @SuppressWarnings("unchecked")
    public static SkillMetadata loadFromYaml(InputStream is) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(is);

        SkillMetadata metadata = new SkillMetadata();
        
        Map<String, Object> metaData = (Map<String, Object>) data.get("metadata");
        if (metaData != null) {
            metadata.id = (String) metaData.get("id");
            metadata.name = (String) metaData.get("name");
            metadata.version = (String) metaData.get("version");
            metadata.description = (String) metaData.get("description");
            metadata.author = (String) metaData.get("author");
            metadata.type = (String) metaData.get("type");
            metadata.form = (String) metaData.get("form");     // 新增：加载 form 字段
            metadata.category = (String) metaData.get("category"); // 新增：加载 category 字段
            metadata.skillCategory = (String) metaData.get("skillCategory"); // 新增：加载 skillCategory 字段
            metadata.sceneType = (String) metaData.get("sceneType"); // 新增：加载 sceneType 字段
            metadata.purposes = (List<String>) metaData.get("purposes"); // 新增：加载 purposes 字段
            metadata.dependencies = (List<String>) metaData.get("dependencies");
        }
        
        Map<String, Object> specData = (Map<String, Object>) data.get("spec");
        if (specData != null) {
            if (metadata.type == null) {
                metadata.type = (String) specData.get("type");
            }
            metadata.config = (Map<String, Object>) specData.get("config");

            Object specSkillForm = specData.get("skillForm");
            if (specSkillForm != null) {
                if (metadata.form == null) {
                    metadata.form = (String) specSkillForm;
                    logger.info("[SkillMetadata] Loaded spec.skillForm: {} for skill: {}", specSkillForm, metadata.id);
                } else {
                    logger.info("[SkillMetadata] Skill {} has both metadata.form ({}) and spec.skillForm ({}), using metadata.form", metadata.id, metadata.form, specSkillForm);
                }
            } else {
                logger.debug("[SkillMetadata] Skill {} has no spec.skillForm", metadata.id);
            }

            if (metadata.skillCategory == null) {
                metadata.skillCategory = (String) specData.get("skillCategory");
                if (metadata.skillCategory != null) {
                    logger.info("[SkillMetadata] Loaded spec.skillCategory: {} for skill: {}", metadata.skillCategory, metadata.id);
                }
            }

            if (metadata.sceneType == null) {
                metadata.sceneType = (String) specData.get("sceneType");
                if (metadata.sceneType != null) {
                    logger.info("[SkillMetadata] Loaded spec.sceneType: {} for skill: {}", metadata.sceneType, metadata.id);
                }
            }

            if (metadata.purposes == null) {
                @SuppressWarnings("unchecked")
                List<String> specPurposes = (List<String>) specData.get("purposes");
                if (specPurposes != null) {
                    metadata.purposes = specPurposes;
                    logger.info("[SkillMetadata] Loaded spec.purposes: {} for skill: {}", specPurposes, metadata.id);
                }
            }

            Map<String, Object> uiData = new java.util.HashMap<>();
            if (specData.containsKey("nexusUi")) {
                uiData.put("nexusUi", specData.get("nexusUi"));
            }
            if (specData.containsKey("menus")) {
                uiData.put("menus", specData.get("menus"));
            }
            if (specData.containsKey("pages")) {
                uiData.put("pages", specData.get("pages"));
            }
            if (specData.containsKey("components")) {
                uiData.put("components", specData.get("components"));
            }
            if (specData.containsKey("routes")) {
                uiData.put("routes", specData.get("routes"));
            }
            if (!uiData.isEmpty()) {
                metadata.ui = uiData;
            }
        } else {
            logger.debug("[SkillMetadata] No spec section found for skill: {}", metadata.id);
        }
        
        if (metadata.config == null) {
            metadata.config = (Map<String, Object>) data.get("config");
        }

        return metadata;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public List<String> getPurposes() {
        return purposes;
    }

    public void setPurposes(List<String> purposes) {
        this.purposes = purposes;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Map<String, Object> getUi() {
        return ui;
    }

    public void setUi(Map<String, Object> ui) {
        this.ui = ui;
    }

    @Override
    public String toString() {
        return "SkillMetadata{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", form='" + form + '\'' +
                ", skillCategory='" + skillCategory + '\'' +
                ", sceneType='" + sceneType + '\'' +
                ", purposes=" + purposes +
                '}';
    }
}
