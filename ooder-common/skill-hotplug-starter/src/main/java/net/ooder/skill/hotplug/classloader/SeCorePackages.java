package net.ooder.skill.hotplug.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * SE 核心包配置工具类
 * 
 * <p>用于判断类是否属于 SE 核心包，需要从父类加载器加载</p>
 * 
 * <p>支持两种配置方式：</p>
 * <ul>
 *   <li>静态配置：通过 {@link #PACKAGES} 硬编码</li>
 *   <li>动态配置：从 META-INF/se-core-packages 文件加载</li>
 * </ul>
 * 
 * @author ooder Team
 * @since 2.3.1
 */
public final class SeCorePackages {

    private static final Logger logger = LoggerFactory.getLogger(SeCorePackages.class);

    private SeCorePackages() {
    }

    /**
     * SE 核心包列表 - 静态配置（框架级别，极少变化）
     */
    public static final List<String> PACKAGES = Arrays.asList(
            "net.ooder.scene.spi."
    );

    /**
     * 动态加载的核心包集合（从 META-INF/se-core-packages 加载）
     */
    private static volatile Set<String> dynamicCorePackages = new HashSet<>();

    static {
        loadDynamicCorePackages();
    }

    /**
     * 从 classpath 中所有 JAR 加载 META-INF/se-core-packages
     */
    private static void loadDynamicCorePackages() {
        Set<String> packages = new HashSet<>();
        
        try {
            Enumeration<URL> resources = SeCorePackages.class.getClassLoader()
                    .getResources("META-INF/se-core-packages");
            
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream is = url.openStream();
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            packages.add(line);
                        }
                    }
                } catch (IOException e) {
                    logger.warn("Failed to read se-core-packages from: {}", url, e);
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to load dynamic core packages", e);
        }
        
        dynamicCorePackages = Collections.unmodifiableSet(packages);
        
        if (!packages.isEmpty()) {
            logger.info("Loaded {} dynamic core packages from META-INF/se-core-packages: {}", 
                    packages.size(), packages);
        }
    }

    /**
     * 检查类是否属于 SE 核心包
     * 
     * <p>同时检查静态配置和动态配置</p>
     *
     * @param className 类名
     * @return 是否属于 SE 核心包
     */
    public static boolean isSeCoreClass(String className) {
        if (className == null) {
            return false;
        }
        
        for (String pkg : PACKAGES) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        
        for (String pkg : dynamicCorePackages) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 获取 SE 核心包列表（静态配置）
     *
     * @return SE 核心包列表
     */
    public static List<String> getPackages() {
        return PACKAGES;
    }

    /**
     * 获取动态加载的核心包集合
     *
     * @return 动态核心包集合
     */
    public static Set<String> getDynamicCorePackages() {
        return dynamicCorePackages;
    }

    /**
     * 重新加载动态核心包配置
     */
    public static void reloadDynamicCorePackages() {
        loadDynamicCorePackages();
    }
}
