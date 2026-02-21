/**
 * $RCSfile: CacheObject.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CacheObject.java,v $
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

import java.io.Serializable;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 
 * Wrapper for all objects put into cache. It's primary purpose is to maintain
 * references to the linked lists that maintain the creation time of the object
 * and the ordering of the most used objects.
 *
 * This class is optimized for speed rather than strictly correct encapsulation.
 * <p>
 * 
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public final class CacheObject implements Serializable {

    /**
     * Underlying object wrapped by the CacheObject.
     */
    public Object object;

    /**
     * The size of the Cacheable object. The size of the Cacheable
     * object is only computed once when it is added to the cache. This makes
     * the assumption that once objects are added to cache, they are mostly
     * read-only and that their size does not change significantly over time.
     */
    public int size;

    /**
     * A reference to the node in the cache order list. We keep the reference
     * here to avoid linear scans of the list. Every time the object is
     * accessed, the node is removed from its current spot in the list and
     * moved to the front.
     */
    public LinkedListNode lastAccessedListNode;

    /**
     * A reference to the node in the age order list. We keep the reference
     * here to avoid linear scans of the list. The reference is used if the
     * object has to be deleted from the list.
     */
    public LinkedListNode ageListNode;

    /**
     * A count of the number of times the object has been read from cache.
     */
    public int readCount = 0;

    /**
     * Creates a new cache object wrapper. The size of the Cacheable object
     * must be passed in in order to prevent another possibly expensive
     * lookup by querying the object itself for its size.<p>
     *
     * @param object the underlying Object to wrap.
     * @param size the size of the Cachable object in bytes.
     */
    public CacheObject(Object object, int size) {
        this.object = object;
        this.size = size;
    }
}