/**
 * $RCSfile: CompileJava.java,v $
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

import com.alibaba.fastjson.util.TypeUtils;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import net.ooder.common.logging.ChromeProxy;
import net.ooder.common.util.java.DynamicClassLoader;
import net.ooder.common.util.java.TmpJavaFileManager;
import net.ooder.common.util.java.TmpJavaFileObject;
import net.ooder.config.JDSConfig;
import net.ooder.config.JDSUtil;

import javax.tools.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class CompileJava {


    public static boolean isDebug() {
        boolean debug = false;
        try {
            String path = JDSUtil.getJdsRealPath();
            if (path.indexOf("target") > -1) {
                debug = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return debug;

    }

    public static String getDebugLibPath() throws IOException {
        String libPath = JDSUtil.getJdsRealPath() + "lib";
        return libPath;
    }

    public static String getDebugClassPath() throws IOException {
        String classPath = JDSUtil.getJdsRealPath() + "classes";
        return classPath;
    }

    public static boolean compile(Set<File> list, Set<String> libPaths, Set<String> classPaths, ChromeProxy log) throws IOException {
        boolean compile = true;
        Set<File> compList = new HashSet<>();
        String libPath = JDSUtil.getJdsRealPath() + "lib";
        String dest = JDSUtil.getJdsRealPath() + "classes";
        if (libPaths == null) {
            libPaths = new HashSet<>();
        }
        for (File javaFile : list) {
            if (javaFile.exists()) {
                String javaFilePath = javaFile.getAbsolutePath();
                javaFilePath = javaFilePath.substring(0, javaFilePath.length() - ".java".length());
                File classFile = new File(javaFilePath + ".class");
                if (!classFile.exists()) {
                    compList.add(javaFile);
                } else if (javaFile.exists() && (javaFile.lastModified() - classFile.lastModified() > 1000)) {
                    compList.add(javaFile);
                    classFile.delete();
                }
            }
        }
        if (compList.size() > 0) {
            libPaths.add(new File(libPath).getAbsolutePath());
            libPaths.add(new File(dest).getAbsolutePath());
            compile = compile(compList, dest, libPaths, classPaths, log);
        }
        return compile;
    }


    public static boolean compile(File file, Set<String> classPaths, ChromeProxy log) throws IOException {
        Set<File> list = new LinkedHashSet<>();
        list.add(file);
        return compile(list, null, classPaths, log);
    }


    public static boolean compile(File file, Set<String> libPaths, Set<String> classPaths, ChromeProxy log) throws IOException {
        Set<File> list = new LinkedHashSet<>();
        list.add(file);
        return compile(list, libPaths, classPaths, log);
    }

    private static String getSeparator() {
        String osName = System.getProperty("os.name").toLowerCase();
        String separator;
        if ("linux".equals(osName)) {
            separator = ":";
        } else {
            separator = ";";
        }
        return separator;
    }


    private static boolean compile(Set<File> javaFiles, String des, Set<String> libPaths, Set<String> classSrcPaths, ChromeProxy log) throws IOException {
        String classpath = getLibPathFromDirs(libPaths);
        Long start = System.currentTimeMillis();
        log.printLog("start JavaCompiler " + classSrcPaths.toString(), false);
        log.printLog("开始编译，共计" + javaFiles.size() + "个", true);
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            log.printError("没有编译器不能编译");
            throw new RuntimeException("没有编译器不能编译");
        }
        File dir = new File(des);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        List<String> optionList = new ArrayList();
        optionList.add("-d");
        optionList.add(dir.getCanonicalPath());
        optionList.add("-parameters");
        // optionList.add("-verbose");


        if (classpath != null) {
            optionList.add("-classpath");
            classpath = ".;" + JDSConfig.getAbsolutePath("") + getSeparator() + classpath + dir.getCanonicalPath() + File.separator;
            for (String classSrcPath : classSrcPaths) {
                File file = new File(classSrcPath);
                if (file.exists()) {
                    classpath = classpath + getSeparator() + file.getCanonicalPath() + File.separator;
                }
            }
            optionList.add(classpath);
        }
        List<TmpJavaFileObject> files = new ArrayList<>();
        for (File javaFile : javaFiles) {
            if (javaFile.exists()) {
                String fileName = javaFile.getName();
                fileName = fileName.substring(0, fileName.length() - ".java".length());
                FileInputStream inputStream = new FileInputStream(javaFile);
                String javaCode = IOUtility.toString(inputStream);
                IOUtility.shutdownStream(inputStream);
                TmpJavaFileObject fileObject = new TmpJavaFileObject(fileName, javaCode);
                files.add(fileObject);
            }

        }
        // log.printLog("start JavaCompiler optionList=" + optionList, false);
        log.printLog("start JavaCompiler " + classSrcPaths.toString(), false);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        TmpJavaFileManager javaFileManager =
                new TmpJavaFileManager(javaCompiler.getStandardFileManager(diagnostics, null, null));

        if (!javaCompiler.getTask(null, javaFileManager, diagnostics, optionList, null, files).call()) {
            StringBuilder errorMsg = new StringBuilder();
            for (Diagnostic d : diagnostics.getDiagnostics()) {
                JavaFileObject fileObject = (JavaFileObject) d.getSource();
                String err = String.format("Compilation error: Line %d - %s%n", d.getLineNumber(),
                        d.getMessage(null));
                if (fileObject != null) {
                    String fileName = fileObject.getName();
                    String fileNameBuff = "编译 出错" + fileName;
                    err = fileNameBuff + System.lineSeparator() + err;
                }
                errorMsg.append(err);
                System.err.print(err);
            }
            javaFileManager.close();
            javaFileManager.flush();
            throw new IOException(errorMsg.toString());
        }
        List<Class> classes = new ArrayList<>();
        Map<String, JavaFileObject> fileObjectMap = javaFileManager.getFileObjectMap();
        ClassUtility.getFileObjectMap().putAll(fileObjectMap);
        for (Map.Entry<String, JavaFileObject> entry : fileObjectMap.entrySet()) {
            String className = entry.getKey();
            TmpJavaFileObject bytesJavaFileObject = (TmpJavaFileObject) entry.getValue();
            DynamicClassLoader loader = ClassUtility.getDynamicClassLoader(className, bytesJavaFileObject, false);
            Class clazz = null;
            try {
                clazz = loader.findClass(className);
            } catch (ClassNotFoundException e) {
                loader = ClassUtility.getDynamicClassLoader(className, bytesJavaFileObject, true);
                try {
                    clazz = loader.findClass(entry.getKey());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            if (clazz != null) {
                classes.add(clazz);
                ClassUtility.dynClassMap.put(className, clazz);
                TypeUtils.addMapping(className, clazz);
                ClassPool pool = ClassPool.getDefault();
                ByteArrayClassPath path = new ByteArrayClassPath(className, bytesJavaFileObject.getCompiledBytes());
                pool.appendClassPath(path);
            }
        }
        log.printLog("end JavaCompiler " + classSrcPaths.toString(), false);
        javaFileManager.close();
        javaFileManager.flush();

        return true;
    }


    public static boolean compile(String src, Set<String> paths, ChromeProxy chrome) throws IOException {
        File srcFile = new File(src);
        Set<File> list = new LinkedHashSet<File>();
        getAllJavaFile(srcFile, list);
        return compile(list, null, paths, chrome);
    }


    public static synchronized Class dynCompile(String className, String classCode, String classPath) throws IOException, ClassNotFoundException {
        Class clazz = null;

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> fileObjects = new ArrayList<JavaFileObject>();
        String simpleName = className;
        if (simpleName.indexOf(".") > -1) {
            simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
        }
        TmpJavaFileObject sourceJavaFileObject = new TmpJavaFileObject(simpleName, classCode);
        fileObjects.add(sourceJavaFileObject);
        String libPath = JDSUtil.getJdsRealPath() + "lib";
        String dest = JDSUtil.getJdsRealPath() + "classes";

        Set<String> paths = new HashSet<>();
        paths.add(libPath);
        paths.add(dest);
        String classpath = getLibPathFromDirs(paths);
        List<String> optionList = new ArrayList();
        File dir = new File(classPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        optionList.add("-d");
        optionList.add(dir.getCanonicalPath());
        optionList.add("-parameters");
        //  optionList.add("-verbose");
        optionList.add("-classpath");
        classpath = "." + getSeparator() + JDSConfig.getAbsolutePath("") + getSeparator() + classpath + dir.getCanonicalPath() + "/" + getSeparator() + classPath;
        optionList.add(classpath);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        TmpJavaFileManager javaFileManager =
                new TmpJavaFileManager(javaCompiler.getStandardFileManager(collector, null, null));

        if (!javaCompiler.getTask(null, javaFileManager, collector, optionList, null, fileObjects).call()) {
            StringBuilder errorMsg = new StringBuilder();
            for (Diagnostic d : collector.getDiagnostics()) {
                String err = String.format("Compilation error: Line %d - %s%n", d.getLineNumber(),
                        d.getMessage(null));
                errorMsg.append(err);
                System.err.print(err);
            }
            javaFileManager.close();
            javaFileManager.flush();
            throw new IOException(errorMsg.toString());
        }

        Map<String, JavaFileObject> fileObjectMap = javaFileManager.getFileObjectMap();
        TmpJavaFileObject bytesJavaFileObject = (TmpJavaFileObject) fileObjectMap.get(className);
        if (bytesJavaFileObject != null) {
            String fileName = className.replace(".", "/") + ".class";
            File classFile = new File(classPath, fileName);
            sourceJavaFileObject = bytesJavaFileObject;
            ClassUtility.getFileObjectMap().put(className, sourceJavaFileObject);
            DynamicClassLoader loader = new DynamicClassLoader(bytesJavaFileObject, className);//ClassUtility.getDynamicClassLoader(className, sourceJavaFileObject, false);
            clazz = loader.findClass(className);
            if (clazz != null) {
                ClassUtility.getDynClassMap().put(className, clazz);
                TypeUtils.addMapping(className, clazz);
                loader.dumpFile(classFile);
                ClassPool pool = ClassPool.getDefault();
                ByteArrayClassPath path = new ByteArrayClassPath(className, bytesJavaFileObject.getCompiledBytes());
                pool.appendClassPath(path);

            }
        }

        return clazz;

    }


    public static boolean compile(String src, Set<String> paths, Set<String> classPaths, ChromeProxy chrome) throws IOException {
        File srcFile = new File(src);
        Set<File> list = new LinkedHashSet<>();
        getAllJavaFile(srcFile, list);
        return compile(list, paths, classPaths, chrome);
    }

    public static Set<File> getAllJavaFile(File srcFile) {
        return getAllJavaFile(srcFile, null);
    }

    private static Set<File> getAllJavaFile(File srcFile, Set<File> list) {
        if (list == null) {
            list = new LinkedHashSet<>();
        }
        if (srcFile.isDirectory()) {
            File[] fs = srcFile.listFiles();
            for (int i = 0; i < fs.length; i++) {
                getAllJavaFile(fs[i], list);
            }
        } else {
            if (srcFile.getName().endsWith(".java")) {
                list.add(srcFile);
            }

        }
        return list;
    }

    private static void getLibPathFromDir(File dir, StringBuilder sb) throws IOException {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            for (int i = 0; i < fs.length; i++) {
                getLibPathFromDir(fs[i], sb);
            }
        } else {
            if (dir.getName().endsWith(".jar")) {
                sb.append(dir.getCanonicalPath());
                sb.append(getSeparator());
            }
        }
    }


    private static String getLibPathFromDirs(Set<String> dir) throws IOException {
        if (dir == null || dir.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String path : dir) {
            if (path != null) {
                File d = new File(path);
                if (d.isDirectory()) {
                    getLibPathFromDir(d, sb);
                }
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {


    }

}
