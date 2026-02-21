/**
 * $RCSfile: ZipUtil.java,v $
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: zh
 * Date: 2009-8-7
 * Time: 10:34:41
 * To change this template use File | Settings | File Templates.
 */
public class ZipUtil {

  /**
   * 压缩指定文件或文件夹的内容生成指定.zip文件或生成到指定文件夹
   *
   * @param src 要压缩的文件或文件夹路径,可以是文件名或文件夹名
   * @param des 目标路径,可以是文件名或文件夹名
   * @throws IOException
   */
  public static void zip(String src, String des) throws IOException {
    zip(src, des, false);
  }



  public static void jar(String src, String des) throws IOException {
    zip(src, des, true);
  }

  private static void zip(String src, String des, boolean flag) throws IOException {
    File srcFile = new File(src);
    File desFile = new File(des);
    if (desFile.isDirectory()) {
      desFile = new File(desFile, srcFile.getName());
    }
    String name = desFile.getName();
    File parent = desFile.getParentFile();
    String extName = flag ? ".jar" : ".zip";
    if (!name.endsWith(extName)) {
      desFile = new File(parent, name + extName);
    }
    if (!parent.exists()) {
      parent.mkdirs();
    }
    if (desFile.exists()) {
      desFile.delete();
    }
    desFile.createNewFile();


    ZipOutputStream zos = flag ? new JarOutputStream(new FileOutputStream(desFile)) : new ZipOutputStream(new FileOutputStream(desFile));
    String absPath = null;
    if (srcFile.isDirectory()) {
      absPath = srcFile.getCanonicalPath();
      if (!absPath.endsWith(File.separator)) {
        absPath += File.separator;
      }
    }
    zipFile(srcFile, zos, absPath);
    zos.close();
  }

  private static void zipFile(File file, ZipOutputStream zos, String absPath) throws IOException {
    if (file.isDirectory()) {
//      zos.putNextEntry(new ZipEntry(file.getName()));
      File[] fs = file.listFiles();
      for (int i = 0; i < fs.length; i++) {
        File f = fs[i];
        zipFile(f, zos, absPath);
      }
    } else {
      InputStream is = new BufferedInputStream(new FileInputStream(file));
      String name = null;
      if (absPath == null) {
        name = file.getName();
      } else {
        name = file.getCanonicalPath().replace(absPath, "");
      }
//      name=new String(name.getBytes(),"gbk");
//      name=new String(name.getBytes(),"gbk");
      name=StringUtility.replace(name, File.separator,"/");
      zos.putNextEntry(new ZipEntry(name));
      IOUtility.write(is, zos);
      is.close();
      zos.closeEntry();
    }
  }

  /**
   * 解压.zip文件到指定文件夹
   *
   * @param src 要解压的文件
   * @param des 目标文件夹
   * @throws IOException
   */
  public static void unzip(String src, String des) throws IOException {
    unzip(src, des, false);
  }

  public static void unjar(String src, String des) throws IOException {
    unzip(src, des, true);
  }

  private static void unzip(String src, String des, boolean flag) throws IOException {
    File srcFile = new File(src);
    File desDir = new File(des);

    String extName = flag ? ".jar" : ".zip";
    if (!srcFile.getName().endsWith(extName)) {
      throw new IllegalArgumentException("源文件不正确");
    }
    if (!desDir.exists()) {
      desDir.mkdirs();
    }

    ZipInputStream zis = flag ? new JarInputStream(new FileInputStream(srcFile)) : new ZipInputStream(new FileInputStream(srcFile));
    ZipEntry entry = null;
    while ((entry = zis.getNextEntry()) != null) {
      if (!entry.isDirectory()) {
        File file = new File(desDir, entry.getName());
        File parent = file.getParentFile();
        if (!parent.exists()) {
          parent.mkdirs();
        }
        if (file.exists()) {
          file.delete();
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        IOUtility.write(zis, fos);
      }
    }
  }

  public static byte[] getBytesFromJar(String jarPath, String contentPath) throws IOException {
    JarFile jarFile = new JarFile(jarPath);
//    Enumeration en= jarFile.entries();
//    while(en.hasMoreElements()){
//      JarEntry e= (JarEntry) en.nextElement();
//      System.out.println(e.getName());
//    }
    JarEntry entry = (JarEntry) jarFile.getEntry(contentPath);
    InputStream is = jarFile.getInputStream(entry);
    byte[] bs = IOUtility.getBytesFromInputStream(is);
    is.close();
    return bs;
  }
  

  /**
   * 取jar中的指定内容,生成指定的文件
   * @param jarPath
   * @param contentPath
   * @param desFile
   * @throws IOException
   */
  public static void getJarContentToFile(String jarPath, String contentPath, File desFile) throws IOException {
      IOUtility.writeBytesToNewFile(getBytesFromJar(jarPath, contentPath), desFile);
  }


  public static void main(String[] args) throws IOException {
    ziptest();
//    unziptest();
  }

  private static void unziptest() throws IOException {

    String src = "E:\\mywork\\tcyx\\javaPro\\test\\src.zip";
    String des = "E:\\mywork\\tcyx\\javaPro\\test\\out";
    unzip(src, des);
  }

  private static void ziptest() throws IOException {

    String src = "E:\\mywork\\tcyx\\javaPro\\test\\src";
    String des = "E:\\mywork\\tcyx\\javaPro\\test";
    jar(src, des);
  }
}
