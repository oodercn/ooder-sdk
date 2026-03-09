package net.ooder.scene.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * CAP 路由表
 *
 * <p>维护 CAP 能力到 Skill 的映射关系，支持多种路由策略。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>绑定管理 - 添加/移除 CAP 到 Skill 的绑定</li>
 *   <li>策略路由 - 支持优先级、轮询、随机、最小负载四种策略</li>
 *   <li>状态管理 - 跟踪绑定的可用性和负载</li>
 * </ul>
 *
 * <h3>路由策略说明：</h3>
 * <ul>
 *   <li>{@link RoutingStrategy#PRIORITY} - 选择优先级最高的可用绑定</li>
 *   <li>{@link RoutingStrategy#ROUND_ROBIN} - 轮询选择可用绑定</li>
 *   <li>{@link RoutingStrategy#RANDOM} - 随机选择可用绑定</li>
 *   <li>{@link RoutingStrategy#LEAST_LOAD} - 选择负载最低的可用绑定</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class CapRoutingTable {

    /** CAP -> Skill 绑定列表（按优先级降序排列） */
    private final Map<String, List<SkillBinding>> capBindings = new ConcurrentHashMap<>();

    /** 路由策略 */
    private volatile RoutingStrategy strategy = RoutingStrategy.PRIORITY;

    /** 轮询索引 */
    private final Map<String, Integer> roundRobinIndex = new ConcurrentHashMap<>();

    /**
     * 添加绑定
     *
     * @param capId 能力ID
     * @param binding Skill 绑定
     */
    public void addBinding(String capId, SkillBinding binding) {
        capBindings.computeIfAbsent(capId, k -> new ArrayList<>()).add(binding);
        sortBindings(capId);
    }

    /**
     * 批量添加绑定
     *
     * @param capId 能力ID
     * @param bindings Skill 绑定列表
     */
    public void addBindings(String capId, List<SkillBinding> bindings) {
        capBindings.computeIfAbsent(capId, k -> new ArrayList<>()).addAll(bindings);
        sortBindings(capId);
    }

    /**
     * 移除绑定
     *
     * @param capId 能力ID
     * @param skillId Skill ID
     */
    public void removeBinding(String capId, String skillId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings != null) {
            bindings.removeIf(b -> b.getSkillId().equals(skillId));
        }
    }

    /**
     * 移除所有绑定
     *
     * @param capId 能力ID
     */
    public void removeAllBindings(String capId) {
        capBindings.remove(capId);
    }

    /**
     * 获取 Skill 绑定（根据路由策略）
     *
     * @param capId 能力ID
     * @return 选中的 Skill 绑定，如果没有可用绑定则返回 null
     */
    public SkillBinding getSkill(String capId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings == null || bindings.isEmpty()) {
            return null;
        }

        List<SkillBinding> available = getAvailableBindings(capId);
        if (available.isEmpty()) {
            return null;
        }

        return selectByStrategy(capId, available);
    }

    /**
     * 获取所有绑定
     *
     * @param capId 能力ID
     * @return 绑定列表
     */
    public List<SkillBinding> getBindings(String capId) {
        return capBindings.getOrDefault(capId, Collections.emptyList());
    }

    /**
     * 获取可用绑定
     *
     * @param capId 能力ID
     * @return 可用绑定列表
     */
    public List<SkillBinding> getAvailableBindings(String capId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings == null) {
            return Collections.emptyList();
        }
        return bindings.stream()
                .filter(SkillBinding::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * 检查是否有绑定
     *
     * @param capId 能力ID
     * @return true 表示有绑定
     */
    public boolean hasBinding(String capId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        return bindings != null && !bindings.isEmpty();
    }

    /**
     * 检查是否有可用绑定
     *
     * @param capId 能力ID
     * @return true 表示有可用绑定
     */
    public boolean hasAvailableBinding(String capId) {
        return !getAvailableBindings(capId).isEmpty();
    }

    /**
     * 设置路由策略
     *
     * @param strategy 路由策略
     */
    public void setStrategy(RoutingStrategy strategy) {
        this.strategy = strategy != null ? strategy : RoutingStrategy.PRIORITY;
    }

    /**
     * 获取当前路由策略
     *
     * @return 路由策略
     */
    public RoutingStrategy getStrategy() {
        return strategy;
    }

    /**
     * 获取所有已注册的 CAP ID
     *
     * @return CAP ID 集合
     */
    public Set<String> getRegisteredCapIds() {
        return new HashSet<>(capBindings.keySet());
    }

    /**
     * 清空所有绑定
     */
    public void clear() {
        capBindings.clear();
        roundRobinIndex.clear();
    }

    /**
     * 记录调用
     *
     * @param capId 能力ID
     * @param skillId Skill ID
     */
    public void recordInvoke(String capId, String skillId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings != null) {
            bindings.stream()
                    .filter(b -> b.getSkillId().equals(skillId))
                    .findFirst()
                    .ifPresent(SkillBinding::recordInvoke);
        }
    }

    /**
     * 更新绑定可用性
     *
     * @param capId 能力ID
     * @param skillId Skill ID
     * @param available 是否可用
     */
    public void updateAvailability(String capId, String skillId, boolean available) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings != null) {
            bindings.stream()
                    .filter(b -> b.getSkillId().equals(skillId))
                    .findFirst()
                    .ifPresent(b -> b.setAvailable(available));
        }
    }

    /**
     * 更新绑定负载
     *
     * @param capId 能力ID
     * @param skillId Skill ID
     * @param load 负载值（0.0-1.0）
     */
    public void updateLoad(String capId, String skillId, double load) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings != null) {
            bindings.stream()
                    .filter(b -> b.getSkillId().equals(skillId))
                    .findFirst()
                    .ifPresent(b -> b.setLoad(load));
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 按优先级排序绑定
     */
    private void sortBindings(String capId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings != null) {
            bindings.sort(Comparator.comparingInt(SkillBinding::getPriority).reversed());
        }
    }

    /**
     * 根据策略选择绑定
     */
    private SkillBinding selectByStrategy(String capId, List<SkillBinding> available) {
        switch (strategy) {
            case PRIORITY:
                return selectByPriority(available);
            case ROUND_ROBIN:
                return selectByRoundRobin(capId, available);
            case RANDOM:
                return selectByRandom(available);
            case LEAST_LOAD:
                return selectByLeastLoad(available);
            default:
                return selectByPriority(available);
        }
    }

    /**
     * 优先级选择
     */
    private SkillBinding selectByPriority(List<SkillBinding> available) {
        return available.get(0);
    }

    /**
     * 轮询选择
     */
    private SkillBinding selectByRoundRobin(String capId, List<SkillBinding> available) {
        int index = roundRobinIndex.computeIfAbsent(capId, k -> 0);
        SkillBinding selected = available.get(index % available.size());
        roundRobinIndex.put(capId, (index + 1) % available.size());
        return selected;
    }

    /**
     * 随机选择
     */
    private SkillBinding selectByRandom(List<SkillBinding> available) {
        int index = ThreadLocalRandom.current().nextInt(available.size());
        return available.get(index);
    }

    /**
     * 最小负载选择
     */
    private SkillBinding selectByLeastLoad(List<SkillBinding> available) {
        return available.stream()
                .min(Comparator.comparingDouble(SkillBinding::getLoad))
                .orElse(available.get(0));
    }
}
