/**
 * $RCSfile: RedisPoolUtil.java,v $
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
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import redis.clients.jedis.*;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisPoolUtil {
    //private static RedisPoolUtil REDIS_POOL_UTIL;
    private final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RedisPoolUtil.class);
    private String SERVER_URL = "192.168.80.64";

    private int PORT = 6379;

    private String PASSWORD = null;

    private int MAXTOTAL = 100;

    private int MAXIDLE = 50;

    private int MAXWAITMILLS = 50000;

    private boolean TESTONRETURN = true;

    private boolean TESTWHILEIDLE = true;

    private int TIMEOUT = 1000;

    private JedisPoolAbstract POOL = null;

    private String REDISKEY = "REDIS";


    public static final String THREAD_LOCK = "Thread Lock";

    private static Map<String, RedisPoolUtil> poolUtilMap = new HashMap();

    private RedisPoolUtil(String key) {
        this.REDISKEY = key;

    }


    public static RedisPoolUtil getInstance(String redisKey) {
        synchronized (redisKey.intern()) {
            RedisPoolUtil REDIS_POOL_UTIL = poolUtilMap.get(redisKey);
            if (null == REDIS_POOL_UTIL) {

                if (null == REDIS_POOL_UTIL) {
                    REDIS_POOL_UTIL = new RedisPoolUtil(redisKey);
                    poolUtilMap.put(redisKey, REDIS_POOL_UTIL);
                }
            }

            return REDIS_POOL_UTIL;
        }
    }

    public void init() {
        final JedisPoolConfig config = new JedisPoolConfig();

        loadProperties();
        // 最大连接数
        config.setMaxTotal(MAXTOTAL);
        // 最大空闲数
        config.setMaxIdle(MAXIDLE);
        // 连接池获取连接最大等待时间
        config.setMaxWaitMillis(MAXWAITMILLS);
        // 放回连接池的时候检测是否连接正常
        config.setTestOnReturn(TESTONRETURN);
        // 检测空闲连接池是否可用
        config.setTestWhileIdle(TESTWHILEIDLE);


        logger.info("start init redis pool :[" + REDISKEY + "]");
        logger.info("************************************************");
        logger.info("- redis " + REDISKEY + "[SERVER_URL] :   " + SERVER_URL + "*");
        logger.info("- redis " + REDISKEY + "[PORT] :   " + PORT + "*");
        logger.info("- redis " + REDISKEY + "[MAXTOTAL] :   " + MAXTOTAL + "*");
        logger.info("- redis " + REDISKEY + "[MAXIDLE] :   " + MAXIDLE + "*");
        logger.info("************************************************");


        if (SERVER_URL.indexOf(";") > -1) {
            Set<String> urlSet = new HashSet<String>();
            for (String url : SERVER_URL.split(";")) {
                urlSet.add(url);
            }
            String masterName = System.getProperty("masterName");
            POOL = new JedisSentinelPool(masterName, urlSet, config, PASSWORD);
        } else {
            POOL = new JedisPool(config, SERVER_URL, PORT, TIMEOUT, PASSWORD);
        }


    }

    /**
     * 获取jedis实例
     *
     * @return
     */
    public synchronized Jedis getClient() {

        if (POOL == null) {
            try {
                init();
                return POOL.getResource();
            } catch (final Exception e) {
                e.printStackTrace();
                System.out.println("新建redis连接");
                return new Jedis(SERVER_URL, 6379);
            }

        }
        //        System.out.println("该redis连接来自：" + POOL.toString() );
        //        System.out.println("当前活跃连接数：" + POOL.getNumActive());
        //        return POOL.getResource();
        //        final Jedis jedis = new Jedis(SERVER_URL,6379);
        return POOL.getResource();
    }

    /**
     * 返回给连接池
     *
     * @param jedis
     */
    public synchronized void closeConn(final Jedis jedis) {
        try {
            if (jedis != null) {
//				System.out.println("将要归还的连接是：" + jedis.toString());
                jedis.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("归还连接失败");
        }
    }

    // 序列化
    public byte[] serialize(final Object obj) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;
        if (obj == null) {
            return null;
        }
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            final byte[] byt = bos.toByteArray();
            return byt;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 反序列化
    public Object unserizlize(final byte[] byt) {
        if (byt == null) {
            return null;
        }
        ObjectInputStream oii = null;
        ByteArrayInputStream bis = null;
        bis = new ByteArrayInputStream(byt);
        try {
            oii = new ObjectInputStream(bis);
            final Object obj = oii.readObject();
            return obj;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void loadProperties() {
        String serverUrl = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.serverURL");
        if (serverUrl == null) {
            serverUrl = CommonConfig.getValue(REDISKEY + ".serverURL");
        }

        String port = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.port");
        if (port == null) {
            port = CommonConfig.getValue(REDISKEY + ".port");
        }

        String password = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.password");
        if (password == null) {
            password = CommonConfig.getValue(REDISKEY + ".password");
        }

        String maxTotal = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.maxTotal");
        if (maxTotal == null) {
            maxTotal = CommonConfig.getValue(REDISKEY + ".maxTotal");
        }

        String maxIdle = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.maxIdle");
        if (maxIdle == null) {
            maxIdle = CommonConfig.getValue(REDISKEY + ".maxIdle");
        }

        String maxWaitMillis = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.maxWaitMillis");
        if (maxWaitMillis == null) {
            maxWaitMillis = CommonConfig.getValue(REDISKEY + ".maxWaitMillis");
        }

        String testOnReturn = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.testOnReturn");
        if (testOnReturn == null) {
            testOnReturn = CommonConfig.getValue(REDISKEY + ".testOnReturn");
        }

        String testWhileIdle = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.testWhileIdle");
        if (testWhileIdle == null) {
            testWhileIdle = CommonConfig.getValue(REDISKEY + ".testWhileIdle");
        }

        String timeOut = CommonConfig.getValue(REDISKEY + ".database.defaultProvider.timeout");
        if (timeOut == null) {
            timeOut = CommonConfig.getValue(REDISKEY + ".timeout");
        }

        try {

            if (serverUrl != null) {
                SERVER_URL = serverUrl;
            }


            if (port != null) {
                PORT = Integer.parseInt(port);
            }
            if (password != null) {
                PASSWORD = String.valueOf(password);
            }
            if (maxTotal != null) {
                MAXTOTAL = Integer.parseInt(maxTotal);
            }
            if (maxIdle != null) {
                MAXIDLE = Integer.parseInt(maxIdle);
            }
            if (maxWaitMillis != null) {
                MAXWAITMILLS = Integer.parseInt(maxWaitMillis);
            }
            if (testOnReturn != null) {
                TESTONRETURN = Boolean.valueOf(testOnReturn);
            }
            if (testWhileIdle != null) {
                TESTWHILEIDLE = Boolean.valueOf(testWhileIdle);
            }
            if (timeOut != null) {
                TIMEOUT = Integer.parseInt(timeOut);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}