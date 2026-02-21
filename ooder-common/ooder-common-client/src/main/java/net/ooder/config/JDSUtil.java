/**
 * $RCSfile: JDSUtil.java,v $
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
package net.ooder.config;

import net.ooder.common.util.StringUtility;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class JDSUtil {

    static final String[] tpath = new String[]{"bin", "lib", "classes"};

    public static String getJdsPath() throws MalformedURLException, IOException {
        return getJdsPath(null);
    }


    public static String getJdsPath(Class clazz) throws IOException {
        if (clazz == null) {
            clazz = getDefaultClazz();
        }
        String runningURL = (new URL(clazz.getProtectionDomain()
                .getCodeSource().getLocation(), ".")).openConnection()
                .getPermission().getName();
        return runningURL;
    }

    private static Class getDefaultClazz() {
        Class clazz = null;
        try {
            clazz = Class.forName("net.ooder.JDSInit");
        } catch (ClassNotFoundException e) {
            clazz = JDSUtil.class;
        }
        return clazz;
    }

    public static String getJdsRealPath() throws MalformedURLException, IOException {
        return getJdsRealPath(null);
    }

    public static String getRootPath(String realPath) throws MalformedURLException, IOException {
        for (String path : tpath) {
            realPath = StringUtility.replace(realPath, "/" + path + "/", "/");
            realPath = StringUtility.replace(realPath, "\\" + path + "\\", "\\");
        }
        return realPath;
    }

    public static String getJdsRealPath(Class clazz) throws MalformedURLException, IOException {
        if (clazz == null) {
            clazz = getDefaultClazz();
        }
        String realPath = JDSUtil.getJdsPath(clazz);
        realPath = getRootPath(realPath);
        return realPath;
    }


    public static Color String2Color(String str) {
        if (str == null || "".equals(str))
            return Color.BLACK;
        int i = Integer.parseInt(str, 16);
        return new Color(i);
    }

    public static ImageIcon zoomIcon(ImageIcon icon, Dimension size) {
        if (icon != null) {
            try {
                Image menuImage = icon.getImage();
                menuImage = menuImage.getScaledInstance(size.width, size.height, 1);
                icon.setImage(menuImage);
            } catch (Exception e) {
            }
        }
        return icon;
    }

    public static ImageIcon getIconImag(String icon) {
        URL imgUrl = null;
        try {
            imgUrl = new URL(icon);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ImageIcon imageIcon = new ImageIcon(imgUrl);
        return imageIcon;
    }

    ;

    public static Image getThumb(String thumb) {
        URL imgUrl = null;
        try {
            imgUrl = new URL(thumb);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ImageIcon imageIcon = new ImageIcon(imgUrl);
        return imageIcon.getImage();
    }


}
