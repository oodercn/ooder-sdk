package net.ooder.sdk.driver.discovery.impl;

import net.ooder.sdk.driver.discovery.DriverDiscovery;
import net.ooder.sdk.driver.discovery.DriverDiscoveryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DriverDiscoveryImpl implements DriverDiscovery {
    
    private static final Logger log = LoggerFactory.getLogger(DriverDiscoveryImpl.class);
    
    private static final String DEFAULT_BASE_PACKAGE = "net.ooder.sdk.drivers";
    
    private final List<String> scanPackages = new CopyOnWriteArrayList<>();
    private final List<DriverDiscoveryListener> listeners = new CopyOnWriteArrayList<>();
    private final ClassLoader classLoader;
    
    public DriverDiscoveryImpl() {
        this(Thread.currentThread().getContextClassLoader());
    }
    
    public DriverDiscoveryImpl(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
        this.scanPackages.add(DEFAULT_BASE_PACKAGE);
    }
    
    @Override
    public List<DiscoveredDriver> discover() {
        List<DiscoveredDriver> allDrivers = new ArrayList<>();
        for (String basePackage : scanPackages) {
            allDrivers.addAll(discover(basePackage));
        }
        return allDrivers;
    }
    
    @Override
    public List<DiscoveredDriver> discover(String basePackage) {
        log.info("Starting driver discovery in package: {}", basePackage);
        notifyDiscoveryStarted(basePackage);
        List<DiscoveredDriver> drivers = new ArrayList<>();
        try {
            String path = basePackage.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                drivers.addAll(scanResource(resources.nextElement(), basePackage));
            }
            log.info("Discovered {} drivers in package: {}", drivers.size(), basePackage);
            notifyDiscoveryCompleted(drivers.size());
        } catch (IOException e) {
            log.error("Failed to discover drivers in package: {}", basePackage, e);
            notifyDiscoveryError("Discovery failed: " + e.getMessage(), e);
        }
        return drivers;
    }
    
    @Override
    public List<DiscoveredDriver> discoverByAnnotation(Class<?> annotationClass) {
        List<DiscoveredDriver> result = new ArrayList<>();
        for (DiscoveredDriver driver : discover()) {
            if (driver.getDriverClass() != null) {
                for (Annotation ann : driver.getDriverClass().getAnnotations()) {
                    if (ann.annotationType().getName().equals(annotationClass.getName())) {
                        result.add(driver);
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    @Override
    public List<DiscoveredDriver> discoverByInterface(String interfaceId) {
        List<DiscoveredDriver> result = new ArrayList<>();
        for (DiscoveredDriver driver : discover()) {
            if (interfaceId.equals(driver.getInterfaceId())) {
                result.add(driver);
            }
        }
        return result;
    }
    
    @Override
    public void addDiscoveryListener(DriverDiscoveryListener listener) {
        if (listener != null) listeners.add(listener);
    }
    
    @Override
    public void removeDiscoveryListener(DriverDiscoveryListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void setScanPackages(List<String> packages) {
        scanPackages.clear();
        if (packages != null) scanPackages.addAll(packages);
    }
    
    @Override
    public List<String> getScanPackages() {
        return new ArrayList<>(scanPackages);
    }
    
    private List<DiscoveredDriver> scanResource(URL resource, String basePackage) {
        List<DiscoveredDriver> drivers = new ArrayList<>();
        String protocol = resource.getProtocol();
        if ("file".equals(protocol)) {
            drivers.addAll(scanFileDirectory(resource, basePackage));
        } else if ("jar".equals(protocol)) {
            drivers.addAll(scanJarFile(resource, basePackage));
        }
        return drivers;
    }
    
    private List<DiscoveredDriver> scanFileDirectory(URL resource, String basePackage) {
        List<DiscoveredDriver> drivers = new ArrayList<>();
        try {
            java.io.File dir = new java.io.File(resource.getFile());
            if (dir.exists() && dir.isDirectory()) {
                drivers.addAll(scanDirectory(dir, basePackage));
            }
        } catch (Exception e) {
            log.debug("Failed to scan file directory: {}", resource, e);
        }
        return drivers;
    }
    
    private List<DiscoveredDriver> scanDirectory(java.io.File dir, String packageName) {
        List<DiscoveredDriver> drivers = new ArrayList<>();
        java.io.File[] files = dir.listFiles();
        if (files == null) return drivers;
        for (java.io.File file : files) {
            if (file.isDirectory()) {
                drivers.addAll(scanDirectory(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                DiscoveredDriver driver = tryLoadDriver(className);
                if (driver != null) drivers.add(driver);
            }
        }
        return drivers;
    }
    
    private List<DiscoveredDriver> scanJarFile(URL resource, String basePackage) {
        List<DiscoveredDriver> drivers = new ArrayList<>();
        try {
            String jarPath = resource.getPath();
            int idx = jarPath.indexOf('!');
            if (idx > 0) {
                jarPath = jarPath.substring(0, idx).replace("file:", "");
                try (JarFile jarFile = new JarFile(jarPath)) {
                    String packagePath = basePackage.replace('.', '/');
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(packagePath) && name.endsWith(".class")) {
                            String className = name.substring(0, name.length() - 6).replace('/', '.');
                            DiscoveredDriver driver = tryLoadDriver(className);
                            if (driver != null) drivers.add(driver);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.debug("Failed to scan JAR file: {}", resource, e);
        }
        return drivers;
    }
    
    private DiscoveredDriver tryLoadDriver(String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum()) return null;
            String interfaceId = extractInterfaceId(clazz);
            if (interfaceId == null) return null;
            DiscoveredDriver driver = new DiscoveredDriver();
            driver.setInterfaceId(interfaceId);
            driver.setSkillId(clazz.getSimpleName());
            driver.setClassName(className);
            driver.setDriverClass(clazz);
            driver.setSingleton(true);
            driver.setPriority(0);
            notifyDriverDiscovered(driver);
            return driver;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            log.debug("Class not found: {}", className);
            return null;
        } catch (Exception e) {
            log.debug("Failed to load driver class: {}", className, e);
            return null;
        }
    }
    
    private String extractInterfaceId(Class<?> clazz) {
        for (Class<?> iface : clazz.getInterfaces()) {
            String name = iface.getName();
            if (name.startsWith("net.ooder.sdk.") && 
                (name.contains(".msg.") || name.contains(".cmd.") || 
                 name.contains(".vfs.") || name.contains(".driver."))) {
                return iface.getSimpleName();
            }
        }
        for (Annotation ann : clazz.getAnnotations()) {
            String annName = ann.annotationType().getSimpleName();
            if ("DriverImplementation".equals(annName) || "SkillDriver".equals(annName)) {
                try {
                    java.lang.reflect.Method m = ann.annotationType().getMethod("value");
                    return (String) m.invoke(ann);
                } catch (Exception e) {
                    return clazz.getSimpleName();
                }
            }
        }
        return null;
    }
    
    private void notifyDriverDiscovered(DiscoveredDriver driver) {
        for (DriverDiscoveryListener listener : listeners) {
            try { listener.onDriverDiscovered(driver); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
    
    private void notifyDiscoveryStarted(String basePackage) {
        for (DriverDiscoveryListener listener : listeners) {
            try { listener.onDiscoveryStarted(basePackage); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
    
    private void notifyDiscoveryCompleted(int count) {
        for (DriverDiscoveryListener listener : listeners) {
            try { listener.onDiscoveryCompleted(count); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
    
    private void notifyDiscoveryError(String message, Throwable error) {
        for (DriverDiscoveryListener listener : listeners) {
            try { listener.onDiscoveryError(message, error); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
}
