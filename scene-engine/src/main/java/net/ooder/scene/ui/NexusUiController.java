package net.ooder.scene.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nexus UI 管理 Controller
 * 提供 UI Skills 的管理接口
 *
 * @author ooder
 * @since 2.3
 */
@RestController
@RequestMapping("/api/v1/ui")
public class NexusUiController {

    private static final Logger log = LoggerFactory.getLogger(NexusUiController.class);

    @Autowired
    private NexusUiRegistry uiRegistry;

    @Autowired
    private NexusUiLoader uiLoader;

    /**
     * 获取所有已注册的 UI
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAllUis() {
        List<NexusUiConfig> uis = uiRegistry.listAll();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", uis);
        result.put("total", uis.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取指定 UI
     */
    @GetMapping("/{skillId}")
    public ResponseEntity<Map<String, Object>> getUi(@PathVariable String skillId) {
        NexusUiConfig ui = uiRegistry.get(skillId).orElse(null);

        Map<String, Object> result = new HashMap<>();
        if (ui != null) {
            result.put("code", 200);
            result.put("data", ui);
        } else {
            result.put("code", 404);
            result.put("message", "UI not found: " + skillId);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 按类型获取 UI
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> listUisByType(@PathVariable String type) {
        List<NexusUiConfig> uis = uiRegistry.listByType(type);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", uis);
        result.put("total", uis.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有菜单
     */
    @GetMapping("/menus")
    public ResponseEntity<Map<String, Object>> getAllMenus() {
        List<MenuConfig> menus = uiRegistry.getAllMenus();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", menus);
        result.put("total", menus.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有路由
     */
    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> getAllRoutes() {
        List<RouteConfig> routes = uiRegistry.getAllRoutes();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", routes);
        result.put("total", routes.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 手动加载 UI Skill
     */
    @PostMapping("/{skillId}/load")
    public ResponseEntity<Map<String, Object>> loadUiSkill(@PathVariable String skillId) {
        log.info("Loading UI skill: {}", skillId);

        boolean success = uiLoader.loadUiSkill(skillId);

        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "UI skill loaded successfully");
            result.put("skillId", skillId);
        } else {
            result.put("code", 500);
            result.put("message", "Failed to load UI skill");
            result.put("skillId", skillId);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 重新加载 UI Skill
     */
    @PostMapping("/{skillId}/reload")
    public ResponseEntity<Map<String, Object>> reloadUiSkill(@PathVariable String skillId) {
        log.info("Reloading UI skill: {}", skillId);

        boolean success = uiLoader.reloadUiSkill(skillId);

        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "UI skill reloaded successfully");
            result.put("skillId", skillId);
        } else {
            result.put("code", 500);
            result.put("message", "Failed to reload UI skill");
            result.put("skillId", skillId);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 卸载 UI Skill
     */
    @PostMapping("/{skillId}/unload")
    public ResponseEntity<Map<String, Object>> unloadUiSkill(@PathVariable String skillId) {
        log.info("Unloading UI skill: {}", skillId);

        uiLoader.unloadUiSkill(skillId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "UI skill unloaded successfully");
        result.put("skillId", skillId);

        return ResponseEntity.ok(result);
    }

    /**
     * 重新扫描并加载所有 UI Skills
     */
    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scanAndLoadUis() {
        log.info("Scanning and loading all UI skills...");

        uiLoader.scanAndLoadInstalledUiSkills();
        List<NexusUiConfig> loadedUis = uiLoader.getLoadedUis();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Scan completed");
        result.put("loadedCount", loadedUis.size());
        result.put("loadedUis", loadedUis);

        return ResponseEntity.ok(result);
    }

    /**
     * 检查 UI Skill 是否已加载
     */
    @GetMapping("/{skillId}/status")
    public ResponseEntity<Map<String, Object>> getUiStatus(@PathVariable String skillId) {
        boolean isLoaded = uiLoader.isLoaded(skillId);
        boolean isRegistered = uiRegistry.isRegistered(skillId);

        Map<String, Object> data = new HashMap<>();
        data.put("skillId", skillId);
        data.put("loaded", isLoaded);
        data.put("registered", isRegistered);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);

        return ResponseEntity.ok(result);
    }
}
