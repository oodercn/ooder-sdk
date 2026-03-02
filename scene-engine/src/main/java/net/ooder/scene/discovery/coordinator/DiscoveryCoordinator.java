package net.ooder.scene.discovery.coordinator;

import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.skill.model.RichSkill;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillPackage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 发现协调器
 *
 * <p>SceneEngine层核心组件，负责：</p>
 * <ul>
 *   <li>控制缓存策略（何时使用缓存、何时刷新）</li>
 *   <li>聚合多个SDK发现器的结果</li>
 *   <li>将贫血模型转换为充血模型</li>
 *   <li>管理发现状态</li>
 * </ul>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>有状态控制：管理发现状态和缓存策略</li>
 *   <li>聚合器：协调多个SDK发现器</li>
 *   <li>转换器：SkillPackage → RichSkill</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class DiscoveryCoordinator {

    private final Map<String, SkillDiscoverer> discoverers;
    private final CacheManager cacheManager;
    private DiscoveryState state;

    public DiscoveryCoordinator(CacheManager cacheManager) {
        this.discoverers = new HashMap<>();
        this.cacheManager = cacheManager;
        this.state = DiscoveryState.IDLE;
    }

    /**
     * 注册发现器
     *
     * @param source 来源标识（local/github/gitee/udp）
     * @param discoverer SDK发现器
     */
    public void registerDiscoverer(String source, SkillDiscoverer discoverer) {
        discoverers.put(source.toLowerCase(), discoverer);
    }

    /**
     * 发现Skill（统一入口）
     *
     * <p>控制逻辑：</p>
     * <ol>
     *   <li>检查缓存是否有效</li>
     *   <li>缓存有效：直接返回缓存数据</li>
     *   <li>缓存无效：调用SDK发现器 → 转换模型 → 保存缓存</li>
     * </ol>
     *
     * @param source 来源（local/github/gitee/udp/all）
     * @return RichSkill列表
     */
    public CompletableFuture<List<RichSkill>> discover(String source) {
        return CompletableFuture.supplyAsync(() -> {
            // 状态控制：检查缓存
            if (shouldUseCache(source)) {
                List<RichSkill> cached = getFromCache(source);
                if (cached != null && !cached.isEmpty()) {
                    return cached;
                }
            }

            // 状态控制：开始发现
            state = DiscoveryState.DISCOVERING;

            try {
                List<RichSkill> results;

                if ("all".equalsIgnoreCase(source)) {
                    // 聚合所有来源
                    results = discoverFromAllSources();
                } else {
                    // 从指定来源发现
                    results = discoverFromSource(source);
                }

                // 去重（按skillId+version）
                results = deduplicate(results);

                // 保存到缓存
                saveToCache(source, results);

                // 更新状态
                state = DiscoveryState.IDLE;

                return results;

            } catch (Exception e) {
                state = DiscoveryState.ERROR;
                throw new RuntimeException("Discovery failed for source: " + source, e);
            }
        });
    }

    /**
     * 搜索Skill
     *
     * @param keyword 关键词
     * @return 匹配的RichSkill列表
     */
    public CompletableFuture<List<RichSkill>> search(String keyword) {
        return discover("all")
            .thenApply(skills -> skills.stream()
                .filter(s -> matchesKeyword(s, keyword))
                .collect(Collectors.toList()));
    }

    /**
     * 刷新缓存
     *
     * @param source 来源
     * @return 刷新后的RichSkill列表
     */
    public CompletableFuture<List<RichSkill>> refresh(String source) {
        // 清除缓存
        clearCache(source);
        // 重新发现
        return discover(source);
    }

    /**
     * 获取Skill详情
     *
     * @param skillId Skill ID
     * @return RichSkill
     */
    public CompletableFuture<RichSkill> getSkillDetail(String skillId) {
        return discover("all")
            .thenApply(skills -> skills.stream()
                .filter(s -> s.getSkillId().equals(skillId))
                .findFirst()
                .orElse(null));
    }

    /**
     * 从指定来源发现
     */
    private List<RichSkill> discoverFromSource(String source) {
        SkillDiscoverer discoverer = discoverers.get(source.toLowerCase());
        if (discoverer == null) {
            return new ArrayList<>();
        }

        try {
            // 调用SDK发现器（贫血模型）
            List<SkillPackage> packages = discoverer.discover().get();

            // 转换为充血模型
            return enrichPackages(packages, source);

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 从所有来源发现
     */
    private List<RichSkill> discoverFromAllSources() {
        List<RichSkill> allResults = new ArrayList<>();

        for (Map.Entry<String, SkillDiscoverer> entry : discoverers.entrySet()) {
            try {
                List<SkillPackage> packages = entry.getValue().discover().get();
                List<RichSkill> enriched = enrichPackages(packages, entry.getKey());
                allResults.addAll(enriched);
            } catch (Exception e) {
                // 忽略单个来源的错误
            }
        }

        return allResults;
    }

    /**
     * 将贫血模型转换为充血模型
     */
    private List<RichSkill> enrichPackages(List<SkillPackage> packages, String source) {
        if (packages == null) {
            return new ArrayList<>();
        }

        return packages.stream()
            .map(pkg -> {
                RichSkill richSkill = new RichSkill(pkg);
                richSkill.setSource(RichSkill.DiscoverySource.valueOf(source.toUpperCase()));
                richSkill.setCacheManager(cacheManager);
                return richSkill;
            })
            .collect(Collectors.toList());
    }

    /**
     * 去重（按skillId+version）
     */
    private List<RichSkill> deduplicate(List<RichSkill> skills) {
        Map<String, RichSkill> uniqueMap = new LinkedHashMap<>();

        for (RichSkill skill : skills) {
            String key = skill.getSkillId() + "@" + skill.getVersion();
            if (!uniqueMap.containsKey(key)) {
                uniqueMap.put(key, skill);
            }
        }

        return new ArrayList<>(uniqueMap.values());
    }

    /**
     * 判断是否使用缓存
     */
    private boolean shouldUseCache(String source) {
        // 缓存策略：总是先检查缓存
        return cacheManager != null;
    }

    /**
     * 从缓存获取
     */
    private List<RichSkill> getFromCache(String source) {
        if (cacheManager == null) {
            return null;
        }
        // 简化实现
        return null;
    }

    /**
     * 保存到缓存
     */
    private void saveToCache(String source, List<RichSkill> skills) {
        if (cacheManager == null || skills == null || skills.isEmpty()) {
            return;
        }
        // 简化实现
    }

    /**
     * 清除缓存
     */
    private void clearCache(String source) {
        if (cacheManager == null) {
            return;
        }
        // 简化实现
    }

    /**
     * 关键词匹配
     */
    private boolean matchesKeyword(RichSkill skill, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        String lowerKeyword = keyword.toLowerCase();
        return skill.getSkillId().toLowerCase().contains(lowerKeyword)
            || skill.getName().toLowerCase().contains(lowerKeyword)
            || (skill.getDescription() != null && skill.getDescription().toLowerCase().contains(lowerKeyword));
    }

    /**
     * 获取当前状态
     */
    public DiscoveryState getState() {
        return state;
    }

    /**
     * 获取已注册的发现器
     */
    public Map<String, SkillDiscoverer> getDiscoverers() {
        return new HashMap<>(discoverers);
    }

    /**
     * 发现状态枚举
     */
    public enum DiscoveryState {
        IDLE,        // 空闲
        DISCOVERING, // 发现中
        ERROR        // 错误
    }
}
