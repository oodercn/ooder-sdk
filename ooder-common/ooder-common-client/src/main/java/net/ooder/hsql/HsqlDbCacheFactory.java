
/**
 * $RCSfile: HsqlDbCacheFactory.java,v $
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
package net.ooder.hsql;

import net.ooder.common.cache.Cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author wenzhang
 *
 */
public class HsqlDbCacheFactory {
    public static Cache createCache(String name, int maxCacheSize, long maxLifetime, HsqlDbServer db) {
        Cache cache = (Cache)caches.get(name);
        if (cache != null)
            return cache;
        else {
            cache = new HsqlDbCache(name, maxCacheSize, maxLifetime, db);
            caches.put(name, cache);
        }

        return cache;
    }

    private static Map caches = new HashMap();
}
