/**
 * $RCSfile: FileClassLoader.java,v $
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

import net.ooder.common.util.java.DynamicClassLoader;
import net.ooder.config.JDSUtil;

import java.io.*;

public class FileClassLoader extends ClassLoader {

    public String classPath;

    public File classFile;

    public FileClassLoader(String classPath) {
        this.classPath = classPath;
    }


    @Override
    public Class<?> loadClass(String name) {
        Class c = null;
        try {
            c = super.loadClass(name);
        } catch (Throwable t) {
            System.out.println(t.getMessage() + "[class name =" + name + "]");
        }
        return c;
    }

    @Override
    public Package[] getPackages() {
        return super.getPackages();
    }


    @Override
    public Class findClass(String className) throws ClassNotFoundException {
        Class theClass = null;
        try {
            if (classPath == null) {
                classPath = JDSUtil.getJdsRealPath() + "classes";
            }
            String fileName = className.replace(".", "/") + ".class";
            String javaName = className.replace(".", "/") + ".java";
            this.classFile = new File(classPath, fileName);
            File javaClassFile = new File(classPath, javaName);

            if (!classFile.exists() || (javaClassFile.exists() && (javaClassFile.lastModified() - classFile.lastModified() > 1000))) {
                if (javaClassFile.exists()) {
                    String content = IOUtility.toString(new FileInputStream(javaClassFile));
                    if (content.length() > 0) {
                        theClass = CompileJava.dynCompile(className, content, classPath);
                        if (theClass != null) {
                            if (theClass.getClassLoader() instanceof DynamicClassLoader) {
                                DynamicClassLoader loader = (DynamicClassLoader) theClass.getClassLoader();
                                try {
                                    loader.dumpFile(classFile);
                                } catch (Exception e) {

                                }
                            }
                        } else {
                            throw new ClassNotFoundException(classFile.getPath() + " 编译失败");
                        }
                    }
                }

            } else {
                byte[] bs = getEntryBytes(classFile);
                theClass = super.defineClass(className, bs, 0, bs.length);
                if (theClass.getPackage() == null) {
                    String packageName = className.substring(0, className.lastIndexOf("."));
                    definePackage(packageName, "ESDDynPackage", "1.0.0", "ESD", null, null, null, null);
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        if (theClass == null) {
            ClassUtility.getDisableClass().add(className);
        }

        return theClass;
    }

    public boolean delete() {
        boolean isDel = false;
        if (classFile != null && classFile.exists()) {
            isDel = classFile.delete();
        }
        return isDel;
    }

    public File getClassFile() {
        return classFile;
    }

    public void setClassFile(File classFile) {
        this.classFile = classFile;
    }

    /**
     * 取得jar内容数据
     *
     * @return
     * @throws IOException
     */
    private byte[] getEntryBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());
        int c = 0;
        byte[] buff = new byte[10240];
        while ((c = is.read(buff)) > 0) {
            bos.write(buff, 0, c);
        }
        is.close();
        return bos.toByteArray();
    }


}
