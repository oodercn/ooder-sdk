package net.ooder.skills.container.core;

import net.ooder.skills.container.api.CapabilityRegistry;
import net.ooder.skills.container.api.SkillContainer;
import net.ooder.skills.container.api.SkillHandle;
import net.ooder.skills.container.api.annotation.Skill;
import net.ooder.skills.container.api.annotation.Capability;
import net.ooder.skills.container.config.ContainerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.*;

/**
 * Skills 容器实现 - 完整版
 */
public class SkillContainerImpl implements SkillContainer {

    private static final Logger logger = LoggerFactory.getLogger(SkillContainerImpl.class);

    private final Map<String, SkillHandle> skills = new ConcurrentHashMap<>();
    private final Map<String, BlueGreenDeployment> deployments = new ConcurrentHashMap<>();
    private final CapabilityRegistry capabilityRegistry;
    private ContainerConfig config;
    private volatile boolean initialized = false;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    public SkillContainerImpl() {
        this.capabilityRegistry = new CapabilityRegistryImpl();
    }

    @Override
    public void initialize(ContainerConfig config) {
        this.config = config;
        logger.info("Initializing Skills Container with workspace: {}", config.getWorkspace());

        // 创建工作目录结构
        createWorkspaceStructure();

        this.initialized = true;
        logger.info("Skills Container initialized successfully");
    }

    private void createWorkspaceStructure() {
        try {
            Path workspace = Paths.get(config.getWorkspace());
            Files.createDirectories(workspace);
            Files.createDirectories(workspace.resolve("skills"));
            Files.createDirectories(workspace.resolve("deployments"));
            Files.createDirectories(workspace.resolve("backups"));
            Files.createDirectories(workspace.resolve("temp"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create workspace structure", e);
        }
    }

    @Override
    public SkillHandle loadSkill(SkillPackage skillPackage) {
        if (!initialized) {
            throw new IllegalStateException("Container not initialized");
        }

        String skillId = skillPackage.getSkillId();
        logger.info("Loading skill: {} v{}", skillId, skillPackage.getVersion());

        if (skills.containsKey(skillId)) {
            throw new IllegalStateException("Skill already loaded: " + skillId);
        }

        SkillHandleImpl handle = new SkillHandleImpl(skillId, skillPackage.getVersion());
        handle.setState(SkillHandle.SkillState.LOADING);

        try {
            // 1. 下载/复制 Skill Jar 到工作目录
            Path skillPath = downloadSkill(skillPackage);
            handle.setJarPath(skillPath.toString());

            // 2. 创建独立的 ClassLoader
            ClassLoader classLoader = createSkillClassLoader(skillPath);
            handle.setClassLoader(classLoader);

            // 3. 扫描并加载 Skill 类
            Class<?> skillClass = scanSkillClass(classLoader, skillPath);
            handle.setSkillClass(skillClass);

            // 4. 解析 @Skill 注解
            Skill skillAnnotation = skillClass.getAnnotation(Skill.class);
            if (skillAnnotation != null) {
                handle.setName(skillAnnotation.name());
                handle.setDescription(skillAnnotation.description());
            }

            // 5. 实例化 Skill
            Object instance = skillClass.getDeclaredConstructor().newInstance();
            handle.setInstance(instance);

            // 6. 扫描并注册能力
            scanAndRegisterCapabilities(handle, instance);

            handle.setState(SkillHandle.SkillState.LOADED);
            skills.put(skillId, handle);

            if (config.isAutoStart()) {
                startSkill(skillId);
            }

            logger.info("Skill loaded successfully: {}", skillId);
            return handle;

        } catch (Exception e) {
            handle.setState(SkillHandle.SkillState.FAILED);
            logger.error("Failed to load skill: {}", skillId, e);
            throw new RuntimeException("Failed to load skill: " + skillId, e);
        }
    }

    private Path downloadSkill(SkillPackage skillPackage) throws IOException {
        Path targetDir = Paths.get(config.getWorkspace(), "skills");
        Path targetPath = targetDir.resolve(skillPackage.getSkillId() + "-" + skillPackage.getVersion() + ".jar");

        // 如果已存在，直接返回
        if (Files.exists(targetPath)) {
            return targetPath;
        }

        // 从 location 复制
        Path sourcePath = Paths.get(skillPackage.getLocation());
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath;
    }

    private ClassLoader createSkillClassLoader(Path skillPath) throws Exception {
        URL[] urls = { skillPath.toUri().toURL() };
        return new URLClassLoader(urls, this.getClass().getClassLoader());
    }

    private Class<?> scanSkillClass(ClassLoader classLoader, Path skillPath) throws Exception {
        try (JarFile jarFile = new JarFile(skillPath.toFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.endsWith(".class")) {
                    String className = name.replace("/", ".").replace(".class", "");
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Skill.class)) {
                            return clazz;
                        }
                    } catch (ClassNotFoundException e) {
                        // 忽略无法加载的类
                    }
                }
            }
        }

        throw new RuntimeException("No @Skill annotated class found in jar");
    }

    private void scanAndRegisterCapabilities(SkillHandleImpl handle, Object instance) {
        Class<?> clazz = instance.getClass();

        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            Capability capAnnotation = method.getAnnotation(Capability.class);
            if (capAnnotation != null) {
                CapabilityRegistry.Capability capability = new CapabilityRegistry.Capability();
                capability.setId(capAnnotation.id());
                capability.setName(capAnnotation.name());
                capability.setCategory(capAnnotation.category());
                capability.setDescription(capAnnotation.description());
                capability.setVersion(handle.getVersion());

                handle.addCapability(capability);
                capabilityRegistry.registerCapability(handle.getSkillId(), capability);

                logger.debug("Registered capability: {} from skill: {}", capAnnotation.id(), handle.getSkillId());
            }
        }
    }

    @Override
    public void unloadSkill(String skillId) {
        SkillHandle handle = skills.get(skillId);
        if (handle == null) {
            logger.warn("Skill not found: {}", skillId);
            return;
        }

        logger.info("Unloading skill: {}", skillId);

        if (handle.getState() == SkillHandle.SkillState.RUNNING) {
            stopSkill(skillId);
        }

        // 注销能力
        for (CapabilityRegistry.Capability cap : handle.getCapabilities()) {
            capabilityRegistry.unregisterCapability(skillId, cap.getId());
        }

        // 关闭 ClassLoader
        if (handle instanceof SkillHandleImpl) {
            ClassLoader cl = ((SkillHandleImpl) handle).getClassLoader();
            if (cl instanceof URLClassLoader) {
                try {
                    ((URLClassLoader) cl).close();
                } catch (IOException e) {
                    logger.warn("Error closing classloader for skill: {}", skillId, e);
                }
            }
        }

        ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.UNLOADED);
        skills.remove(skillId);

        logger.info("Skill unloaded: {}", skillId);
    }

    @Override
    public void startSkill(String skillId) {
        SkillHandle handle = skills.get(skillId);
        if (handle == null) {
            throw new IllegalStateException("Skill not loaded: " + skillId);
        }

        if (handle.getState() == SkillHandle.SkillState.RUNNING) {
            logger.warn("Skill already running: {}", skillId);
            return;
        }

        logger.info("Starting skill: {}", skillId);
        ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.STARTING);

        try {
            Object instance = handle.getInstance();
            if (instance != null) {
                // 调用 start 方法（如果存在）
                try {
                    java.lang.reflect.Method startMethod = instance.getClass().getMethod("start");
                    startMethod.invoke(instance);
                } catch (NoSuchMethodException e) {
                    // 没有 start 方法，忽略
                }
            }

            ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.RUNNING);
            logger.info("Skill started: {}", skillId);

        } catch (Exception e) {
            ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.FAILED);
            logger.error("Failed to start skill: {}", skillId, e);
            throw new RuntimeException("Failed to start skill: " + skillId, e);
        }
    }

    @Override
    public void stopSkill(String skillId) {
        SkillHandle handle = skills.get(skillId);
        if (handle == null) {
            logger.warn("Skill not found: {}", skillId);
            return;
        }

        if (handle.getState() != SkillHandle.SkillState.RUNNING) {
            logger.warn("Skill not running: {}", skillId);
            return;
        }

        logger.info("Stopping skill: {}", skillId);
        ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.STOPPING);

        try {
            Object instance = handle.getInstance();
            if (instance != null) {
                // 调用 stop 方法（如果存在）
                try {
                    java.lang.reflect.Method stopMethod = instance.getClass().getMethod("stop");
                    stopMethod.invoke(instance);
                } catch (NoSuchMethodException e) {
                    // 没有 stop 方法，忽略
                }
            }

            ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.STOPPED);
            logger.info("Skill stopped: {}", skillId);

        } catch (Exception e) {
            ((SkillHandleImpl) handle).setState(SkillHandle.SkillState.FAILED);
            logger.error("Failed to stop skill: {}", skillId, e);
            throw new RuntimeException("Failed to stop skill: " + skillId, e);
        }
    }

    @Override
    public UpdateResult updateSkill(String skillId, String newVersion, UpdateOptions options) {
        logger.info("Updating skill: {} to version {}", skillId, newVersion);

        UpdateResult result = new UpdateResult();
        result.setSkillId(skillId);
        result.setNewVersion(newVersion);

        SkillHandle handle = skills.get(skillId);
        if (handle == null) {
            result.setSuccess(false);
            result.setMessage("Skill not found: " + skillId);
            return result;
        }

        String oldVersion = handle.getVersion();
        result.setOldVersion(oldVersion);

        try {
            if (options.isBlueGreen() && config.isEnableBlueGreenDeployment()) {
                // 蓝绿部署
                performBlueGreenUpdate(skillId, oldVersion, newVersion, result);
            } else {
                // 直接更新
                performDirectUpdate(skillId, oldVersion, newVersion, result);
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Update failed: " + e.getMessage());
            logger.error("Failed to update skill: {}", skillId, e);

            if (options.isAutoRollback()) {
                rollbackUpdate(skillId, oldVersion);
            }
        }

        return result;
    }

    private void performBlueGreenUpdate(String skillId, String oldVersion, String newVersion, UpdateResult result) throws Exception {
        logger.info("Performing blue-green update for skill: {}", skillId);

        // 1. 创建蓝绿部署上下文
        BlueGreenDeployment deployment = new BlueGreenDeployment();
        deployment.setSkillId(skillId);
        deployment.setOldVersion(oldVersion);
        deployment.setNewVersion(newVersion);
        deployment.setBlueHandle((SkillHandleImpl) skills.get(skillId));

        // 2. 部署新版本（Green）
        SkillPackage greenPackage = new SkillPackage();
        greenPackage.setSkillId(skillId);
        greenPackage.setVersion(newVersion);
        greenPackage.setLocation(downloadNewVersion(skillId, newVersion));

        SkillHandle greenHandle = loadSkill(greenPackage);
        deployment.setGreenHandle((SkillHandleImpl) greenHandle);

        // 3. 健康检查
        if (!performHealthCheck(greenHandle)) {
            unloadSkill(skillId);
            throw new RuntimeException("Health check failed for new version");
        }

        // 4. 切换流量
        deployment.setActiveEnvironment(BlueGreenDeployment.Environment.GREEN);
        deployments.put(skillId, deployment);

        // 5. 停止旧版本（延迟）
        executor.schedule(() -> {
            try {
                stopSkill(skillId + "-" + oldVersion);
            } catch (Exception e) {
                logger.warn("Error stopping old version: {}", oldVersion, e);
            }
        }, 30, TimeUnit.SECONDS);

        result.setSuccess(true);
        result.setMessage("Blue-green update completed successfully");
        logger.info("Blue-green update completed for skill: {}", skillId);
    }

    private void performDirectUpdate(String skillId, String oldVersion, String newVersion, UpdateResult result) throws Exception {
        // 直接更新
        stopSkill(skillId);
        unloadSkill(skillId);

        SkillPackage newPackage = new SkillPackage();
        newPackage.setSkillId(skillId);
        newPackage.setVersion(newVersion);
        newPackage.setLocation(downloadNewVersion(skillId, newVersion));

        loadSkill(newPackage);

        result.setSuccess(true);
        result.setMessage("Direct update completed successfully");
        logger.info("Direct update completed for skill: {}", skillId);
    }

    private String downloadNewVersion(String skillId, String newVersion) throws IOException {
        // TODO: 从远程仓库下载新版本
        // 临时返回路径
        return config.getWorkspace() + "/skills/" + skillId + "-" + newVersion + ".jar";
    }

    private boolean performHealthCheck(SkillHandle handle) {
        // TODO: 实现健康检查逻辑
        return handle.getState() == SkillHandle.SkillState.RUNNING;
    }

    private void rollbackUpdate(String skillId, String targetVersion) {
        logger.info("Rolling back skill: {} to version {}", skillId, targetVersion);
        try {
            stopSkill(skillId);
            unloadSkill(skillId);

            SkillPackage rollbackPackage = new SkillPackage();
            rollbackPackage.setSkillId(skillId);
            rollbackPackage.setVersion(targetVersion);
            rollbackPackage.setLocation(config.getWorkspace() + "/skills/" + skillId + "-" + targetVersion + ".jar");

            loadSkill(rollbackPackage);
            logger.info("Rollback completed for skill: {}", skillId);
        } catch (Exception e) {
            logger.error("Rollback failed for skill: {}", skillId, e);
        }
    }

    @Override
    public List<SkillHandle> getLoadedSkills() {
        return new ArrayList<>(skills.values());
    }

    @Override
    public CapabilityRegistry getCapabilityRegistry() {
        return capabilityRegistry;
    }

    @Override
    public void shutdown() {
        logger.info("Shutting down Skills Container");

        for (SkillHandle handle : new ArrayList<>(skills.values())) {
            try {
                unloadSkill(handle.getSkillId());
            } catch (Exception e) {
                logger.error("Error unloading skill during shutdown: {}", handle.getSkillId(), e);
            }
        }

        executor.shutdown();
        this.initialized = false;
        logger.info("Skills Container shutdown complete");
    }

    /**
     * 蓝绿部署上下文
     */
    private static class BlueGreenDeployment {
        private String skillId;
        private String oldVersion;
        private String newVersion;
        private SkillHandleImpl blueHandle;
        private SkillHandleImpl greenHandle;
        private Environment activeEnvironment;

        enum Environment {
            BLUE, GREEN
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getOldVersion() {
            return oldVersion;
        }

        public void setOldVersion(String oldVersion) {
            this.oldVersion = oldVersion;
        }

        public String getNewVersion() {
            return newVersion;
        }

        public void setNewVersion(String newVersion) {
            this.newVersion = newVersion;
        }

        public SkillHandleImpl getBlueHandle() {
            return blueHandle;
        }

        public void setBlueHandle(SkillHandleImpl blueHandle) {
            this.blueHandle = blueHandle;
        }

        public SkillHandleImpl getGreenHandle() {
            return greenHandle;
        }

        public void setGreenHandle(SkillHandleImpl greenHandle) {
            this.greenHandle = greenHandle;
        }

        public Environment getActiveEnvironment() {
            return activeEnvironment;
        }

        public void setActiveEnvironment(Environment activeEnvironment) {
            this.activeEnvironment = activeEnvironment;
        }
    }
}
