package net.ooder.scene.core.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 插件类加载器
 * <p>解决 Skill 插件与主应用的 ClassLoader 隔离问题</p>
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>优先从主应用加载 SE 核心类，确保类型一致性</li>
 *   <li>支持插件加载自定义类</li>
 *   <li>线程安全的类加载</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <pre>
 * // 主应用创建插件加载器
 * PluginClassLoader pluginLoader = new PluginClassLoader(
 *     new URL[]{pluginJar.toURI().toURL()},
 *     mainAppClassLoader
 * );
 *
 * // 加载插件类
 * Class<?> pluginClass = pluginLoader.loadClass("com.skill.ChatController");
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class PluginClassLoader extends URLClassLoader {

    private static final Logger log = LoggerFactory.getLogger(PluginClassLoader.class);

    /**
     * SE 核心包 - 必须从主应用加载，确保类型一致性
     */
    private static final List<String> SE_CORE_PACKAGES = Arrays.asList(
            // SPI 接口
            "net.ooder.scene.spi.",
            // 对话服务
            "net.ooder.scene.skill.conversation.",
            // 存储服务
            "net.ooder.scene.skill.conversation.storage.",
            // 知识库服务
            "net.ooder.scene.skill.knowledge.",
            // 工具服务
            "net.ooder.scene.skill.tool.",
            // 上下文
            "net.ooder.scene.llm.context.",
            // 基础接口
            "net.ooder.scene.skill."
    );

    /**
     * 父类加载器（主应用）
     */
    private final ClassLoader parentClassLoader;

    /**
     * 已加载类的缓存（用于性能优化）
     */
    private final ConcurrentMap<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    /**
     * 创建插件类加载器
     *
     * @param urls 插件 JAR 的 URL
     * @param parentClassLoader 父类加载器（主应用）
     */
    public PluginClassLoader(URL[] urls, ClassLoader parentClassLoader) {
        super(urls, parentClassLoader);
        this.parentClassLoader = parentClassLoader;
        log.info("PluginClassLoader created with {} URLs", urls.length);
    }

    /**
     * 加载类
     * <p>优先从主应用加载 SE 核心类</p>
     *
     * @param name 类名
     * @param resolve 是否解析类
     * @return 加载的类
     * @throws ClassNotFoundException 如果类未找到
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 1. 检查缓存
        Class<?> cachedClass = loadedClasses.get(name);
        if (cachedClass != null) {
            if (resolve) {
                resolveClass(cachedClass);
            }
            return cachedClass;
        }

        // 2. 检查是否已加载
        Class<?> c = findLoadedClass(name);
        if (c != null) {
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }

        // 3. 如果是 SE 核心类，优先从主应用加载
        if (isSeCoreClass(name)) {
            try {
                c = parentClassLoader.loadClass(name);
                if (resolve) {
                    resolveClass(c);
                }
                // 缓存类
                loadedClasses.put(name, c);
                log.debug("Loaded SE core class from parent: {}", name);
                return c;
            } catch (ClassNotFoundException e) {
                log.warn("SE core class not found in parent ClassLoader: {}", name);
                // 继续尝试从插件加载
            }
        }

        // 4. 标准双亲委派
        try {
            c = super.loadClass(name, resolve);
            // 缓存类
            if (c != null) {
                loadedClasses.put(name, c);
            }
            return c;
        } catch (ClassNotFoundException e) {
            log.debug("Class not found: {}", name);
            throw e;
        }
    }

    /**
     * 检查是否为 SE 核心类
     *
     * @param className 类名
     * @return 是否为 SE 核心类
     */
    private boolean isSeCoreClass(String className) {
        for (String pkg : SE_CORE_PACKAGES) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找类（不加载）
     * <p>重写以支持从插件查找类</p>
     *
     * @param name 类名
     * @return 类对象
     * @throws ClassNotFoundException 如果类未找到
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 如果是 SE 核心类，不从插件查找
        if (isSeCoreClass(name)) {
            throw new ClassNotFoundException("SE core class should be loaded from parent: " + name);
        }
        return super.findClass(name);
    }

    /**
     * 获取资源
     * <p>优先从主应用获取 SE 相关资源</p>
     *
     * @param name 资源名
     * @return 资源的 URL
     */
    @Override
    public URL getResource(String name) {
        // 如果是 SE 相关资源，优先从主应用获取
        if (name.contains("net/ooder/scene/")) {
            URL url = parentClassLoader.getResource(name);
            if (url != null) {
                return url;
            }
        }
        return super.getResource(name);
    }

    /**
     * 获取父类加载器
     *
     * @return 父类加载器
     */
    public ClassLoader getParentClassLoader() {
        return parentClassLoader;
    }

    /**
     * 获取已加载类数量
     *
     * @return 已加载类数量
     */
    public int getLoadedClassCount() {
        return loadedClasses.size();
    }

    /**
     * 清空类缓存
     * <p>用于内存优化</p>
     */
    public void clearCache() {
        loadedClasses.clear();
        log.info("PluginClassLoader cache cleared");
    }

    @Override
    public void close() {
        clearCache();
        try {
            super.close();
            log.info("PluginClassLoader closed");
        } catch (Exception e) {
            log.error("Error closing PluginClassLoader", e);
        }
    }
}
