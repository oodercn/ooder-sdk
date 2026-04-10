package net.ooder.skill.hotplug.ui;

import net.ooder.skill.hotplug.model.SkillMenu;
import net.ooder.skill.hotplug.model.SkillPage;
import net.ooder.skill.hotplug.model.SkillUiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UI路由注册器
 * 管理Skill的UI页面路由
 */
public class UiRouteRegistry {

    private static final Logger logger = LoggerFactory.getLogger(UiRouteRegistry.class);

    private final Map<String, SkillUiConfig> uiConfigs = new ConcurrentHashMap<>();
    private final Map<String, SkillPage> pageRoutes = new ConcurrentHashMap<>();
    private final Map<String, List<SkillMenu>> menuRegistry = new ConcurrentHashMap<>();

    public void register(String skillId, SkillUiConfig config) {
        if (config == null) {
            return;
        }

        uiConfigs.put(skillId, config);

        registerPages(skillId, config);
        registerMenus(skillId, config);

        logger.info("[UiRouteRegistry] Registered UI routes for skill: {}, pages={}, menus={}",
                skillId, config.getPages().size(), config.getMenus().size());
    }

    private void registerPages(String skillId, SkillUiConfig config) {
        for (SkillPage page : config.getPages()) {
            String routeKey = skillId + ":" + page.getPath();
            pageRoutes.put(routeKey, page);
            logger.debug("[UiRouteRegistry] Registered page: {} -> {}", routeKey, page.getName());
        }
    }

    private void registerMenus(String skillId, SkillUiConfig config) {
        if (config.hasMenus()) {
            menuRegistry.put(skillId, config.getMenus());
        }
    }

    public void unregister(String skillId) {
        SkillUiConfig config = uiConfigs.remove(skillId);
        if (config != null) {
            for (SkillPage page : config.getPages()) {
                String routeKey = skillId + ":" + page.getPath();
                pageRoutes.remove(routeKey);
            }
            menuRegistry.remove(skillId);
            logger.info("[UiRouteRegistry] Unregistered UI routes for skill: {}", skillId);
        }
    }

    public SkillUiConfig getUiConfig(String skillId) {
        return uiConfigs.get(skillId);
    }

    public SkillPage getPage(String skillId, String path) {
        String routeKey = skillId + ":" + path;
        return pageRoutes.get(routeKey);
    }

    public List<SkillPage> getPages(String skillId) {
        SkillUiConfig config = uiConfigs.get(skillId);
        return config != null ? config.getPages() : Collections.emptyList();
    }

    public List<SkillMenu> getMenus(String skillId) {
        return menuRegistry.getOrDefault(skillId, Collections.emptyList());
    }

    public List<SkillMenu> getMenusByRole(String skillId, String role) {
        List<SkillMenu> allMenus = menuRegistry.get(skillId);
        if (allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }

        List<SkillMenu> roleMenus = new ArrayList<>();
        for (SkillMenu menu : allMenus) {
            if (role.equals(menu.getRole())) {
                roleMenus.add(menu);
            }
        }
        return roleMenus;
    }

    public Map<String, List<SkillMenu>> getAllMenus() {
        return new HashMap<>(menuRegistry);
    }

    public List<SkillUiConfig> getAllUiConfigs() {
        return new ArrayList<>(uiConfigs.values());
    }

    public boolean hasUi(String skillId) {
        return uiConfigs.containsKey(skillId);
    }

    public Set<String> getRegisteredSkillIds() {
        return new HashSet<>(uiConfigs.keySet());
    }

    public void clear() {
        uiConfigs.clear();
        pageRoutes.clear();
        menuRegistry.clear();
        logger.info("[UiRouteRegistry] Cleared all UI routes");
    }

    public Map<String, Object> getRouteInfo(String skillId) {
        SkillUiConfig config = uiConfigs.get(skillId);
        if (config == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("skillId", skillId);
        info.put("basePath", config.getBasePath());

        List<Map<String, Object>> pagesInfo = new ArrayList<>();
        for (SkillPage page : config.getPages()) {
            Map<String, Object> pageInfo = new LinkedHashMap<>();
            pageInfo.put("id", page.getId());
            pageInfo.put("name", page.getName());
            pageInfo.put("path", page.getPath());
            pageInfo.put("htmlPath", page.getDefaultHtmlPath());
            pageInfo.put("visible", page.isVisible());
            pagesInfo.add(pageInfo);
        }
        info.put("pages", pagesInfo);

        List<Map<String, Object>> menusInfo = new ArrayList<>();
        for (SkillMenu menu : config.getMenus()) {
            Map<String, Object> menuInfo = new LinkedHashMap<>();
            menuInfo.put("id", menu.getId());
            menuInfo.put("name", menu.getName());
            menuInfo.put("icon", menu.getIcon());
            menuInfo.put("path", menu.getFullPath());
            menuInfo.put("order", menu.getOrder());
            menuInfo.put("role", menu.getRole());
            menuInfo.put("visible", menu.isVisible());
            menusInfo.add(menuInfo);
        }
        info.put("menus", menusInfo);

        return info;
    }
}
