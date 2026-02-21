/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: ObjectTypeDeterminer.java,v $
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
package net.ooder.jds.core.esb.util;

/**
 * Determines what the key and and element class of a Map or Collection should be. For Maps, the elements are the
 * values. For Collections, the elements are the elements of the collection.
 *
 * @author Gabriel Zimmerman
 */
public interface ObjectTypeDeterminer {
    public Class getKeyClass(Class parentClass, String property);

    public Class getElementClass(Class parentClass, String property, Object key);

    public String getKeyProperty(Class parentClass, String property);
    
    public boolean shouldCreateIfNew(Class parentClass, 
            String property, 
            Object target,
            String keyProperty,
            boolean isIndexAccessed);
}
