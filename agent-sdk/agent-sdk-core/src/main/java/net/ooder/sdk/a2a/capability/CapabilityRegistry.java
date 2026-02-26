package net.ooder.sdk.a2a.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 能力注册表
 *
 * <p>管理所有Skill的能力声明</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class CapabilityRegistry {

    private static final Logger log = LoggerFactory.getLogger(CapabilityRegistry.class);

    /**
     * Skill ID -> 能力列表
     */
    private final Map<String, List<CapabilityDeclaration>> skillCapabilities;

    /**
     * 能力ID -> (Skill ID -> 能力声明)
     */
    private final Map<String, Map<String, CapabilityDeclaration>> capabilityIndex;

    public CapabilityRegistry() {
        this.skillCapabilities = new ConcurrentHashMap<>();
        this.capabilityIndex = new ConcurrentHashMap<>();
    }

    /**
     * 注册能力
     *
     * @param skillId Skill标识
     * @param capability 能力声明
     */
    public void registerCapability(String skillId, CapabilityDeclaration capability) {
        if (skillId == null || capability == null || capability.getId() == null) {
            throw new IllegalArgumentException("skillId and capability cannot be null");
        }

        // 添加到Skill能力列表
        skillCapabilities.computeIfAbsent(skillId, k -> new ArrayList<>()).add(capability);

        // 添加到能力索引
        capabilityIndex.computeIfAbsent(capability.getId(), k -> new ConcurrentHashMap<>())
                      .put(skillId, capability);

        log.info("Registered capability: {} for skill: {}", capability.getId(), skillId);
    }

    /**
     * 批量注册能力
     *
     * @param skillId Skill标识
     * @param capabilities 能力列表
     */
    public void registerCapabilities(String skillId, List<CapabilityDeclaration> capabilities) {
        if (capabilities != null) {
            for (CapabilityDeclaration capability : capabilities) {
                registerCapability(skillId, capability);
            }
        }
    }

    /**
     * 注销能力
     *
     * @param skillId Skill标识
     * @param capabilityId 能力ID
     */
    public void unregisterCapability(String skillId, String capabilityId) {
        List<CapabilityDeclaration> capabilities = skillCapabilities.get(skillId);
        if (capabilities != null) {
            capabilities.removeIf(c -> c.getId().equals(capabilityId));
        }

        Map<String, CapabilityDeclaration> skillMap = capabilityIndex.get(capabilityId);
        if (skillMap != null) {
            skillMap.remove(skillId);
        }

        log.info("Unregistered capability: {} for skill: {}", capabilityId, skillId);
    }

    /**
     * 注销Skill的所有能力
     *
     * @param skillId Skill标识
     */
    public void unregisterSkillCapabilities(String skillId) {
        List<CapabilityDeclaration> capabilities = skillCapabilities.remove(skillId);
        if (capabilities != null) {
            for (CapabilityDeclaration capability : capabilities) {
                Map<String, CapabilityDeclaration> skillMap = capabilityIndex.get(capability.getId());
                if (skillMap != null) {
                    skillMap.remove(skillId);
                }
            }
        }
        log.info("Unregistered all capabilities for skill: {}", skillId);
    }

    /**
     * 获取Skill的所有能力
     *
     * @param skillId Skill标识
     * @return 能力列表
     */
    public List<CapabilityDeclaration> getSkillCapabilities(String skillId) {
        return skillCapabilities.getOrDefault(skillId, Collections.emptyList());
    }

    /**
     * 根据ID获取能力
     *
     * @param capabilityId 能力ID
     * @return 能力声明，如果不存在返回null
     */
    public CapabilityDeclaration getCapability(String capabilityId) {
        Map<String, CapabilityDeclaration> skillMap = capabilityIndex.get(capabilityId);
        if (skillMap != null && !skillMap.isEmpty()) {
            // 返回第一个找到的能力
            return skillMap.values().iterator().next();
        }
        return null;
    }

    /**
     * 获取提供指定能力的所有Skill
     *
     * @param capabilityId 能力ID
     * @return Skill ID列表
     */
    public List<String> getSkillsByCapability(String capabilityId) {
        Map<String, CapabilityDeclaration> skillMap = capabilityIndex.get(capabilityId);
        if (skillMap != null) {
            return new ArrayList<>(skillMap.keySet());
        }
        return Collections.emptyList();
    }

    /**
     * 搜索能力
     *
     * @param keyword 关键词
     * @return 匹配的能力列表
     */
    public List<CapabilityDeclaration> searchCapabilities(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCapabilities();
        }

        String lowerKeyword = keyword.toLowerCase();
        return capabilityIndex.values().stream()
                .flatMap(m -> m.values().stream())
                .filter(c -> {
                    if (c.getId() != null && c.getId().toLowerCase().contains(lowerKeyword)) {
                        return true;
                    }
                    if (c.getName() != null && c.getName().toLowerCase().contains(lowerKeyword)) {
                        return true;
                    }
                    if (c.getDescription() != null && c.getDescription().toLowerCase().contains(lowerKeyword)) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取所有能力
     *
     * @return 能力列表
     */
    public List<CapabilityDeclaration> getAllCapabilities() {
        return capabilityIndex.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }

    /**
     * 检查能力是否存在
     *
     * @param capabilityId 能力ID
     * @return true如果存在
     */
    public boolean hasCapability(String capabilityId) {
        return capabilityIndex.containsKey(capabilityId);
    }

    /**
     * 检查Skill是否拥有指定能力
     *
     * @param skillId Skill标识
     * @param capabilityId 能力ID
     * @return true如果拥有
     */
    public boolean skillHasCapability(String skillId, String capabilityId) {
        Map<String, CapabilityDeclaration> skillMap = capabilityIndex.get(capabilityId);
        return skillMap != null && skillMap.containsKey(skillId);
    }

    /**
     * 获取已注册Skill数量
     */
    public int getSkillCount() {
        return skillCapabilities.size();
    }

    /**
     * 获取已注册能力数量
     */
    public int getCapabilityCount() {
        return capabilityIndex.size();
    }

    /**
     * 清空所有注册
     */
    public void clear() {
        skillCapabilities.clear();
        capabilityIndex.clear();
        log.info("Cleared all capability registrations");
    }
}
