/**
 * $RCSfile: ImgUtil.java,v $
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ImgUtil {
   
    /**
     * @param srcImageFile    源图像文件图像地址
     * @param resultImageFile 缩放后的图像地址
     */
    public static void scaleByWidth(String srcImageFile, String resultImageFile, int cWidth) {
        try {
            // 读去图片
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            int width = src.getWidth();
            int scale = width / cWidth;
            scale(srcImageFile, resultImageFile, scale, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param srcImageFile    源图像文件图像地址
     * @param resultImageFile 缩放后的图像地址
     */
    public static void scaleByHeight(String srcImageFile, String resultImageFile, int cHeight) {
        try {
            // 读去图片
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            int height = src.getHeight();
            int scale = height / cHeight;
            scale(srcImageFile, resultImageFile, scale, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scale(String srcImageFile, String resultImageFile,
                             int scale, boolean flag) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            scale(src, resultImageFile, scale, flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param src             源图像文件图像地址
     * @param resultImageFile 缩放后的图像地址
     * @param scale           缩放比例
     * @param flag            缩放选择:true 放大; false 缩小;
     */
    static void scale(BufferedImage src, String resultImageFile,
                      int scale, boolean flag) throws IOException {


        // 图片宽度
        int width = src.getWidth();
        // 图片高度
        int height = src.getHeight();
        if (flag) {
            width = width * scale;
            height = height * scale;
        } else {
            // 缩小
            width = width / scale;
            height = height / scale;
        }
        Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        // 绘制处理后的图片
        g.drawImage(image, 0, 0, null);
        g.dispose();
        // 输出到文件
        ImageIO.write(tag, "png", new File(resultImageFile));

    }
//
//    /**
//     * 把指定目录src下的所有文件压缩生成名字为name的jar文件,放到des目录。如果src中有.java文件,则编译后再压缩
//     *
//     * @param src            要压缩的文件夹
//     * @param des            目标文件夹
//     * @param name           生成的jar文件名字
//     * @param regex          匹配正则式
//     * @param reload         是否重加载
//     */
//    public static void zip(String src, String des, String name, String regex, boolean reload) {
//        if (!new File(src).isDirectory() || !new File(des).isDirectory()) {
//            log.printError("源或目标位置必须是目录");
//            throw new IllegalArgumentException("源或目标位置必须是目录");
//        }
//        log.printLog("开始编译..", true);
//        if (reload) {
//            FileUtil.delete(new File(des + "/" + name));
//        }
//        FileUtil.zip(src, des, name, regex);
//    }
}