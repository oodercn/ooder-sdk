package net.ooder.scene.discovery.impl;

import net.ooder.scene.discovery.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CapabilityDiscoveryServiceImpl implements CapabilityDiscoveryService {
    private Map<String, DiscoveryProvider> providers;
    private DiscoveryScope currentScope;
    private Map<String, SceneDetail> sceneCache;
    private Map<String, CapabilityDetail> capabilityCache;

    public CapabilityDiscoveryServiceImpl() {
        this.providers = new ConcurrentHashMap<>();
        this.currentScope = DiscoveryScope.PERSONAL;
        this.sceneCache = new ConcurrentHashMap<>();
        this.capabilityCache = new ConcurrentHashMap<>();
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
            if (sceneCache.containsKey(sceneId)) {
                return sceneCache.get(sceneId);
            }

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, sceneId);
                        List<DiscoveredItem> items = provider.discover(query).get();
                        if (!items.isEmpty()) {
                            DiscoveredItem item = items.get(0);
                            SceneDetail detail = new SceneDetail(sceneId, item.getName());
                            detail.setDescription((String) item.getMetadata("description"));
                            detail.setCategory((String) item.getMetadata("category"));
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
            if (capabilityCache.containsKey(capId)) {
                return capabilityCache.get(capId);
            }

            for (DiscoveryProvider provider : getActiveProviders()) {
                try {
                    if (provider.isRunning()) {
                        DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.CAPABILITY, capId);
                        List<DiscoveredItem> items = provider.discover(query).get();
                        if (!items.isEmpty()) {
                            DiscoveredItem item = items.get(0);
                            CapabilityDetail detail = new CapabilityDetail(capId, item.getName());
                            detail.setVersion((String) item.getMetadata("version"));
                            detail.setCategory((String) item.getMetadata("category"));
                            detail.setDescription((String) item.getMetadata("description"));
                            detail.setStatus((String) item.getMetadata("status"));
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
        providers.remove(providerName);
    }

    @Override
    public void setDiscoveryScope(DiscoveryScope scope) {
        this.currentScope = scope;
    }

    @Override
    public DiscoveryScope getDiscoveryScope() {
        return currentScope;
    }

    private List<DiscoveryProvider> getActiveProviders() {
        return providers.values().stream()
                .filter(p -> p.isApplicable(currentScope))
                .sorted(Comparator.comparingInt(DiscoveryProvider::getPriority).reversed())
                .collect(Collectors.toList());
    }
}
