package net.ooder.sdk.discovery;

import net.ooder.sdk.plugin.SkillMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Skill发现服务
 *
 * <p>实现多种Skill发现机制:</p>
 * <ul>
 *   <li>本地文件系统扫描</li>
 *   <li>运行时动态注册</li>
 *   <li>远程仓库查询 (预留)</li>
 *   <li>mDNS服务发现 (预留)</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(SkillDiscoveryService.class);

    /**
     * YAML解析器
     */
    private final SkillYamlParser yamlParser;

    /**
     * 已发现的Skill缓存
     */
    private final Map<String, DiscoveredSkill> discoveredSkills;

    /**
     * 扫描目录列表
     */
    private final List<File> scanDirectories;

    /**
     * 定时任务调度器
     */
    private final ScheduledExecutorService scheduler;

    /**
     * 是否正在运行
     */
    private volatile boolean running;

    /**
     * 自动扫描间隔（秒）
     */
    private int scanIntervalSeconds = 60;

    /**
     * 扫描任务Future
     */
    private ScheduledFuture<?> scanTaskFuture;

    public SkillDiscoveryService() {
        this.yamlParser = new SkillYamlParser();
        this.discoveredSkills = new ConcurrentHashMap<>();
        this.scanDirectories = new CopyOnWriteArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SkillDiscovery-Scanner");
            t.setDaemon(true);
            return t;
        });
    }

    // ==================== 核心发现方法 ====================

    /**
     * 从本地文件系统扫描发现Skill
     *
     * @param directory 扫描目录
     * @return 发现的Skill列表
     */
    public List<DiscoveredSkill> discoverFromFilesystem(File directory) {
        log.info("Scanning directory for skills: {}", directory.getAbsolutePath());

        List<SkillMetadata> metadataList = yamlParser.scanAndParseDirectory(directory);
        List<DiscoveredSkill> discovered = new ArrayList<>();

        for (SkillMetadata metadata : metadataList) {
            DiscoveredSkill skill = new DiscoveredSkill(
                    metadata.getId(),
                    metadata,
                    DiscoverySource.FILESYSTEM,
                    directory.getAbsolutePath()
            );
            discoveredSkills.put(metadata.getId(), skill);
            discovered.add(skill);
            log.info("Discovered skill: {} (version: {}) from filesystem",
                    metadata.getId(), metadata.getVersion());
        }

        log.info("Discovered {} skills from directory: {}", discovered.size(), directory.getAbsolutePath());
        return discovered;
    }

    /**
     * 从单个JAR文件发现Skill
     *
     * @param jarFile JAR文件
     * @return 发现的Skill，如果解析失败返回null
     */
    public DiscoveredSkill discoverFromJar(File jarFile) {
        try {
            SkillMetadata metadata = yamlParser.parseFromJar(jarFile);
            DiscoveredSkill skill = new DiscoveredSkill(
                    metadata.getId(),
                    metadata,
                    DiscoverySource.FILESYSTEM,
                    jarFile.getAbsolutePath()
            );
            discoveredSkills.put(metadata.getId(), skill);
            log.info("Discovered skill: {} from JAR: {}", metadata.getId(), jarFile.getName());
            return skill;
        } catch (SkillParseException e) {
            log.error("Failed to discover skill from JAR: {}", jarFile.getName(), e);
            return null;
        }
    }

    /**
     * 动态注册Skill
     *
     * @param metadata Skill元数据
     * @param source 来源标识
     * @return 发现的Skill
     */
    public DiscoveredSkill registerSkill(SkillMetadata metadata, String source) {
        if (metadata == null || metadata.getId() == null) {
            throw new IllegalArgumentException("Metadata and id cannot be null");
        }

        DiscoveredSkill skill = new DiscoveredSkill(
                metadata.getId(),
                metadata,
                DiscoverySource.DYNAMIC,
                source
        );
        discoveredSkills.put(metadata.getId(), skill);
        log.info("Dynamically registered skill: {} from source: {}", metadata.getId(), source);
        return skill;
    }

    /**
     * 从远程仓库查询Skill (预留)
     *
     * @param repositoryUrl 仓库URL
     * @return CompletableFuture
     */
    public CompletableFuture<List<DiscoveredSkill>> discoverFromRemote(String repositoryUrl) {
        log.info("Remote discovery not yet implemented. Repository: {}", repositoryUrl);
        // TODO: 实现远程仓库查询
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    /**
     * 通过mDNS发现Skill (预留)
     *
     * @return CompletableFuture
     */
    public CompletableFuture<List<DiscoveredSkill>> discoverViaMdns() {
        log.info("mDNS discovery not yet implemented");
        // TODO: 实现mDNS服务发现
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    // ==================== 扫描管理 ====================

    /**
     * 添加扫描目录
     *
     * @param directory 扫描目录
     */
    public void addScanDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            scanDirectories.add(directory);
            log.info("Added scan directory: {}", directory.getAbsolutePath());
        } else {
            log.warn("Invalid scan directory: {}", directory);
        }
    }

    /**
     * 移除扫描目录
     *
     * @param directory 扫描目录
     */
    public void removeScanDirectory(File directory) {
        scanDirectories.remove(directory);
        log.info("Removed scan directory: {}", directory != null ? directory.getAbsolutePath() : "null");
    }

    /**
     * 启动自动扫描
     */
    public void startAutoScan() {
        if (running) {
            log.warn("Auto scan is already running");
            return;
        }

        running = true;
        scanTaskFuture = scheduler.scheduleAtFixedRate(
                this::performScan,
                0,
                scanIntervalSeconds,
                TimeUnit.SECONDS
        );
        log.info("Started auto scan with interval: {} seconds", scanIntervalSeconds);
    }

    /**
     * 停止自动扫描
     */
    public void stopAutoScan() {
        running = false;
        if (scanTaskFuture != null) {
            scanTaskFuture.cancel(false);
            scanTaskFuture = null;
        }
        log.info("Stopped auto scan");
    }

    /**
     * 执行一次扫描
     */
    public void performScan() {
        if (scanDirectories.isEmpty()) {
            return;
        }

        log.debug("Performing scan of {} directories", scanDirectories.size());

        for (File directory : scanDirectories) {
            try {
                discoverFromFilesystem(directory);
            } catch (Exception e) {
                log.error("Error scanning directory: {}", directory.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 设置扫描间隔
     *
     * @param seconds 扫描间隔（秒）
     */
    public void setScanInterval(int seconds) {
        if (seconds < 10) {
            throw new IllegalArgumentException("Scan interval must be at least 10 seconds");
        }
        this.scanIntervalSeconds = seconds;

        // 如果正在运行，重启扫描任务
        if (running) {
            stopAutoScan();
            startAutoScan();
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 获取所有已发现的Skill
     *
     * @return Skill列表
     */
    public List<DiscoveredSkill> getAllDiscoveredSkills() {
        return new ArrayList<>(discoveredSkills.values());
    }

    /**
     * 根据ID获取已发现的Skill
     *
     * @param skillId Skill标识
     * @return DiscoveredSkill或null
     */
    public DiscoveredSkill getDiscoveredSkill(String skillId) {
        return discoveredSkills.get(skillId);
    }

    /**
     * 根据来源获取Skill
     *
     * @param source 发现来源
     * @return Skill列表
     */
    public List<DiscoveredSkill> getSkillsBySource(DiscoverySource source) {
        List<DiscoveredSkill> result = new ArrayList<>();
        for (DiscoveredSkill skill : discoveredSkills.values()) {
            if (skill.getSource() == source) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * 检查Skill是否已发现
     *
     * @param skillId Skill标识
     * @return true如果已发现
     */
    public boolean isDiscovered(String skillId) {
        return discoveredSkills.containsKey(skillId);
    }

    /**
     * 移除已发现的Skill
     *
     * @param skillId Skill标识
     * @return 被移除的Skill或null
     */
    public DiscoveredSkill removeDiscoveredSkill(String skillId) {
        return discoveredSkills.remove(skillId);
    }

    /**
     * 清除所有已发现的Skill
     */
    public void clearDiscoveredSkills() {
        discoveredSkills.clear();
        log.info("Cleared all discovered skills");
    }

    /**
     * 获取已发现Skill数量
     *
     * @return Skill数量
     */
    public int getDiscoveredCount() {
        return discoveredSkills.size();
    }

    // ==================== 关闭 ====================

    /**
     * 关闭服务
     */
    public void shutdown() {
        stopAutoScan();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("SkillDiscoveryService shutdown complete");
    }

    // ==================== 内部类 ====================

    /**
     * 发现的Skill
     */
    public static class DiscoveredSkill {
        private final String id;
        private final SkillMetadata metadata;
        private final DiscoverySource source;
        private final String location;
        private final long discoveredAt;

        public DiscoveredSkill(String id, SkillMetadata metadata, DiscoverySource source, String location) {
            this.id = id;
            this.metadata = metadata;
            this.source = source;
            this.location = location;
            this.discoveredAt = System.currentTimeMillis();
        }

        public String getId() {
            return id;
        }

        public SkillMetadata getMetadata() {
            return metadata;
        }

        public DiscoverySource getSource() {
            return source;
        }

        public String getLocation() {
            return location;
        }

        public long getDiscoveredAt() {
            return discoveredAt;
        }

        @Override
        public String toString() {
            return "DiscoveredSkill{" +
                    "id='" + id + '\'' +
                    ", version='" + (metadata != null ? metadata.getVersion() : "unknown") + '\'' +
                    ", source=" + source +
                    ", location='" + location + '\'' +
                    '}';
        }
    }

    /**
     * 发现来源
     */
    public enum DiscoverySource {
        FILESYSTEM("本地文件系统"),
        REMOTE_REPOSITORY("远程仓库"),
        MDNS("mDNS服务发现"),
        DYNAMIC("动态注册");

        private final String displayName;

        DiscoverySource(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
