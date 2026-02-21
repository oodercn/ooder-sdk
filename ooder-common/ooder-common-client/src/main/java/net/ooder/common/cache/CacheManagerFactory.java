/**
 * $RCSfile: CacheManagerFactory.java,v $
 * $Revision: 1.3 $
 * $Date: 2016/10/22 14:54:54 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CacheManagerFactory.java,v $
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
import net.ooder.common.cache.redis.RedisCacheManager;
import net.ooder.common.util.Constants;

import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * Title: 常用代码打包
 * </p>
 * <p>
 * Description: 缓存管理器工厂类
 * <p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 * @see Cache
 */
public class CacheManagerFactory {

    // private static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, CacheManagerFactory.class);

    private static CacheManagerFactory instance = null;

    private static Map cacheManagers;

    public static String REFKEY = "databaseRef";

    public static final String THREAD_LOCK = "Thread Lock";
    public static String HTTPCACHE = "HTTPCACHE";
    public static final long DEFAULT_CACHEEXPIRETIME = 10000 * 24 * Constants.HOUR;
    public static final Integer DEFAULT_CACHESIZE = 10 * 1024 * 1024;

    private CacheManagerFactory() {
        cacheManagers = new TreeMap();
    }

    public Map getCacheManagerMap() {
        return cacheManagers;
    }

    public static CacheManagerFactory getInstance() {

        synchronized (THREAD_LOCK) {
            if (instance == null) {
                instance = new CacheManagerFactory();
            }
        }

        return instance;
    }

    public CacheManager getCacheManager(String configKey) {
        if (configKey == null) {
            throw new IllegalArgumentException("Parameters 'configKey' can't be null.");
        }

        CacheManager manager = (CacheManager) cacheManagers.get(configKey);
        if (manager == null) {
            manager = loadCacheManager(configKey);
            cacheManagers.put(configKey, manager);
        }

        return manager;
    }

    protected static <T> Cache<String, T> getCache(String configKey, String cacheKey) {
        return getInstance().getCacheManager(configKey).getCache(cacheKey);
    }

    public static <T> Cache<String, T> createCache(String configKey, String cacheName) {
        return createCache(configKey, cacheName, DEFAULT_CACHESIZE, DEFAULT_CACHEEXPIRETIME);
    }

    public static <T> Cache<String, T> createCache(String configKey, String cacheName, int maxCacheSize, long maxLifetime) {
        return getInstance().getCacheManager(configKey).createCache(configKey + "." + cacheName, maxCacheSize, maxLifetime);
    }

    private CacheManager loadCacheManager(String configKey) {
        CacheManager manager = null;
        String databsekey = CommonConfig.getValue(configKey + ".cache." + REFKEY);
        if (databsekey == null) {
            databsekey = configKey;
        }
        String serverURL = CommonConfig.getValue(databsekey + ".database.defaultProvider.serverURL");
        if (serverURL == null) {
            serverURL = CommonConfig.getValue(databsekey + ".serverURL");
        }
        if (serverURL != null) {
            manager = new RedisCacheManager(configKey);
        } else {
            manager = new MemCacheManager(configKey);

        }

        return manager;
    }


}