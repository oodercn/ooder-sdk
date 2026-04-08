package net.ooder.skill.hotplug.ui;

import net.ooder.skill.hotplug.model.SkillComponent;
import net.ooder.skill.hotplug.model.SkillMenu;
import net.ooder.skill.hotplug.model.SkillPage;
import net.ooder.skill.hotplug.model.SkillUiConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * UI配置解析辅助类
 */
public class UiConfigResolver {

    public SkillUiConfig resolve(String skillId, Map<String, Object> spec) {
        if (spec == null) {
            return null;
        }

        SkillUiConfig config = new SkillUiConfig();
        config.setSkillId(skillId);
        config.setBasePath("/skill/" + skillId);

        resolveMenus(config, spec);
        resolvePages(config, spec);
        resolveComponents(config, spec);
        resolveNexusUi(config, spec);

        return config;
    }

    @SuppressWarnings("unchecked")
    private void resolveMenus(SkillUiConfig config, Map<String, Object> spec) {
        Object menusObj = spec.get("menus");
        if (menusObj == null) {
            return;
        }

        if (menusObj instanceof Map) {
            Map<String, Object> menusByRole = (Map<String, Object>) menusObj;
            for (Map.Entry<String, Object> entry : menusByRole.entrySet()) {
                String role = entry.getKey();
                Object roleMenusObj = entry.getValue();

                if (roleMenusObj instanceof List) {
                    List<Map<String, Object>> roleMenus = (List<Map<String, Object>>) roleMenusObj;
                    for (Map<String, Object> menuData : roleMenus) {
                        SkillMenu menu = parseMenu(menuData, role, config.getSkillId());
                        config.getMenus().add(menu);
                    }
                }
            }
        } else if (menusObj instanceof List) {
            List<Map<String, Object>> menus = (List<Map<String, Object>>) menusObj;
            for (Map<String, Object> menuData : menus) {
                SkillMenu menu = parseMenu(menuData, null, config.getSkillId());
                config.getMenus().add(menu);
            }
        }
    }

    private SkillMenu parseMenu(Map<String, Object> data, String role, String skillId) {
        SkillMenu menu = new SkillMenu();
        menu.setId((String) data.get("id"));
        menu.setName((String) data.get("name"));
        menu.setIcon((String) data.get("icon"));
        menu.setPath((String) data.get("path"));
        menu.setDescription((String) data.get("description"));
        menu.setParentId((String) data.get("parentId"));
        menu.setRole(role);
        menu.setSkillId(skillId);

        Object orderObj = data.get("order");
        if (orderObj instanceof Number) {
            menu.setOrder(((Number) orderObj).intValue());
        }

        Object visibleObj = data.get("visible");
        if (visibleObj instanceof Boolean) {
            menu.setVisible((Boolean) visibleObj);
        }

        return menu;
    }

    @SuppressWarnings("unchecked")
    private void resolvePages(SkillUiConfig config, Map<String, Object> spec) {
        Object pagesObj = spec.get("pages");
        if (pagesObj == null) {
            return;
        }

        if (pagesObj instanceof List) {
            List<Map<String, Object>> pages = (List<Map<String, Object>>) pagesObj;
            for (Map<String, Object> pageData : pages) {
                SkillPage page = parsePage(pageData, config.getSkillId());
                config.getPages().add(page);
            }
        }

        Object routesObj = spec.get("routes");
        if (routesObj instanceof List) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) routesObj;
            for (Map<String, Object> routeData : routes) {
                String path = (String) routeData.get("path");
                if (path != null && (path.endsWith(".html") || path.contains("/pages/"))) {
                    SkillPage page = new SkillPage();
                    page.setId(path.replaceAll("[^a-zA-Z0-9]", "-"));
                    page.setPath(path);
                    page.setHtmlPath(path);
                    page.setSkillId(config.getSkillId());
                    
                    String produces = (String) routeData.get("produces");
                    page.setVisible(!"text/event-stream".equals(produces));
                    
                    config.getPages().add(page);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private SkillPage parsePage(Map<String, Object> data, String skillId) {
        SkillPage page = new SkillPage();
        page.setId((String) data.get("id"));
        page.setName((String) data.get("name"));
        page.setPath((String) data.get("path"));
        page.setHtmlPath((String) data.get("htmlPath"));
        page.setJsPath((String) data.get("jsPath"));
        page.setCssPath((String) data.get("cssPath"));
        page.setRole((String) data.get("role"));
        page.setSkillId(skillId);

        Object visibleObj = data.get("visible");
        if (visibleObj instanceof Boolean) {
            page.setVisible((Boolean) visibleObj);
        }

        Object cacheableObj = data.get("cacheable");
        if (cacheableObj instanceof Boolean) {
            page.setCacheable((Boolean) cacheableObj);
        }

        Object jsDepsObj = data.get("jsDependencies");
        if (jsDepsObj instanceof List) {
            for (Object dep : (List<?>) jsDepsObj) {
                page.getJsDependencies().add(String.valueOf(dep));
            }
        }

        Object cssDepsObj = data.get("cssDependencies");
        if (cssDepsObj instanceof List) {
            for (Object dep : (List<?>) cssDepsObj) {
                page.getCssDependencies().add(String.valueOf(dep));
            }
        }

        return page;
    }

    @SuppressWarnings("unchecked")
    private void resolveComponents(SkillUiConfig config, Map<String, Object> spec) {
        Object componentsObj = spec.get("components");
        if (componentsObj == null) {
            return;
        }

        if (componentsObj instanceof List) {
            List<Map<String, Object>> components = (List<Map<String, Object>>) componentsObj;
            for (Map<String, Object> compData : components) {
                SkillComponent component = parseComponent(compData, config.getSkillId());
                config.getComponents().add(component);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private SkillComponent parseComponent(Map<String, Object> data, String skillId) {
        SkillComponent component = new SkillComponent();
        component.setId((String) data.get("id"));
        component.setName((String) data.get("name"));
        component.setType((String) data.get("type"));
        component.setPath((String) data.get("path"));
        component.setSelector((String) data.get("selector"));
        component.setSkillId(skillId);

        Object propsObj = data.get("props");
        if (propsObj instanceof Map) {
            component.setProps((Map<String, Object>) propsObj);
        }

        return component;
    }

    @SuppressWarnings("unchecked")
    private void resolveNexusUi(SkillUiConfig config, Map<String, Object> spec) {
        Object nexusUiObj = spec.get("nexusUi");
        if (nexusUiObj instanceof Map) {
            config.setNexusUi((Map<String, Object>) nexusUiObj);
        }
    }
}
