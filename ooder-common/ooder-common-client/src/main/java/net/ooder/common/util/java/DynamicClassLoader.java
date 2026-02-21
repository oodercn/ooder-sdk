/**
 * $RCSfile: DynamicClassLoader.java,v $
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
package net.ooder.common.util.java;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.IOUtility;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DynamicClassLoader extends ClassLoader {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, DynamicClassLoader.class);
    static String[] skipInner = new String[]{"Customizer", "BeanInfo"};
    private String className;
    private Set classNameSet = new HashSet();
    private byte[] bytes;


    public DynamicClassLoader(TmpJavaFileObject myJavaFileObject, String className) {
        this.bytes = myJavaFileObject.getCompiledBytes();
        this.className = className;
    }


    public void dumpFile(File file) throws IOException {
        IOUtility.writeBytesToNewFile(bytes, file);
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        try {
            clazz = super.loadClass(name);
        } catch (Throwable e) {
            clazz = ClassUtility.loadClass(name);
        }
        return clazz;
    }

    @Override
    public Package[] getPackages() {
        return super.getPackages();
    }


    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {

        Class clazz = null;


        //过滤代理产生非动态类
        if (className != null && !name.equals(className) && name.startsWith(className)) {
            for (String skipStr : skipInner) {
                if (name.equals(className + skipStr)) {
                    return null;
                }
            }
        }


        try {
            clazz = super.findClass(name);
        } catch (ClassNotFoundException e) {
        }

        if (clazz == null) {
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
            }
        }

        if (clazz == null) {
            clazz = ClassUtility.dynClassMap.get(name);
        }

        if (clazz == null) {
            clazz = ClassUtility.fileClassMap.get(name);
        }


        try {
            if (clazz == null && bytes != null && bytes.length > 0) {
                clazz = defineClass(name, bytes, 0, bytes.length);
                if (clazz != null && clazz.getPackage() == null) {
                    String packageName = name.substring(0, name.lastIndexOf("."));
                    definePackage(packageName, "ESDDynPackage", "1.0.0", "ESD", null, null, null, null);
                }
            }

        } catch (Throwable e) {

            if (!classNameSet.contains(name)) {
                classNameSet.add(name);
                ClassUtility.clear(name);
                clazz = ClassUtility.loadClass(name);
            } else {
                e.printStackTrace();
                //  throw new ClassNotFoundException();
            }


            log.warn(e.getMessage());
            //e.printStackTrace();
            //clazz = ClassUtility.loadClass(name);
            //throw new ClassNotFoundException();
        }

        return clazz;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTmpJavaFileObject(TmpJavaFileObject myJavaFileObject) {
        super.clearAssertionStatus();
        this.bytes = myJavaFileObject.getCompiledBytes();
    }
}

