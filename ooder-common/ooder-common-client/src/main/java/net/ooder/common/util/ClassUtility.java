/**
 * $RCSfile: ClassUtility.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:55 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
 * <p>
 * $RCSfile: ClassUtility.java,v $
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
/**
 * $RCSfile: ClassUtility.java,v $
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

import net.ooder.annotation.Debug;
import net.ooder.common.util.java.DynamicClassLoader;
import net.ooder.common.util.java.TmpJavaFileObject;
import net.ooder.esb.config.manager.EsbBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public final class ClassUtility {


    private static ClassUtility instance = new ClassUtility();

    public static Map<String, Class<?>> dynClassMap = new ConcurrentHashMap<>();

    public static Set<String> contextClassPath = new HashSet<>();

    public static Set<String> disableClass = new HashSet<>();

    public static Set<String> debugClass = new HashSet<>();

    public static Map<String, Class<?>> fileClassMap = new ConcurrentHashMap<>();

    private static Map<String, JavaFileObject> fileObjectMap = new ConcurrentHashMap<>();

    public static Map<String, DynamicClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ClassUtility.class);

    private static final String InitClassName = "net.ooder.JDSInit";


    private ClassUtility() {

    }

    public static boolean isDebug(String className) {
        try {
            if (debugClass.contains(className)) {
                return true;
            } else if (!disableClass.contains(className)) {
                Class clazz = ClassUtility.loadClass(className);
                Debug debug = (Debug) clazz.getAnnotation(Debug.class);
                if (debug != null) {
                    debugClass.add(className);
                    return true;
                }
            }
        } catch (Exception e) {
            disableClass.add(className);
        }
        return false;

    }


    public static boolean isAssignableFrom(Class innerClazz, Class parentClass) {
        for (Class interfaces : parentClass.getInterfaces()) {
            if (interfaces.getName().equals(innerClazz.getName())) {
                return true;
            }
        }
        return false;
    }


    public static DynamicClassLoader getDynamicClassLoader(String className, TmpJavaFileObject fileObject, Boolean dyn) {
        synchronized (className) {
            DynamicClassLoader loader = classLoaderMap.get(className);
            if (loader == null || (loader.getClassName() != null && !loader.getClassName().equals(className)) || dyn) {
                loader = new DynamicClassLoader(fileObject, className);
                classLoaderMap.put(className, loader);
            } else {
                loader.setTmpJavaFileObject(fileObject);
            }
            return loader;
        }
    }

    public static Set<Class> checkBase(Set<Class> sourceSet) {
        Set<Class> checkClassSet = new HashSet<>();
        for (Class viewClass : sourceSet) {
            if (viewClass != null && !viewClass.getName().startsWith("java.lang")) {
                checkClassSet.add(viewClass);
            }
        }
        return checkClassSet;
    }


    /**
     * Loads the class with the specified name.
     *
     * @param className the name of the class
     * @return the resulting <code>Class</code> object
     * @throws ClassNotFoundException if the class was not found
     */
    public static Class loadClass(String className) throws ClassNotFoundException {
        //   synchronized (className) {
        long start = System.currentTimeMillis();

        if (className.equals("int")) {
            return int.class;
        } else if (className.equals("long")) {
            return long.class;
        } else if (className.equals("char")) {
            return Character.class;
        } else if (className.equals("short")) {
            return short.class;
        } else if (className.equals("byte")) {
            return byte.class;
        } else if (className.equals("float")) {
            return float.class;
        } else if (className.equals("double")) {
            return double.class;
        } else if (className.equals("boolean")) {
            return boolean.class;
        }
        Class theClass = null;


        if (!CompileJava.isDebug()) {
            theClass = fileClassMap.get(className);
            if (theClass == null) {
                theClass = dynClassMap.get(className);
                TmpJavaFileObject fileObject = (TmpJavaFileObject) fileObjectMap.get(className);
                if (fileObject != null) {
                    if (theClass == null) {
                        DynamicClassLoader loader = getDynamicClassLoader(className, fileObject, false);
                        try {
                            theClass = loader.findClass(className);
                            if (theClass != null) {
                                dynClassMap.put(className, theClass);
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (theClass.getClassLoader() instanceof DynamicClassLoader &&
                            !((DynamicClassLoader) theClass.getClassLoader()).getClassName().equals(className)
                            ) {
                        DynamicClassLoader loader = new DynamicClassLoader(fileObject, className);
                        classLoaderMap.put(className, loader);
                    }
                }
            }


            if (theClass == null) {
                theClass = EsbBeanFactory.findClass(className);
            }


            if (theClass == null) {
                try {
                    theClass = Class.forName(className);
                } catch (ClassNotFoundException e1) {
                    try {
                        theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                    } catch (ClassNotFoundException e2) {

                    }
                }
            }

            if (theClass == null) {
                try {
                    theClass = instance.getClass().getClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                }
            }
            if (System.currentTimeMillis() - start > 5) {
                logger.info("end  instance.getClass().loadClass  ---end= " + className + "[" + className + "] times=" + (System.currentTimeMillis() - start));
            }
            if ((theClass == null || (theClass.getClassLoader() instanceof DynamicClassLoader)) && !disableClass.contains(className)) {
                List<String> copyClassPath = new ArrayList<String>();
                copyClassPath.addAll(contextClassPath);
                for (String classPath : copyClassPath) {
                    try {
                        theClass = loadClassByFile(classPath, className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (theClass != null) {
                        break;
                    }
                }
            }
            if (System.currentTimeMillis() - start > 5) {
                logger.info("end  loadClassByFile ---end= " + className + "[" + className + "] times=" + (System.currentTimeMillis() - start));
            }
            if (theClass == null) {
                theClass = EsbBeanFactory.findClass(className);
            } else {
                EsbBeanFactory.addClassCache(className, theClass);
            }

            if (theClass == null) {
                if (System.currentTimeMillis() - start > 5) {
                    System.out.println("end EsbBeanFactory.findClass= " + className + " times=" + (System.currentTimeMillis() - start));
                }
                disableClass.add(className);
                throw new ClassNotFoundException("class " + className + "  NotFound!");
            } else {
                disableClass.remove(className);
            }
        } else {


            try {
                theClass = Class.forName(className);
            } catch (ClassNotFoundException e1) {
                try {
                    theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                } catch (ClassNotFoundException e2) {

                }
            }


            if (theClass == null) {
                try {
                    theClass = instance.getClass().getClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                }
            }
        }


        return theClass;
        //  }

    }

    public static Set<String> getDisableClass() {
        return disableClass;
    }

    public static Class loadClassByFile(String classPath, String className) throws ClassNotFoundException {
        Class theClass = fileClassMap.get(className);
        if (theClass == null) {
            FileClassLoader classLoader = new FileClassLoader(classPath);
            theClass = classLoader.loadClass(className);
            if (theClass == null) {
                theClass = classLoader.findClass(className);
            }
            if (theClass != null) {
                fileClassMap.put(className, theClass);
                disableClass.remove(className);
            }
        } else {
            disableClass.remove(className);
        }
        return theClass;
    }


    public static Package getPackage(String packageName) {
        Package innerPack = Package.getPackage(packageName);
        if (innerPack != null) {
            return innerPack;
        }
        for (Package pack : ClassUtility.getAllDynPacks()) {
            if (pack.getName().equals(packageName)) {
                return pack;
            }
        }
        return null;
    }

    public static Set<String> getContextClassPath() {
        return contextClassPath;
    }

    public static void setContextClassPath(Set<String> contextClassPath) {
        ClassUtility.contextClassPath = contextClassPath;
    }

    /**
     * Loads the resource with the specified name.
     *
     * @param name the name of the resource
     * @return the resulting <code>java.io.InputStream</code> object
     */
    public static InputStream loadResource(String name) {
        InputStream in = instance.getClass().getResourceAsStream(name);
        if (in == null) {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
            if (in == null) {
                in = instance.getClass().getClassLoader().getResourceAsStream(name);
            }
        }
        return in;
    }

    /**
     * Loads the resource with the specified name.
     *
     * @param name the name of the resource
     * @return the result <code>java.net.URL</code> object
     */
    public static URL loadResourceURL(String name) {
        URL url = instance.getClass().getResource(name);

        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(name);
            if (url == null) {
                url = instance.getClass().getClassLoader().getResource(name);
            }
        }
        return url;
    }

    public static void clear(String className) {
        dynClassMap.remove(className);
        fileObjectMap.remove(className);
        classLoaderMap.remove(className);
        fileClassMap.remove(className);
        disableClass.remove(className);
        EsbBeanFactory.clear(className);

    }

    public static void delete(String className) {
        clear(className);
        try {
            Class clazz = loadClass(className);
            if (clazz.getClassLoader() instanceof FileClassLoader) {
                FileClassLoader fileClassLoader = (FileClassLoader) clazz.getClassLoader();
                fileClassLoader.delete();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Set<Package> getAllDynPacks() {
        Set<Package> packages = new HashSet<>();
        Set<String> keySet = dynClassMap.keySet();
        for (String key : keySet) {
            Class clazz = dynClassMap.get(key);
            packages.add(clazz.getPackage());
        }
        Set<String> fileKeySet = fileClassMap.keySet();
        for (String key : fileKeySet) {
            Class clazz = fileClassMap.get(key);
            if (clazz != null && clazz.getPackage() != null && !packages.contains(clazz.getPackage())) {
                packages.add(clazz.getPackage());
            }
        }
        return packages;

    }

    public static Map<String, Class<?>> getFileClassMap() {
        return fileClassMap;
    }

    public static void setFileClassMap(Map<String, Class<?>> fileClassMap) {
        ClassUtility.fileClassMap = fileClassMap;
    }

    public static Map<String, Class<?>> getDynClassMap() {
        return dynClassMap;
    }

    public static void setDynClassMap(Map<String, Class<?>> dynClassMap) {
        ClassUtility.dynClassMap = dynClassMap;
    }

    public static Map<String, JavaFileObject> getFileObjectMap() {
        return fileObjectMap;
    }

    public static void setFileObjectMap(Map<String, JavaFileObject> fileObjectMap) {
        ClassUtility.fileObjectMap = fileObjectMap;
    }
}
