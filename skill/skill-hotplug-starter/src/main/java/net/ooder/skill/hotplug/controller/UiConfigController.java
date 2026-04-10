package net.ooder.skill.hotplug.controller;

import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.model.SkillUiConfig;
import net.ooder.skill.hotplug.ui.ComponentLoader;
import net.ooder.skill.hotplug.ui.PageCacheManager;
import net.ooder.skill.hotplug.ui.PageVersionManager;
import net.ooder.skill.hotplug.ui.UiRouteRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * UI配置控制器
 * 提供Skill UI配置查询API
 */
@RestController
@RequestMapping("/api/v1/skill-ui")
public class UiConfigController {

    private static final Logger logger = LoggerFactory.getLogger(UiConfigController.class);

    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private UiRouteRegistry uiRouteRegistry;

    @Autowired(required = false)
    private ComponentLoader componentLoader;

    @Autowired(required = false)
    private PageCacheManager pageCacheManager;

    @Autowired(required = false)
    private PageVersionManager pageVersionManager;

    @GetMapping("/menus")
    public ResponseEntity<Map<String, Object>> getAllMenus() {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, List<?>> allMenus = new LinkedHashMap<>();

        for (String skillId : uiRouteRegistry.getRegisteredSkillIds()) {
            allMenus.put(skillId, uiRouteRegistry.getMenus(skillId));
        }

        result.put("success", true);
        result.put("data", allMenus);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/menus/{skillId}")
    public ResponseEntity<Map<String, Object>> getMenusBySkill(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!uiRouteRegistry.hasUi(skillId)) {
            result.put("success", false);
            result.put("error", "Skill not found or has no UI: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", uiRouteRegistry.getMenus(skillId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/menus/{skillId}/role/{role}")
    public ResponseEntity<Map<String, Object>> getMenusByRole(
            @PathVariable String skillId,
            @PathVariable String role) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!uiRouteRegistry.hasUi(skillId)) {
            result.put("success", false);
            result.put("error", "Skill not found or has no UI: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", uiRouteRegistry.getMenusByRole(skillId, role));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pages/{skillId}")
    public ResponseEntity<Map<String, Object>> getPagesBySkill(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!uiRouteRegistry.hasUi(skillId)) {
            result.put("success", false);
            result.put("error", "Skill not found or has no UI: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", uiRouteRegistry.getPages(skillId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/config/{skillId}")
    public ResponseEntity<Map<String, Object>> getUiConfig(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        SkillUiConfig config = uiRouteRegistry.getUiConfig(skillId);
        if (config == null) {
            result.put("success", false);
            result.put("error", "Skill UI config not found: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", config);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/route-info/{skillId}")
    public ResponseEntity<Map<String, Object>> getRouteInfo(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Object> routeInfo = uiRouteRegistry.getRouteInfo(skillId);
        if (routeInfo.isEmpty()) {
            result.put("success", false);
            result.put("error", "Skill route info not found: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", routeInfo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listSkillsWithUi() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> skills = new ArrayList<>();

        for (String skillId : uiRouteRegistry.getRegisteredSkillIds()) {
            Map<String, Object> skillInfo = new LinkedHashMap<>();
            skillInfo.put("skillId", skillId);
            skillInfo.put("menus", uiRouteRegistry.getMenus(skillId).size());
            skillInfo.put("pages", uiRouteRegistry.getPages(skillId).size());
            skills.add(skillInfo);
        }

        result.put("success", true);
        result.put("data", skills);
        result.put("total", skills.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/components/{skillId}")
    public ResponseEntity<Map<String, Object>> listComponents(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (componentLoader == null) {
            result.put("success", false);
            result.put("error", "Component loader not available");
            return ResponseEntity.status(503).body(result);
        }

        List<Map<String, Object>> components = componentLoader.listComponents(skillId);
        result.put("success", true);
        result.put("data", components);
        result.put("total", components.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/components/{skillId}/{componentId}")
    public ResponseEntity<Map<String, Object>> getComponent(
            @PathVariable String skillId,
            @PathVariable String componentId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (componentLoader == null) {
            result.put("success", false);
            result.put("error", "Component loader not available");
            return ResponseEntity.status(503).body(result);
        }

        Map<String, Object> component = componentLoader.loadComponentWithMetadata(skillId, componentId);
        if (component.isEmpty()) {
            result.put("success", false);
            result.put("error", "Component not found: " + componentId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", component);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/page-content/{skillId}/**")
    public ResponseEntity<Map<String, Object>> getPageContent(
            @PathVariable String skillId,
            HttpServletRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageCacheManager == null) {
            result.put("success", false);
            result.put("error", "Page cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        String requestPath = request.getRequestURI();
        String prefix = "/api/v1/skill-ui/page-content/" + skillId + "/";
        String pagePath = requestPath.substring(prefix.length());

        Map<String, Object> pageData = pageCacheManager.getPageWithMetadata(skillId, pagePath);
        if (!pageData.containsKey("content")) {
            result.put("success", false);
            result.put("error", "Page not found: " + pagePath);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", pageData);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/cache/{skillId}")
    public ResponseEntity<Map<String, Object>> clearSkillCache(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageCacheManager == null) {
            result.put("success", false);
            result.put("error", "Page cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        pageCacheManager.clearCacheForSkill(skillId);
        
        if (componentLoader != null) {
            componentLoader.clearCache(skillId);
        }

        result.put("success", true);
        result.put("message", "Cache cleared for skill: " + skillId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/cache")
    public ResponseEntity<Map<String, Object>> clearAllCache() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageCacheManager == null) {
            result.put("success", false);
            result.put("error", "Page cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        pageCacheManager.clearAllCache();
        
        if (componentLoader != null) {
            componentLoader.clearAllCache();
        }

        result.put("success", true);
        result.put("message", "All cache cleared");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageCacheManager == null) {
            result.put("success", false);
            result.put("error", "Page cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", pageCacheManager.getCacheStats());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/versions/{skillId}")
    public ResponseEntity<Map<String, Object>> getSkillVersionInfo(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageVersionManager == null) {
            result.put("success", false);
            result.put("error", "Page version manager not available");
            return ResponseEntity.status(503).body(result);
        }

        Map<String, Object> versionInfo = pageVersionManager.getSkillVersionInfo(skillId);
        if (versionInfo.isEmpty()) {
            result.put("success", false);
            result.put("error", "Skill version not found: " + skillId);
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", versionInfo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/versions/{skillId}/pages")
    public ResponseEntity<Map<String, Object>> getPageVersions(@PathVariable String skillId) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageVersionManager == null) {
            result.put("success", false);
            result.put("error", "Page version manager not available");
            return ResponseEntity.status(503).body(result);
        }

        List<Map<String, Object>> versions = pageVersionManager.getAllPageVersions(skillId);
        result.put("success", true);
        result.put("data", versions);
        result.put("total", versions.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/versions/stats")
    public ResponseEntity<Map<String, Object>> getVersionStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pageVersionManager == null) {
            result.put("success", false);
            result.put("error", "Page version manager not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", pageVersionManager.getVersionStats());
        return ResponseEntity.ok(result);
    }
}
