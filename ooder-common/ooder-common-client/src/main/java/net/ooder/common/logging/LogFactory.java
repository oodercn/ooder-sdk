/**
 * $RCSfile: LogFactory.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:06 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: LogFactory.java,v $
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
package net.ooder.common.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ooder.common.CommonConfig;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: Factory for creating {@link Log} instances.</p>
 * 
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public abstract class LogFactory {

    // ----------------------------------------------------- Manifest Constants

    /**
     * The fully qualified class name of the fallback <code>LogFactory</code>
     * implementation class to use, if no other can be found.
     */
    public static final String FACTORY_DEFAULT = "net.ooder.common.logging.impl.LogFactoryImpl";

    // ----------------------------------------------------------- Protect Variable
    protected String           configKey;

    // ----------------------------------------------------------- Constructors

    /**
     * Protected constructor that is not available for public use.
     */
    protected LogFactory() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Convenience method to derive a name from the specified class and
     * call <code>getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public abstract Log getInstance(Class clazz) throws LogConfigurationException;

    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public abstract Log getInstance(String name) throws LogConfigurationException;

    /**
     * Release any internal references to previously created {@link Log}
     * instances returned by this factory.  This is useful environments
     * like servlet containers, which implement application reloading by
     * throwing away a ClassLoader.  Dangling references to objects in that
     * class loader would prevent garbage collection.
     */
    public abstract void release();

    // ------------------------------------------------------- Static Variables

    /**
     * The previously constructed <code>LogFactory</code> instances, keyed by
     * the <code>configKey</code> with which it was configed.
     */
    protected static Map factories = new HashMap();

    // --------------------------------------------------------- Static Methods

    /**
     * <p>Construct (if necessary) and return a <code>LogFactory</code> instance.
     * @exception LogConfigurationException if the implementation class is not
     *  available or cannot be instantiated.
     */
    public static LogFactory getFactory(final String configKey) throws LogConfigurationException {

        // Identify the class loader we will be using
        ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                return getContextClassLoader();
            }
        });

        // Return any previously registered factory for this class loader
        LogFactory factory = getCachedFactory(configKey);
        if (factory != null)
            return factory;

        String ref = CommonConfig.getValue(configKey + ".log.ref");
        if (ref != null) {
            factory = getCachedFactory(ref);
            if (factory == null) {
                factory = getFactory(ref);
            }
        }

        if (factory == null) {
            String factoryClass = CommonConfig.getValue(configKey + ".log.logFactory");
            if (factory == null && factoryClass != null) {
                factory = newFactory(factoryClass, contextClassLoader);
            }
        }

        // Try the fallback implementation class
        if (factory == null) {
            factory = newFactory(FACTORY_DEFAULT, LogFactory.class.getClassLoader());
        }

        if (factory != null) {
            /**
             * Always cache using configKey.
             */
            factory.configKey = configKey;
            cacheFactory(configKey, factory);
        }

        return factory;
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param clazz Class for which a log name will be derived
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static Log getLog(String configKey, Class clazz) throws LogConfigurationException {

        return (getFactory(configKey).getInstance(clazz));

    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static Log getLog(String configKey, String name) throws LogConfigurationException {

        return (getFactory(configKey).getInstance(name));

    }

    /**
     * Release any internal references to previously created {@link LogFactory}
     * instances that have been associated with the specified class loader
     * (if any), after calling the instance method <code>release()</code> on
     * each of them.
     *
     * @param classLoader ClassLoader for which to release the LogFactory
     */
    public static void release(String configKey) {

        synchronized (factories) {
            LogFactory factory = (LogFactory) factories.get(configKey);
            if (factory != null) {
                factory.release();
                factories.remove(configKey);
            }
        }

    }

    /**
     * Release any internal references to previously created {@link LogFactory}
     * instances, after calling the instance method <code>release()</code> on
     * each of them.  This is useful environments like servlet containers,
     * which implement application reloading by throwing away a ClassLoader.
     * Dangling references to objects in that class loader would prevent
     * garbage collection.
     */
    public static void releaseAll() {

        synchronized (factories) {
            Iterator ite = factories.values().iterator();
            while (ite.hasNext()) {
                LogFactory element = (LogFactory) ite.next();
                element.release();
            }
            factories.clear();
        }

    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the thread context class loader if available.
     * Otherwise return null.
     * 
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     *
     * @exception LogConfigurationException if a suitable class loader
     * cannot be identified.
     */
    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        ClassLoader classLoader = null;

        try {
            // Are we running on a JDK 1.2 or later system?
            Method method = Thread.class.getMethod("getContextClassLoader", null);

            // Get the thread context class loader (if there is one)
            try {
                classLoader = (ClassLoader) method.invoke(Thread.currentThread(), null);
            } catch (IllegalAccessException e) {
                throw new LogConfigurationException("Unexpected IllegalAccessException", e);
            } catch (InvocationTargetException e) {
                /**
                 * InvocationTargetException is thrown by 'invoke' when
                 * the method being invoked (getContextClassLoader) throws
                 * an exception.
                 * 
                 * getContextClassLoader() throws SecurityException when
                 * the context class loader isn't an ancestor of the
                 * calling class's class loader, or if security
                 * permissions are restricted.
                 * 
                 * In the first case (not related), we want to ignore and
                 * keep going.  We cannot help but also ignore the second
                 * with the logic below, but other calls elsewhere (to
                 * obtain a class loader) will trigger this exception where
                 * we can make a distinction.
                 */
                if (e.getTargetException() instanceof SecurityException) {
                    ; // ignore
                } else {
                    // Capture 'e.getTargetException()' exception for details
                    // alternate: log 'e.getTargetException()', and pass back 'e'.
                    throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
                }
            }
        } catch (NoSuchMethodException e) {
            // Assume we are running on JDK 1.1
            classLoader = LogFactory.class.getClassLoader();
        }

        // Return the selected class loader
        return classLoader;
    }

    /**
     * Check cached factories (keyed by classLoader)
     */
    private static LogFactory getCachedFactory(String configKey) {
        LogFactory factory = null;

        if (configKey != null)
            factory = (LogFactory) factories.get(configKey);

        return factory;
    }

    private static void cacheFactory(String configKey, LogFactory factory) {
        if (configKey != null && factory != null)
            factories.put(configKey, factory);
    }

    /**
     * Return a new instance of the specified <code>LogFactory</code>
     * implementation class, loaded by the specified class loader.
     * If that fails, try the class loader used to load this
     * (abstract) LogFactory.
     *
     * @param factoryClass Fully qualified name of the <code>LogFactory</code>
     *  implementation class
     * @param classLoader ClassLoader from which to load this class
     *
     * @exception LogConfigurationException if a suitable instance
     *  cannot be created
     */
    protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader) throws LogConfigurationException {
        Object result = AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                try {
                    if (classLoader != null) {
                        try {
                            // first the given class loader param (thread class loader)

                            // warning: must typecast here & allow exception
                            // to be generated/caught & recast propertly.
                            return (LogFactory) classLoader.loadClass(factoryClass).newInstance();
                        } catch (ClassNotFoundException ex) {
                            if (classLoader == LogFactory.class.getClassLoader()) {
                                // Nothing more to try, onwards.
                                throw ex;
                            }
                            // ignore exception, continue
                        } catch (NoClassDefFoundError e) {
                            if (classLoader == LogFactory.class.getClassLoader()) {
                                // Nothing more to try, onwards.
                                throw e;
                            }

                        } catch (ClassCastException e) {

                            if (classLoader == LogFactory.class.getClassLoader()) {
                                // Nothing more to try, onwards (bug in loader implementation).
                                throw e;
                            }
                        }
                        // ignore exception, continue  
                    }

                    /* At this point, either classLoader == null, OR
                     * classLoader was unable to load factoryClass..
                     * try the class loader that loaded this class:
                     * LogFactory.getClassLoader().
                     * 
                     * Notes:
                     * a) LogFactory.class.getClassLoader() may return 'null'
                     *    if LogFactory is loaded by the bootstrap classloader.
                     * b) The Java endorsed library mechanism is instead
                     *    Class.forName(factoryClass);
                     */
                    // warning: must typecast here & allow exception
                    // to be generated/caught & recast propertly.
                    return (LogFactory) Class.forName(factoryClass).newInstance();
                } catch (Exception e) {
                    return new LogConfigurationException(e);
                }
            }
        });

        if (result instanceof LogConfigurationException)
            throw (LogConfigurationException) result;

        return (LogFactory) result;
    }
}
