package net.ooder.scene.core.preview;

import net.ooder.scene.core.template.*;
import net.ooder.scene.core.dependency.DependencyCheckEngine;
import net.ooder.scene.core.dependency.DependencyCheckEngine.DependencyCheckResult;
import net.ooder.scene.skill.install.SceneConfigLoader;
import net.ooder.scene.ui.MenuConfig;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 场景预览引擎
 * 
 * <p>提供场景技能安装前的预览功能，包括：</p>
 * <ul>
 *   <li>基本信息预览</li>
 *   <li>角色配置预览</li>
 *   <li>激活步骤预览</li>
 *   <li>菜单配置预览</li>
 *   <li>依赖检查预览</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PreviewEngine {

    private static final Logger log = LoggerFactory.getLogger(PreviewEngine.class);

    private final SceneConfigLoader configLoader;
    private final DependencyCheckEngine dependencyCheckEngine;

    public PreviewEngine(SceneConfigLoader configLoader, DependencyCheckEngine dependencyCheckEngine) {
        this.configLoader = configLoader;
        this.dependencyCheckEngine = dependencyCheckEngine;
    }

    /**
     * 预览技能
     * 
     * @param skillPackage 技能包
     * @return 预览结果
     */
    public CompletableFuture<PreviewResult> preview(SkillPackage skillPackage) {
        return CompletableFuture.supplyAsync(() -> {
            PreviewResult result = new PreviewResult();
            result.setSkillId(skillPackage.getSkillId());
            result.setPreviewTime(System.currentTimeMillis());

            try {
                result.setBasicInfo(previewBasicInfo(skillPackage));
                result.setConfigInfo(previewConfig(skillPackage));
                result.setDependencyInfo(previewDependencies(skillPackage));
                result.setSuccess(true);
                result.setMessage("预览成功");

            } catch (Exception e) {
                log.error("[preview] Failed to preview skill: {}", skillPackage.getSkillId(), e);
                result.setSuccess(false);
                result.setMessage("预览失败: " + e.getMessage());
                result.setErrorType(e.getClass().getSimpleName());
            }

            return result;
        });
    }

    /**
     * 预览基本信息
     */
    private BasicInfoPreview previewBasicInfo(SkillPackage skillPackage) {
        BasicInfoPreview preview = new BasicInfoPreview();
        
        Map<String, Object> metadata = skillPackage.getMetadata();
        if (metadata != null) {
            preview.setName((String) metadata.getOrDefault("name", skillPackage.getSkillId()));
            preview.setDisplayName((String) metadata.get("displayName"));
            preview.setDescription((String) metadata.get("description"));
            preview.setVersion((String) metadata.getOrDefault("version", "1.0.0"));
            preview.setType((String) metadata.get("type"));
            preview.setCategory((String) metadata.get("category"));
            preview.setAuthor((String) metadata.get("author"));
            preview.setTags((List<String>) metadata.get("tags"));
        }

        preview.setSkillId(skillPackage.getSkillId());
        preview.setInstallSize(estimateSize(skillPackage));

        return preview;
    }

    /**
     * 预览配置信息
     */
    private ConfigInfoPreview previewConfig(SkillPackage skillPackage) {
        ConfigInfoPreview preview = new ConfigInfoPreview();

        try {
            if (configLoader != null) {
                SceneTemplate template = configLoader.loadSceneConfig(
                    skillPackage.getSkillId(), skillPackage);
                
                if (template != null) {
                    preview.setTemplateId(template.getTemplateId());
                    preview.setTemplateName(template.getTemplateName());
                    preview.setSceneType(template.getSceneType() != null 
                        ? template.getSceneType().name() : null);
                    preview.setVisibility(template.getVisibility());
                    preview.setCategory(template.getCategory());

                    preview.setRoles(previewRoles(template));
                    preview.setActivationSteps(previewActivationSteps(template));
                    preview.setMenus(previewMenus(template));
                    preview.setPrivateCapabilities(previewPrivateCapabilities(template));

                    preview.setHasValidConfig(true);
                } else {
                    preview.setHasValidConfig(false);
                    preview.setConfigMessage("未找到场景配置");
                }
            } else {
                preview.setHasValidConfig(false);
                preview.setConfigMessage("配置加载器不可用");
            }
        } catch (Exception e) {
            log.warn("[previewConfig] Failed to load config", e);
            preview.setHasValidConfig(false);
            preview.setConfigMessage("配置加载失败: " + e.getMessage());
        }

        return preview;
    }

    private List<RolePreview> previewRoles(SceneTemplate template) {
        List<RolePreview> roles = new ArrayList<>();
        List<RoleConfig> roleConfigs = template.getRoles();
        
        if (roleConfigs != null) {
            for (RoleConfig config : roleConfigs) {
                RolePreview preview = new RolePreview();
                preview.setRoleId(config.getRoleId());
                preview.setRoleName(config.getRoleName() != null 
                    ? config.getRoleName() : config.getName());
                preview.setDescription(config.getDescription());
                preview.setRequired(config.isRequired());
                preview.setMinCount(config.getMinCount());
                preview.setMaxCount(config.getMaxCount());
                preview.setPermissions(config.getPermissions());
                roles.add(preview);
            }
        }
        
        return roles;
    }

    private Map<String, List<ActivationStepPreview>> previewActivationSteps(SceneTemplate template) {
        Map<String, List<ActivationStepPreview>> result = new HashMap<>();
        Map<String, List<ActivationStepConfig>> stepsMap = template.getActivationSteps();
        
        if (stepsMap != null) {
            for (Map.Entry<String, List<ActivationStepConfig>> entry : stepsMap.entrySet()) {
                List<ActivationStepPreview> steps = new ArrayList<>();
                for (ActivationStepConfig config : entry.getValue()) {
                    ActivationStepPreview preview = new ActivationStepPreview();
                    preview.setStepId(config.getStepId());
                    preview.setStepName(config.getStepName() != null 
                        ? config.getStepName() : config.getName());
                    preview.setDescription(config.getDescription());
                    preview.setStepType(config.getStepType());
                    preview.setOrder(config.getOrder());
                    preview.setRequired(config.isRequired());
                    preview.setSkippable(config.isSkippable());
                    preview.setAutoExecute(config.isAutoExecute());
                    steps.add(preview);
                }
                result.put(entry.getKey(), steps);
            }
        }
        
        return result;
    }

    private Map<String, List<MenuPreview>> previewMenus(SceneTemplate template) {
        Map<String, List<MenuPreview>> result = new HashMap<>();
        Map<String, List<MenuConfig>> menusMap = template.getMenus();
        
        if (menusMap != null) {
            for (Map.Entry<String, List<MenuConfig>> entry : menusMap.entrySet()) {
                List<MenuPreview> menus = new ArrayList<>();
                for (MenuConfig config : entry.getValue()) {
                    MenuPreview preview = new MenuPreview();
                    preview.setId(config.getMenuId());
                    preview.setName(config.getTitle());
                    preview.setIcon(config.getIcon());
                    preview.setUrl(config.getPath());
                    preview.setOrder(config.getOrder());
                    menus.add(preview);
                }
                result.put(entry.getKey(), menus);
            }
        }
        
        return result;
    }

    private List<PrivateCapabilityPreview> previewPrivateCapabilities(SceneTemplate template) {
        List<PrivateCapabilityPreview> result = new ArrayList<>();
        List<SceneTemplate.PrivateCapabilityConfig> capabilities = template.getPrivateCapabilities();
        
        if (capabilities != null) {
            for (SceneTemplate.PrivateCapabilityConfig config : capabilities) {
                PrivateCapabilityPreview preview = new PrivateCapabilityPreview();
                preview.setCapId(config.getCapabilityId());
                preview.setName(config.getCapabilityName());
                preview.setDescription(config.getDescription());
                result.add(preview);
            }
        }
        
        return result;
    }

    /**
     * 预览依赖信息
     */
    private DependencyInfoPreview previewDependencies(SkillPackage skillPackage) {
        DependencyInfoPreview preview = new DependencyInfoPreview();

        try {
            if (configLoader != null && dependencyCheckEngine != null) {
                SceneTemplate template = configLoader.loadSceneConfig(
                    skillPackage.getSkillId(), skillPackage);
                
                if (template != null && template.getDependencies() != null) {
                    DependencyCheckResult checkResult = dependencyCheckEngine
                        .checkDependencies(template).join();
                    
                    preview.setAllSatisfied(checkResult.isAllSatisfied());
                    preview.setMessage(checkResult.getMessage());
                    preview.setRequiredCount(checkResult.getRequiredCount());
                    preview.setSatisfiedCount(checkResult.getSatisfiedCount());
                    preview.setUnsatisfiedRequired(checkResult.getUnsatisfiedRequired());
                    preview.setHasDependencies(true);
                } else {
                    preview.setHasDependencies(false);
                    preview.setMessage("无依赖配置");
                }
            } else {
                preview.setHasDependencies(false);
                preview.setMessage("依赖检查不可用");
            }
        } catch (Exception e) {
            log.warn("[previewDependencies] Failed to check dependencies", e);
            preview.setHasDependencies(false);
            preview.setMessage("依赖检查失败: " + e.getMessage());
        }

        return preview;
    }

    private long estimateSize(SkillPackage skillPackage) {
        return 1024 * 1024;
    }

    /**
     * 预览结果
     */
    public static class PreviewResult {
        private String skillId;
        private long previewTime;
        private boolean success;
        private String message;
        private String errorType;
        private BasicInfoPreview basicInfo;
        private ConfigInfoPreview configInfo;
        private DependencyInfoPreview dependencyInfo;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public long getPreviewTime() { return previewTime; }
        public void setPreviewTime(long previewTime) { this.previewTime = previewTime; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getErrorType() { return errorType; }
        public void setErrorType(String errorType) { this.errorType = errorType; }
        public BasicInfoPreview getBasicInfo() { return basicInfo; }
        public void setBasicInfo(BasicInfoPreview basicInfo) { this.basicInfo = basicInfo; }
        public ConfigInfoPreview getConfigInfo() { return configInfo; }
        public void setConfigInfo(ConfigInfoPreview configInfo) { this.configInfo = configInfo; }
        public DependencyInfoPreview getDependencyInfo() { return dependencyInfo; }
        public void setDependencyInfo(DependencyInfoPreview dependencyInfo) { this.dependencyInfo = dependencyInfo; }
    }

    /**
     * 基本信息预览
     */
    public static class BasicInfoPreview {
        private String skillId;
        private String name;
        private String displayName;
        private String description;
        private String version;
        private String type;
        private String category;
        private String author;
        private List<String> tags;
        private long installSize;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public long getInstallSize() { return installSize; }
        public void setInstallSize(long installSize) { this.installSize = installSize; }
    }

    /**
     * 配置信息预览
     */
    public static class ConfigInfoPreview {
        private boolean hasValidConfig;
        private String configMessage;
        private String templateId;
        private String templateName;
        private String sceneType;
        private String visibility;
        private String category;
        private List<RolePreview> roles;
        private Map<String, List<ActivationStepPreview>> activationSteps;
        private Map<String, List<MenuPreview>> menus;
        private List<PrivateCapabilityPreview> privateCapabilities;

        public boolean isHasValidConfig() { return hasValidConfig; }
        public void setHasValidConfig(boolean hasValidConfig) { this.hasValidConfig = hasValidConfig; }
        public String getConfigMessage() { return configMessage; }
        public void setConfigMessage(String configMessage) { this.configMessage = configMessage; }
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getTemplateName() { return templateName; }
        public void setTemplateName(String templateName) { this.templateName = templateName; }
        public String getSceneType() { return sceneType; }
        public void setSceneType(String sceneType) { this.sceneType = sceneType; }
        public String getVisibility() { return visibility; }
        public void setVisibility(String visibility) { this.visibility = visibility; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<RolePreview> getRoles() { return roles; }
        public void setRoles(List<RolePreview> roles) { this.roles = roles; }
        public Map<String, List<ActivationStepPreview>> getActivationSteps() { return activationSteps; }
        public void setActivationSteps(Map<String, List<ActivationStepPreview>> activationSteps) { this.activationSteps = activationSteps; }
        public Map<String, List<MenuPreview>> getMenus() { return menus; }
        public void setMenus(Map<String, List<MenuPreview>> menus) { this.menus = menus; }
        public List<PrivateCapabilityPreview> getPrivateCapabilities() { return privateCapabilities; }
        public void setPrivateCapabilities(List<PrivateCapabilityPreview> privateCapabilities) { this.privateCapabilities = privateCapabilities; }
    }

    /**
     * 角色预览
     */
    public static class RolePreview {
        private String roleId;
        private String roleName;
        private String description;
        private boolean required;
        private int minCount;
        private int maxCount;
        private List<String> permissions;

        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public int getMinCount() { return minCount; }
        public void setMinCount(int minCount) { this.minCount = minCount; }
        public int getMaxCount() { return maxCount; }
        public void setMaxCount(int maxCount) { this.maxCount = maxCount; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    }

    /**
     * 激活步骤预览
     */
    public static class ActivationStepPreview {
        private String stepId;
        private String stepName;
        private String description;
        private String stepType;
        private int order;
        private boolean required;
        private boolean skippable;
        private boolean autoExecute;

        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStepType() { return stepType; }
        public void setStepType(String stepType) { this.stepType = stepType; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public boolean isSkippable() { return skippable; }
        public void setSkippable(boolean skippable) { this.skippable = skippable; }
        public boolean isAutoExecute() { return autoExecute; }
        public void setAutoExecute(boolean autoExecute) { this.autoExecute = autoExecute; }
    }

    /**
     * 菜单预览
     */
    public static class MenuPreview {
        private String id;
        private String name;
        private String icon;
        private String url;
        private int order;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
    }

    /**
     * 私有能力预览
     */
    public static class PrivateCapabilityPreview {
        private String capId;
        private String name;
        private String description;

        public String getCapId() { return capId; }
        public void setCapId(String capId) { this.capId = capId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 依赖信息预览
     */
    public static class DependencyInfoPreview {
        private boolean hasDependencies;
        private boolean allSatisfied;
        private String message;
        private int requiredCount;
        private int satisfiedCount;
        private List<DependencyCheckEngine.DependencyCheckItem> unsatisfiedRequired;

        public boolean isHasDependencies() { return hasDependencies; }
        public void setHasDependencies(boolean hasDependencies) { this.hasDependencies = hasDependencies; }
        public boolean isAllSatisfied() { return allSatisfied; }
        public void setAllSatisfied(boolean allSatisfied) { this.allSatisfied = allSatisfied; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getRequiredCount() { return requiredCount; }
        public void setRequiredCount(int requiredCount) { this.requiredCount = requiredCount; }
        public int getSatisfiedCount() { return satisfiedCount; }
        public void setSatisfiedCount(int satisfiedCount) { this.satisfiedCount = satisfiedCount; }
        public List<DependencyCheckEngine.DependencyCheckItem> getUnsatisfiedRequired() { return unsatisfiedRequired; }
        public void setUnsatisfiedRequired(List<DependencyCheckEngine.DependencyCheckItem> unsatisfiedRequired) { this.unsatisfiedRequired = unsatisfiedRequired; }
    }
}
