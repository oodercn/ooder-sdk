package net.ooder.skill.hotplug.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Skill专用类加载器
 * 
 * <p>实现类隔离和双亲委派机制的灵活控制</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>优先从主应用加载 SE 核心类，确保类型一致性</li>
 *   <li>支持插件加载自定义类</li>
 *   <li>线程安全的类加载</li>
 * </ul>
 * 
 * <p>SE 核心包配置由 {@link SeCorePackages} 提供</p>
 * 
 * @author ooder Team
 * @since 2.3.1
 */
public class PluginClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(PluginClassLoader.class);

    private final ClassLoader parent;

    private final String skillId;

    private final ConcurrentMap<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, URL> resourceCache = new ConcurrentHashMap<>();

    private static final String[] PARENT_FIRST_PACKAGES = {
            "java.",
            "javax.",
            "sun.",
            "com.sun.",
            "org.xml.",
            "org.w3c.",
            "org.ietf.jgss",
            "org.omg.",
            "com.oracle.",
            "jdk.",
            "net.ooder.core.",
            "net.ooder.agent.",
            "org.springframework.",
            "org.slf4j.",
            "org.apache.commons.logging.",
            "org.apache.logging.log4j.",
            "ch.qos.logback.",
            "com.fasterxml.jackson.",
            "io.micrometer.",
            "reactor.",
            "io.netty."
    };

    private static final String[] PARENT_FIRST_CLASSES = {
            "net.ooder.skill.hotplug.SkillLifecycle",
            "net.ooder.skill.hotplug.PluginContext",
            "net.ooder.skill.hotplug.model.SkillPackage",
            "net.ooder.skill.hotplug.config.SkillConfiguration"
    };

    public PluginClassLoader(String skillId, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.skillId = skillId;
        this.parent = parent;
        logger.debug("Created PluginClassLoader for skill: {}", skillId);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = loadedClasses.get(name);
        if (clazz != null) {
            return clazz;
        }

        synchronized (getClassLoadingLock(name)) {
            clazz = loadedClasses.get(name);
            if (clazz != null) {
                return clazz;
            }

            try {
                if (shouldLoadFromParent(name)) {
                    clazz = loadFromParent(name);
                    if (clazz != null) {
                        loadedClasses.put(name, clazz);
                        if (resolve) {
                            resolveClass(clazz);
                        }
                        return clazz;
                    }
                }

                clazz = findClass(name);
                if (clazz != null) {
                    loadedClasses.put(name, clazz);
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    logger.debug("Loaded class from plugin [{}]: {}", skillId, name);
                    return clazz;
                }

            } catch (ClassNotFoundException e) {
                clazz = loadFromParent(name);
                if (clazz != null) {
                    loadedClasses.put(name, clazz);
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                }
                throw e;
            }
        }

        throw new ClassNotFoundException(name);
    }

    private Class<?> loadFromParent(String name) {
        try {
            if (parent != null) {
                return parent.loadClass(name);
            }
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    /**
     * 判断是否应该从父类加载器加载
     * 
     * <p>包括：</p>
     * <ul>
     *   <li>Java 核心类</li>
     *   <li>Spring 等框架类</li>
     *   <li>SE 核心类（通过 {@link SeCorePackages} 判断）</li>
     *   <li>Skill 框架核心类</li>
     * </ul>
     */
    private boolean shouldLoadFromParent(String name) {
        if (SeCorePackages.isSeCoreClass(name)) {
            return true;
        }

        for (String prefix : PARENT_FIRST_PACKAGES) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }

        for (String className : PARENT_FIRST_CLASSES) {
            if (name.equals(className)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public URL findResource(String name) {
        URL resource = resourceCache.get(name);
        if (resource != null) {
            return resource;
        }

        resource = super.findResource(name);
        if (resource != null) {
            resourceCache.put(name, resource);
        }

        return resource;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        return super.findResources(name);
    }

    public int getLoadedClassCount() {
        return loadedClasses.size();
    }

    public String getSkillId() {
        return skillId;
    }

    public java.util.Set<String> getLoadedClassNames() {
        return new java.util.HashSet<>(loadedClasses.keySet());
    }

    public void cleanup() {
        logger.debug("Cleaning up PluginClassLoader for skill: {}", skillId);
        loadedClasses.clear();
        resourceCache.clear();
    }

    @Override
    public void close() throws IOException {
        cleanup();
        super.close();
    }

    @Override
    public String toString() {
        return "PluginClassLoader[" + skillId + "]";
    }
}
