package net.ooder.scene.skill.instance;

import net.ooder.scene.skill.state.SkillLifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Skill实例池
 *
 * <p>按用户隔离管理Skill实例，提供：</p>
 * <ul>
 *   <li>用户级别的实例隔离</li>
 *   <li>实例生命周期管理</li>
 *   <li>空闲超时清理</li>
 *   <li>实例数限制</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillInstancePool {

    private static final Logger log = LoggerFactory.getLogger(SkillInstancePool.class);

    /**
     * 用户 -> (SkillId -> Instance) 映射
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, SkillInstance>> userInstances;

    /**
     * 全局共享实例（系统级Skill）
     */
    private final ConcurrentHashMap<String, SkillInstance> sharedInstances;

    /**
     * 实例工厂
     */
    private SkillInstanceFactory instanceFactory;

    /**
     * 清理调度器
     */
    private final ScheduledExecutorService cleanupExecutor;

    /**
     * 空闲超时时间（毫秒）
     */
    private long idleTimeout = 30 * 60 * 1000;  // 默认30分钟

    /**
     * 每用户最大实例数
     */
    private int maxInstancesPerUser = 50;

    /**
     * 是否启用自动清理
     */
    private boolean autoCleanupEnabled = true;

    public SkillInstancePool() {
        this.userInstances = new ConcurrentHashMap<>();
        this.sharedInstances = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "skill-instance-cleanup");
            t.setDaemon(true);
            return t;
        });

        // 启动定时清理任务
        if (autoCleanupEnabled) {
            startCleanupTask();
        }
    }

    /**
     * 设置实例工厂
     */
    public void setInstanceFactory(SkillInstanceFactory instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    /**
     * 设置空闲超时时间
     */
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * 设置每用户最大实例数
     */
    public void setMaxInstancesPerUser(int maxInstancesPerUser) {
        this.maxInstancesPerUser = maxInstancesPerUser;
    }

    // ==================== 核心方法 ====================

    /**
     * 获取或创建实例
     */
    public SkillInstance getOrCreate(String userId, String skillId) {
        ConcurrentHashMap<String, SkillInstance> userPool = getUserPool(userId);

        SkillInstance instance = userPool.get(skillId);
        if (instance != null && !instance.isDestroyed()) {
            log.debug("Found existing instance for user={}, skill={}", userId, skillId);
            return instance;
        }

        // 检查实例数限制
        if (userPool.size() >= maxInstancesPerUser) {
            log.warn("User {} reached max instance limit: {}", userId, maxInstancesPerUser);
            cleanupIdleInstances(userId);
        }

        // 创建新实例
        if (instanceFactory == null) {
            throw new IllegalStateException("InstanceFactory not set");
        }

        instance = instanceFactory.create(userId, skillId);
        SkillInstance existing = userPool.putIfAbsent(skillId, instance);

        if (existing != null && !existing.isDestroyed()) {
            // 另一个线程已创建
            log.debug("Another thread created instance for user={}, skill={}", userId, skillId);
            return existing;
        }

        log.info("Created new instance for user={}, skill={}, instanceId={}",
                userId, skillId, instance.getInstanceId());
        return instance;
    }

    /**
     * 获取实例（不创建）
     */
    public Optional<SkillInstance> get(String userId, String skillId) {
        Map<String, SkillInstance> userPool = userInstances.get(userId);
        if (userPool != null) {
            SkillInstance instance = userPool.get(skillId);
            if (instance != null && !instance.isDestroyed()) {
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }

    /**
     * 获取用户所有实例
     */
    public Map<String, SkillInstance> getUserInstances(String userId) {
        Map<String, SkillInstance> userPool = userInstances.get(userId);
        if (userPool == null) {
            return Collections.emptyMap();
        }

        // 过滤掉已销毁的实例
        Map<String, SkillInstance> result = new HashMap<>();
        for (Map.Entry<String, SkillInstance> entry : userPool.entrySet()) {
            if (!entry.getValue().isDestroyed()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 获取用户实例池
     */
    public ConcurrentHashMap<String, SkillInstance> getUserPool(String userId) {
        return userInstances.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
    }

    /**
     * 移除实例
     */
    public void remove(String userId, String skillId) {
        Map<String, SkillInstance> userPool = userInstances.get(userId);
        if (userPool != null) {
            SkillInstance instance = userPool.remove(skillId);
            if (instance != null) {
                log.info("Removed instance for user={}, skill={}", userId, skillId);
                instance.destroy();
            }
        }
    }

    /**
     * 清理用户所有实例
     */
    public void cleanupUser(String userId) {
        Map<String, SkillInstance> userPool = userInstances.remove(userId);
        if (userPool != null) {
            log.info("Cleaning up all instances for user: {}", userId);
            for (SkillInstance instance : userPool.values()) {
                try {
                    instance.destroy();
                } catch (Exception e) {
                    log.error("Error destroying instance: {}", instance.getSkillId(), e);
                }
            }
            userPool.clear();
        }
    }

    /**
     * 停止用户所有实例（但不销毁）
     */
    public void stopUserInstances(String userId) {
        Map<String, SkillInstance> userPool = userInstances.get(userId);
        if (userPool != null) {
            log.info("Stopping all instances for user: {}", userId);
            for (SkillInstance instance : userPool.values()) {
                try {
                    if (instance.getState() == SkillLifecycleState.RUNNING) {
                        instance.stop();
                    }
                } catch (Exception e) {
                    log.error("Error stopping instance: {}", instance.getSkillId(), e);
                }
            }
        }
    }

    /**
     * 获取所有用户ID
     */
    public Set<String> getAllUserIds() {
        return new HashSet<>(userInstances.keySet());
    }

    /**
     * 获取实例统计信息
     */
    public PoolStats getStats() {
        PoolStats stats = new PoolStats();

        int totalUsers = userInstances.size();
        int totalInstances = 0;
        int runningInstances = 0;

        for (Map<String, SkillInstance> userPool : userInstances.values()) {
            totalInstances += userPool.size();
            for (SkillInstance instance : userPool.values()) {
                if (instance.getState() == SkillLifecycleState.RUNNING) {
                    runningInstances++;
                }
            }
        }

        stats.setTotalUsers(totalUsers);
        stats.setTotalInstances(totalInstances);
        stats.setRunningInstances(runningInstances);
        stats.setSharedInstances(sharedInstances.size());

        return stats;
    }

    // ==================== 清理方法 ====================

    /**
     * 启动定时清理任务
     */
    private void startCleanupTask() {
        cleanupExecutor.scheduleWithFixedDelay(() -> {
            try {
                cleanupAllIdleInstances();
            } catch (Exception e) {
                log.error("Error in cleanup task", e);
            }
        }, idleTimeout, idleTimeout / 2, TimeUnit.MILLISECONDS);

        log.info("Started instance cleanup task with interval: {}ms", idleTimeout / 2);
    }

    /**
     * 清理所有空闲实例
     */
    public void cleanupAllIdleInstances() {
        for (String userId : new ArrayList<>(userInstances.keySet())) {
            cleanupIdleInstances(userId);
        }
    }

    /**
     * 清理指定用户的空闲实例
     */
    public void cleanupIdleInstances(String userId) {
        Map<String, SkillInstance> userPool = userInstances.get(userId);
        if (userPool == null) return;

        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, SkillInstance>> it = userPool.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, SkillInstance> entry = it.next();
            SkillInstance instance = entry.getValue();

            boolean shouldCleanup = false;

            // 1. 已销毁的实例
            if (instance.isDestroyed()) {
                shouldCleanup = true;
            }
            // 2. 已停止且空闲超时
            else if (instance.getState() == SkillLifecycleState.STOPPED &&
                    (now - instance.getLastActiveTime() > idleTimeout)) {
                shouldCleanup = true;
            }
            // 3. 运行中但长时间无活动（可选）
            else if (instance.getState() == SkillLifecycleState.RUNNING &&
                    (now - instance.getLastActiveTime() > idleTimeout * 2)) {
                log.warn("Instance {} has been idle for too long, stopping", instance.getSkillId());
                instance.stop();
                shouldCleanup = true;
            }

            if (shouldCleanup) {
                try {
                    instance.destroy();
                    it.remove();
                    log.debug("Cleaned up idle instance: user={}, skill={}", userId, entry.getKey());
                } catch (Exception e) {
                    log.error("Error cleaning up instance: {}", entry.getKey(), e);
                }
            }
        }

        // 如果用户池为空，移除用户
        if (userPool.isEmpty()) {
            userInstances.remove(userId);
        }
    }

    /**
     * 关闭池
     */
    public void shutdown() {
        log.info("Shutting down SkillInstancePool...");

        // 停止清理任务
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 清理所有实例
        for (String userId : new ArrayList<>(userInstances.keySet())) {
            cleanupUser(userId);
        }

        // 清理共享实例
        for (SkillInstance instance : sharedInstances.values()) {
            instance.destroy();
        }
        sharedInstances.clear();

        log.info("SkillInstancePool shutdown complete");
    }

    // ==================== 内部类 ====================

    /**
     * 池统计信息
     */
    public static class PoolStats {
        private int totalUsers;
        private int totalInstances;
        private int runningInstances;
        private int sharedInstances;

        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getTotalInstances() {
            return totalInstances;
        }

        public void setTotalInstances(int totalInstances) {
            this.totalInstances = totalInstances;
        }

        public int getRunningInstances() {
            return runningInstances;
        }

        public void setRunningInstances(int runningInstances) {
            this.runningInstances = runningInstances;
        }

        public int getSharedInstances() {
            return sharedInstances;
        }

        public void setSharedInstances(int sharedInstances) {
            this.sharedInstances = sharedInstances;
        }

        @Override
        public String toString() {
            return "PoolStats{" +
                    "totalUsers=" + totalUsers +
                    ", totalInstances=" + totalInstances +
                    ", runningInstances=" + runningInstances +
                    ", sharedInstances=" + sharedInstances +
                    '}';
        }
    }
}
