package net.ooder.scene.skill.install;

import net.ooder.scene.core.template.ActivationStepConfig;
import net.ooder.scene.core.template.RoleConfig;
import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.skill.exception.SceneValidationException;
import net.ooder.scene.ui.MenuConfig;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 场景配置加载器
 *
 * <p>从技能包的 skill.yaml 读取场景配置</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(SceneConfigLoader.class);

    /**
     * 从技能包加载场景配置
     *
     * @param skillId      技能ID
     * @param skillPackage 技能包
     * @return 场景模板配置，如果不存在则返回 null
     */
    public SceneTemplate loadSceneConfig(String skillId, SkillPackage skillPackage) {
        if (skillPackage == null) {
            log.warn("[loadSceneConfig] SkillPackage is null for: {}", skillId);
            return null;
        }

        try {
            Map<String, Object> metadata = skillPackage.getMetadata();
            if (metadata == null) {
                log.debug("[loadSceneConfig] No metadata found for: {}", skillId);
                return null;
            }

            Object spec = metadata.get("spec");
            if (!(spec instanceof Map)) {
                log.debug("[loadSceneConfig] No spec found in metadata for: {}", skillId);
                return null;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> specMap = (Map<String, Object>) spec;

            SceneTemplate template = new SceneTemplate();
            template.setTemplateId(skillId);
            template.setTemplateName((String) metadata.get("name"));
            template.setDescription((String) metadata.get("description"));
            template.setVersion((String) metadata.get("version"));

            loadCapabilityConfig(template, specMap);
            loadSceneConfig(template, specMap);
            loadRolesConfig(template, specMap);
            loadActivationStepsConfig(template, specMap);
            loadMenusConfig(template, specMap);
            loadPrivateCapabilitiesConfig(template, specMap);
            loadUiSkillsConfig(template, specMap);

            log.info("[loadSceneConfig] Loaded scene config for: {}", skillId);
            return template;

        } catch (Exception e) {
            log.error("[loadSceneConfig] Failed to load scene config for {}: {}", skillId, e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCapabilityConfig(SceneTemplate template, Map<String, Object> spec) {
        Object capability = spec.get("capability");
        if (capability instanceof Map) {
            Map<String, Object> capMap = (Map<String, Object>) capability;
            template.setCategory((String) capMap.get("category"));
            template.setCapabilityCode((String) capMap.get("code"));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSceneConfig(SceneTemplate template, Map<String, Object> spec) {
        Object scene = spec.get("scene");
        if (scene instanceof Map) {
            Map<String, Object> sceneMap = (Map<String, Object>) scene;
            template.setSceneType((String) sceneMap.get("type"));
            template.setVisibility((String) sceneMap.get("visibility"));
            template.setParticipantMode((String) sceneMap.get("participantMode"));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRolesConfig(SceneTemplate template, Map<String, Object> spec) {
        Object roles = spec.get("roles");
        if (!(roles instanceof List)) {
            return;
        }

        List<RoleConfig> roleConfigs = new ArrayList<>();
        List<Map<String, Object>> rolesList = (List<Map<String, Object>>) roles;

        for (Map<String, Object> roleMap : rolesList) {
            RoleConfig role = new RoleConfig();
            role.setRoleId((String) roleMap.get("name"));
            role.setRoleName((String) roleMap.get("name"));
            role.setName((String) roleMap.get("name"));
            role.setDescription((String) roleMap.get("description"));

            Object required = roleMap.get("required");
            role.setRequired(required instanceof Boolean ? (Boolean) required : true);

            Object minCount = roleMap.get("minCount");
            role.setMinCount(minCount instanceof Number ? ((Number) minCount).intValue() : 1);

            Object maxCount = roleMap.get("maxCount");
            role.setMaxCount(maxCount instanceof Number ? ((Number) maxCount).intValue() : 1);

            Object permissions = roleMap.get("permissions");
            if (permissions instanceof List) {
                role.setPermissions((List<String>) permissions);
            }

            roleConfigs.add(role);
        }

        template.setRoles(roleConfigs);
    }

    @SuppressWarnings("unchecked")
    private void loadActivationStepsConfig(SceneTemplate template, Map<String, Object> spec) {
        Object activationSteps = spec.get("activationSteps");
        if (!(activationSteps instanceof Map)) {
            return;
        }

        Map<String, List<ActivationStepConfig>> stepsMap = new HashMap<>();
        Map<String, Object> stepsByRole = (Map<String, Object>) activationSteps;

        for (Map.Entry<String, Object> entry : stepsByRole.entrySet()) {
            String roleId = entry.getKey();
            Object steps = entry.getValue();

            if (!(steps instanceof List)) {
                continue;
            }

            List<ActivationStepConfig> stepConfigs = new ArrayList<>();
            List<Map<String, Object>> stepsList = (List<Map<String, Object>>) steps;

            int order = 1;
            for (Map<String, Object> stepMap : stepsList) {
                ActivationStepConfig step = new ActivationStepConfig();
                step.setStepId((String) stepMap.get("stepId"));
                step.setStepName((String) stepMap.get("name"));
                step.setName((String) stepMap.get("name"));
                step.setDescription((String) stepMap.get("description"));
                step.setOrder(order++);

                Object required = stepMap.get("required");
                step.setRequired(required instanceof Boolean ? (Boolean) required : true);

                Object skippable = stepMap.get("skippable");
                step.setSkippable(skippable instanceof Boolean ? (Boolean) skippable : false);

                Object autoExecute = stepMap.get("autoExecute");
                step.setAutoExecute(autoExecute instanceof Boolean ? (Boolean) autoExecute : false);

                Object privateCapabilities = stepMap.get("privateCapabilities");
                if (privateCapabilities instanceof List) {
                    step.setPrivateCapabilities((List<String>) privateCapabilities);
                }

                stepConfigs.add(step);
            }

            stepsMap.put(roleId, stepConfigs);
        }

        template.setActivationSteps(stepsMap);
    }

    @SuppressWarnings("unchecked")
    private void loadMenusConfig(SceneTemplate template, Map<String, Object> spec) {
        Object menus = spec.get("menus");
        if (!(menus instanceof Map)) {
            return;
        }

        Map<String, List<MenuConfig>> menusMap = new HashMap<>();
        Map<String, Object> menusByRole = (Map<String, Object>) menus;

        for (Map.Entry<String, Object> entry : menusByRole.entrySet()) {
            String roleId = entry.getKey();
            Object roleMenus = entry.getValue();

            if (!(roleMenus instanceof List)) {
                continue;
            }

            List<MenuConfig> menuConfigs = new ArrayList<>();
            List<Map<String, Object>> menusList = (List<Map<String, Object>>) roleMenus;

            for (Map<String, Object> menuMap : menusList) {
                MenuConfig menu = new MenuConfig();
                menu.setMenuId((String) menuMap.get("id"));
                menu.setTitle((String) menuMap.get("name"));
                menu.setIcon((String) menuMap.get("icon"));
                menu.setPath((String) menuMap.get("url"));

                Object order = menuMap.get("order");
                menu.setOrder(order instanceof Number ? ((Number) order).intValue() : 0);

                Object visible = menuMap.get("visible");
                menu.setVisible(visible instanceof Boolean ? (Boolean) visible : true);

                menuConfigs.add(menu);
            }

            menusMap.put(roleId, menuConfigs);
        }

        template.setMenus(menusMap);
    }

    @SuppressWarnings("unchecked")
    private void loadPrivateCapabilitiesConfig(SceneTemplate template, Map<String, Object> spec) {
        Object privateCapabilities = spec.get("privateCapabilities");
        if (!(privateCapabilities instanceof List)) {
            return;
        }

        List<SceneTemplate.PrivateCapabilityConfig> capConfigs = new ArrayList<>();
        List<Map<String, Object>> capsList = (List<Map<String, Object>>) privateCapabilities;

        for (Map<String, Object> capMap : capsList) {
            SceneTemplate.PrivateCapabilityConfig cap = new SceneTemplate.PrivateCapabilityConfig();
            cap.setCapabilityId((String) capMap.get("capId"));
            cap.setCapabilityName((String) capMap.get("name"));
            cap.setDescription((String) capMap.get("description"));
            capConfigs.add(cap);
        }

        template.setPrivateCapabilities(capConfigs);
    }

    @SuppressWarnings("unchecked")
    private void loadUiSkillsConfig(SceneTemplate template, Map<String, Object> spec) {
        Object uiSkills = spec.get("uiSkills");
        if (!(uiSkills instanceof List)) {
            return;
        }

        List<SceneTemplate.UiSkillConfig> uiSkillConfigs = new ArrayList<>();
        List<Map<String, Object>> uiSkillsList = (List<Map<String, Object>>) uiSkills;

        for (Map<String, Object> uiSkillMap : uiSkillsList) {
            SceneTemplate.UiSkillConfig uiSkill = new SceneTemplate.UiSkillConfig();
            uiSkill.setSkillId((String) uiSkillMap.get("skillId"));
            uiSkill.setName((String) uiSkillMap.get("name"));
            uiSkill.setDescription((String) uiSkillMap.get("description"));
            uiSkill.setEntryPoint((String) uiSkillMap.get("entryPoint"));
            uiSkill.setEntryComponent((String) uiSkillMap.get("entryComponent"));
            uiSkill.setType((String) uiSkillMap.getOrDefault("type", "web"));
            uiSkill.setTheme((String) uiSkillMap.getOrDefault("theme", "default"));

            Object enabled = uiSkillMap.get("enabled");
            uiSkill.setEnabled(enabled instanceof Boolean ? (Boolean) enabled : true);

            Object order = uiSkillMap.get("order");
            uiSkill.setOrder(order instanceof Number ? ((Number) order).intValue() : 0);

            Object dependencies = uiSkillMap.get("dependencies");
            if (dependencies instanceof List) {
                uiSkill.setDependencies((List<String>) dependencies);
            }

            Object config = uiSkillMap.get("config");
            if (config instanceof Map) {
                uiSkill.setConfig((Map<String, Object>) config);
            }

            Object routes = uiSkillMap.get("routes");
            if (routes instanceof List) {
                List<SceneTemplate.UiSkillConfig.RouteConfig> routeConfigs = new ArrayList<>();
                List<Map<String, Object>> routesList = (List<Map<String, Object>>) routes;
                for (Map<String, Object> routeMap : routesList) {
                    SceneTemplate.UiSkillConfig.RouteConfig route = new SceneTemplate.UiSkillConfig.RouteConfig();
                    route.setPath((String) routeMap.get("path"));
                    route.setComponent((String) routeMap.get("component"));
                    route.setName((String) routeMap.get("name"));
                    route.setTitle((String) routeMap.get("title"));
                    route.setIcon((String) routeMap.get("icon"));
                    
                    Object authRequired = routeMap.get("authRequired");
                    route.setAuthRequired(authRequired instanceof Boolean ? (Boolean) authRequired : true);
                    
                    Object roles = routeMap.get("roles");
                    if (roles instanceof List) {
                        route.setRoles((List<String>) roles);
                    }
                    
                    Object meta = routeMap.get("meta");
                    if (meta instanceof Map) {
                        route.setMeta((Map<String, Object>) meta);
                    }
                    
                    routeConfigs.add(route);
                }
                uiSkill.setRoutes(routeConfigs);
            }

            Object components = uiSkillMap.get("components");
            if (components instanceof List) {
                List<SceneTemplate.UiSkillConfig.ComponentConfig> componentConfigs = new ArrayList<>();
                List<Map<String, Object>> componentsList = (List<Map<String, Object>>) components;
                for (Map<String, Object> compMap : componentsList) {
                    SceneTemplate.UiSkillConfig.ComponentConfig component = new SceneTemplate.UiSkillConfig.ComponentConfig();
                    component.setComponentId((String) compMap.get("componentId"));
                    component.setName((String) compMap.get("name"));
                    component.setType((String) compMap.get("type"));
                    component.setSelector((String) compMap.get("selector"));
                    component.setTemplate((String) compMap.get("template"));
                    component.setStyle((String) compMap.get("style"));
                    
                    Object lazy = compMap.get("lazy");
                    component.setLazy(lazy instanceof Boolean ? (Boolean) lazy : false);
                    
                    Object props = compMap.get("props");
                    if (props instanceof Map) {
                        component.setProps((Map<String, Object>) props);
                    }
                    
                    Object slots = compMap.get("slots");
                    if (slots instanceof List) {
                        component.setSlots((List<String>) slots);
                    }
                    
                    componentConfigs.add(component);
                }
                uiSkill.setComponents(componentConfigs);
            }

            uiSkillConfigs.add(uiSkill);
        }

        template.setUiSkills(uiSkillConfigs);
        log.debug("[loadUiSkillsConfig] Loaded {} UI skills", uiSkillConfigs.size());
    }

    /**
     * 验证场景配置完整性
     *
     * @param skillId  技能ID
     * @param template 场景模板
     * @throws SceneValidationException 验证失败时抛出
     */
    public void validateSceneConfig(String skillId, SceneTemplate template) {
        if (template == null) {
            throw new SceneValidationException(skillId, "SCENE_CONFIG_MISSING",
                "场景配置缺失: 技能包中未定义场景配置，且未找到场景模板。" +
                "请在 skill.yaml 中添加 spec.roles、spec.activationSteps、spec.menus 配置，" +
                "或在 src/main/resources/templates/ 目录创建场景模板文件。");
        }

        if (template.getRoles() == null || template.getRoles().isEmpty()) {
            throw new SceneValidationException(skillId, "ROLES_MISSING",
                "场景缺少角色定义: 请在 skill.yaml 的 spec.roles 中定义场景角色");
        }

        boolean hasRequiredRole = template.getRoles().stream()
            .anyMatch(role -> role.isRequired() && role.getMinCount() > 0);
        if (!hasRequiredRole) {
            throw new SceneValidationException(skillId, "REQUIRED_ROLE_MISSING",
                "场景缺少必需角色: 请至少定义一个 required=true 且 minCount>0 的角色");
        }

        if (template.getActivationSteps() == null || template.getActivationSteps().isEmpty()) {
            throw new SceneValidationException(skillId, "ACTIVATION_STEPS_MISSING",
                "场景缺少激活步骤: 请在 skill.yaml 的 spec.activationSteps 中定义激活流程");
        }

        for (RoleConfig role : template.getRoles()) {
            if (role.isRequired()) {
                List<ActivationStepConfig> steps = template.getActivationStepsForRole(role.getRoleId());
                if (steps == null || steps.isEmpty()) {
                    steps = template.getActivationStepsForRole(role.getName());
                }
                if (steps == null || steps.isEmpty()) {
                    throw new SceneValidationException(skillId, "ROLE_ACTIVATION_STEPS_MISSING",
                        "必需角色缺少激活步骤: 角色 " + role.getName() + " 需要定义激活步骤");
                }
            }
        }

        if (template.getMenus() == null || template.getMenus().isEmpty()) {
            throw new SceneValidationException(skillId, "MENUS_MISSING",
                "场景缺少菜单配置: 请在 skill.yaml 的 spec.menus 中定义菜单");
        }

        for (RoleConfig role : template.getRoles()) {
            if (role.isRequired()) {
                List<MenuConfig> menus = template.getMenusForRole(role.getRoleId());
                if (menus == null || menus.isEmpty()) {
                    menus = template.getMenusForRole(role.getName());
                }
                if (menus == null || menus.isEmpty()) {
                    throw new SceneValidationException(skillId, "ROLE_MENUS_MISSING",
                        "必需角色缺少菜单: 角色 " + role.getName() + " 需要定义菜单");
                }
            }
        }

        log.info("[validateSceneConfig] Scene config validated for: {}", skillId);
    }
}
