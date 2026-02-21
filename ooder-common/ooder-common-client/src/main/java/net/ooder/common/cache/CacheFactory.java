/**
 * $RCSfile: CacheFactory.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CacheFactory.java,v $
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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 缓存工厂</p>
 * 
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @see Cache
 * @author wenzhang li
 * @version 1.0
 */
public class CacheFactory {
    
    public static Cache createCache(String name, int maxCacheSize, long maxLifetime) {
        
        Cache cache = (Cache)caches.get(name);
        if (cache != null)
            return cache;
        else {
            cache = new DefaultCache(name, maxCacheSize, maxLifetime);
            caches.put(name, cache);
        }

        return cache;
    }

    private static Map caches = new HashMap();
}