package net.ooder.sdk.dependency;

import java.util.*;

/**
 * 依赖图
 *
 * <p>管理所有依赖节点及其关系，提供拓扑排序等功能。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class DependencyGraph {

    /**
     * 节点映射表
     */
    private final Map<String, DependencyNode> nodes;

    public DependencyGraph() {
        this.nodes = new LinkedHashMap<>();
    }

    // ==================== 节点管理 ====================

    /**
     * 添加节点
     *
     * @param node 依赖节点
     * @return 添加的节点（如果已存在则返回已有节点）
     */
    public DependencyNode addNode(DependencyNode node) {
        if (node == null) {
            return null;
        }

        DependencyNode existing = nodes.get(node.getSkillId());
        if (existing != null) {
            return existing;
        }

        nodes.put(node.getSkillId(), node);
        return node;
    }

    /**
     * 获取节点
     *
     * @param skillId Skill标识
     * @return 节点或null
     */
    public DependencyNode getNode(String skillId) {
        return nodes.get(skillId);
    }

    /**
     * 检查是否存在节点
     *
     * @param skillId Skill标识
     * @return true如果存在
     */
    public boolean hasNode(String skillId) {
        return nodes.containsKey(skillId);
    }

    /**
     * 移除节点
     *
     * @param skillId Skill标识
     * @return 被移除的节点或null
     */
    public DependencyNode removeNode(String skillId) {
        DependencyNode node = nodes.remove(skillId);
        if (node != null) {
            // 清理依赖关系
            for (DependencyNode dep : new ArrayList<>(node.getResolvedDependencies())) {
                node.removeResolvedDependency(dep);
            }
            for (DependencyNode dependent : new ArrayList<>(node.getDependents())) {
                dependent.removeResolvedDependency(node);
            }
        }
        return node;
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    public Collection<DependencyNode> getAllNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * 获取所有节点ID
     *
     * @return ID列表
     */
    public Set<String> getAllNodeIds() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    // ==================== 图操作 ====================

    /**
     * 添加边（依赖关系）
     *
     * @param from 依赖方
     * @param to   被依赖方
     */
    public void addEdge(String from, String to) {
        DependencyNode fromNode = nodes.get(from);
        DependencyNode toNode = nodes.get(to);

        if (fromNode != null && toNode != null) {
            fromNode.addResolvedDependency(toNode);
        }
    }

    /**
     * 移除边
     *
     * @param from 依赖方
     * @param to   被依赖方
     */
    public void removeEdge(String from, String to) {
        DependencyNode fromNode = nodes.get(from);
        DependencyNode toNode = nodes.get(to);

        if (fromNode != null && toNode != null) {
            fromNode.removeResolvedDependency(toNode);
        }
    }

    // ==================== 拓扑排序 ====================

    /**
     * 执行拓扑排序
     *
     * <p>返回按依赖顺序排列的节点列表（被依赖的在前）。</p>
     *
     * @return 排序后的节点列表
     * @throws CircularDependencyException 如果存在循环依赖
     */
    public List<DependencyNode> topologicalSort() throws CircularDependencyException {
        List<DependencyNode> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (DependencyNode node : nodes.values()) {
            if (!visited.contains(node.getSkillId())) {
                visit(node, visited, visiting, result);
            }
        }

        return result;
    }

    private void visit(DependencyNode node, Set<String> visited, Set<String> visiting,
                       List<DependencyNode> result) throws CircularDependencyException {
        String skillId = node.getSkillId();

        if (visiting.contains(skillId)) {
            // 发现循环依赖
            List<String> cycle = findCyclePath(skillId);
            throw new CircularDependencyException("Circular dependency detected: " + cycle, cycle);
        }

        if (visited.contains(skillId)) {
            return;
        }

        visiting.add(skillId);

        // 先访问依赖的节点
        for (DependencyNode dep : node.getResolvedDependencies()) {
            visit(dep, visited, visiting, result);
        }

        visiting.remove(skillId);
        visited.add(skillId);
        result.add(node);
    }

    // ==================== 循环依赖检测 ====================

    /**
     * 检查是否存在循环依赖
     *
     * @return true如果存在
     */
    public boolean hasCircularDependency() {
        for (DependencyNode node : nodes.values()) {
            if (node.hasCircularDependency(node.getSkillId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找所有循环依赖
     *
     * @return 循环依赖列表
     */
    public List<List<String>> findAllCycles() {
        List<List<String>> cycles = new ArrayList<>();
        Set<String> checked = new HashSet<>();

        for (DependencyNode node : nodes.values()) {
            if (!checked.contains(node.getSkillId())) {
                List<String> cycle = findCycleStartingFrom(node);
                if (!cycle.isEmpty()) {
                    cycles.add(cycle);
                    checked.addAll(cycle);
                }
            }
        }

        return cycles;
    }

    private List<String> findCycleStartingFrom(DependencyNode start) {
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        if (findCycleDFS(start, start.getSkillId(), path, visited)) {
            return path;
        }

        return Collections.emptyList();
    }

    private boolean findCycleDFS(DependencyNode node, String targetId,
                                  List<String> path, Set<String> visited) {
        path.add(node.getSkillId());

        if (node.getSkillId().equals(targetId) && path.size() > 1) {
            return true;
        }

        if (visited.contains(node.getSkillId())) {
            path.remove(path.size() - 1);
            return false;
        }

        visited.add(node.getSkillId());

        for (DependencyNode dep : node.getResolvedDependencies()) {
            if (findCycleDFS(dep, targetId, path, visited)) {
                return true;
            }
        }

        path.remove(path.size() - 1);
        visited.remove(node.getSkillId());
        return false;
    }

    private List<String> findCyclePath(String startId) {
        DependencyNode start = nodes.get(startId);
        if (start == null) {
            return Collections.singletonList(startId);
        }

        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        if (findCycleDFS(start, startId, path, visited)) {
            return path;
        }

        return Collections.singletonList(startId);
    }

    // ==================== 查询方法 ====================

    /**
     * 获取根节点（没有依赖的节点）
     *
     * @return 根节点列表
     */
    public List<DependencyNode> getRootNodes() {
        List<DependencyNode> roots = new ArrayList<>();
        for (DependencyNode node : nodes.values()) {
            if (node.getResolvedDependencies().isEmpty()) {
                roots.add(node);
            }
        }
        return roots;
    }

    /**
     * 获取叶子节点（没有被依赖的节点）
     *
     * @return 叶子节点列表
     */
    public List<DependencyNode> getLeafNodes() {
        List<DependencyNode> leaves = new ArrayList<>();
        for (DependencyNode node : nodes.values()) {
            if (node.getDependents().isEmpty()) {
                leaves.add(node);
            }
        }
        return leaves;
    }

    /**
     * 获取节点的所有依赖（递归）
     *
     * @param skillId Skill标识
     * @return 所有依赖的Skill ID
     */
    public Set<String> getAllDependencies(String skillId) {
        Set<String> allDeps = new HashSet<>();
        DependencyNode node = nodes.get(skillId);
        if (node != null) {
            collectDependencies(node, allDeps, new HashSet<>());
        }
        return allDeps;
    }

    private void collectDependencies(DependencyNode node, Set<String> result, Set<String> visited) {
        if (visited.contains(node.getSkillId())) {
            return;
        }
        visited.add(node.getSkillId());

        for (DependencyNode dep : node.getResolvedDependencies()) {
            result.add(dep.getSkillId());
            collectDependencies(dep, result, visited);
        }
    }

    /**
     * 获取节点的所有反向依赖（递归）
     *
     * @param skillId Skill标识
     * @return 所有反向依赖的Skill ID
     */
    public Set<String> getAllDependents(String skillId) {
        Set<String> allDependents = new HashSet<>();
        DependencyNode node = nodes.get(skillId);
        if (node != null) {
            collectDependents(node, allDependents, new HashSet<>());
        }
        return allDependents;
    }

    private void collectDependents(DependencyNode node, Set<String> result, Set<String> visited) {
        if (visited.contains(node.getSkillId())) {
            return;
        }
        visited.add(node.getSkillId());

        for (DependencyNode dependent : node.getDependents()) {
            result.add(dependent.getSkillId());
            collectDependents(dependent, result, visited);
        }
    }

    /**
     * 计算图的深度
     *
     * @return 最大深度
     */
    public int calculateDepth() {
        int maxDepth = 0;
        for (DependencyNode node : nodes.values()) {
            int depth = calculateNodeDepth(node, new HashMap<>());
            maxDepth = Math.max(maxDepth, depth);
        }
        return maxDepth;
    }

    private int calculateNodeDepth(DependencyNode node, Map<String, Integer> memo) {
        String skillId = node.getSkillId();
        if (memo.containsKey(skillId)) {
            return memo.get(skillId);
        }

        int maxDepDepth = 0;
        for (DependencyNode dep : node.getResolvedDependencies()) {
            int depDepth = calculateNodeDepth(dep, memo);
            maxDepDepth = Math.max(maxDepDepth, depDepth);
        }

        int depth = maxDepDepth + 1;
        memo.put(skillId, depth);
        return depth;
    }

    // ==================== 统计信息 ====================

    /**
     * 获取节点数量
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * 获取边数量（依赖关系数）
     */
    public int getEdgeCount() {
        int count = 0;
        for (DependencyNode node : nodes.values()) {
            count += node.getResolvedDependencies().size();
        }
        return count;
    }

    /**
     * 清空图
     */
    public void clear() {
        nodes.clear();
    }

    @Override
    public String toString() {
        return "DependencyGraph{" +
                "nodes=" + nodes.size() +
                ", edges=" + getEdgeCount() +
                '}';
    }
}
