package net.ooder.skill.hotplug.classloader;

import net.ooder.skill.hotplug.HotPlugProperties;
import net.ooder.skill.hotplug.model.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类加载器管理器
 * 负责创建、缓存和销毁Skill专用的类加载器
 */
@Component
public class ClassLoaderManager {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderManager.class);

    @Autowired
    private HotPlugProperties properties;

    // Skill ID -> PluginClassLoader 映射
    private final Map<String, PluginClassLoader> classLoaders = new ConcurrentHashMap<>();

    // 父类加载器
    private ClassLoader parentClassLoader;

    /**
     * 初始化
     */
    public void init() {
        this.parentClassLoader = Thread.currentThread().getContextClassLoader();
        logger.info("ClassLoaderManager initialized with parent: {}", parentClassLoader);
    }

    /**
     * 为Skill创建类加载器
     */
    public PluginClassLoader createClassLoader(SkillPackage skillPackage) {
        String skillId = skillPackage.getMetadata().getId();

        // 检查是否已存在
        PluginClassLoader existing = classLoaders.get(skillId);
        if (existing != null) {
            logger.warn("ClassLoader already exists for skill: {}, closing old one", skillId);
            destroyClassLoader(skillId);
        }

        try {
            // 构建URL列表
            URL[] urls = buildUrls(skillPackage);

            // 创建新的类加载器
            PluginClassLoader classLoader = new PluginClassLoader(skillId, urls, getParentClassLoader());

            // 缓存
            classLoaders.put(skillId, classLoader);

            logger.info("Created ClassLoader for skill: {} with {} URLs", skillId, urls.length);
            return classLoader;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create ClassLoader for skill: " + skillId, e);
        }
    }

    /**
     * 获取指定Skill的类加载器
     */
    public PluginClassLoader getClassLoader(String skillId) {
        return classLoaders.get(skillId);
    }

    /**
     * 销毁指定Skill的类加载器
     */
    public void destroyClassLoader(String skillId) {
        PluginClassLoader classLoader = classLoaders.remove(skillId);
        if (classLoader != null) {
            try {
                logger.info("Destroying ClassLoader for skill: {}", skillId);
                classLoader.close();
            } catch (IOException e) {
                logger.error("Error closing ClassLoader for skill: {}", skillId, e);
            }
        }
    }

    /**
     * 获取所有类加载器
     */
    public Map<String, PluginClassLoader> getAllClassLoaders() {
        return new ConcurrentHashMap<>(classLoaders);
    }

    /**
     * 获取已加载的类统计信息
     */
    public Map<String, ClassLoaderStats> getStats() {
        Map<String, ClassLoaderStats> stats = new ConcurrentHashMap<>();
        for (Map.Entry<String, PluginClassLoader> entry : classLoaders.entrySet()) {
            PluginClassLoader cl = entry.getValue();
            stats.put(entry.getKey(), new ClassLoaderStats(
                    entry.getKey(),
                    cl.getLoadedClassCount(),
                    cl.getLoadedClassNames()
            ));
        }
        return stats;
    }

    /**
     * 清理所有类加载器
     */
    public void cleanup() {
        logger.info("Cleaning up all ClassLoaders...");
        for (Map.Entry<String, PluginClassLoader> entry : classLoaders.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                logger.error("Error closing ClassLoader: {}", entry.getKey(), e);
            }
        }
        classLoaders.clear();
    }

    // ==================== 私有方法 ====================

    private ClassLoader getParentClassLoader() {
        if (parentClassLoader == null) {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
        }
        return parentClassLoader;
    }

    private URL[] buildUrls(SkillPackage skillPackage) throws MalformedURLException {
        java.util.List<URL> urls = new java.util.ArrayList<>();

        // 添加主JAR
        urls.add(skillPackage.getFile().toURI().toURL());

        // 添加依赖JAR
        if (skillPackage.getMetadata().getDependencies() != null) {
            Path libDir = Paths.get(properties.getPluginDirectory(), "lib");
            for (String dep : skillPackage.getMetadata().getDependencies()) {
                Path depPath = libDir.resolve(dep);
                if (Files.exists(depPath)) {
                    urls.add(depPath.toUri().toURL());
                } else {
                    logger.warn("Dependency not found: {}", depPath);
                }
            }
        }

        return urls.toArray(new URL[0]);
    }

    /**
     * 从JAR文件提取依赖信息
     */
    public java.util.List<String> extractDependencies(File jarFile) {
        java.util.List<String> dependencies = new java.util.ArrayList<>();

        try (JarFile jar = new JarFile(jarFile)) {
            // 读取MANIFEST.MF中的依赖信息
            java.util.jar.Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String deps = manifest.getMainAttributes().getValue("Dependencies");
                if (deps != null) {
                    for (String dep : deps.split(",")) {
                        dependencies.add(dep.trim());
                    }
                }
            }

            // 检查lib目录
            java.util.Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith("lib/") && entry.getName().endsWith(".jar")) {
                    dependencies.add(entry.getName().substring(4)); // 去掉 "lib/" 前缀
                }
            }

        } catch (IOException e) {
            logger.error("Error extracting dependencies from: {}", jarFile, e);
        }

        return dependencies;
    }

    /**
     * 类加载器统计信息
     */
    public static class ClassLoaderStats {
        private final String skillId;
        private final int loadedClassCount;
        private final java.util.Set<String> loadedClasses;

        public ClassLoaderStats(String skillId, int loadedClassCount, java.util.Set<String> loadedClasses) {
            this.skillId = skillId;
            this.loadedClassCount = loadedClassCount;
            this.loadedClasses = loadedClasses;
        }

        public String getSkillId() {
            return skillId;
        }

        public int getLoadedClassCount() {
            return loadedClassCount;
        }

        public java.util.Set<String> getLoadedClasses() {
            return loadedClasses;
        }
    }
}
