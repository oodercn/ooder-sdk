package net.ooder.scene.ui;

import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.InstalledSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nexus UI 加载器
 * 负责扫描和加载已安装的 UI Skills
 *
 * <p>在应用启动时自动扫描已安装的 UI Skills，并注册到 NexusUiRegistry</p>
 *
 * @author ooder
 * @since 2.3
 */
@Component
public class NexusUiLoader {

    private static final Logger log = LoggerFactory.getLogger(NexusUiLoader.class);

    /** UI Skill 类型标识 */
    private static final String UI_SKILL_TYPE = "nexus-ui";

    @Autowired
    private SkillPackageManager skillPackageManager;

    @Autowired
    private NexusUiRegistry uiRegistry;

    /** 已加载的 UI Skill 缓存 */
    private final Map<String, NexusUiConfig> loadedUis = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("NexusUiLoader initializing...");
        scanAndLoadInstalledUiSkills();
        log.info("NexusUiLoader initialized, loaded {} UI skills", loadedUis.size());
    }

    /**
     * 扫描并加载已安装的 UI Skills
     */
    public void scanAndLoadInstalledUiSkills() {
        log.info("Scanning installed UI skills...");

        try {
            List<InstalledSkill> installedSkills = skillPackageManager.listInstalled().join();

            for (InstalledSkill skill : installedSkills) {
                loadUiSkill(skill.getSkillId());
            }

            log.info("Scanned {} installed skills, loaded {} UI skills",
                    installedSkills.size(), loadedUis.size());

        } catch (Exception e) {
            log.error("Failed to scan installed UI skills", e);
        }
    }

    /**
     * 加载指定的 UI Skill
     * @param skillId Skill ID
     * @return 是否成功加载
     */
    public boolean loadUiSkill(String skillId) {
        // 如果已经加载，跳过
        if (loadedUis.containsKey(skillId)) {
            log.debug("UI skill already loaded: {}", skillId);
            return true;
        }

        try {
            // 获取 Skill 元数据
            SkillManifest manifest = skillPackageManager.getManifest(skillId).join();
            if (manifest == null) {
                log.warn("Skill manifest not found: {}", skillId);
                return false;
            }

            // 检查是否是 UI Skill
            if (!isUiSkill(manifest)) {
                log.debug("Not a UI skill: {}", skillId);
                return false;
            }

            // 解析 UI 配置
            NexusUiConfig config = parseUiConfig(skillId, manifest);
            if (config == null) {
                log.warn("Failed to parse UI config for skill: {}", skillId);
                return false;
            }

            // 注册到注册表
            uiRegistry.register(config);
            loadedUis.put(skillId, config);

            log.info("Loaded UI skill: {} ({})", skillId, config.getName());
            return true;

        } catch (Exception e) {
            log.error("Failed to load UI skill: {}", skillId, e);
            return false;
        }
    }

    /**
     * 卸载指定的 UI Skill
     * @param skillId Skill ID
     */
    public void unloadUiSkill(String skillId) {
        NexusUiConfig config = loadedUis.remove(skillId);
        if (config != null) {
            uiRegistry.unregister(skillId);
            log.info("Unloaded UI skill: {}", skillId);
        }
    }

    /**
     * 重新加载指定的 UI Skill
     * @param skillId Skill ID
     * @return 是否成功加载
     */
    public boolean reloadUiSkill(String skillId) {
        unloadUiSkill(skillId);
        return loadUiSkill(skillId);
    }

    /**
     * 检查 Skill 是否是 UI Skill
     * @param manifest Skill 元数据
     * @return 是否是 UI Skill
     */
    private boolean isUiSkill(SkillManifest manifest) {
        // 检查 skillType
        if (UI_SKILL_TYPE.equals(manifest.getSkillType())) {
            return true;
        }

        // 检查 config 中的 ui 配置
        Map<String, Object> config = manifest.getConfig();
        if (config != null && config.containsKey("ui")) {
            return true;
        }

        return false;
    }

    /**
     * 解析 UI 配置
     * @param skillId Skill ID
     * @param manifest Skill 元数据
     * @return UI 配置
     */
    private NexusUiConfig parseUiConfig(String skillId, SkillManifest manifest) {
        NexusUiConfig config = new NexusUiConfig();
        config.setSkillId(skillId);
        config.setName(manifest.getName());
        config.setDescription(manifest.getDescription());
        config.setType(UI_SKILL_TYPE);

        // 从 config 中解析 UI 配置
        Map<String, Object> manifestConfig = manifest.getConfig();
        if (manifestConfig != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> uiConfig = (Map<String, Object>) manifestConfig.get("ui");

            if (uiConfig != null) {
                // 解析图标
                config.setIcon((String) uiConfig.get("icon"));

                // 解析入口路径
                config.setEntryPath((String) uiConfig.get("entryPath"));

                // 解析菜单配置
                @SuppressWarnings("unchecked")
                Map<String, Object> menuConfig = (Map<String, Object>) uiConfig.get("menu");
                if (menuConfig != null) {
                    config.setMenu(parseMenuConfig(menuConfig));
                }

                // 解析路由配置
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> routesConfig = (List<Map<String, Object>>) uiConfig.get("routes");
                if (routesConfig != null) {
                    config.setRoutes(parseRoutesConfig(routesConfig));
                }

                // 解析脚本和样式
                @SuppressWarnings("unchecked")
                List<String> scripts = (List<String>) uiConfig.get("scripts");
                config.setScripts(scripts);

                @SuppressWarnings("unchecked")
                List<String> styles = (List<String>) uiConfig.get("styles");
                config.setStyles(styles);
            }
        }

        return config;
    }

    /**
     * 解析菜单配置
     * @param menuConfig 菜单配置 Map
     * @return 菜单配置对象
     */
    private MenuConfig parseMenuConfig(Map<String, Object> menuConfig) {
        MenuConfig menu = new MenuConfig();
        menu.setMenuId((String) menuConfig.get("menuId"));
        menu.setTitle((String) menuConfig.get("title"));
        menu.setParentId((String) menuConfig.get("parentId"));
        menu.setIcon((String) menuConfig.get("icon"));
        menu.setPath((String) menuConfig.get("path"));

        if (menuConfig.containsKey("order")) {
            menu.setOrder((Integer) menuConfig.get("order"));
        }

        if (menuConfig.containsKey("visible")) {
            menu.setVisible((Boolean) menuConfig.get("visible"));
        }

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) menuConfig.get("permissions");
        menu.setPermissions(permissions);

        return menu;
    }

    /**
     * 解析路由配置
     * @param routesConfig 路由配置列表
     * @return 路由配置对象列表
     */
    private List<RouteConfig> parseRoutesConfig(List<Map<String, Object>> routesConfig) {
        List<RouteConfig> routes = new java.util.ArrayList<>();

        for (Map<String, Object> routeConfig : routesConfig) {
            RouteConfig route = new RouteConfig();
            route.setPath((String) routeConfig.get("path"));
            route.setComponent((String) routeConfig.get("component"));
            route.setTitle((String) routeConfig.get("title"));

            if (routeConfig.containsKey("exact")) {
                route.setExact((Boolean) routeConfig.get("exact"));
            }

            if (routeConfig.containsKey("requireAuth")) {
                route.setRequireAuth((Boolean) routeConfig.get("requireAuth"));
            }

            routes.add(route);
        }

        return routes;
    }

    /**
     * 获取已加载的 UI Skill 列表
     * @return UI 配置列表
     */
    public List<NexusUiConfig> getLoadedUis() {
        return new java.util.ArrayList<>(loadedUis.values());
    }

    /**
     * 检查 UI Skill 是否已加载
     * @param skillId Skill ID
     * @return 是否已加载
     */
    public boolean isLoaded(String skillId) {
        return loadedUis.containsKey(skillId);
    }
}
