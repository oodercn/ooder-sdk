package net.ooder.skill.hotplug.cache;

import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.model.SkillMetadata;
import net.ooder.skill.hotplug.model.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill懒加载器
 * 支持按需加载Skill JAR，减少启动时间
 */
public class LazySkillLoader {

    private static final Logger logger = LoggerFactory.getLogger(LazySkillLoader.class);

    private final PluginManager pluginManager;
    private final MetadataCache metadataCache;

    private final Map<String, SkillRegistration> registrations = new ConcurrentHashMap<>();
    private final Set<String> loadedSkills = ConcurrentHashMap.newKeySet();
    private final Set<String> preloadSkills = ConcurrentHashMap.newKeySet();

    private boolean lazyLoadEnabled = true;

    public LazySkillLoader(PluginManager pluginManager, MetadataCache metadataCache) {
        this.pluginManager = pluginManager;
        this.metadataCache = metadataCache;
    }

    public void register(String skillId, Path jarPath, SkillMetadata metadata) {
        SkillRegistration registration = new SkillRegistration(skillId, jarPath, metadata);
        registrations.put(skillId, registration);

        metadataCache.put(skillId, metadata);

        logger.debug("[LazySkillLoader] Registered skill: {} (lazy={})", skillId, lazyLoadEnabled);
    }

    public void registerForPreload(String skillId) {
        preloadSkills.add(skillId);
        logger.debug("[LazySkillLoader] Marked for preload: {}", skillId);
    }

    public void preload() {
        if (!lazyLoadEnabled) {
            return;
        }

        logger.info("[LazySkillLoader] Preloading {} skills...", preloadSkills.size());

        for (String skillId : preloadSkills) {
            try {
                loadSkill(skillId);
            } catch (Exception e) {
                logger.warn("[LazySkillLoader] Failed to preload skill: {}", skillId, e);
            }
        }
    }

    public boolean ensureLoaded(String skillId) {
        if (loadedSkills.contains(skillId)) {
            return true;
        }

        if (!registrations.containsKey(skillId)) {
            logger.debug("[LazySkillLoader] Skill not registered: {}", skillId);
            return false;
        }

        try {
            loadSkill(skillId);
            return true;
        } catch (Exception e) {
            logger.error("[LazySkillLoader] Failed to load skill: {}", skillId, e);
            return false;
        }
    }

    private void loadSkill(String skillId) {
        if (loadedSkills.contains(skillId)) {
            logger.debug("[LazySkillLoader] Skill already loaded: {}", skillId);
            return;
        }

        SkillRegistration registration = registrations.get(skillId);
        if (registration == null) {
            throw new IllegalStateException("Skill not registered: " + skillId);
        }

        logger.info("[LazySkillLoader] Loading skill: {}", skillId);

        try {
            SkillPackage pkg = createSkillPackage(registration);
            pluginManager.installSkill(pkg);
            loadedSkills.add(skillId);

            logger.info("[LazySkillLoader] Successfully loaded skill: {}", skillId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load skill: " + skillId, e);
        }
    }

    private SkillPackage createSkillPackage(SkillRegistration registration) throws java.io.IOException {
        File jarFile = registration.getJarPath().toFile();
        return SkillPackage.fromFile(jarFile);
    }

    public void unload(String skillId) {
        loadedSkills.remove(skillId);
        registrations.remove(skillId);
        metadataCache.remove(skillId);
        preloadSkills.remove(skillId);

        logger.debug("[LazySkillLoader] Unloaded skill: {}", skillId);
    }

    public boolean isLoaded(String skillId) {
        return loadedSkills.contains(skillId);
    }

    public boolean isRegistered(String skillId) {
        return registrations.containsKey(skillId);
    }

    public SkillMetadata getMetadata(String skillId) {
        SkillRegistration registration = registrations.get(skillId);
        return registration != null ? registration.getMetadata() : null;
    }

    public Set<String> getRegisteredSkillIds() {
        return new HashSet<>(registrations.keySet());
    }

    public Set<String> getLoadedSkillIds() {
        return new HashSet<>(loadedSkills);
    }

    public Set<String> getPendingSkillIds() {
        Set<String> pending = new HashSet<>(registrations.keySet());
        pending.removeAll(loadedSkills);
        return pending;
    }

    public void setLazyLoadEnabled(boolean enabled) {
        this.lazyLoadEnabled = enabled;
    }

    public boolean isLazyLoadEnabled() {
        return lazyLoadEnabled;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("lazyLoadEnabled", lazyLoadEnabled);
        stats.put("totalRegistered", registrations.size());
        stats.put("loaded", loadedSkills.size());
        stats.put("pending", registrations.size() - loadedSkills.size());
        stats.put("preloadQueue", preloadSkills.size());
        stats.put("metadataCacheStats", metadataCache.getStats());
        return stats;
    }

    private static class SkillRegistration {
        private final String skillId;
        private final Path jarPath;
        private final SkillMetadata metadata;
        private final long registeredAt;

        public SkillRegistration(String skillId, Path jarPath, SkillMetadata metadata) {
            this.skillId = skillId;
            this.jarPath = jarPath;
            this.metadata = metadata;
            this.registeredAt = System.currentTimeMillis();
        }

        public String getSkillId() {
            return skillId;
        }

        public Path getJarPath() {
            return jarPath;
        }

        public SkillMetadata getMetadata() {
            return metadata;
        }

        public long getRegisteredAt() {
            return registeredAt;
        }
    }
}
