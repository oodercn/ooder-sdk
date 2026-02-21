/**
 * $RCSfile: RedisCacheManager.java,v $
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
package net.ooder.common.cache.redis;

import net.ooder.common.CommonConfig;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManager;
import net.ooder.common.cache.CacheManagerFactory;

import java.util.HashMap;
import java.util.Map;

public class RedisCacheManager implements CacheManager {
	
	private final boolean cacheEnabled = true;
	
	private String configKey = null;
	
	private String driver;
	
	private String serverURL = "192.168.80.64";
	
	private final int port = 6379;
	
	private String username;
	
	private String password;
	
	private static final String MEMKEY = "REDIS";
	
	private int minConnections = 3;
	
	private int maxConnections = 10;

	 String cacheSIzeString ;
	
	/**
	 * Maximum time a connection can be open before it's reopened (in days)
	 */
	private int connectionTimeout = 30 * 1000;
	
	private static final Map CACHES = new HashMap();
	
	public static final String THREAD_LOCK = "Thread Lock";
	
	private RedisCacheManager() {
	
	}
	
	public RedisCacheManager(final String systemCode) {
		String  databsekey = MEMKEY;
		final String key = systemCode + ".cache." + CacheManagerFactory.REFKEY;
//		System.out.println(key);
		// common_config.xml --> org.cache.databaseRef
	 	databsekey = CommonConfig.getValue(key);
		if (databsekey != null) {
			configKey = databsekey;
		}
		System.out.println("configkey : " + configKey);
		loadProperties();
		
		
	}
	
	private void loadProperties() {
		driver = CommonConfig.getValue(configKey + ".database.defaultProvider.driver");
		serverURL = CommonConfig.getValue(configKey + ".database.defaultProvider.serverURL");
		username = CommonConfig.getValue(configKey + ".database.defaultProvider.username");
		password = CommonConfig.getValue(configKey + ".database.defaultProvider.password");
		final String minCons = CommonConfig.getValue(configKey + ".database.defaultProvider.minConnections");
		final String maxCons = CommonConfig.getValue(configKey + ".database.defaultProvider.maxConnections");
		final String conTimeout = CommonConfig.getValue(configKey + ".database.defaultProvider.connectionTimeout");
		try {
			if (minCons != null) {
				minConnections = Integer.parseInt(minCons);
			}
			if (maxCons != null) {
				maxConnections = Integer.parseInt(maxCons);
			}
			if (conTimeout != null) {
				connectionTimeout = Integer.parseInt(conTimeout);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Cache createCache(final String name, final int maxcacheSIze, final long maxLifetime) {
		
		return getCacheNS(name, maxcacheSIze, maxLifetime);
	}
	
	public Cache getCacheNS(final String cacheKey) {
		
		int cacheSIze = 10 * 1024 * 1024; // 1 MB
		
		final String cacheSIzeString = CommonConfig.getValue(configKey + ".cache." + cacheKey + ".size");
		if (cacheSIzeString != null) {
			try {
				cacheSIze = Integer.parseInt(cacheSIzeString);
			} catch (final Exception e) {
			}
		}
		
		// Default cache life time is a week
		long cacheLifeTime = 7 * 24 * net.ooder.common.util.Constants.HOUR;
		
		final String cacheLifeTimeString = CommonConfig.getValue(configKey + ".cache." + cacheKey + ".lifeTime");
		if (cacheLifeTimeString != null) {
			try {
				cacheLifeTime = Long.parseLong(cacheLifeTimeString);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		
		return getCacheNS(cacheKey, cacheSIze, cacheLifeTime);
	}
	
	/**
	 * Gets Cache object for certain application with cacheKey
	 */
	private Cache getCacheNS(final String cacheKey, final int maxcacheSIze, final long maxLifetime) {
		final String key = configKey + "." + cacheKey;
		Cache cache = (Cache) CACHES.get(key);
		if (cache == null) {
			cache = new RedisCache(configKey,cacheKey, maxcacheSIze, maxLifetime);
			CACHES.put(key, cache);
		}
		return cache;
	}
	
	/**
	 * Returns all CACHES that this this manager contained.
	 *
	 * @return all CACHES map, key - cache name, value - cache object
	 */
	public Map getAllCacheNS() {
		return CACHES;
	}
	
	@Override
	public Map getAllCache() {
		return getAllCacheNS();
	}
	
	/**
	 * Returns true if cache is globally enabled. Cache should only be disabled for testing purposes, since it has a
	 * huge impact on performance.
	 *
	 * @return true if cache is globally enabled.
	 */
	public boolean isCacheEnabledNS() {
		return cacheEnabled;
	}
	
	/**
	 * Returns true if cache is globally enabled. Cache should only be disabled for testing purposes, since it has a
	 * huge impact on performance.
	 *
	 * @return true if cache is globally enabled.
	 */
	@Override
	public boolean isCacheEnabled() {
		return isCacheEnabledNS();
	}
	
	@Override
	public Cache getCache(final String cacheKey) {
		return getCacheNS(cacheKey);
	}
	
}
