/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: ObjectFactory.java,v $
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



import java.io.Serializable;

import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectFactory implements Serializable {
    private static final Log LOG = LogFactory.getLog(ObjectFactory.class);

    private transient ClassLoader ccl;
    private static ThreadLocal<ObjectFactory> thSelf = new ThreadLocal<ObjectFactory>();


    public static ObjectFactory getObjectFactory() {
        return thSelf.get();
    }

    /**
     * Allows for ObjectFactory implementations that support
     * Actions without no-arg constructors.
     *
     * @return true if no-arg constructor is required, false otherwise
     */
    public boolean isNoArgConstructorRequired() {
        return true;
    }

    /**
     * Utility method to obtain the class matched to className. Caches look ups so that subsequent
     * lookups will be faster.
     *
     * @param className The fully qualified name of the class to return
     * @return The class itself
     * @throws ClassNotFoundException
     */
    public Class getClassInstance(String className) throws ClassNotFoundException {
        if (ccl != null) {
            return ccl.loadClass(className);
        }

        return ClassLoaderUtil.loadClass(className, this.getClass());
    }

  
    /**
     * Build a generic Java object of the given type.
     *
     * @param clazz the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link net.ooder.jds.core.esb.ActionContext}
     */
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        return clazz.newInstance();
    }

   

   
   
    static class ContinuationsClassLoader extends ClassLoader {
        
    }
}
