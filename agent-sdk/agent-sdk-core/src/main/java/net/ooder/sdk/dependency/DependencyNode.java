package net.ooder.sdk.dependency;

import net.ooder.sdk.plugin.SkillDependency;
import net.ooder.sdk.plugin.SkillMetadata;

import java.util.*;

/**
 * 依赖节点
 *
 * <p>表示依赖图中的一个节点，包含Skill及其依赖关系。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class DependencyNode {

    /**
     * Skill标识
     */
    private final String skillId;

    /**
     * Skill元数据
     */
    private SkillMetadata metadata;

    /**
     * 依赖声明
     */
    private final List<SkillDependency> declaredDependencies;

    /**
     * 实际解析出的依赖节点
     */
    private final List<DependencyNode> resolvedDependencies;

    /**
     * 依赖此节点的节点（反向依赖）
     */
    private final List<DependencyNode> dependents;

    /**
     * 节点状态
     */
    private NodeState state;

    /**
     * 解析深度
     */
    private int depth;

    public DependencyNode(String skillId) {
        this.skillId = skillId;
        this.declaredDependencies = new ArrayList<>();
        this.resolvedDependencies = new ArrayList<>();
        this.dependents = new ArrayList<>();
        this.state = NodeState.UNRESOLVED;
        this.depth = 0;
    }

    public DependencyNode(SkillMetadata metadata) {
        this(metadata.getId());
        this.metadata = metadata;
        if (metadata.getDependencies() != null) {
            this.declaredDependencies.addAll(metadata.getDependencies());
        }
    }

    // ==================== 依赖管理 ====================

    /**
     * 添加声明的依赖
     *
     * @param dependency 依赖声明
     */
    public void addDeclaredDependency(SkillDependency dependency) {
        if (dependency != null && !declaredDependencies.contains(dependency)) {
            declaredDependencies.add(dependency);
        }
    }

    /**
     * 添加解析后的依赖节点
     *
     * @param node 依赖节点
     */
    public void addResolvedDependency(DependencyNode node) {
        if (node != null && !resolvedDependencies.contains(node)) {
            resolvedDependencies.add(node);
            node.addDependent(this);
        }
    }

    /**
     * 添加反向依赖
     *
     * @param node 依赖此节点的节点
     */
    private void addDependent(DependencyNode node) {
        if (node != null && !dependents.contains(node)) {
            dependents.add(node);
        }
    }

    /**
     * 移除解析后的依赖
     *
     * @param node 依赖节点
     */
    public void removeResolvedDependency(DependencyNode node) {
        if (node != null) {
            resolvedDependencies.remove(node);
            node.dependents.remove(this);
        }
    }

    // ==================== 循环依赖检测 ====================

    /**
     * 检查是否存在循环依赖
     *
     * @param targetId 目标Skill ID
     * @return true如果存在循环依赖
     */
    public boolean hasCircularDependency(String targetId) {
        return hasCircularDependency(targetId, new HashSet<>());
    }

    private boolean hasCircularDependency(String targetId, Set<String> visited) {
        if (skillId.equals(targetId)) {
            return true;
        }

        if (visited.contains(skillId)) {
            return false;
        }

        visited.add(skillId);

        for (DependencyNode dependent : dependents) {
            if (dependent.hasCircularDependency(targetId, visited)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取循环依赖路径
     *
     * @param targetId 目标Skill ID
     * @return 循环路径，如果不存在返回空列表
     */
    public List<String> getCircularPath(String targetId) {
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        if (findCircularPath(targetId, path, visited)) {
            return path;
        }
        return Collections.emptyList();
    }

    private boolean findCircularPath(String targetId, List<String> path, Set<String> visited) {
        path.add(skillId);

        if (skillId.equals(targetId) && path.size() > 1) {
            return true;
        }

        if (visited.contains(skillId)) {
            path.remove(path.size() - 1);
            return false;
        }

        visited.add(skillId);

        for (DependencyNode dependent : dependents) {
            if (dependent.findCircularPath(targetId, path, visited)) {
                return true;
            }
        }

        path.remove(path.size() - 1);
        visited.remove(skillId);
        return false;
    }

    // ==================== Getters ====================

    public String getSkillId() {
        return skillId;
    }

    public SkillMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SkillMetadata metadata) {
        this.metadata = metadata;
    }

    public List<SkillDependency> getDeclaredDependencies() {
        return Collections.unmodifiableList(declaredDependencies);
    }

    public List<DependencyNode> getResolvedDependencies() {
        return Collections.unmodifiableList(resolvedDependencies);
    }

    public List<DependencyNode> getDependents() {
        return Collections.unmodifiableList(dependents);
    }

    public NodeState getState() {
        return state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * 获取依赖数量
     */
    public int getDependencyCount() {
        return resolvedDependencies.size();
    }

    /**
     * 获取反向依赖数量
     */
    public int getDependentCount() {
        return dependents.size();
    }

    @Override
    public String toString() {
        return "DependencyNode{" +
                "skillId='" + skillId + '\'' +
                ", state=" + state +
                ", depth=" + depth +
                ", deps=" + resolvedDependencies.size() +
                ", dependents=" + dependents.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyNode that = (DependencyNode) o;
        return Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }

    // ==================== 节点状态 ====================

    public enum NodeState {
        UNRESOLVED("未解析"),
        RESOLVING("解析中"),
        RESOLVED("已解析"),
        FAILED("解析失败"),
        EXCLUDED("已排除");

        private final String displayName;

        NodeState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
