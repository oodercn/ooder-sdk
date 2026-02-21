/**
 * $RCSfile: JarCompile.java,v $
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

import net.ooder.common.logging.ChromeProxy;
import net.ooder.common.logging.LogSetpLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


public class JarCompile {

    /**
     * @param src
     * @param des
     * @param name
     * @param cfg            配置文件
     * @param jarContentPath
     * @throws IOException
     */
    public static void compileAndZip(String src, String des, String name, File cfg, List<String> jarContentPath, ChromeProxy log) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(cfg));
        String reg = prop.getProperty("reg");
        compileAndZip(src, des, name, reg, jarContentPath, log);
    }


    /**
     * 把指定目录src下的所有文件压缩生成名字为name的jar文件,放到des目录。如果src中有.java文件,则编译后再压缩
     *
     * @param src            要压缩的文件夹
     * @param des            目标文件夹
     * @param name           生成的jar文件名字
     * @param regex          匹配正则式
     * @param jarContentPath 要提取的jar中的文件路径列表,路径的格式为 "E:\\mywork\\tcyx\\javaPro\\test\\src.jar!net\\itjds\\jartest\\Jartest1.java"
     * @throws IOException
     */
    public static void compileAndZip(String src, String des, String name, String regex, List<String> jarContentPath, ChromeProxy log) throws IOException {


        if (log == null) {
            log = new LogSetpLog();
        }

        File srcDir = new File(src);
        File desDir = new File(des);
        if (!srcDir.isDirectory() || !desDir.isDirectory()) {
            log.printError("源或目标位置必须是目录");
            throw new IllegalArgumentException("源或目标位置必须是目录");
        }
        Set<File> notJava = new LinkedHashSet<File>();
        Set<File> javas = new LinkedHashSet<>();

        log.printLog("读取JAR文件", true);
        //取jar中的内容,覆盖src中的内容
        String tmpSrc = src;
        if (!tmpSrc.endsWith(File.separator)) {
            tmpSrc += File.separator;
        }
        if (jarContentPath != null) {
            for (int i = 0, c = jarContentPath.size(); i < c; i++) {
                try {
                    String p = jarContentPath.get(i);
                    String[] ss = p.split("!");
                    File newFile = new File(tmpSrc + ss[1]);
                    ZipUtil.getJarContentToFile(ss[0], ss[1], newFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        getFiles(srcDir, notJava, javas, regex);
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        tmpDir.mkdirs();

        log.printLog("开始编译..", true);
        Set<String> pathSet = new HashSet<>();
        pathSet.add(tmpDir.getCanonicalPath());
        CompileJava.compile(javas, pathSet, pathSet, log);
        copy(notJava, tmpDir, srcDir.getCanonicalPath());

        ZipUtil.jar(tmpDir.getCanonicalPath(), des + "/" + name);
        log.printLog("压缩完成..." + des + "/" + name, true);
        tmpDir.delete();
    }

    private static void copy(Set<File> list, File dir, String srcPre) throws IOException {
        String dirPath = dir.getCanonicalPath();
        for (File f :list) {
            String name = f.getCanonicalPath();
            name = name.replace(srcPre, "");
            File newFile = new File(dirPath + name);
            IOUtility.xcopy(f, newFile);
        }
    }

    private static void getFiles(File file, Set<File> notJava, Set<File> javas, String regex) throws IOException {
        String name = file.getCanonicalPath();
        if (regex == null || "".equals(regex) || name.matches(regex)) {
            if (file.isDirectory()) {
                File[] fs = file.listFiles();
                for (int i = 0; i < fs.length; i++) {
                    getFiles(fs[i], notJava, javas, regex);
                }
            } else {
                if (file.getName().endsWith(".java")) {
                    javas.add(file);
                } else {
                    notJava.add(file);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String src = "E:\\mywork\\tcyx\\javaPro\\test\\src";
        String des = "E:\\mywork\\tcyx\\javaPro\\test\\out";
        File f = new File(src, "abc.properties");
        compileAndZip(src, des, "out.jar", f, null, null);

    }
}
