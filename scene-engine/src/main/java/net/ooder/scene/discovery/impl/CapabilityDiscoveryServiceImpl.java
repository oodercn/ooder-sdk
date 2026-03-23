package net.ooder.scene.discovery.impl;

import net.ooder.scene.discovery.*;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 能力发现服务实现类
 * 
 * <p>统一能力发现服务的实现，整合多种发现方式，对外提供一致的发现流程接口。</p>
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>管理多个发现提供者</li>
 *   <li>根据发现范围选择合适的提供者</li>
 *   <li>聚合多个提供者的发现结果</li>
 *   <li>缓存发现结果</li>
 * </ul>
 * 
 * <h3>发现源优先级：</h3>
 * <ol>
 *   <li>Local FS (50) - 本地缓存，所有范围</li>
 *   <li>UDP Broadcast (100) - 个人网络、部门分享</li>
 *   <li>mDNS (90) - 个人网络、部门分享</li>
 *   <li>SkillCenter API (80) - 部门分享、公司管理、公共社区</li>
 * </ol>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 */
@Service
public class CapabilityDiscoveryServiceImpl implements CapabilityDiscoveryService {

    /** 发现提供者注册表 */
    private final Map<String, DiscoveryProvider> providers = new ConcurrentHashMap<>();

    /** 当前发现范围 */
    private volatile DiscoveryScope currentScope = DiscoveryScope.PERSONAL;

    /** 场景详情缓存 */
    private final Map<String, SceneDetail> sceneCache = new ConcurrentHashMap<>();

    /** 能力详情缓存 */
    private final Map<String, CapabilityDetail> capabilityCache = new ConcurrentHashMap<>();

    /**
     * 初始化方法
     * 注册默认的发现提供者
     */
    @PostConstruct
    public void init() {
        // 可以在这里注册默认的发现提供者
    }

    /**
     * 销毁方法
     * 清理缓存和停止所有提供者
     */
    @PreDestroy
    public void destroy() {
        providers.values().forEach(DiscoveryProvider::stop);
        providers.clear();
        sceneCache.clear();
        capabilityCache.clear();
    }

    @Override
    public CompletableFuture<SyncResult> syncAllIndexes() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int sceneCount = 0;
                int capabilityCount = 0;
                int skillCount = 0;

                for (DiscoveryProvider provider : getActiveProviders()) {
                    if (provider.isRunning()) {
                        DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, "*");
                        List<DiscoveredItem> scenes = provider.discover(query).get();
                        sceneCount += scenes.size();

                        query = new DiscoveryQuery(DiscoveryType.CAPABILITY, "*");
                        List<DiscoveredItem> capabilities = provider.discover(query).get();
                        capabilityCount += capabilities.size();

                        query = new DiscoveryQuery(DiscoveryType.SKILL, "*");
                        List<DiscoveredItem> skills = provider.discover(query).get();
                        skillCount += skills.size();
                    }
                }

                return SyncResult.success(sceneCount, capabilityCount, skillCount);
            } catch (Exception e) {
                return SyncResult.failure(e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> listScenes(String category) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, category != null ? category : "*");

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        List<DiscoveredItem> items = provider.discover(query).get();
                        results.addAll(items);
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return results.stream()
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> searchScenes(String query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            DiscoveryQuery discoveryQuery = new DiscoveryQuery(DiscoveryType.SCENE, query);

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        List<DiscoveredItem> items = provider.discover(discoveryQuery).get();
                        results.addAll(items);
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return results.stream()
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<SceneDetail> getSceneDetail(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            // 先检查缓存
            SceneDetail cached = sceneCache.get(sceneId);
            if (cached != null) {
                return cached;
            }

            // 从发现提供者获取
            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, sceneId);
                        List<DiscoveredItem> items = provider.discover(query).get();
                        if (!items.isEmpty()) {
                            DiscoveredItem item = items.get(0);
                            SceneDetail detail = new SceneDetail(sceneId, item.getName());
                            detail.setDescription(item.getMetadata() != null ? (String) item.getMetadata().get("description") : null);
                            detail.setCategory(item.getMetadata() != null ? (String) item.getMetadata().get("category") : null);
                            sceneCache.put(sceneId, detail);
                            return detail;
                        }
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> getAvailableSkills(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SKILL, sceneId);

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        List<DiscoveredItem> items = provider.discover(query).get();
                        results.addAll(items);
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return results.stream()
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> listCapabilities(String category) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.CAPABILITY, category != null ? category : "*");

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        List<DiscoveredItem> items = provider.discover(query).get();
                        results.addAll(items);
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return results.stream()
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            DiscoveryQuery discoveryQuery = new DiscoveryQuery(DiscoveryType.CAPABILITY, query);

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        List<DiscoveredItem> items = provider.discover(discoveryQuery).get();
                        results.addAll(items);
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return results.stream()
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId) {
        return CompletableFuture.supplyAsync(() -> {
            // 先检查缓存
            CapabilityDetail cached = capabilityCache.get(capId);
            if (cached != null) {
                return cached;
            }

            // 从发现提供者获取
            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.CAPABILITY, capId);
                        List<DiscoveredItem> items = provider.discover(query).get();
                        if (!items.isEmpty()) {
                            DiscoveredItem item = items.get(0);
                            CapabilityDetail detail = new CapabilityDetail(capId, item.getName());
                            detail.setVersion(item.getMetadata() != null ? (String) item.getMetadata().get("version") : null);
                            detail.setCategory(item.getMetadata() != null ? (String) item.getMetadata().get("category") : null);
                            detail.setDescription(item.getMetadata() != null ? (String) item.getMetadata().get("description") : null);
                            detail.setStatus(item.getMetadata() != null ? (String) item.getMetadata().get("status") : null);
                            capabilityCache.put(capId, detail);
                            return detail;
                        }
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> getAvailableSkillsForCapability(String capId) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SKILL, capId);

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        List<DiscoveredItem> items = provider.discover(query).get();
                        results.addAll(items);
                    }
                } catch (Exception e) {
                    // 继续尝试其他提供者
                }
            }

            return results.stream()
                    .distinct()
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void registerProvider(DiscoveryProvider provider) {
        providers.put(provider.getProviderName(), provider);
    }

    @Override
    public void unregisterProvider(String providerName) {
        DiscoveryProvider provider = providers.remove(providerName);
        if (provider != null) {
            provider.stop();
        }
    }

    @Override
    public void setDiscoveryScope(DiscoveryScope scope) {
        this.currentScope = scope;
    }

    @Override
    public DiscoveryScope getDiscoveryScope() {
        return currentScope;
    }

    // ==================== 私有方法 ====================

    /**
     * 获取当前活跃的发现提供者列表
     * 按优先级排序
     * 
     * @return 排序后的提供者列表
     */
    private List<DiscoveryProvider> getActiveProviders() {
        return providers.values().stream()
                .filter(p -> p.isApplicable(currentScope))
                .sorted(Comparator.comparingInt(DiscoveryProvider::getPriority).reversed())
                .collect(Collectors.toList());
    }
}
