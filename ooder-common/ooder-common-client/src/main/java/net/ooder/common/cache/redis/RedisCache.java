/**
 * $RCSfile: RedisCache.java,v $
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

import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.*;

public class RedisCache<K, V> implements Cache<K, V> {

    private final Log logger = LogFactory.getLog(Constants.COMMON_CONFIGKEY, RedisCache.class);
    private final String configKey;
    /**
     * Maximum size in bytes that the cache can grow to.
     */
    protected int maxCacheSize;

    /**
     * Maintains the current size of the cache in bytes.
     */
    protected int cacheSize = 0;

    /**
     * Maximum length of time objects can exist in cache before expiring.
     */
    protected long maxLifetime = 30 * Constants.MINUTE;

    /**
     * Maintain the number of cache hits and misses. A cache hit occurs every time the get method is called and the
     * cache contains the requested object. A cache miss represents the opposite occurence.
     * <p>
     * <p>
     * Keeping track of cache hits and misses lets one measure how efficient the cache is; the higher the percentage of
     * hits, the more efficient.
     */
    protected long cacheHits, cacheMisses = 0L;

    /**
     * The name of the cache.
     */
    protected String name;

    private boolean isFull;

    protected boolean isStopPutWhenFull;

    private final String serverURL = "192.168.80.64";

    public RedisCache(String configKey, final String name, final int maxSize, final Long maxLifetime) {
        this.name = name;
        this.configKey = configKey;
        if (name.length() > 64) {
            final Throwable thrwo = new Throwable("name length >64!");
            thrwo.printStackTrace();
        }

        maxCacheSize = maxSize;
        if (maxLifetime != null) {
            this.maxLifetime = maxLifetime;
        }
    }

    @Override
    public synchronized void clear() {
        final Set<String> keySet = keySet();
        if (keySet.size() > 0) {
            Jedis jedis = null;
            try {
                jedis = getClient();
                logger.info("clear cache [" + configKey + "]" + this.getName() + " size=" + keySet.size());
                jedis.del(keySet.toArray(new String[keySet.size()]));
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                RedisPoolUtil.getInstance(configKey).closeConn(jedis);
            }
        }

    }

    @Override
    public long getCacheHits() {
        // Throwable thrwo = new Throwable("未实现！");
        // thrwo.printStackTrace();
        return 0;
    }

    @Override
    public long getCacheMisses() {
        // Throwable thrwo = new Throwable();
        // thrwo.printStackTrace();
        return 0;
    }

    @Override
    public int getCacheSize() {

        return 0;
    }

    @Override
    public int getMaxCacheSize() {

        return maxCacheSize;
    }

    @Override
    public long getMaxLifetime() {

        return maxLifetime;
    }

    @Override
    public K getName() {

        return (K) name;
    }

    @Override
    public void setMaxCacheSize(final int maxSize) {
        final Throwable thrwo = new Throwable();
        thrwo.printStackTrace();

    }

    @Override
    public void setMaxLifetime(final long maxLifetime) {

        this.maxLifetime = maxLifetime;

    }

    @Override
    public void setStopPutWhenFull(final boolean flag) {
        final Throwable thrwo = new Throwable();
        thrwo.printStackTrace();

    }

    @Override
    public boolean containsKey(final Object key) {
        final Jedis jedis = getClient();
        try {
            final Object object = jedis.get(key.toString());
            return object == null ? false : true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            RedisPoolUtil.getInstance(configKey).closeConn(jedis);
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        final Throwable thrwo = new Throwable("未实现！");
        thrwo.printStackTrace();
        return false;
    }

    @Override
    public Set entrySet() {

        final Throwable thrwo = new Throwable();
        thrwo.printStackTrace();
        return null;
    }

    @Override
    public V get(final Object key) {
        synchronized (key) {
            Object obj = null;
            if (key != null) {
                String realkey = key.toString();
                if (!realkey.startsWith(name + "[")) {
                    realkey = name + "[" + key.toString() + "]";
                }
                final Jedis jedis = getClient();
                try {
                    final byte[] byt = jedis.get(realkey.getBytes());
                    if (byt != null) {
                        obj = unserizlize(byt);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    logger.error("get key faild,key is " + key);
                } finally {
                    RedisPoolUtil.getInstance(configKey).closeConn(jedis);
                }
            }
            return (V) obj;
        }

    }

    @Override
    public boolean isEmpty() {
        boolean empty = true;
        final Jedis jedis = getClient();
        try {
            // judge by keyset
            final Set<String> keySet = jedis.keys("*");
            empty = keySet.isEmpty();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            RedisPoolUtil.getInstance(configKey).closeConn(jedis);
        }
        return empty;
    }

    @Override
    public synchronized Set keySet() {
        final Set keySet = new HashSet();
        keySet.addAll(getKeys());
        return keySet;
    }

    @Override
    public synchronized V put(K key, V value) {
        if (null == key) {
            return null;
        }

        if (key != null) {
            String realkey = key.toString();
            if (!realkey.startsWith(name + "[")) {
                realkey = name + "[" + key.toString() + "]";
            }
            //logger.info("clear key:="+key+" value="+value);
            if (value != null) {
                final Jedis jedis = getClient();
                try {
                    // System.out.println("序列化对象之后是：" + serialize(value));
                    jedis.psetex(realkey.getBytes(), maxLifetime, serialize(value));
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    RedisPoolUtil.getInstance(configKey).closeConn(jedis);
                }
            } else {
                remove(realkey);
            }
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            K key = (K) i.next();
            if (key != null) {
                V value = (V) map.get(key);
                put(key, value);
            }

        }
    }

    @Override
    public V remove(final Object key) {
        Object obj = null;
        if (key != null) {
            final Jedis jedis = getClient();
            try {
                 String realkey = key.toString();
                if (!realkey.startsWith(name + "[")) {
                    realkey = name + "[" + key.toString() + "]";
                }

                obj = jedis.get(realkey);
                if (obj != null) {
                    obj = jedis.del(realkey);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                RedisPoolUtil.getInstance(configKey).closeConn(jedis);
            }
        }
        return (V) obj;
    }

    @Override
    public int size() {
        int size = 0;
        final Jedis jedis = getClient();
        try {
            final Set<String> keys = jedis.keys("*");
            size = keys.size();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            RedisPoolUtil.getInstance(configKey).closeConn(jedis);
        }
        return size;

    }

    @Override
    public Collection values() {
        logger.info("----- RedisCache-values()--in -----");
        final List<Object> values = new ArrayList();
        final Set<String> keySet = keySet();
        Object obj;
        final Jedis jedis = getClient();
        try {
            for (String key : keySet) {
                try {
                    obj = unserizlize(jedis.get(key.getBytes()));
                    // System.out.println("反序列化对象是：" + obj);
                } catch (final Exception e) {
                    e.printStackTrace();
                    continue;
                }
                values.add(obj);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            RedisPoolUtil.getInstance(configKey).closeConn(jedis);
        }
        return values;
    }

    public Jedis getClient() {
        try {
            return RedisPoolUtil.getInstance(configKey).getClient();
        } catch (final Exception e) {
            e.printStackTrace();
            logger.error("-----getJedisClient from RedisUtil failed");
        }
        return null;
    }

    public Set<String> getKeys() {
        Set<String> stringKeys = null;
        final Jedis jedis = getClient();
        try {
            // System.out.println("name is " + name);
            final Set<String> keys = jedis.keys(name + "\\[*");
            stringKeys = new HashSet<String>();
            if (!keys.isEmpty()) {
                for (final String k : keys) {
                    stringKeys.add(k);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            RedisPoolUtil.getInstance(configKey).closeConn(jedis);
        }
        return stringKeys;
    }

    /**
     * Returns the size of an object in bytes. Determining size by serialization is only used as a last resort.
     *
     * @return the size of an object in bytes.
     */
    protected int calculateSize(final Object object) {
        // If the object is Cacheable, ask it its size.
        if (object instanceof Cacheable) {
            return ((Cacheable) object).getCachedSize();
        }
        // Coherence puts DataInputStream objects in cache.
        else if (object instanceof DataInputStream) {
            int size = 1;
            try {
                size = ((DataInputStream) object).available();
            } catch (final IOException ioe) {
            }
            return size;
        }
        // Check for other common types of objects put into cache.
        else if (object instanceof Long) {
            return CacheSizes.sizeOfLong();
        } else if (object instanceof Integer) {
            return CacheSizes.sizeOfObject() + CacheSizes.sizeOfInt();
        } else if (object instanceof Boolean) {
            return CacheSizes.sizeOfObject() + CacheSizes.sizeOfBoolean();
        } else if (object instanceof long[]) {
            final long[] array = (long[]) object;
            return CacheSizes.sizeOfObject() + array.length * CacheSizes.sizeOfLong();
        }
        // Default behavior -- serialize the object to determine its size.
        else {
            int size = 1;
            try {
                // Default to serializing the object out to determine size.
                final NullOutputStream out = new NullOutputStream();
                final ObjectOutputStream outObj = new ObjectOutputStream(out);
                outObj.writeObject(object);
                size = out.size();
            } catch (final IOException ioe) {
                size = 1024;
                // log.warn("the cache "+ this.getName()+" calculateSize err the
                // defaultSize=\"1024\"");
                // ioe.printStackTrace();
            }
            return size;
        }
    }

    /**
     * An extension of OutputStream that does nothing but calculate the number of bytes written through it.
     */
    private static class NullOutputStream extends OutputStream {

        int size = 0;

        @Override
        public void write(final int b) throws IOException {
            size++;
        }

        @Override
        public void write(final byte[] b) throws IOException {
            size += b.length;
        }

        @Override
        public void write(final byte[] b, final int off, final int len) {
            size += len;
        }

        /**
         * Returns the number of bytes written out through the stream.
         *
         * @return the number of bytes written to the stream.
         */
        public int size() {
            return size;
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

}
