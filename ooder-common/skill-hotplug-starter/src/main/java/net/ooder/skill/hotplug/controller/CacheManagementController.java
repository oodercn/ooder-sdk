package net.ooder.skill.hotplug.controller;

import net.ooder.skill.hotplug.cache.CacheManager;
import net.ooder.skill.hotplug.cache.LazySkillLoader;
import net.ooder.skill.hotplug.cache.MetadataCache;
import net.ooder.skill.hotplug.cache.RouteCache;
import net.ooder.skill.hotplug.cache.ServiceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存管理控制器
 * 提供缓存管理API
 */
@RestController
@RequestMapping("/api/v1/skill-cache")
public class CacheManagementController {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagementController.class);

    @Autowired(required = false)
    private CacheManager cacheManager;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAllStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (cacheManager == null) {
            result.put("success", false);
            result.put("error", "Cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", cacheManager.getAllStats());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (cacheManager == null) {
            result.put("status", "DOWN");
            result.put("error", "Cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", cacheManager.getHealthStatus());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Map<String, Object>> clearAll() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (cacheManager == null) {
            result.put("success", false);
            result.put("error", "Cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        cacheManager.clearAll();
        result.put("success", true);
        result.put("message", "All caches cleared");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/evict-expired")
    public ResponseEntity<Map<String, Object>> evictExpired() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (cacheManager == null) {
            result.put("success", false);
            result.put("error", "Cache manager not available");
            return ResponseEntity.status(503).body(result);
        }

        cacheManager.evictAllExpired();
        result.put("success", true);
        result.put("message", "Expired entries evicted");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/metadata/stats")
    public ResponseEntity<Map<String, Object>> getMetadataStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        MetadataCache cache = cacheManager != null ? cacheManager.getMetadataCache() : null;
        if (cache == null) {
            result.put("success", false);
            result.put("error", "Metadata cache not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", cache.getStats());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/metadata")
    public ResponseEntity<Map<String, Object>> clearMetadataCache() {
        Map<String, Object> result = new LinkedHashMap<>();

        MetadataCache cache = cacheManager != null ? cacheManager.getMetadataCache() : null;
        if (cache == null) {
            result.put("success", false);
            result.put("error", "Metadata cache not available");
            return ResponseEntity.status(503).body(result);
        }

        cache.clear();
        result.put("success", true);
        result.put("message", "Metadata cache cleared");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/route/stats")
    public ResponseEntity<Map<String, Object>> getRouteStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        RouteCache cache = cacheManager != null ? cacheManager.getRouteCache() : null;
        if (cache == null) {
            result.put("success", false);
            result.put("error", "Route cache not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", cache.getStats());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/route")
    public ResponseEntity<Map<String, Object>> clearRouteCache() {
        Map<String, Object> result = new LinkedHashMap<>();

        RouteCache cache = cacheManager != null ? cacheManager.getRouteCache() : null;
        if (cache == null) {
            result.put("success", false);
            result.put("error", "Route cache not available");
            return ResponseEntity.status(503).body(result);
        }

        cache.clear();
        result.put("success", true);
        result.put("message", "Route cache cleared");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/service/stats")
    public ResponseEntity<Map<String, Object>> getServiceStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        ServiceCache cache = cacheManager != null ? cacheManager.getServiceCache() : null;
        if (cache == null) {
            result.put("success", false);
            result.put("error", "Service cache not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", cache.getStats());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/service")
    public ResponseEntity<Map<String, Object>> clearServiceCache() {
        Map<String, Object> result = new LinkedHashMap<>();

        ServiceCache cache = cacheManager != null ? cacheManager.getServiceCache() : null;
        if (cache == null) {
            result.put("success", false);
            result.put("error", "Service cache not available");
            return ResponseEntity.status(503).body(result);
        }

        cache.clear();
        result.put("success", true);
        result.put("message", "Service cache cleared");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/lazy-loader/stats")
    public ResponseEntity<Map<String, Object>> getLazyLoaderStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        LazySkillLoader loader = cacheManager != null ? cacheManager.getLazySkillLoader() : null;
        if (loader == null) {
            result.put("success", false);
            result.put("error", "Lazy loader not available");
            return ResponseEntity.status(503).body(result);
        }

        result.put("success", true);
        result.put("data", loader.getStats());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/lazy-loader/preload")
    public ResponseEntity<Map<String, Object>> preloadSkills() {
        Map<String, Object> result = new LinkedHashMap<>();

        LazySkillLoader loader = cacheManager != null ? cacheManager.getLazySkillLoader() : null;
        if (loader == null) {
            result.put("success", false);
            result.put("error", "Lazy loader not available");
            return ResponseEntity.status(503).body(result);
        }

        loader.preload();
        result.put("success", true);
        result.put("message", "Preload completed");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/lazy-loader/enable")
    public ResponseEntity<Map<String, Object>> enableLazyLoad() {
        Map<String, Object> result = new LinkedHashMap<>();

        LazySkillLoader loader = cacheManager != null ? cacheManager.getLazySkillLoader() : null;
        if (loader == null) {
            result.put("success", false);
            result.put("error", "Lazy loader not available");
            return ResponseEntity.status(503).body(result);
        }

        loader.setLazyLoadEnabled(true);
        result.put("success", true);
        result.put("message", "Lazy load enabled");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/lazy-loader/disable")
    public ResponseEntity<Map<String, Object>> disableLazyLoad() {
        Map<String, Object> result = new LinkedHashMap<>();

        LazySkillLoader loader = cacheManager != null ? cacheManager.getLazySkillLoader() : null;
        if (loader == null) {
            result.put("success", false);
            result.put("error", "Lazy loader not available");
            return ResponseEntity.status(503).body(result);
        }

        loader.setLazyLoadEnabled(false);
        result.put("success", true);
        result.put("message", "Lazy load disabled");
        return ResponseEntity.ok(result);
    }
}
