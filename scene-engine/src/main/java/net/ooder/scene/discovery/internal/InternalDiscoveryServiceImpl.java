package net.ooder.scene.discovery.internal;

import net.ooder.engine.ConnectInfo;
import net.ooder.scene.discovery.api.*;
import net.ooder.scene.discovery.install.InstallTaskManager;
import net.ooder.scene.discovery.storage.MultiRepoConfigManager;
import net.ooder.scene.discovery.storage.VfsPathStrategy;
import net.ooder.sdk.discovery.git.GitRepositoryDiscoverer;
import net.ooder.sdk.discovery.git.GitHubDiscoverer;
import net.ooder.sdk.discovery.git.GiteeDiscoverer;
import net.ooder.sdk.service.storage.persistence.StorageManager;
import net.ooder.sdk.service.storage.vfs.VfsManager;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.core.discovery.LocalDiscoverer;
import net.ooder.skills.core.discovery.SkillCenterDiscoverer;
import net.ooder.skills.core.discovery.UdpDiscoverer;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 内部发现服务实现
 * 
 * 整合所有发现渠道，提供统一的管理接口
 * 支持：缓存管理、完整性检查、依赖管理、安装恢复
 * 
 * @author ooder Team
 * @since 2.3
 */
public class InternalDiscoveryServiceImpl implements InternalDiscoveryService {

    private final VfsManager vfsManager;
    private final StorageManager storageManager;
    private final MultiRepoConfigManager repoConfigManager;
    private final InstallTaskManager installTaskManager;
    
    // 发现器集合
    private final Map<String, SkillDiscoverer> discoverers;
    private final Map<String, GitRepositoryDiscoverer> gitDiscoverers;
    
    // 内存缓存
    private final Map<String, CacheEntry> memoryCache;
    private final Map<String, UnifiedSkillInfo> unifiedSkillIndex;
    
    // 配置
    private long cacheTtlMs = 3600000; // 默认1小时
    private int maxCacheSize = 1000;
    
    public InternalDiscoveryServiceImpl(VfsManager vfsManager, StorageManager storageManager) {
        this.vfsManager = vfsManager;
        this.storageManager = storageManager;
        this.repoConfigManager = new MultiRepoConfigManager(vfsManager, storageManager);
        this.installTaskManager = new InstallTaskManager(vfsManager);
        this.discoverers = new ConcurrentHashMap<>();
        this.gitDiscoverers = new ConcurrentHashMap<>();
        this.memoryCache = new ConcurrentHashMap<>();
        this.unifiedSkillIndex = new ConcurrentHashMap<>();
        
        // 初始化发现器
        initializeDiscoverers();
        
        // 加载统一技能索引
        loadUnifiedSkillIndex();
    }

    /**
     * 初始化发现器
     */
    private void initializeDiscoverers() {
        // 本地发现器
        discoverers.put("local", new LocalDiscoverer());
        
        // UDP发现器
        discoverers.put("udp", new UdpDiscoverer());
        
        // 技能中心发现器
        discoverers.put("skillcenter", new SkillCenterDiscoverer());
        
        // Git发现器（延迟初始化，需要配置）
        // GitHub和Gitee发现器将在需要时根据用户配置创建
    }

    @Override
    public CompletableFuture<DiscoveryResult> discoverInternal(DiscoveryRequest request, boolean useCache) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = generateCacheKey(request);
            
            // 尝试从内存缓存获取
            if (useCache) {
                CacheEntry cached = memoryCache.get(cacheKey);
                if (cached != null && !cached.isExpired()) {
                    return cached.getResult();
                }
                
                // 尝试从VFS缓存获取
                DiscoveryResult vfsCached = loadFromVfsCache(request);
                if (vfsCached != null) {
                    // 更新内存缓存
                    memoryCache.put(cacheKey, new CacheEntry(vfsCached, cacheTtlMs));
                    return vfsCached;
                }
            }
            
            // 执行发现
            DiscoveryResult result = performDiscovery(request);
            
            // 更新缓存
            if (useCache && result != null) {
                memoryCache.put(cacheKey, new CacheEntry(result, cacheTtlMs));
                saveToVfsCache(request, result);
            }
            
            // 更新统一索引
            updateUnifiedIndex(result, request.getSource());
            
            // 保存发现历史
            saveDiscoveryHistory(request, result);
            
            return result;
        });
    }

    @Override
    public CompletableFuture<DiscoveryResult> refreshInternal(DiscoveryRequest request) {
        return discoverInternal(request, false);
    }

    @Override
    public CompletableFuture<IntegrityCheckResult> checkIntegrityInternal(String skillId, String source) {
        return CompletableFuture.supplyAsync(() -> {
            IntegrityCheckResult result = new IntegrityCheckResult();
            result.setSkillId(skillId);
            
            try {
                // 获取技能路径
                String skillPath = VfsPathStrategy.getPackagePath(skillId, "latest");
                
                // 检查文件是否存在
                boolean exists = vfsManager.exists(skillPath).get();
                if (!exists) {
                    result.setValid(false);
                    result.setMissingFiles(Arrays.asList("skill-package"));
                    return result;
                }
                
                // 获取清单文件
                String manifestPath = VfsPathStrategy.getSkillManifestPath(source, skillId, "latest");
                boolean manifestExists = vfsManager.exists(manifestPath).get();
                
                if (!manifestExists) {
                    result.setValid(false);
                    result.setMissingFiles(Arrays.asList("skill-manifest.yaml"));
                    return result;
                }
                
                // 计算校验和（简化实现）
                byte[] content = vfsManager.readFile(manifestPath).get();
                if (content != null) {
                    String checksum = calculateChecksum(content);
                    result.setChecksum(checksum);
                }
                
                result.setValid(true);
                
                // 保存完整性报告
                saveIntegrityReport(skillId, "latest", result);
                
            } catch (Exception e) {
                result.setValid(false);
                result.setMissingFiles(Arrays.asList("verification-failed: " + e.getMessage()));
            }
            
            return result;
        });
    }

    @Override
    public CompletableFuture<DependencyCheckResult> checkDependenciesInternal(String skillId, String version) {
        return CompletableFuture.supplyAsync(() -> {
            DependencyCheckResult result = new DependencyCheckResult();
            result.setSkillId(skillId);
            result.setVersion(version);
            
            List<DependencyInfo> missingDeps = new ArrayList<>();
            List<DependencyInfo> installedDeps = new ArrayList<>();
            List<DependencyConflict> conflicts = new ArrayList<>();
            
            try {
                // 获取依赖树
                DependencyTree tree = loadDependencyTree(skillId, version);
                
                if (tree == null) {
                    // 没有依赖信息，认为满足
                    result.setSatisfied(true);
                    return result;
                }
                
                // 检查每个依赖
                for (DependencyNode node : tree.getDependencies()) {
                    String depId = node.getSkillId();
                    String depVersion = node.getVersion();
                    
                    // 检查是否已安装
                    boolean installed = checkSkillInstalled(depId, depVersion);
                    
                    DependencyInfo depInfo = new DependencyInfo();
                    depInfo.setSkillId(depId);
                    depInfo.setVersion(depVersion);
                    depInfo.setSource(node.getSource());
                    
                    if (installed) {
                        installedDeps.add(depInfo);
                    } else {
                        missingDeps.add(depInfo);
                    }
                }
                
                result.setMissingDependencies(missingDeps);
                result.setInstalledDependencies(installedDeps);
                result.setConflicts(conflicts);
                result.setSatisfied(missingDeps.isEmpty() && conflicts.isEmpty());
                
            } catch (Exception e) {
                result.setSatisfied(false);
            }
            
            return result;
        });
    }

    @Override
    public CompletableFuture<DependencyInstallResult> installDependenciesInternal(
            String skillId, 
            String version,
            InstallProgressCallback progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            // 首先检查依赖
            DependencyCheckResult checkResult = checkDependenciesInternal(skillId, version).join();
            
            if (checkResult.isSatisfied()) {
                // 依赖已满足
                DependencyInstallResult result = new DependencyInstallResult();
                result.setInstallId("skip_" + System.currentTimeMillis());
                result.setSkillId(skillId);
                result.setSuccess(true);
                result.setInstalledSkills(new ArrayList<>());
                result.setStartTime(System.currentTimeMillis());
                result.setEndTime(System.currentTimeMillis());
                result.setErrorMessage("Dependencies already satisfied");
                return result;
            }
            
            // 获取依赖列表
            List<String> dependencies = checkResult.getMissingDependencies().stream()
                    .map(DependencyInfo::getSkillId)
                    .collect(Collectors.toList());
            
            // 创建安装任务
            String installId = installTaskManager.createInstallTask(skillId, version, dependencies);
            
            // 等待安装完成（带进度回调）
            try {
                while (true) {
                    InstallProgress progress = installTaskManager.getInstallProgress(installId);
                    if (progress == null) {
                        break;
                    }
                    
                    if (progressCallback != null) {
                        progressCallback.onProgress(progress);
                    }
                    
                    String status = progress.getStatus();
                    if ("COMPLETED".equals(status)) {
                        break;
                    }
                    if ("FAILED".equals(status) || "CANCELLED".equals(status)) {
                        break;
                    }
                    
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 获取最终结果
            InstallTaskManager.InstallTask task = installTaskManager.getActiveTasks().stream()
                    .filter(t -> installId.equals(t.getInstallId()))
                    .findFirst()
                    .orElse(null);
            
            DependencyInstallResult result = new DependencyInstallResult();
            result.setInstallId(installId);
            result.setSkillId(skillId);
            
            if (task != null) {
                result.setSuccess("COMPLETED".equals(task.getStatus()));
                result.setInstalledSkills(dependencies);
                result.setStartTime(task.getStartTime());
                result.setEndTime(task.getEndTime());
                result.setErrorMessage(task.getError());
            } else {
                result.setSuccess(false);
                result.setErrorMessage("Task not found");
            }
            
            if (progressCallback != null) {
                progressCallback.onComplete(result);
            }
            
            return result;
        });
    }

    @Override
    public CompletableFuture<DependencyInstallResult> resumeInstallInternal(
            String installId,
            InstallProgressCallback progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            boolean resumed = installTaskManager.resumeInstall(installId);
            
            if (!resumed) {
                DependencyInstallResult result = new DependencyInstallResult();
                result.setInstallId(installId);
                result.setSuccess(false);
                result.setErrorMessage("Failed to resume installation");
                return result;
            }
            
            // 等待安装完成
            try {
                while (true) {
                    InstallProgress progress = installTaskManager.getInstallProgress(installId);
                    if (progress == null) {
                        break;
                    }
                    
                    if (progressCallback != null) {
                        progressCallback.onProgress(progress);
                    }
                    
                    String status = progress.getStatus();
                    if ("COMPLETED".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status)) {
                        break;
                    }
                    
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 获取结果
            InstallTaskManager.InstallTask task = installTaskManager.getActiveTasks().stream()
                    .filter(t -> installId.equals(t.getInstallId()))
                    .findFirst()
                    .orElse(null);
            
            DependencyInstallResult result = new DependencyInstallResult();
            result.setInstallId(installId);
            
            if (task != null) {
                result.setSkillId(task.getSkillId());
                result.setSuccess("COMPLETED".equals(task.getStatus()));
                result.setStartTime(task.getStartTime());
                result.setEndTime(task.getEndTime());
                result.setErrorMessage(task.getError());
            }
            
            if (progressCallback != null) {
                progressCallback.onComplete(result);
            }
            
            return result;
        });
    }

    @Override
    public CompletableFuture<InstallProgress> getInstallProgress(String installId) {
        return CompletableFuture.supplyAsync(() -> {
            return installTaskManager.getInstallProgress(installId);
        });
    }

    @Override
    public CompletableFuture<Boolean> cancelInstall(String installId) {
        return CompletableFuture.supplyAsync(() -> {
            return installTaskManager.cancelInstall(installId);
        });
    }

    @Override
    public CompletableFuture<Boolean> cleanupFailedInstall(String installId) {
        return CompletableFuture.supplyAsync(() -> {
            return installTaskManager.cleanupFailedInstall(installId);
        });
    }

    @Override
    public CompletableFuture<List<DiscoveryHistory>> getDiscoveryHistory() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String historyPath = VfsPathStrategy.getHistoryPath();
                List<String> historyFiles = vfsManager.listFiles(historyPath).get();
                
                List<DiscoveryHistory> history = new ArrayList<>();
                if (historyFiles != null) {
                    for (String file : historyFiles) {
                        if (file.endsWith(".json")) {
                            String path = historyPath + "/" + file;
                            byte[] content = vfsManager.readFile(path).get();
                            if (content != null) {
                                DiscoveryHistory item = parseHistoryItem(new String(content));
                                if (item != null) {
                                    history.add(item);
                                }
                            }
                        }
                    }
                }
                
                // 按时间排序
                history.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                return history;
                
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });
    }

    @Override
    public CompletableFuture<List<UnifiedSkillInfo>> getUnifiedSkillList() {
        return CompletableFuture.supplyAsync(() -> {
            return new ArrayList<>(unifiedSkillIndex.values());
        });
    }

    /**
     * 执行发现
     */
    private DiscoveryResult performDiscovery(DiscoveryRequest request) {
        DiscoveryResult result = new DiscoveryResult();
        List<DiscoveryService.SkillInfo> skills = new ArrayList<>();
        
        String source = request.getSource();
        
        if (source == null || "all".equalsIgnoreCase(source)) {
            // 从所有来源发现
            for (Map.Entry<String, SkillDiscoverer> entry : discoverers.entrySet()) {
                try {
                    List<SkillPackage> packages = entry.getValue().discover().get();
                    for (SkillPackage pkg : packages) {
                        skills.add(convertToSkillInfo(pkg, entry.getKey()));
                    }
                } catch (Exception e) {
                    // 记录错误但继续
                }
            }
            
            // 从Git发现器发现
            for (Map.Entry<String, GitRepositoryDiscoverer> entry : gitDiscoverers.entrySet()) {
                try {
                    List<SkillPackage> packages = entry.getValue().discoverSkills().get();
                    for (SkillPackage pkg : packages) {
                        skills.add(convertToSkillInfo(pkg, entry.getKey()));
                    }
                } catch (Exception e) {
                    // 记录错误但继续
                }
            }
        } else {
            // 从指定来源发现
            SkillDiscoverer discoverer = discoverers.get(source);
            if (discoverer != null) {
                try {
                    List<SkillPackage> packages = discoverer.discover().get();
                    for (SkillPackage pkg : packages) {
                        skills.add(convertToSkillInfo(pkg, source));
                    }
                } catch (Exception e) {
                    // 记录错误
                }
            }
            
            GitRepositoryDiscoverer gitDiscoverer = gitDiscoverers.get(source);
            if (gitDiscoverer != null) {
                try {
                    List<SkillPackage> packages = gitDiscoverer.discoverSkills().get();
                    for (SkillPackage pkg : packages) {
                        skills.add(convertToSkillInfo(pkg, source));
                    }
                } catch (Exception e) {
                    // 记录错误
                }
            }
        }
        
        // 去重
        skills = deduplicateSkills(skills);
        
        result.setSkills(skills);
        result.setTotalCount(skills.size());
        result.setTimestamp(System.currentTimeMillis());
        
        return result;
    }

    /**
     * 技能去重
     */
    private List<DiscoveryService.SkillInfo> deduplicateSkills(List<DiscoveryService.SkillInfo> skills) {
        Map<String, DiscoveryService.SkillInfo> uniqueSkills = new LinkedHashMap<>();
        
        for (DiscoveryService.SkillInfo skill : skills) {
            String key = skill.getSkillId() + "@" + skill.getVersion();
            
            if (uniqueSkills.containsKey(key)) {
                // 合并来源
                DiscoveryService.SkillInfo existing = uniqueSkills.get(key);
                Set<String> sources = new HashSet<>();
                if (existing.getSource() != null) {
                    sources.addAll(Arrays.asList(existing.getSource().split(",")));
                }
                sources.add(skill.getSource());
                existing.setSource(String.join(",", sources));
            } else {
                uniqueSkills.put(key, skill);
            }
        }
        
        return new ArrayList<>(uniqueSkills.values());
    }

    /**
     * 更新统一索引
     */
    private void updateUnifiedIndex(DiscoveryResult result, String source) {
        if (result == null || result.getSkills() == null) {
            return;
        }
        
        for (DiscoveryService.SkillInfo skill : result.getSkills()) {
            String key = skill.getSkillId() + "@" + skill.getVersion();
            
            UnifiedSkillInfo unified = unifiedSkillIndex.computeIfAbsent(key, k -> {
                UnifiedSkillInfo info = new UnifiedSkillInfo();
                info.setSkillId(skill.getSkillId());
                info.setName(skill.getName());
                info.setVersion(skill.getVersion());
                info.setSources(new ArrayList<>());
                return info;
            });
            
            // 添加来源
            if (!unified.getSources().contains(source)) {
                unified.getSources().add(source);
            }
            
            unified.setLastDiscovered(System.currentTimeMillis());
            
            // 设置优先来源（本地 > 技能中心 > Git > UDP）
            if (unified.getPreferredSource() == null) {
                unified.setPreferredSource(determinePreferredSource(unified.getSources()));
            }
        }
        
        // 保存索引
        saveUnifiedSkillIndex();
    }

    /**
     * 确定优先来源
     */
    private String determinePreferredSource(List<String> sources) {
        // 优先级：local > skillcenter > github/gitee > udp
        if (sources.contains("local")) return "local";
        if (sources.contains("skillcenter")) return "skillcenter";
        if (sources.contains("github")) return "github";
        if (sources.contains("gitee")) return "gitee";
        if (sources.contains("udp")) return "udp";
        return sources.isEmpty() ? null : sources.get(0);
    }

    /**
     * 保存统一技能索引
     */
    private void saveUnifiedSkillIndex() {
        try {
            String path = VfsPathStrategy.getUnifiedSkillIndexPath();
            // 简化实现，实际应该序列化为JSON
            vfsManager.createDirectory(path.substring(0, path.lastIndexOf('/')));
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 加载统一技能索引
     */
    private void loadUnifiedSkillIndex() {
        try {
            String path = VfsPathStrategy.getUnifiedSkillIndexPath();
            // 简化实现
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 保存发现历史
     */
    private void saveDiscoveryHistory(DiscoveryRequest request, DiscoveryResult result) {
        try {
            String historyId = UUID.randomUUID().toString();
            String path = VfsPathStrategy.getHistoryItemPath(historyId);
            
            // 创建历史记录
            // 简化实现
            
            vfsManager.writeFile(path, "{}".getBytes());
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 保存完整性报告
     */
    private void saveIntegrityReport(String skillId, String version, IntegrityCheckResult result) {
        try {
            String path = VfsPathStrategy.getIntegrityReportPath(skillId, version);
            // 简化实现
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 检查技能是否已安装
     */
    private boolean checkSkillInstalled(String skillId, String version) {
        try {
            String path = VfsPathStrategy.getPackagePath(skillId, version);
            return vfsManager.exists(path).get();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 加载依赖树
     */
    private DependencyTree loadDependencyTree(String skillId, String version) {
        try {
            String path = VfsPathStrategy.getDependencyTreePath(skillId, version);
            // 简化实现
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成缓存键
     */
    private String generateCacheKey(DiscoveryRequest request) {
        return request.getSource() + "_" + request.getSceneId() + "_" + request.getCategory();
    }

    /**
     * 从VFS缓存加载
     */
    private DiscoveryResult loadFromVfsCache(DiscoveryRequest request) {
        // 简化实现
        return null;
    }

    /**
     * 保存到VFS缓存
     */
    private void saveToVfsCache(DiscoveryRequest request, DiscoveryResult result) {
        // 简化实现
    }

    /**
     * 转换SkillPackage到SkillInfo
     */
    private DiscoveryService.SkillInfo convertToSkillInfo(SkillPackage pkg, String source) {
        DiscoveryService.SkillInfo info = new DiscoveryService.SkillInfo();
        info.setSkillId(pkg.getSkillId());
        info.setName(pkg.getName());
        info.setVersion(pkg.getVersion());
        info.setSource(source);
        info.setDescription(pkg.getDescription());
        info.setCategory(pkg.getCategory());
        info.setTags(pkg.getTags());
        return info;
    }

    /**
     * 计算校验和
     */
    private String calculateChecksum(byte[] content) {
        // 简化实现，实际应该使用SHA-256
        return String.valueOf(Arrays.hashCode(content));
    }

    /**
     * 解析历史记录
     */
    private DiscoveryHistory parseHistoryItem(String json) {
        // 简化实现
        return null;
    }

    /**
     * 配置GitHub/Gitee发现器
     */
    public void configureGitDiscoverer(String userId, String source, ConnectInfo connectInfo) {
        // 保存ConnectInfo
        repoConfigManager.saveConnectInfo(userId, source, connectInfo);
        
        // 创建发现器
        if ("github".equalsIgnoreCase(source)) {
            GitHubDiscoverer discoverer = new GitHubDiscoverer();
            gitDiscoverers.put("github", discoverer);
        } else if ("gitee".equalsIgnoreCase(source)) {
            GiteeDiscoverer discoverer = new GiteeDiscoverer();
            gitDiscoverers.put("gitee", discoverer);
        }
    }

    /**
     * 获取多仓库配置管理器
     */
    public MultiRepoConfigManager getRepoConfigManager() {
        return repoConfigManager;
    }

    /**
     * 获取安装任务管理器
     */
    public InstallTaskManager getInstallTaskManager() {
        return installTaskManager;
    }

    /**
     * 设置缓存TTL
     */
    public void setCacheTtlMs(long ttlMs) {
        this.cacheTtlMs = ttlMs;
    }

    /**
     * 设置最大缓存大小
     */
    public void setMaxCacheSize(int size) {
        this.maxCacheSize = size;
    }

    /**
     * 清理内存缓存
     */
    public void clearMemoryCache() {
        memoryCache.clear();
    }

    /**
     * 缓存条目
     */
    private static class CacheEntry {
        private final DiscoveryResult result;
        private final long expireTime;

        public CacheEntry(DiscoveryResult result, long ttlMs) {
            this.result = result;
            this.expireTime = System.currentTimeMillis() + ttlMs;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }

        public DiscoveryResult getResult() {
            return result;
        }
    }

    /**
     * 依赖树
     */
    private static class DependencyTree {
        private String skillId;
        private String version;
        private List<DependencyNode> dependencies;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public List<DependencyNode> getDependencies() { return dependencies; }
        public void setDependencies(List<DependencyNode> dependencies) { this.dependencies = dependencies; }
    }

    /**
     * 依赖节点
     */
    private static class DependencyNode {
        private String skillId;
        private String version;
        private String source;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }
}
