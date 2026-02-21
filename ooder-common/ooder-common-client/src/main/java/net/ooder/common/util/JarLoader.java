/**
 * $RCSfile: JarLoader.java,v $
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by IntelliJ IDEA. User: zh Date: 2009-8-6 Time: 15:31:05 To change this template use File | Settings | File
 * Templates.
 */
public class JarLoader extends ClassLoader {
    private JarFile jarFile;

    private static Hashtable<String, Set<Class<?>>> classMaps = new Hashtable<String, Set<Class<?>>>();

    private static Map<String, JarEntry> javaSourceMaps = new HashMap<String, JarEntry>();

    private void setJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    public JarLoader(ClassLoader parent) {
        super(parent);
    }

    public JarLoader(String jarPath) throws IOException {
        super(Thread.currentThread().getContextClassLoader());
        this.jarFile = new JarFile(jarPath);
    }

    public JarLoader(File jarFile) throws IOException {
        super(Thread.currentThread().getContextClassLoader());
        this.jarFile = new JarFile(jarFile);
    }

    public JarLoader(File jarFile, ClassLoader parent) throws IOException {
        super(parent);
        this.jarFile = new JarFile(jarFile);
    }

    public Set<Class<?>> getAllClassByPackage() throws IOException {
        return getClassByPackage("", false);
    }

    public Set<Class<?>> getAllClassByPackage(boolean reload) throws IOException {
        return getClassByPackage("", reload);
    }

    public Set<Class<?>> getClassByPackage(final String name) throws IOException {
        return getClassByPackage(name, false);
    }

    /**
     * 获取jar文件中的指定包名前缀的所有类
     *
     * @param name   包名前缀
     * @param reload 是否重加载
     * @return
     * @throws IOException
     */
    public Set<Class<?>> getClassByPackage(final String name, boolean reload) throws IOException {
        Set<Class<?>> classes = classMaps.get(this.jarFile.getName() + name);
        if (classes == null) {
            classes = loadClasses(name);
            classMaps.put(this.jarFile.getName() + name, classes);
        } else if (reload) {
            JarLoader loader = new JarLoader(this.getParent());
            loader.setJarFile(this.jarFile);
            classes = loader.loadClasses(name);
            classMaps.put(this.jarFile.getName() + name, classes);
        }
        return classes;
    }

    /**
     * 加载指定包前缀的所有类
     *
     * @param packageName 包名前缀
     * @return
     * @throws IOException
     */
    private Set<Class<?>> loadClasses(final String packageName) throws IOException {
        String tmpName = packageName.replace('.', '/');
        JarEntry entry = null;
        Enumeration<JarEntry> es = jarFile.entries();
        Set<Class<?>> classes = new HashSet<>();
        while (es.hasMoreElements()) {
            entry = es.nextElement();
            String eName = entry.getName();
            if (eName.endsWith(".class") && ("".equals(tmpName) || eName.startsWith(tmpName))) {
                if (getClassByEntry(entry) != null) {
                    classes.add(getClassByEntry(entry));
                }
            }
        }
        return classes;
    }

    /**
     * 获取JAVA源码
     *
     * @return
     * @throws IOException
     */
    public byte[] loadJava(String className) throws IOException {
        String tmpName = className.replace('.', '/');
        byte[] bs = null;
        JarEntry entry = javaSourceMaps.get(className);
        if (entry != null) {
            bs = getEntryBytes(entry);
        } else {
            Enumeration<JarEntry> es = jarFile.entries();
            while (es.hasMoreElements()) {
                entry = es.nextElement();
                String eName = entry.getName();
                if (eName.endsWith(".java") && (eName.startsWith(tmpName))) {
                    bs = getEntryBytes(entry);
                    javaSourceMaps.put(className, entry);
                }
            }
        }
        return bs;
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        Class c = null;
        try {
            c = super.loadClass(name);

        } catch (Throwable t) {
            // System.out.println(t.getMessage() + "[class name =" + name + "]");
        }

        return ClassUtility.loadClass(name);
    }

    /**
     * 从jar内容生成class
     *
     * @param entry
     * @return
     * @throws IOException
     */
    private Class getClassByEntry(JarEntry entry) throws IOException {
        String name = entry.getName();
        name = name.replace('/', '.');
        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - ".class".length());
        }

        Class c = null;
        try {
            c = super.loadClass(name);

        } catch (Throwable t) {
            // System.out.println(t.getMessage() + "[class name =" + name + "]");


        }
        if (c == null) {

            byte[] bs = getEntryBytes(entry);
            try {
                c = super.defineClass(name, bs, 0, bs.length);
            } catch (Exception ee) {
                ee.printStackTrace();
            }

        }
        return c;
    }

    /**
     * 取得jar内容数据
     *
     * @param entry
     * @return
     * @throws IOException
     */
    private byte[] getEntryBytes(JarEntry entry) throws IOException {
        InputStream is = jarFile.getInputStream(entry);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());
        int c = 0;
        byte[] buff = new byte[10240];
        while ((c = is.read(buff)) > 0) {
            bos.write(buff, 0, c);
        }
        is.close();
        return bos.toByteArray();
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {

        Class clazz = null;
        try {
            clazz = loadClass(name);
        } catch (ClassNotFoundException e) {
        }

        if (clazz.getPackage() == null) {
            String packageName = name.substring(0, name.lastIndexOf("."));
            definePackage(packageName, "ESDDynPackage", "1.0.0", "ESD", null, null, null, null);
        }
        return clazz;

    }

}
