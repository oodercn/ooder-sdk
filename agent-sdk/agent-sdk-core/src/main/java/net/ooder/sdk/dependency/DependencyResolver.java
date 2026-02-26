package net.ooder.sdk.dependency;

import net.ooder.sdk.discovery.SkillDiscoveryService;
import net.ooder.sdk.plugin.SkillDependency;
import net.ooder.sdk.plugin.SkillMetadata;
import net.ooder.sdk.version.VersionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 依赖解析器
 *
 * <p>解析Skill的依赖关系，构建依赖图，检测循环依赖。</p>
 *
 * <p>采用三级依赖解析策略:</p>
 * <ol>
 *   <li>显式声明优先 - 优先使用显式声明的版本</li>
 *   <li>最新版本优先 - 选择满足条件的最新版本</li>
 *   <li>就近原则 - 选择距离最近的版本</li>
 * </ol>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class DependencyResolver {

    private static final Logger log = LoggerFactory.getLogger(DependencyResolver.class);

    /**
     * Skill发现服务
     */
    private final SkillDiscoveryService discoveryService;

    /**
     * 版本管理器
     */
    private final VersionManager versionManager;

    /**
     * 已解析的依赖图
     */
    private final DependencyGraph dependencyGraph;

    /**
     * 解析选项
     */
    private ResolutionOptions options;

    public DependencyResolver(SkillDiscoveryService discoveryService, VersionManager versionManager) {
        this.discoveryService = discoveryService;
        this.versionManager = versionManager;
        this.dependencyGraph = new DependencyGraph();
        this.options = new ResolutionOptions();
    }

    // ==================== 核心解析方法 ====================

    /**
     * 解析Skill的依赖
     *
     * @param metadata Skill元数据
     * @return 解析结果
     */
    public ResolutionResult resolve(SkillMetadata metadata) {
        if (metadata == null || metadata.getId() == null) {
            return ResolutionResult.failure("Invalid metadata");
        }

        log.info("Resolving dependencies for skill: {}", metadata.getId());

        // 清空之前的解析结果
        dependencyGraph.clear();

        try {
            // 创建根节点
            DependencyNode rootNode = new DependencyNode(metadata);
            rootNode.setDepth(0);
            dependencyGraph.addNode(rootNode);

            // 递归解析依赖
            resolveDependencies(rootNode, 1, new HashSet<>());

            // 检查循环依赖
            if (dependencyGraph.hasCircularDependency()) {
                List<List<String>> cycles = dependencyGraph.findAllCycles();
                log.error("Circular dependencies detected: {}", cycles);
                return ResolutionResult.failure("Circular dependencies detected: " + cycles);
            }

            // 执行拓扑排序
            List<DependencyNode> sortedNodes = dependencyGraph.topologicalSort();

            // 构建解析结果
            ResolutionResult result = buildResolutionResult(sortedNodes, metadata.getId());

            log.info("Dependency resolution completed for skill: {}. Total dependencies: {}",
                    metadata.getId(), result.getResolvedDependencies().size());

            return result;

        } catch (CircularDependencyException e) {
            log.error("Circular dependency detected: {}", e.getFormattedCycle(), e);
            return ResolutionResult.failure("Circular dependency: " + e.getFormattedCycle());
        } catch (Exception e) {
            log.error("Failed to resolve dependencies for skill: {}", metadata.getId(), e);
            return ResolutionResult.failure("Resolution failed: " + e.getMessage());
        }
    }

    /**
     * 递归解析依赖
     *
     * @param node 当前节点
     * @param depth 当前深度
     * @param resolving 正在解析的节点集合（用于循环检测）
     */
    private void resolveDependencies(DependencyNode node, int depth, Set<String> resolving) {
        String skillId = node.getSkillId();

        if (resolving.contains(skillId)) {
            log.warn("Potential circular dependency detected at: {}", skillId);
            return;
        }

        if (depth > options.getMaxDepth()) {
            log.warn("Max dependency depth reached for skill: {}", skillId);
            return;
        }

        node.setState(DependencyNode.NodeState.RESOLVING);
        resolving.add(skillId);

        for (SkillDependency declaredDep : node.getDeclaredDependencies()) {
            try {
                resolveDependency(node, declaredDep, depth, resolving);
            } catch (Exception e) {
                log.error("Failed to resolve dependency: {} for skill: {}",
                        declaredDep.getId(), skillId, e);

                if (!declaredDep.isOptional() && options.isFailOnMissing()) {
                    throw new RuntimeException("Required dependency not found: " + declaredDep.getId(), e);
                }
            }
        }

        node.setState(DependencyNode.NodeState.RESOLVED);
        node.setDepth(depth);
        resolving.remove(skillId);
    }

    /**
     * 解析单个依赖
     */
    private void resolveDependency(DependencyNode parent, SkillDependency dependency,
                                   int depth, Set<String> resolving) {
        String depId = dependency.getId();

        // 检查是否已存在
        DependencyNode existingNode = dependencyGraph.getNode(depId);
        if (existingNode != null) {
            // 检查版本兼容性
            if (checkVersionCompatibility(existingNode, dependency)) {
                parent.addResolvedDependency(existingNode);
                log.debug("Reusing existing node for dependency: {}", depId);
                return;
            }
        }

        // 尝试发现依赖
        SkillMetadata depMetadata = findDependencyMetadata(depId, dependency);

        if (depMetadata == null) {
            if (dependency.isOptional()) {
                log.debug("Optional dependency not found: {}", depId);
                return;
            }
            throw new RuntimeException("Dependency not found: " + depId);
        }

        // 创建依赖节点
        DependencyNode depNode = new DependencyNode(depMetadata);
        depNode.setDepth(depth);
        dependencyGraph.addNode(depNode);
        parent.addResolvedDependency(depNode);

        log.debug("Resolved dependency: {} (version: {}) for skill: {}",
                depId, depMetadata.getVersion(), parent.getSkillId());

        // 递归解析依赖的依赖
        resolveDependencies(depNode, depth + 1, resolving);
    }

    /**
     * 查找依赖的元数据
     */
    private SkillMetadata findDependencyMetadata(String depId, SkillDependency dependency) {
        // 1. 从已发现的Skill中查找
        SkillDiscoveryService.DiscoveredSkill discovered = discoveryService.getDiscoveredSkill(depId);
        if (discovered != null && discovered.getMetadata() != null) {
            SkillMetadata metadata = discovered.getMetadata();
            if (dependency.matchesVersion(metadata.getVersion())) {
                return metadata;
            }
        }

        // 2. 从版本管理器中查找
        if (versionManager.hasVersion(depId)) {
            String version = versionManager.getVersion(depId);
            if (dependency.matchesVersion(version)) {
                // 创建简化元数据
                SkillMetadata metadata = new SkillMetadata();
                metadata.setId(depId);
                metadata.setVersion(version);
                return metadata;
            }
        }

        return null;
    }

    /**
     * 检查版本兼容性
     */
    private boolean checkVersionCompatibility(DependencyNode node, SkillDependency dependency) {
        if (node.getMetadata() == null) {
            return true; // 无法检查，假设兼容
        }

        String actualVersion = node.getMetadata().getVersion();
        return dependency.matchesVersion(actualVersion);
    }

    /**
     * 构建解析结果
     */
    private ResolutionResult buildResolutionResult(List<DependencyNode> sortedNodes, String rootId) {
        ResolutionResult result = new ResolutionResult();
        result.setSuccess(true);
        result.setRootSkillId(rootId);

        // 排除根节点，只包含依赖
        for (DependencyNode node : sortedNodes) {
            if (!node.getSkillId().equals(rootId)) {
                result.addResolvedDependency(node.getSkillId(), node.getMetadata());
            }
        }

        result.setDependencyGraph(dependencyGraph);
        result.setResolutionOrder(extractSkillIds(sortedNodes, rootId));

        return result;
    }

    private List<String> extractSkillIds(List<DependencyNode> nodes, String excludeId) {
        List<String> ids = new ArrayList<>();
        for (DependencyNode node : nodes) {
            if (!node.getSkillId().equals(excludeId)) {
                ids.add(node.getSkillId());
            }
        }
        return ids;
    }

    // ==================== 查询方法 ====================

    /**
     * 获取依赖图
     */
    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    /**
     * 检查是否存在循环依赖
     */
    public boolean hasCircularDependency() {
        return dependencyGraph.hasCircularDependency();
    }

    /**
     * 查找所有循环依赖
     */
    public List<List<String>> findAllCycles() {
        return dependencyGraph.findAllCycles();
    }

    // ==================== 配置方法 ====================

    /**
     * 设置解析选项
     */
    public void setOptions(ResolutionOptions options) {
        this.options = options != null ? options : new ResolutionOptions();
    }

    /**
     * 获取解析选项
     */
    public ResolutionOptions getOptions() {
        return options;
    }

    // ==================== 内部类 ====================

    /**
     * 解析选项
     */
    public static class ResolutionOptions {
        private int maxDepth = 10;
        private boolean failOnMissing = true;
        private boolean includeOptional = true;

        public int getMaxDepth() {
            return maxDepth;
        }

        public void setMaxDepth(int maxDepth) {
            this.maxDepth = Math.max(1, maxDepth);
        }

        public boolean isFailOnMissing() {
            return failOnMissing;
        }

        public void setFailOnMissing(boolean failOnMissing) {
            this.failOnMissing = failOnMissing;
        }

        public boolean isIncludeOptional() {
            return includeOptional;
        }

        public void setIncludeOptional(boolean includeOptional) {
            this.includeOptional = includeOptional;
        }
    }

    /**
     * 解析结果
     */
    public static class ResolutionResult {
        private boolean success;
        private String message;
        private String rootSkillId;
        private final Map<String, SkillMetadata> resolvedDependencies;
        private List<String> resolutionOrder;
        private DependencyGraph dependencyGraph;

        public ResolutionResult() {
            this.resolvedDependencies = new LinkedHashMap<>();
        }

        public static ResolutionResult success(String rootSkillId, Map<String, SkillMetadata> dependencies) {
            ResolutionResult result = new ResolutionResult();
            result.setSuccess(true);
            result.setRootSkillId(rootSkillId);
            result.resolvedDependencies.putAll(dependencies);
            return result;
        }

        public static ResolutionResult failure(String message) {
            ResolutionResult result = new ResolutionResult();
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }

        public void addResolvedDependency(String skillId, SkillMetadata metadata) {
            resolvedDependencies.put(skillId, metadata);
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRootSkillId() {
            return rootSkillId;
        }

        public void setRootSkillId(String rootSkillId) {
            this.rootSkillId = rootSkillId;
        }

        public Map<String, SkillMetadata> getResolvedDependencies() {
            return Collections.unmodifiableMap(resolvedDependencies);
        }

        public List<String> getResolutionOrder() {
            return resolutionOrder;
        }

        public void setResolutionOrder(List<String> resolutionOrder) {
            this.resolutionOrder = resolutionOrder;
        }

        public DependencyGraph getDependencyGraph() {
            return dependencyGraph;
        }

        public void setDependencyGraph(DependencyGraph dependencyGraph) {
            this.dependencyGraph = dependencyGraph;
        }

        @Override
        public String toString() {
            return "ResolutionResult{" +
                    "success=" + success +
                    ", rootSkillId='" + rootSkillId + '\'' +
                    ", dependencies=" + resolvedDependencies.size() +
                    '}';
        }
    }
}
