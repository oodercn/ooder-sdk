/**
 * $RCSfile: CaselessStringKeyHashMap.java,v $
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
package net.ooder.common.util;

import net.ooder.common.cache.Cacheable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 不区分字符串键的大小写的HashMap。
 * <p>Title: </p>
 * <p>Description:  </p>
 * <p></p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: www.raddev.cn</p>
 *
 * @author wenzhang
 * @version 2.0
 */
public class CaselessStringKeyHashMap<T, K> extends HashMap<T, K> implements Cacheable, Serializable {
    public CaselessStringKeyHashMap() {
        super();
    }

    @Override
    public K get(Object key) {
        if (key instanceof String) {
            return super.get(((String) key).toUpperCase());
        } else {
            return super.get(key);
        }
    }

    @Override
    public void putAll(Map<? extends T, ? extends K> m) {
        for (Map.Entry<? extends T, ? extends K> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public K put(T key, K value) {
        if (key instanceof String) {
            return super.put((T) key.toString().toUpperCase(), value);
        } else {
            return super.put(key, value);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return super.containsKey(((String) key).toUpperCase());
        } else {
            return super.containsKey(key);
        }
    }

    @Override
    public K remove(Object key) {
        if (key instanceof String) {
            return super.remove((T) key.toString().toUpperCase());
        } else {
            return super.remove(key);
        }
    }

    @Override
    public int getCachedSize() {
        int size = 0;
        size = size + this.toString().length();
        return size;
    }
}
