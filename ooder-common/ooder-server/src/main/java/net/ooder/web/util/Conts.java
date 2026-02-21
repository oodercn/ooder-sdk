package net.ooder.web.util;

import java.util.Map;

/**
 * 内容类型常量类
 * 定义了HTTP响应中常用的Content-Type映射关系
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
public class Conts {
    static java.util.Hashtable<String, String> map = new java.util.Hashtable<String, String>();

    static {
        fillMap();
    }


    static void setSuffix(String k, String v) {
        map.put(k, v);
    }

    public static Map<String, String> getSuffixMap() {
        return map;
    }

    static void fillMap() {
        setSuffix("", "content/unknown");
        setSuffix(".uu", "application/octet-stream");
        setSuffix(".exe", "application/octet-stream");
        setSuffix(".ps", "application/postscript");
        setSuffix(".zip", "application/zip");
        setSuffix(".xml", "  applcation/xml");
        setSuffix(".js", "application/javascript");
        setSuffix(".cls", "application/javascript");
        setSuffix(".sh", "application/x-shar");
        setSuffix(".tar", "application/x-tar");
        setSuffix(".snd", "audio/basic");
        setSuffix(".au", "audio/basic");
        setSuffix(".wav", "audio/x-wav");
        setSuffix(".gif", "image/gif");
        setSuffix(".png", "image/png");
        setSuffix(".ini", "配置信息");
        setSuffix(".jpg", "image/jpeg");
        setSuffix(".jpeg", "image/jpeg");
        setSuffix(".htm", "HTML Document");
        setSuffix(".vv", "HTML Document");
        setSuffix(".html", "HTML Document");
        setSuffix(".text", "text/plain");
        setSuffix(".c", "text/plain");
        setSuffix(".cc", "text/plain");
        setSuffix(".c++", "text/plain");
        setSuffix(".h", "text/plain");
        setSuffix(".pl", "text/plain");
        setSuffix(".css", "text/css");
        setSuffix(".txt", "text/plain");
        setSuffix(".java", "text/plain");
    }


}
