package net.ooder.scene.skill.runtime;

import net.ooder.scene.core.*;
import net.ooder.scene.skill.adapter.SkillSDKAdapter;
import net.ooder.scene.skill.instance.SkillInstance;
import net.ooder.scene.skill.instance.SkillInstancePool;
import net.ooder.scene.skill.state.SkillLifecycleState;
import net.ooder.sdk.api.capability.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Skill运行时 - 管理所有Skill实例
 *
 * <p>核心运行时，单例模式，整个应用共享一个实例</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
@Component
public class SkillRuntime {

    private static final Logger log = LoggerFactory.getLogger(SkillRuntime.class);

    private SkillInstancePool instancePool;
    private SkillSDKAdapter sdkAdapter;

    public void setInstancePool(SkillInstancePool instancePool) {
        this.instancePool = instancePool;
    }

    public void setSdkAdapter(SkillSDKAdapter sdkAdapter) {
        this.sdkAdapter = sdkAdapter;
    }

    @PostConstruct
    public void init() {
        log.info("SkillRuntime initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("SkillRuntime destroying...");
        instancePool.shutdown();
        log.info("SkillRuntime destroyed");
    }

    // ==================== 安装管理 ====================

    /**
     * 安装Skill
     */
    public SkillInstallResult install(String userId, String skillId, Map<String, Object> config) {
        log.info("Installing skill for user={}, skill={}", userId, skillId);

        try {
            // 通过适配器安装
            boolean success = sdkAdapter.installSkill(skillId, config);

            if (success) {
                log.info("Skill installed successfully: user={}, skill={}", userId, skillId);
                return SkillInstallResult.success(skillId, "1.0");
            } else {
                log.error("Failed to install skill: user={}, skill={}", userId, skillId);
                return SkillInstallResult.failed(skillId, "Installation failed");
            }

        } catch (Exception e) {
            log.error("Exception installing skill: user={}, skill={}", userId, skillId, e);
            return SkillInstallResult.failed(skillId, e.getMessage());
        }
    }

    /**
     * 卸载Skill
     */
    public SkillUninstallResult uninstall(String userId, String skillId, boolean removeData) {
        log.info("Uninstalling skill for user={}, skill={}", userId, skillId);

        try {
            // 1. 销毁实例
            Optional<SkillInstance> instanceOpt = instancePool.get(userId, skillId);
            if (instanceOpt.isPresent()) {
                instanceOpt.get().destroy();
                instancePool.remove(userId, skillId);
            }

            // 2. 通过适配器卸载
            boolean success = sdkAdapter.uninstallSkill(skillId, removeData);

            if (success) {
                log.info("Skill uninstalled successfully: user={}, skill={}", userId, skillId);
                return SkillUninstallResult.success(skillId);
            } else {
                log.error("Failed to uninstall skill: user={}, skill={}", userId, skillId);
                return SkillUninstallResult.failed(skillId, "Uninstall failed");
            }

        } catch (Exception e) {
            log.error("Exception uninstalling skill: user={}, skill={}", userId, skillId, e);
            return SkillUninstallResult.failed(skillId, e.getMessage());
        }
    }

    // ==================== 生命周期管理 ====================

    /**
     * 启动Skill
     */
    public boolean start(String userId, String skillId) {
        log.info("Starting skill for user={}, skill={}", userId, skillId);

        try {
            SkillInstance instance = instancePool.getOrCreate(userId, skillId);
            return instance.start();
        } catch (Exception e) {
            log.error("Exception starting skill: user={}, skill={}", userId, skillId, e);
            return false;
        }
    }

    /**
     * 停止Skill
     */
    public boolean stop(String userId, String skillId) {
        log.info("Stopping skill for user={}, skill={}", userId, skillId);

        Optional<SkillInstance> instanceOpt = instancePool.get(userId, skillId);
        return instanceOpt.map(SkillInstance::stop).orElse(false);
    }

    /**
     * 重启Skill
     */
    public boolean restart(String userId, String skillId) {
        log.info("Restarting skill for user={}, skill={}", userId, skillId);

        stop(userId, skillId);
        return start(userId, skillId);
    }

    /**
     * 销毁Skill
     */
    public boolean destroy(String userId, String skillId) {
        log.info("Destroying skill for user={}, skill={}", userId, skillId);

        Optional<SkillInstance> instanceOpt = instancePool.get(userId, skillId);
        if (instanceOpt.isPresent()) {
            instanceOpt.get().destroy();
            instancePool.remove(userId, skillId);
            return true;
        }
        return false;
    }

    /**
     * 获取Skill状态
     */
    public SkillLifecycleState getState(String userId, String skillId) {
        Optional<SkillInstance> instanceOpt = instancePool.get(userId, skillId);
        if (instanceOpt.isPresent()) {
            return instanceOpt.get().getState();
        }

        // 如果没有实例，查询SDK状态
        return sdkAdapter.getSkillState(skillId);
    }

    // ==================== 调用执行 ====================

    /**
     * 同步调用
     */
    public Object invoke(String userId, String skillId, String capability, Map<String, Object> params) {
        // 1. 获取实例（自动创建）
        SkillInstance instance = instancePool.getOrCreate(userId, skillId);

        // 2. 执行调用（实例内部会自动启动）
        return instance.invoke(capability, params);
    }

    /**
     * 异步调用
     */
    public CompletableFuture<Object> invokeAsync(String userId, String skillId,
                                                  String capability, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> invoke(userId, skillId, capability, params));
    }

    /**
     * 批量调用
     */
    public List<InvokeResult> batchInvoke(String userId, List<InvokeRequest> requests) {
        List<CompletableFuture<InvokeResult>> futures = requests.stream()
                .map(req -> invokeAsync(userId, req.getSkillId(), req.getCapability(), req.getParams())
                        .thenApply(result -> new InvokeResult(req, result, null))
                        .exceptionally(ex -> new InvokeResult(req, null, ex)))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    // ==================== 查询方法 ====================

    /**
     * 获取Skill信息
     */
    public SkillInfo getSkill(String userId, String skillId) {
        Optional<SkillInstance> instanceOpt = instancePool.get(userId, skillId);

        SkillInfo info = new SkillInfo();
        info.setSkillId(skillId);

        if (instanceOpt.isPresent()) {
            SkillInstance instance = instanceOpt.get();
            info.setState(instance.getState());
            info.setInvokeCount(instance.getInvokeCount());
            info.setLastActiveTime(instance.getLastActiveTime());
        } else {
            // 从SDK获取状态
            info.setState(sdkAdapter.getSkillState(skillId));
        }

        // 从SDK获取能力列表
        List<net.ooder.skills.api.Capability> capabilities = sdkAdapter.listCapabilities(skillId);
        // 注意：类型不匹配，需要转换或调整设计
        // 暂时不设置capabilities

        return info;
    }

    /**
     * 列出用户所有Skill
     */
    public List<SkillInfo> listSkills(String userId) {
        Map<String, SkillInstance> userInstances = instancePool.getUserInstances(userId);

        return userInstances.values().stream()
                .map(instance -> {
                    SkillInfo info = new SkillInfo();
                    info.setSkillId(instance.getSkillId());
                    info.setState(instance.getState());
                    info.setInvokeCount(instance.getInvokeCount());
                    info.setLastActiveTime(instance.getLastActiveTime());
                    return info;
                })
                .collect(Collectors.toList());
    }

    /**
     * 清理用户所有Skill
     */
    public void cleanupUser(String userId) {
        log.info("Cleaning up all skills for user: {}", userId);
        instancePool.cleanupUser(userId);
    }

    // ==================== 内部类 ====================

    /**
     * 调用请求
     */
    public static class InvokeRequest {
        private String skillId;
        private String capability;
        private Map<String, Object> params;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getCapability() { return capability; }
        public void setCapability(String capability) { this.capability = capability; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }

    /**
     * 调用结果
     */
    public static class InvokeResult {
        private InvokeRequest request;
        private Object result;
        private Throwable error;

        public InvokeResult(InvokeRequest request, Object result, Throwable error) {
            this.request = request;
            this.result = result;
            this.error = error;
        }

        public InvokeRequest getRequest() { return request; }
        public Object getResult() { return result; }
        public Throwable getError() { return error; }
        public boolean isSuccess() { return error == null; }
    }

    /**
     * Skill信息
     */
    public static class SkillInfo {
        private String skillId;
        private SkillLifecycleState state;
        private List<Capability> capabilities;
        private long invokeCount;
        private long lastActiveTime;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public SkillLifecycleState getState() { return state; }
        public void setState(SkillLifecycleState state) { this.state = state; }
        public List<Capability> getCapabilities() { return capabilities; }
        public void setCapabilities(List<Capability> capabilities) { this.capabilities = capabilities; }
        public long getInvokeCount() { return invokeCount; }
        public void setInvokeCount(long invokeCount) { this.invokeCount = invokeCount; }
        public long getLastActiveTime() { return lastActiveTime; }
        public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    }
}
