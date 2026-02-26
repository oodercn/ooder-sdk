package net.ooder.skills.api.impl;

import net.ooder.skills.api.SkillInvoker;
import net.ooder.skills.api.SkillInvoker.SkillInvocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SkillInvoker 桥接实现
 * 
 * 连接 agent-sdk 与 scene-engine 的 SkillService，实现真实的 Skill 调用能力。
 * 使用桥接模式解耦 agent-sdk 与 scene-engine 的依赖关系。
 *
 * @author Ooder Team
 * @version 2.3
 */
public class SkillInvokerBridge implements SkillInvoker {

    private static final Logger log = LoggerFactory.getLogger(SkillInvokerBridge.class);

    /**
     * 当前用户ID提供者
     */
    private UserContextProvider contextProvider;

    /**
     * SkillService 提供者 - 通过 SPI 或注入方式获取
     */
    private SkillServiceProvider serviceProvider;

    /**
     * Skill 可用性缓存
     */
    private final Map<String, Boolean> skillAvailabilityCache = new ConcurrentHashMap<>();

    /**
     * 缓存过期时间（毫秒）
     */
    private static final long CACHE_TTL_MS = 30000;

    /**
     * 缓存时间戳
     */
    private final Map<String, Long> cacheTimestamp = new ConcurrentHashMap<>();

    public SkillInvokerBridge() {
    }

    public void setContextProvider(UserContextProvider provider) {
        this.contextProvider = provider;
    }

    public void setServiceProvider(SkillServiceProvider provider) {
        this.serviceProvider = provider;
    }

    @Override
    public Object invoke(String skillId, String capabilityId, Map<String, Object> params) 
            throws SkillInvocationException {
        
        if (serviceProvider == null) {
            throw new SkillInvocationException("SkillServiceProvider not set", skillId, capabilityId);
        }

        try {
            log.debug("Invoking skill: {}, capability: {} via bridge", skillId, capabilityId);

            // 获取当前用户上下文
            String userId = contextProvider != null ? contextProvider.getCurrentUserId() : "system";

            // 通过 SkillService 调用能力
            Object result = serviceProvider.invokeCapability(userId, skillId, capabilityId, params);

            log.debug("Skill invocation completed: {}.{}", skillId, capabilityId);
            return result;

        } catch (Exception e) {
            log.error("Failed to invoke skill: {}.{}", skillId, capabilityId, e);
            throw new SkillInvocationException(
                "Skill invocation failed: " + e.getMessage(), e, skillId, capabilityId);
        }
    }

    @Override
    public boolean isSkillAvailable(String skillId) {
        // 检查缓存
        Long timestamp = cacheTimestamp.get(skillId);
        if (timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_TTL_MS) {
            Boolean cached = skillAvailabilityCache.get(skillId);
            return cached != null && cached;
        }

        // 实时检查
        boolean available = checkSkillAvailability(skillId);
        skillAvailabilityCache.put(skillId, available);
        cacheTimestamp.put(skillId, System.currentTimeMillis());
        
        return available;
    }

    @Override
    public SkillInfo getSkillInfo(String skillId) {
        if (serviceProvider == null) {
            return null;
        }
        return serviceProvider.getSkillInfo(skillId);
    }

    /**
     * 检查 Skill 可用性
     */
    private boolean checkSkillAvailability(String skillId) {
        if (serviceProvider == null) {
            return false;
        }

        try {
            return serviceProvider.isSkillAvailable(skillId);
        } catch (Exception e) {
            log.warn("Failed to check skill availability: {}", skillId, e);
            return false;
        }
    }

    /**
     * 清除可用性缓存
     */
    public void clearAvailabilityCache(String skillId) {
        skillAvailabilityCache.remove(skillId);
        cacheTimestamp.remove(skillId);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCaches() {
        skillAvailabilityCache.clear();
        cacheTimestamp.clear();
    }

    /**
     * 用户上下文提供者接口
     */
    public interface UserContextProvider {
        String getCurrentUserId();
    }

    /**
     * SkillService 提供者接口
     * 用于解耦 agent-sdk 与 scene-engine 的直接依赖
     */
    public interface SkillServiceProvider {
        
        /**
         * 调用 Skill 能力
         */
        Object invokeCapability(String userId, String skillId, String capabilityId, 
                               Map<String, Object> params);
        
        /**
         * 检查 Skill 是否可用
         */
        boolean isSkillAvailable(String skillId);

        /**
         * 获取 Skill 信息
         */
        SkillInfo getSkillInfo(String skillId);
    }
}
