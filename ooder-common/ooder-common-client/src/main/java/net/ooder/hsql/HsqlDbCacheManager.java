
/**
 * $RCSfile: HsqlDbCacheManager.java,v $
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
import net.ooder.common.cache.CacheManager;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSConfig;
import net.ooder.org.conf.OrgConstants;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025-2021
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author 文章
 * @version 5.0
 */
public class HsqlDbCacheManager {
    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), HsqlDbCacheManager.class);
    public static String TABLE_NAME = "CACHE";
    public static final String CREATE_CACHETABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (CACHE_NAME VARCHAR(50), CACHE_KEY VARCHAR(100), CACHE_VALUE OBJECT, UPDATE_TIME BIGINT)";
    public static final String CREATE_INDEX_SQL = "CREATE INDEX IDX_" + TABLE_NAME + "_NAME_KEY ON " + TABLE_NAME + " (CACHE_NAME, CACHE_KEY)";
    public static final String CREATE_INDEX_SQL2 = "CREATE INDEX IDX_" + TABLE_NAME + "_NAME ON " + TABLE_NAME + " (CACHE_NAME)";
    public static final String THREAD_LOCK = "Thread Lock";
    //
//
    private static final String LOAD_CACHE_SQL = "SELECT CACHE_NAME, CACHE_KEY, CACHE_VALUE FROM " + TABLE_NAME;
    private static final String INSERT_CACHE_SQL = "INSERT INTO " + TABLE_NAME + " (CACHE_NAME, CACHE_KEY, CACHE_VALUE) VALUES(?, ?, ?)";
    private static final String DELETE_CACHE_SQL = "DELETE FROM " + TABLE_NAME;
    private boolean cacheEnabled = true;
    private boolean dumpCache = false;

    private static Map cacheMap = new HashMap();
    private static HsqlDbServer db;
    private static HsqlDbCacheManager manager;

    public static HsqlDbCacheManager getInstance() {
        synchronized (THREAD_LOCK) {
            if (manager == null) {
                manager = new HsqlDbCacheManager();
            }
        }
        return manager;
    }

    /**
     * Creates a new cache manager.
     */
    HsqlDbCacheManager() {


        String cacheEnabledStr = JDSConfig.getValue("hsql.cacheEnabled");
        if (cacheEnabledStr != null) {
            cacheEnabled = Boolean.valueOf(cacheEnabledStr);
        }

        String dumpCacheStr = JDSConfig.getValue("hsql.dumpCache");
        if (dumpCacheStr != null) {
            dumpCache = Boolean.valueOf(dumpCacheStr);
        }


        String cacheDbUser = JDSConfig.getValue("hsql.cacheDbUser");
        if (cacheDbUser == null) {
            cacheDbUser = "sa";
        }

        String cacheDbPassword = JDSConfig.getValue("hsql.cacheDbPassword");
        if (cacheDbPassword == null) {
            cacheDbPassword = "";
        }


        String path = JDSConfig.getValue("hsql.dataPath");
        if (path == null || path.equals("")) {
            path = JDSConfig.Config.dataPath() + File.separator + "ClientCache";
        }


        String dbName = JDSConfig.getValue("hsql.dbName");
        if (dbName == null) {
            dbName = "mydb";
        }


        String port = getLocalPort();


        String cacheDbURL = JDSConfig.getValue("hsql.url");
        if (cacheDbURL == null) {
            cacheDbURL = "jdbc:hsqldb:file:" + path + "/" + dbName;
        }

        ResultSet rsTableName = null;
        Statement st = null;
        Connection conn = null;
        try {
            db = new HsqlDbServer(cacheDbURL, cacheDbUser, cacheDbPassword, path, dbName, Integer.valueOf(port), log.isTraceEnabled());
            db.startup();
            conn = db.newConnection();
            // adjust if the database exists
            DatabaseMetaData metaData = conn.getMetaData();
            rsTableName = metaData.getTables(null, null, TABLE_NAME, null);
            if (!rsTableName.next()) {
                st = conn.createStatement();
                st.execute(CREATE_CACHETABLE_SQL);
                st.execute(CREATE_INDEX_SQL);
                st.execute(CREATE_INDEX_SQL2);
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (rsTableName != null) {
                    rsTableName.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e1) {
                log.error("", e1);
            }
        }

        cacheMap = new HashMap();
        // init();
    }

    private String getLocalPort() {
        ServerSocket s = null;
        String port = "8083";
        try {
            s = new ServerSocket(0);
            port = s.getLocalPort() + "";
            s.close();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return port;
    }
//
//    Cache getCacheMap(String cacheKey, String cacheName) {
//
//        Cache cache = (Cache) cacheMap.get(cacheKey + "." + cacheName);
//        if (cache == null) {
//            if (cacheName.indexOf(".") > -1) {
//                cache = CacheManagerFactory.createCache(cacheKey, StringUtility.split(cacheName, ".")[1], 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
//            } else {
//                cache = CacheManagerFactory.createCache(cacheKey, cacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
//            }
//            cacheMap.put(cacheKey + "." + cacheName, cache);
//        }
//        return cache;
//    }

    public void loadCache(String cacheKey) {
        CacheManager cacheManager = CacheManagerFactory.getInstance().getCacheManager(cacheKey);
        if (cacheManager.isCacheEnabled()) {
            Connection conn = null;
            ResultSet rsTableName = null;
            Statement stmnt = null;
            ResultSet rs = null;
            Map cacheLoadMap = new HashMap();
            try {
                conn = db.newConnection();
                DatabaseMetaData metaData = conn.getMetaData();
                rsTableName = metaData.getTables(null, null, "CACHE", null);
                if (rsTableName.next()) {
                    stmnt = conn.createStatement();
                    rs = stmnt.executeQuery(LOAD_CACHE_SQL);
                    int k = 0;
                    while (rs.next()) {
                        k = k++;
                        String cacheName = rs.getString(1);
                        Cache cache = null;//CacheManagerFactory.getInstance().getCacheManager(cacheKey).getCache()
                        if (cacheName.indexOf(".") > -1) {
                            cache = cacheManager.getCache(StringUtility.split(cacheName, ".")[1]);
                        } else {
                            cache = getCache(cacheName);
                        }

                        if (cache != null) {
                            cache.put(rs.getObject(2), rs.getObject(3));
                            Integer cacheCount = (Integer) cacheLoadMap.get(cacheName);
                            if (cacheCount == null) {
                                cacheCount = new Integer(1);
                            } else {
                                cacheCount = new Integer(cacheCount
                                        .intValue() + 1);
                            }
                            cacheLoadMap.put(cacheName, cacheCount);
                        }
                    }
                    rs.close();
                    stmnt.close();
                } else {
                    stmnt = conn.createStatement();
                    stmnt.execute(CREATE_CACHETABLE_SQL);
                }
                rsTableName.close();
                conn.close();
            } catch (Exception e) {
                log.error("", e);
            } finally {
                if (rsTableName != null) {
                    try {
                        rsTableName.close();
                    } catch (SQLException sqle) {
                    }
                }
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqle) {
                    }
                }
                if (stmnt != null) {
                    try {
                        stmnt.close();
                    } catch (SQLException sqle) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException sqle) {
                    }
                }

            }
            Iterator cacheLoadIte = cacheLoadMap.keySet().iterator();
            while (cacheLoadIte.hasNext()) {
                String cacheName = (String) cacheLoadIte.next();
                int cacheCount = ((Integer) cacheLoadMap.get(cacheName))
                        .intValue();
                log.info("Load dumped cache data - " + cacheName + "("
                        + cacheCount + ")");
            }

        } else {
            log.info("- Cache enabled            [false]");
        }

    }

    public void clearAllCache() {
        if (db != null) {
            Connection conn = null;
            PreparedStatement deleteCacheStm = null;
            PreparedStatement insertCacheStm = null;
            Map cacheDumpMap = new HashMap();
            try {
                conn = db.newConnection();
                conn.setAutoCommit(false);
                deleteCacheStm = conn.prepareStatement(DELETE_CACHE_SQL);
                deleteCacheStm.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                log.error("", e);
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (Exception ex) {
                    }
                }
            } finally {
                if (deleteCacheStm != null) {
                    try {
                        deleteCacheStm.close();
                    } catch (Exception e) {
                    }
                }
                if (insertCacheStm != null) {
                    try {
                        insertCacheStm.close();
                    } catch (Exception e) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    ;

    public void dumpCache(String configKey) {
        // Dump cache
        if (db != null) {
            Connection conn = null;
            PreparedStatement deleteCacheStm = null;
            PreparedStatement insertCacheStm = null;
            Map cacheDumpMap = new HashMap();
            try {
                conn = db.newConnection();
                conn.setAutoCommit(false);
                // delete old dumped cache data
                deleteCacheStm = conn.prepareStatement(DELETE_CACHE_SQL);
                deleteCacheStm.executeUpdate();
                insertCacheStm = conn.prepareStatement(INSERT_CACHE_SQL);
                CacheManager cacheManager = CacheManagerFactory.getInstance().getCacheManager(configKey);
                if (cacheManager.isCacheEnabled()) {
                    Map cacheMap = cacheManager.getAllCache();
                    Iterator cacheNameIte = cacheMap.keySet().iterator();
                    while (cacheNameIte.hasNext()) {
                        String cacheName = (String) cacheNameIte.next();
                        Cache cache = (Cache) cacheMap.get(cacheName);
                        Iterator cacheIte = cache.keySet().iterator();
                        while (cacheIte.hasNext()) {
                            Object cacheKey = cacheIte.next();
                            Object cacheValue = cache.get(cacheKey);
                            if (cacheKey != null && cacheValue != null) {
//
//                                if (!(cacheValue instanceof Serializable)) {
//                                    cacheValue = cacheValue.toString();
//                                }
                                try {
                                    insertCacheStm.setString(1, cacheName);
                                    insertCacheStm.setObject(2, cacheKey);
                                    insertCacheStm.setObject(3, cacheValue);
                                    insertCacheStm.executeUpdate();
                                    Integer cacheCount = (Integer) cacheDumpMap
                                            .get(cacheName);
                                    if (cacheCount == null) {
                                        cacheCount = new Integer(1);
                                    } else {
                                        cacheCount = new Integer(cacheCount
                                                .intValue() + 1);
                                    }
                                    cacheDumpMap.put(cacheName, cacheCount);
                                } catch (Exception e) {
                                    log.error("", e);
                                }
                            }


                        }
                    }
                }

                conn.commit();
            } catch (Exception e) {
                log.error("", e);
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (Exception ex) {
                    }
                }
            } finally {
                if (deleteCacheStm != null) {
                    try {
                        deleteCacheStm.close();
                    } catch (Exception e) {
                    }
                }
                if (insertCacheStm != null) {
                    try {
                        insertCacheStm.close();
                    } catch (Exception e) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                    }
                }
            }
            Iterator cacheDumpIte = cacheDumpMap.keySet().iterator();
            while (cacheDumpIte.hasNext()) {
                String cacheName = (String) cacheDumpIte.next();
                int cacheCount = ((Integer) cacheDumpMap.get(cacheName))
                        .intValue();
                log.info("Dumped cache data - " + cacheName + "("
                        + cacheCount + ")");
            }
        }
    }


    public static Cache getCache(String cacheKey) {
        return getInstance().getCacheNS(cacheKey);
    }

    /**
     * Gets Cache object for certain application with cacheKey
     */
    public Cache getCacheNS(String cacheKey) {
        Cache cache = (Cache) cacheMap.get(cacheKey);
        if (cache == null) {
            // Default cache sizes
            int cacheSize = 1024 * 1024; // 1M

            String cacheSizeString = JDSConfig.getValue("cache." + cacheKey + ".size");
            if (cacheSizeString != null) {
                try {
                    cacheSize = Integer.parseInt(cacheSizeString);
                } catch (Exception e) {
                }
            }

            // Default cache life time
            long cacheLifeTime = 8 * net.ooder.common.util.Constants.HOUR;

            String cacheLifeTimeString = JDSConfig.getValue("cache." + cacheKey + ".lifeTime");
            if (cacheLifeTimeString != null) {
                try {
                    cacheLifeTime = Long.parseLong(cacheLifeTimeString);
                } catch (Exception e) {
                }
            }

            // Initialize cache object
            cache = HsqlDbCacheFactory.createCache(cacheKey, cacheSize, cacheLifeTime, db);
            cacheMap.put(cacheKey, cache);
        }

        return cache;
    }

    /**
     * Returns all caches that this this manager contained.
     *
     * @return all caches map, key - cache name, value - cache object
     */
    public Map getAllCacheNS() {
        return cacheMap;
    }

    public static Map getAllCache() {
        return getInstance().getAllCacheNS();
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
    public static boolean isCacheEnabled() {
        return getInstance().isCacheEnabledNS();
    }

}
