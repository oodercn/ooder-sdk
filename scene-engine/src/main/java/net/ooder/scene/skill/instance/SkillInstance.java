package net.ooder.scene.skill.instance;

import net.ooder.scene.skill.adapter.SkillSDKAdapter;
import net.ooder.scene.skill.exception.SkillException;
import net.ooder.scene.skill.exception.SkillNotRunningException;
import net.ooder.scene.skill.state.SkillLifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Skill有状态实例 - 简化版
 *
 * <p>只维护生命周期状态，实际调用通过SkillSDKAdapter转发到SDK</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillInstance {

    private static final Logger log = LoggerFactory.getLogger(SkillInstance.class);

    // ==================== 基本信息 ====================
    private final String skillId;
    private final String userId;
    private final String instanceId;

    // ==================== 状态管理 ====================
    private volatile SkillLifecycleState state;
    private final Object stateLock = new Object();

    // ==================== SDK适配器 ====================
    private final SkillSDKAdapter sdkAdapter;

    // ==================== 统计信息 ====================
    private final long createTime;
    private volatile long lastActiveTime;
    private final AtomicLong invokeCount;

    public SkillInstance(String skillId, String userId, String instanceId, SkillSDKAdapter sdkAdapter) {
        this.skillId = skillId;
        this.userId = userId;
        this.instanceId = instanceId;
        this.sdkAdapter = sdkAdapter;
        this.state = SkillLifecycleState.CREATED;
        this.createTime = System.currentTimeMillis();
        this.lastActiveTime = createTime;
        this.invokeCount = new AtomicLong(0);
    }

    // ==================== 状态转换方法 ====================

    /**
     * 启动Skill
     */
    public boolean start() {
        synchronized (stateLock) {
            if (!canStart()) {
                log.warn("Cannot start skill {} from state {}", skillId, state);
                return false;
            }

            try {
                transitionTo(SkillLifecycleState.STARTING);
                log.info("Starting skill: {}", skillId);

                // 通过适配器启动
                boolean success = sdkAdapter.startSkill(skillId);

                if (success) {
                    transitionTo(SkillLifecycleState.RUNNING);
                    log.info("Skill started successfully: {}", skillId);
                    return true;
                } else {
                    transitionTo(SkillLifecycleState.ERROR);
                    log.error("Failed to start skill: {}", skillId);
                    return false;
                }

            } catch (Exception e) {
                transitionTo(SkillLifecycleState.ERROR);
                log.error("Exception starting skill: {}", skillId, e);
                throw new SkillException(skillId, "Failed to start: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 停止Skill
     */
    public boolean stop() {
        synchronized (stateLock) {
            if (!canStop()) {
                log.warn("Cannot stop skill {} from state {}", skillId, state);
                return false;
            }

            try {
                transitionTo(SkillLifecycleState.STOPPING);
                log.info("Stopping skill: {}", skillId);

                // 通过适配器停止
                boolean success = sdkAdapter.stopSkill(skillId);

                if (success) {
                    transitionTo(SkillLifecycleState.STOPPED);
                    log.info("Skill stopped successfully: {}", skillId);
                    return true;
                } else {
                    transitionTo(SkillLifecycleState.ERROR);
                    log.error("Failed to stop skill: {}", skillId);
                    return false;
                }

            } catch (Exception e) {
                transitionTo(SkillLifecycleState.ERROR);
                log.error("Exception stopping skill: {}", skillId, e);
                throw new SkillException(skillId, "Failed to stop: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 销毁Skill
     */
    public boolean destroy() {
        synchronized (stateLock) {
            if (state == SkillLifecycleState.DESTROYED) {
                return true;
            }

            try {
                // 先停止
                if (state == SkillLifecycleState.RUNNING) {
                    stop();
                }

                // 通过适配器卸载
                sdkAdapter.uninstallSkill(skillId, false);

                transitionTo(SkillLifecycleState.DESTROYED);
                log.info("Skill destroyed: {}", skillId);
                return true;

            } catch (Exception e) {
                log.error("Exception destroying skill: {}", skillId, e);
                return false;
            }
        }
    }

    /**
     * 调用能力
     */
    public Object invoke(String capability, Map<String, Object> params) {
        if (state != SkillLifecycleState.RUNNING) {
            // 自动启动
            if (!start()) {
                throw new SkillNotRunningException(skillId, state);
            }
        }

        lastActiveTime = System.currentTimeMillis();
        invokeCount.incrementAndGet();

        try {
            // 通过适配器调用
            return sdkAdapter.invokeCapability(userId, skillId, capability, params);
        } catch (Exception e) {
            log.error("Failed to invoke skill: {}, capability: {}", skillId, capability, e);
            throw new SkillException(skillId, "Invoke failed: " + e.getMessage(), e);
        }
    }

    // ==================== 状态检查方法 ====================

    public boolean canStart() {
        return state == SkillLifecycleState.CREATED
                || state == SkillLifecycleState.STOPPED
                || state == SkillLifecycleState.ERROR;
    }

    public boolean canStop() {
        return state == SkillLifecycleState.RUNNING;
    }

    public boolean canInvoke() {
        return state == SkillLifecycleState.RUNNING;
    }

    // ==================== 私有方法 ====================

    private void transitionTo(SkillLifecycleState newState) {
        if (!state.canTransitionTo(newState)) {
            log.warn("Invalid state transition: {} -> {} for skill {}", state, newState, skillId);
            // 不抛出异常，只记录警告
        }
        SkillLifecycleState oldState = this.state;
        this.state = newState;
        log.debug("Skill {} state changed: {} -> {}", skillId, oldState, newState);
    }

    // ==================== Getters ====================

    public String getSkillId() {
        return skillId;
    }

    public String getUserId() {
        return userId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public SkillLifecycleState getState() {
        return state;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public long getInvokeCount() {
        return invokeCount.get();
    }

    public boolean isDestroyed() {
        return state == SkillLifecycleState.DESTROYED;
    }

    @Override
    public String toString() {
        return "SkillInstance{" +
                "skillId='" + skillId + '\'' +
                ", userId='" + userId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", state=" + state +
                ", invokeCount=" + invokeCount.get() +
                '}';
    }
}
