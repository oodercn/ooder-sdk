package net.ooder.sdk.cli.core.registry;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.ExtensionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 扩展注册表实现
 *
 * <p>复用 SkillRegistryImpl 的设计模式</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class ExtensionRegistryImpl implements ExtensionRegistry {

    private static final Logger log = LoggerFactory.getLogger(ExtensionRegistryImpl.class);

    private final Map<String, CliExtension> extensions = new ConcurrentHashMap<>();
    private final Set<String> enabledExtensions = ConcurrentHashMap.newKeySet();

    private static final String EXTENSION_CONFIG_FILE = "extension.properties";
    private static final String EXTENSION_CLASS_PROPERTY = "extension.class";

    @Override
    public void register(CliExtension extension) {
        if (extension == null) {
            throw new IllegalArgumentException("Extension cannot be null");
        }

        String id = extension.getId();
        extensions.put(id, extension);

        if (extension.isEnabled()) {
            enabledExtensions.add(id);
        }

        try {
            extension.initialize();
            log.info("Registered CLI extension: {} v{}", id, extension.getVersion());
        } catch (Exception e) {
            log.error("Failed to initialize extension: {}", id, e);
            extensions.remove(id);
            throw new RuntimeException("Failed to initialize extension: " + id, e);
        }
    }

    @Override
    public void unregister(String extensionId) {
        CliExtension extension = extensions.remove(extensionId);
        if (extension != null) {
            enabledExtensions.remove(extensionId);
            try {
                extension.destroy();
                log.info("Unregistered CLI extension: {}", extensionId);
            } catch (Exception e) {
                log.error("Error destroying extension: {}", extensionId, e);
            }
        }
    }

    @Override
    public Optional<CliExtension> getExtension(String extensionId) {
        return Optional.ofNullable(extensions.get(extensionId));
    }

    @Override
    public List<CliExtension> getAllExtensions() {
        return new ArrayList<>(extensions.values());
    }

    @Override
    public List<CliExtension> getEnabledExtensions() {
        return extensions.values().stream()
                .filter(CliExtension::isEnabled)
                .collect(Collectors.toList());
    }

    @Override
    public void enableExtension(String extensionId) {
        CliExtension extension = extensions.get(extensionId);
        if (extension != null) {
            enabledExtensions.add(extensionId);
            log.debug("Enabled extension: {}", extensionId);
        }
    }

    @Override
    public void disableExtension(String extensionId) {
        enabledExtensions.remove(extensionId);
        log.debug("Disabled extension: {}", extensionId);
    }

    @Override
    public void loadExtension(String path) {
        log.info("Loading extension from: {}", path);

        File file = new File(path);
        if (!file.exists()) {
            log.error("Extension file not found: {}", path);
            throw new RuntimeException("Extension file not found: " + path);
        }

        if (file.isDirectory()) {
            loadExtensionFromDirectory(file);
        } else if (file.getName().endsWith(".jar")) {
            loadExtensionFromJar(file);
        } else {
            log.error("Unsupported extension format: {}", path);
            throw new RuntimeException("Unsupported extension format: " + path);
        }
    }

    @Override
    public void scanExtensions(String directory) {
        log.info("Scanning extensions in directory: {}", directory);

        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Extension directory not found or not a directory: {}", directory);
            return;
        }

        File[] files = dir.listFiles((d, name) -> 
                name.endsWith(".jar") || new File(d, name).isDirectory());

        if (files == null || files.length == 0) {
            log.info("No extension files found in directory: {}", directory);
            return;
        }

        for (File file : files) {
            try {
                loadExtension(file.getAbsolutePath());
            } catch (Exception e) {
                log.error("Failed to load extension from: {}", file.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 从JAR文件加载扩展
     */
    private void loadExtensionFromJar(File jarFile) {
        log.debug("Loading extension from JAR: {}", jarFile.getAbsolutePath());

        try (JarFile jar = new JarFile(jarFile)) {
            // 查找扩展配置文件
            JarEntry configEntry = jar.getJarEntry(EXTENSION_CONFIG_FILE);
            if (configEntry == null) {
                log.warn("Extension config file not found in JAR: {}", jarFile.getName());
                return;
            }

            // 读取配置
            Properties props = new Properties();
            props.load(jar.getInputStream(configEntry));

            String extensionClass = props.getProperty(EXTENSION_CLASS_PROPERTY);
            if (extensionClass == null || extensionClass.isEmpty()) {
                log.warn("Extension class not specified in config: {}", jarFile.getName());
                return;
            }

            // 加载扩展类
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jarUrl},
                    getClass().getClassLoader()
            );

            Class<?> clazz = classLoader.loadClass(extensionClass);
            if (CliExtension.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                CliExtension extension = (CliExtension) clazz.getDeclaredConstructor().newInstance();
                register(extension);
            } else {
                log.error("Class {} does not implement CliExtension", extensionClass);
            }

        } catch (IOException | ReflectiveOperationException e) {
            log.error("Failed to load extension from JAR: {}", jarFile.getAbsolutePath(), e);
            throw new RuntimeException("Failed to load extension: " + jarFile.getName(), e);
        }
    }

    /**
     * 从目录加载扩展
     */
    private void loadExtensionFromDirectory(File directory) {
        log.debug("Loading extension from directory: {}", directory.getAbsolutePath());

        File configFile = new File(directory, EXTENSION_CONFIG_FILE);
        if (!configFile.exists()) {
            log.warn("Extension config file not found in directory: {}", directory.getName());
            return;
        }

        try {
            Properties props = new Properties();
            props.load(configFile.toURI().toURL().openStream());

            String extensionClass = props.getProperty(EXTENSION_CLASS_PROPERTY);
            if (extensionClass == null || extensionClass.isEmpty()) {
                log.warn("Extension class not specified in config: {}", directory.getName());
                return;
            }

            // 创建类加载器
            URL classesUrl = new File(directory, "classes").toURI().toURL();
            URL libUrl = new File(directory, "lib").toURI().toURL();

            List<URL> urls = new ArrayList<>();
            urls.add(classesUrl);

            File libDir = new File(directory, "lib");
            if (libDir.exists() && libDir.isDirectory()) {
                File[] jars = libDir.listFiles((d, name) -> name.endsWith(".jar"));
                if (jars != null) {
                    for (File jar : jars) {
                        urls.add(jar.toURI().toURL());
                    }
                }
            }

            URLClassLoader classLoader = new URLClassLoader(
                    urls.toArray(new URL[0]),
                    getClass().getClassLoader()
            );

            Class<?> clazz = classLoader.loadClass(extensionClass);
            if (CliExtension.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                CliExtension extension = (CliExtension) clazz.getDeclaredConstructor().newInstance();
                register(extension);
            } else {
                log.error("Class {} does not implement CliExtension", extensionClass);
            }

        } catch (IOException | ReflectiveOperationException e) {
            log.error("Failed to load extension from directory: {}", directory.getAbsolutePath(), e);
            throw new RuntimeException("Failed to load extension: " + directory.getName(), e);
        }
    }

    /**
     * 获取所有扩展提供的命令
     */
    public List<CliCommand> getAllExtensionCommands() {
        List<CliCommand> commands = new ArrayList<>();
        for (CliExtension extension : getEnabledExtensions()) {
            List<CliCommand> extensionCommands = extension.getCommands();
            if (extensionCommands != null) {
                commands.addAll(extensionCommands);
            }
        }
        return commands;
    }
}
