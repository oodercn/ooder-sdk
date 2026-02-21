/**
 * $RCSfile: MemCacheManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: MemCacheManager.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.cache;

import net.ooder.common.CommonConfig;
import net.ooder.common.util.Constants;

import java.util.Map;
import java.util.TreeMap;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 
 * Central cache management of all caches used by diferent application. Cache sizes are
 * stored as the following common property values: <ul>
 *
 *      <li> <tt>[appConfigKey].cache.[cacheKey].size</tt> -- cache size with
 *              cache key [cacheKey] of application with app config key [appConfigKey].
 *      </ul>
 *
 * All values should be in bytes. Cache can also be enabled or disabled.
 * This value is stored as the <tt>[appConfigKey].cache.enabled</tt> common property. 
 * </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 2.0
 */
public class MemCacheManager implements CacheManager {

    /**
     * Cache map for all Caches in certain application.
     */
    private Map cacheMap;

    private String configKey;
    private boolean cacheEnabled = true;

    /**
     * Creates a new cache manager.
     */
    public MemCacheManager(String configKey) {
        cacheMap = new TreeMap();
        this.configKey = configKey;
        init();
    }

    private void init() {
        //See if cache is supposed to be enabled.
        String enabled = CommonConfig.getValue(configKey + ".cache.enabled");
        if (enabled != null) {
            try {
                cacheEnabled = Boolean.valueOf(enabled).booleanValue();
            } catch (Exception e) {
            }
        }

    }

    /**
     * create cache with specified cacheSize and lifeTime
     * @param name
     * @param maxCacheSize
     * @param maxLifetime
     * @return
     */
    public Cache createCache(String name, int maxCacheSize, long maxLifetime) {
        Cache cache = (Cache) cacheMap.get(name);
        if (cache != null)
            return cache;
        else {
            cache = new DefaultCache(name, maxCacheSize, maxLifetime);
            cacheMap.put(name, cache);
        }

        return cache;
    }

    /**
     *
     * Gets Cache object for certain application with cacheKey
     */
    public Cache getCache(String cacheKey) {

        Cache cache = (Cache) cacheMap.get(cacheKey);
        if (cache == null) {
            // Default cache sizes
            int cacheSize = 30 * 1024 * 1024; // 30 MB

            String cacheSizeString = CommonConfig.getValue(configKey + ".cache." + cacheKey + ".size");
            if (cacheSizeString != null) {
                try {
                    cacheSize = Integer.parseInt(cacheSizeString);
                } catch (Exception e) {
                }
            }

            // Default cache life time
            long cacheLifeTime = 30 * 24 * Constants.HOUR;

            String cacheLifeTimeString = CommonConfig.getValue(configKey + ".cache." + cacheKey + ".lifeTime");
            if (cacheLifeTimeString != null) {
                try {
                    cacheLifeTime = Long.parseLong(cacheLifeTimeString);
                } catch (Exception e) {
                }
            }

            // Initialize cache object
            cache = CacheFactory.createCache(configKey + "." + cacheKey, cacheSize, cacheLifeTime);
            cacheMap.put(configKey + "." + cacheKey, cache);
        }

        return cache;
    }

    /**
     * Returns all caches that this this manager contained.
     *
     * @return all caches map, key - cache name, value - cache object
     */
    public Map getAllCache() {
        return cacheMap;
    }

    /**
     * Returns true if cache is globally enabled. Cache should only be disabled
     * for testing purposes, since it has a huge impact on performance.
     *
     * @return true if cache is globally enabled.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

}