package net.ooder.sdk.core.driver.loader.impl;

import net.ooder.sdk.core.driver.loader.SkillDriverLoader;
import net.ooder.sdk.core.driver.model.DriverPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkillDriverLoaderImpl implements SkillDriverLoader {
    
    private static final Logger log = LoggerFactory.getLogger(SkillDriverLoaderImpl.class);
    
    private final Map<String, DriverPackage> cache = new ConcurrentHashMap<>();
    
    @Override
    public DriverPackage load(String skillId, String version) {
        String cacheKey = skillId + ":" + version;
        DriverPackage cached = cache.get(cacheKey);
        if (cached != null) {
            log.debug("Loaded driver from cache: {}", cacheKey);
            return cached;
        }
        
        log.info("Loading driver: {} version {}", skillId, version);
        return null;
    }
    
    @Override
    public DriverPackage loadFromCache(String skillId) {
        for (DriverPackage pkg : cache.values()) {
            if (skillId.equals(pkg.getSkillId())) {
                log.debug("Found cached driver for skill: {}", skillId);
                return pkg;
            }
        }
        return null;
    }
    
    @Override
    public void cache(DriverPackage driver) {
        if (driver == null || driver.getSkillId() == null) {
            return;
        }
        
        String cacheKey = driver.getSkillId() + ":" + driver.getVersion();
        cache.put(cacheKey, driver);
        log.info("Cached driver: {}", cacheKey);
    }
    
    @Override
    public boolean isCached(String skillId, String version) {
        String cacheKey = skillId + ":" + version;
        return cache.containsKey(cacheKey);
    }
    
    public void clearCache() {
        cache.clear();
        log.info("Driver cache cleared");
    }
    
    public int getCacheSize() {
        return cache.size();
    }
}
