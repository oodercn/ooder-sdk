package net.ooder.scene.discovery.coordinator;

import net.ooder.scene.discovery.CapabilityDTO;
import net.ooder.scene.discovery.adapter.DiscoveryMethod;
import net.ooder.scene.discovery.adapter.SkillDiscovererAdapter;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.skill.model.RichSkill;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *   <li>自动注册发现器（支持 Spring 自动注入）</li>
 * </ul>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>有状态控制：管理发现状态和缓存策略</li>
 *   <li>聚合器：协调多个SDK发现器</li>
 *   <li>转换器：SkillPackage → RichSkill</li>
 *   <li>适配器模式：支持新旧两套接口</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 2.3.0
 */
public class DiscoveryCoordinator {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryCoordinator.class);

    private final Map<String, SkillDiscoverer> discoverers;
    private final Map<String, SkillDiscovererAdapter> adapterDiscoverers;
    private final CacheManager cacheManager;
    private DiscoveryState state;

    public DiscoveryCoordinator(CacheManager cacheManager) {
        this.discoverers = new HashMap<>();
        this.adapterDiscoverers = new HashMap<>();
        this.cacheManager = cacheManager;
        this.state = DiscoveryState.IDLE;
    }

    /**
     * 注册发现器（旧接口）
     *
     * @param source 来源标识（local/github/gitee/udp）
     * @param discoverer SDK发现器
     * @deprecated 使用 {@link #registerAdapter(SkillDiscovererAdapter)} 替代
     */
    @Deprecated
    public void registerDiscoverer(String source, SkillDiscoverer discoverer) {
        discoverers.put(source.toLowerCase(), discoverer);
        logger.info("[DiscoveryCoordinator] Registered legacy discoverer: {}", source);
    }

    /**
     * 注册发现器适配器（新接口）
     *
     * <p>自动从适配器获取发现方法作为来源标识。</p>
     *
     * @param adapter 发现器适配器
     */
    public void registerAdapter(SkillDiscovererAdapter adapter) {
        if (adapter == null) {
            logger.warn("[DiscoveryCoordinator] Cannot register null adapter");
            return;
        }
        String source = adapter.getMethod().getCode();
        adapterDiscoverers.put(source, adapter);
        logger.info("[DiscoveryCoordinator] Registered adapter: {} (priority={})", 
            source, adapter.getPriority());
    }

    /**
     * 批量注册发现器适配器
     *
     * <p>用于 Spring 自动注入所有 SkillDiscovererAdapter Bean。</p>
     *
     * @param adapters 发现器适配器列表
     */
    public void registerAdapters(List<SkillDiscovererAdapter> adapters) {
        if (adapters == null || adapters.isEmpty()) {
            logger.debug("[DiscoveryCoordinator] No adapters to register");
            return;
        }
        
        for (SkillDiscovererAdapter adapter : adapters) {
            registerAdapter(adapter);
        }
        
        logger.info("[DiscoveryCoordinator] Registered {} adapters in total", adapterDiscoverers.size());
    }

    /**
     * 自动注册所有可用的发现器适配器
     *
     * <p>只注册 isAvailable() 返回 true 的适配器。</p>
     *
     * @param adapters 发现器适配器列表
     */
    public void autoRegisterAvailableAdapters(List<SkillDiscovererAdapter> adapters) {
        if (adapters == null || adapters.isEmpty()) {
            logger.debug("[DiscoveryCoordinator] No adapters to auto-register");
            return;
        }

        int registered = 0;
        for (SkillDiscovererAdapter adapter : adapters) {
            if (adapter.isAvailable()) {
                registerAdapter(adapter);
                registered++;
            } else {
                logger.debug("[DiscoveryCoordinator] Skip unavailable adapter: {}", 
                    adapter.getMethod().getCode());
            }
        }

        logger.info("[DiscoveryCoordinator] Auto-registered {}/{} available adapters", 
            registered, adapters.size());
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
            if (shouldUseCache(source)) {
                List<RichSkill> cached = getFromCache(source);
                if (cached != null && !cached.isEmpty()) {
                    logger.debug("[DiscoveryCoordinator] Returning cached results for: {}", source);
                    return cached;
                }
            }

            state = DiscoveryState.DISCOVERING;

            try {
                List<RichSkill> results;

                if ("all".equalsIgnoreCase(source)) {
                    results = discoverFromAllSources();
                } else {
                    results = discoverFromSource(source);
                }

                results = deduplicate(results);
                saveToCache(source, results);
                state = DiscoveryState.IDLE;

                logger.info("[DiscoveryCoordinator] Discovery completed: {} skills from {}", 
                    results.size(), source);
                return results;

            } catch (Exception e) {
                state = DiscoveryState.ERROR;
                logger.error("[DiscoveryCoordinator] Discovery failed for source: {}", source, e);
                throw new RuntimeException("Discovery failed for source: " + source, e);
            }
        });
    }

    /**
     * 发现技能（新接口）
     *
     * <p>使用 DiscoveryRequest 和 DiscoveryResult 进行发现。</p>
     *
     * @param request 发现请求
     * @return 发现结果
     */
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            String source = request.getSource() != null ? request.getSource() : "all";
            result.setSource(source);
            result.setTimestamp(System.currentTimeMillis());

            try {
                SkillDiscovererAdapter adapter = adapterDiscoverers.get(source.toLowerCase());
                if (adapter != null && adapter.isAvailable()) {
                    return adapter.discover(request).get();
                }

                List<RichSkill> richSkills = discover(source).get();
                List<DiscoveryService.SkillInfo> skills = richSkills.stream()
                    .map(this::convertToSkillInfo)
                    .collect(Collectors.toList());

                result.setSkills(skills);
                result.setTotalCount(skills.size());

            } catch (Exception e) {
                logger.error("[DiscoveryCoordinator] Discovery failed: {}", e.getMessage());
                result.setErrorMessage(e.getMessage());
            }

            return result;
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
        clearCache(source);
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
     * 获取单个技能（使用新接口）
     *
     * @param skillId 技能ID
     * @return 技能详情
     */
    public CompletableFuture<CapabilityDTO> discoverOne(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            for (SkillDiscovererAdapter adapter : adapterDiscoverers.values()) {
                if (adapter.isAvailable()) {
                    try {
                        CapabilityDTO dto = adapter.discoverOne(skillId).get();
                        if (dto != null) {
                            return dto;
                        }
                    } catch (Exception e) {
                        logger.debug("[DiscoveryCoordinator] Skill {} not found via {}", 
                            skillId, adapter.getMethod().getCode());
                    }
                }
            }
            return null;
        });
    }

    /**
     * 从指定来源发现
     */
    private List<RichSkill> discoverFromSource(String source) {
        List<RichSkill> results = new ArrayList<>();

        SkillDiscovererAdapter adapter = adapterDiscoverers.get(source.toLowerCase());
        if (adapter != null && adapter.isAvailable()) {
            try {
                DiscoveryRequest request = new DiscoveryRequest();
                request.setSource(source);
                DiscoveryResult result = adapter.discover(request).get();
                
                if (result.getSkills() != null) {
                    for (DiscoveryService.SkillInfo info : result.getSkills()) {
                        results.add(convertToRichSkill(info, source));
                    }
                }
            } catch (Exception e) {
                logger.warn("[DiscoveryCoordinator] Adapter discovery failed for {}: {}", 
                    source, e.getMessage());
            }
        }

        SkillDiscoverer discoverer = discoverers.get(source.toLowerCase());
        if (discoverer != null) {
            try {
                List<SkillPackage> packages = discoverer.discover().get();
                results.addAll(enrichPackages(packages, source));
            } catch (Exception e) {
                logger.warn("[DiscoveryCoordinator] Legacy discovery failed for {}: {}", 
                    source, e.getMessage());
            }
        }

        return results;
    }

    /**
     * 从所有来源发现
     */
    private List<RichSkill> discoverFromAllSources() {
        List<RichSkill> allResults = new ArrayList<>();

        List<SkillDiscovererAdapter> sortedAdapters = adapterDiscoverers.values().stream()
            .filter(SkillDiscovererAdapter::isAvailable)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
            .collect(Collectors.toList());

        for (SkillDiscovererAdapter adapter : sortedAdapters) {
            try {
                DiscoveryRequest request = new DiscoveryRequest();
                request.setSource(adapter.getMethod().getCode());
                DiscoveryResult result = adapter.discover(request).get();

                if (result.getSkills() != null) {
                    for (DiscoveryService.SkillInfo info : result.getSkills()) {
                        allResults.add(convertToRichSkill(info, adapter.getMethod().getCode()));
                    }
                }
            } catch (Exception e) {
                logger.debug("[DiscoveryCoordinator] Adapter {} discovery failed: {}", 
                    adapter.getMethod().getCode(), e.getMessage());
            }
        }

        for (Map.Entry<String, SkillDiscoverer> entry : discoverers.entrySet()) {
            try {
                List<SkillPackage> packages = entry.getValue().discover().get();
                List<RichSkill> enriched = enrichPackages(packages, entry.getKey());
                allResults.addAll(enriched);
            } catch (Exception e) {
                logger.debug("[DiscoveryCoordinator] Legacy discoverer {} failed: {}", 
                    entry.getKey(), e.getMessage());
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
                try {
                    richSkill.setSource(RichSkill.DiscoverySource.valueOf(source.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    richSkill.setSource(RichSkill.DiscoverySource.LOCAL);
                }
                richSkill.setCacheManager(cacheManager);
                return richSkill;
            })
            .collect(Collectors.toList());
    }

    /**
     * 转换 SkillInfo 到 RichSkill
     */
    private RichSkill convertToRichSkill(DiscoveryService.SkillInfo info, String source) {
        SkillPackage pkg = new SkillPackage();
        pkg.setSkillId(info.getSkillId());
        pkg.setName(info.getName());
        pkg.setVersion(info.getVersion());
        pkg.setDescription(info.getDescription());
        pkg.setCategory(info.getCategory());
        pkg.setTags(info.getTags());

        RichSkill richSkill = new RichSkill(pkg);
        try {
            richSkill.setSource(RichSkill.DiscoverySource.valueOf(source.toUpperCase()));
        } catch (IllegalArgumentException e) {
            richSkill.setSource(RichSkill.DiscoverySource.LOCAL);
        }
        richSkill.setCacheManager(cacheManager);
        return richSkill;
    }

    /**
     * 转换 RichSkill 到 SkillInfo
     */
    private DiscoveryService.SkillInfo convertToSkillInfo(RichSkill skill) {
        DiscoveryService.SkillInfo info = new DiscoveryService.SkillInfo();
        info.setSkillId(skill.getSkillId());
        info.setName(skill.getName());
        info.setVersion(skill.getVersion());
        info.setDescription(skill.getDescription());
        info.setCategory(skill.getCategory() != null ? skill.getCategory().getCode() : null);
        info.setTags(skill.getRawPackage() != null ? skill.getRawPackage().getTags() : null);
        info.setSource(skill.getSource() != null ? skill.getSource().name().toLowerCase() : "local");
        return info;
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
        return cacheManager != null;
    }

    /**
     * 从缓存获取
     */
    private List<RichSkill> getFromCache(String source) {
        if (cacheManager == null) {
            return null;
        }
        return null;
    }

    /**
     * 保存到缓存
     */
    private void saveToCache(String source, List<RichSkill> skills) {
        if (cacheManager == null || skills == null || skills.isEmpty()) {
            return;
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache(String source) {
        if (cacheManager == null) {
            return;
        }
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
     * 获取已注册的发现器（旧接口）
     */
    public Map<String, SkillDiscoverer> getDiscoverers() {
        return new HashMap<>(discoverers);
    }

    /**
     * 获取已注册的适配器（新接口）
     */
    public Map<String, SkillDiscovererAdapter> getAdapterDiscoverers() {
        return new HashMap<>(adapterDiscoverers);
    }

    /**
     * 获取所有可用的发现方法
     */
    public List<DiscoveryMethod> getAvailableMethods() {
        List<DiscoveryMethod> methods = new ArrayList<>();
        
        for (Map.Entry<String, SkillDiscovererAdapter> entry : adapterDiscoverers.entrySet()) {
            if (entry.getValue().isAvailable()) {
                methods.add(entry.getValue().getMethod());
            }
        }
        
        return methods;
    }

    /**
     * 发现状态枚举
     */
    public enum DiscoveryState {
        IDLE,
        DISCOVERING,
        ERROR
    }
}
