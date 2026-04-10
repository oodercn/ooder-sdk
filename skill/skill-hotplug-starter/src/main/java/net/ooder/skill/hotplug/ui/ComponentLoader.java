package net.ooder.skill.hotplug.ui;

import net.ooder.skill.hotplug.model.SkillComponent;
import net.ooder.skill.hotplug.model.SkillPackage;
import net.ooder.skill.hotplug.model.SkillUiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 组件动态加载器
 * 负责从Skill JAR中加载UI组件
 */
public class ComponentLoader {

    private static final Logger logger = LoggerFactory.getLogger(ComponentLoader.class);

    private final Map<String, Map<String, String>> componentCache = new ConcurrentHashMap<>();
    private final Map<String, SkillPackage> packageRegistry = new ConcurrentHashMap<>();

    public void registerPackage(String skillId, SkillPackage skillPackage) {
        packageRegistry.put(skillId, skillPackage);
        logger.info("[ComponentLoader] Registered package for skill: {}", skillId);
    }

    public void unregisterPackage(String skillId) {
        packageRegistry.remove(skillId);
        componentCache.remove(skillId);
        logger.info("[ComponentLoader] Unregistered package for skill: {}", skillId);
    }

    public String loadComponent(String skillId, String componentId) {
        String cacheKey = skillId + ":" + componentId;
        
        Map<String, String> skillComponents = componentCache.get(skillId);
        if (skillComponents != null && skillComponents.containsKey(componentId)) {
            return skillComponents.get(componentId);
        }

        SkillPackage pkg = packageRegistry.get(skillId);
        if (pkg == null) {
            logger.warn("[ComponentLoader] Package not found for skill: {}", skillId);
            return null;
        }

        SkillUiConfig uiConfig = loadUiConfig(skillId, pkg);
        if (uiConfig == null) {
            return null;
        }

        SkillComponent component = findComponent(uiConfig, componentId);
        if (component == null) {
            logger.warn("[ComponentLoader] Component not found: {} in skill: {}", componentId, skillId);
            return null;
        }

        String content = loadComponentContent(pkg, component);
        if (content != null) {
            skillComponents = componentCache.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>());
            skillComponents.put(componentId, content);
        }

        return content;
    }

    public Map<String, Object> loadComponentWithMetadata(String skillId, String componentId) {
        SkillPackage pkg = packageRegistry.get(skillId);
        if (pkg == null) {
            return Collections.emptyMap();
        }

        SkillUiConfig uiConfig = loadUiConfig(skillId, pkg);
        if (uiConfig == null) {
            return Collections.emptyMap();
        }

        SkillComponent component = findComponent(uiConfig, componentId);
        if (component == null) {
            return Collections.emptyMap();
        }

        String content = loadComponentContent(pkg, component);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", component.getId());
        result.put("name", component.getName());
        result.put("type", component.getType());
        result.put("selector", component.getSelector());
        result.put("props", component.getProps());
        result.put("skillId", skillId);
        result.put("content", content);

        return result;
    }

    public List<Map<String, Object>> listComponents(String skillId) {
        SkillPackage pkg = packageRegistry.get(skillId);
        if (pkg == null) {
            return Collections.emptyList();
        }

        SkillUiConfig uiConfig = loadUiConfig(skillId, pkg);
        if (uiConfig == null || !uiConfig.hasComponents()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> components = new ArrayList<>();
        for (SkillComponent comp : uiConfig.getComponents()) {
            Map<String, Object> compInfo = new LinkedHashMap<>();
            compInfo.put("id", comp.getId());
            compInfo.put("name", comp.getName());
            compInfo.put("type", comp.getType());
            compInfo.put("selector", comp.getSelector());
            compInfo.put("skillId", skillId);
            components.add(compInfo);
        }

        return components;
    }

    public void clearCache(String skillId) {
        componentCache.remove(skillId);
        logger.info("[ComponentLoader] Cleared cache for skill: {}", skillId);
    }

    public void clearAllCache() {
        componentCache.clear();
        logger.info("[ComponentLoader] Cleared all component cache");
    }

    private SkillComponent findComponent(SkillUiConfig uiConfig, String componentId) {
        for (SkillComponent comp : uiConfig.getComponents()) {
            if (componentId.equals(comp.getId())) {
                return comp;
            }
        }
        return null;
    }

    private String loadComponentContent(SkillPackage pkg, SkillComponent component) {
        String path = component.getPath();
        if (path == null || path.isEmpty()) {
            logger.warn("[ComponentLoader] Component has no path: {}", component.getId());
            return null;
        }

        try {
            InputStream is = pkg.getResource(path);
            if (is == null) {
                String staticPath = "static/" + path;
                is = pkg.getResource(staticPath);
            }

            if (is == null) {
                logger.warn("[ComponentLoader] Component resource not found: {}", path);
                return null;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            return content.toString();

        } catch (Exception e) {
            logger.error("[ComponentLoader] Failed to load component content: {}", path, e);
            return null;
        }
    }

    private SkillUiConfig loadUiConfig(String skillId, SkillPackage pkg) {
        try {
            InputStream is = pkg.getResource("skill.yaml");
            if (is == null) {
                return null;
            }

            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            Map<String, Object> data = yaml.load(is);

            @SuppressWarnings("unchecked")
            Map<String, Object> spec = (Map<String, Object>) data.get("spec");
            if (spec == null) {
                return null;
            }

            UiConfigResolver resolver = new UiConfigResolver();
            return resolver.resolve(skillId, spec);

        } catch (Exception e) {
            logger.error("[ComponentLoader] Failed to load UI config for skill: {}", skillId, e);
            return null;
        }
    }
}
