package net.ooder.sdk.api.provider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 提供者管理器接口
 *
 * 管理能力的多个提供者，支持路由切换
 */
public interface ProviderManager {

    /**
     * 注册提供者
     */
    void registerProvider(String capabilityId, Provider provider);

    /**
     * 注销提供者
     */
    void unregisterProvider(String capabilityId, String providerId);

    /**
     * 获取能力的所有提供者
     */
    List<Provider> getProviders(String capabilityId);

    /**
     * 获取默认提供者
     */
    Provider getDefaultProvider(String capabilityId);

    /**
     * 设置默认提供者
     */
    void setDefaultProvider(String capabilityId, String providerId);

    /**
     * 切换提供者
     */
    CompletableFuture<SwitchResult> switchProvider(String capabilityId, String fromProviderId, String toProviderId);

    /**
     * 获取当前活跃的提供者
     */
    Provider getActiveProvider(String capabilityId);

    /**
     * 获取路由策略
     */
    RoutingStrategy getRoutingStrategy(String capabilityId);

    /**
     * 设置路由策略
     */
    void setRoutingStrategy(String capabilityId, RoutingStrategy strategy);

    /**
     * 执行能力调用（自动路由）
     */
    Object invoke(String capabilityId, Map<String, Object> params);

    /**
     * 提供者定义
     */
    class Provider {
        private String id;
        private String name;
        private String capabilityId;
        private String skillId;
        private ProviderStatus status;
        private Map<String, Object> metadata;
        private int priority;
        private long responseTime;
        private double availability;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCapabilityId() {
            return capabilityId;
        }

        public void setCapabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public ProviderStatus getStatus() {
            return status;
        }

        public void setStatus(ProviderStatus status) {
            this.status = status;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }

        public double getAvailability() {
            return availability;
        }

        public void setAvailability(double availability) {
            this.availability = availability;
        }
    }

    /**
     * 提供者状态
     */
    enum ProviderStatus {
        ACTIVE,
        STANDBY,
        MAINTENANCE,
        OFFLINE
    }

    /**
     * 路由策略
     */
    enum RoutingStrategy {
        PRIORITY,       // 优先级路由
        ROUND_ROBIN,    // 轮询
        RANDOM,         // 随机
        WEIGHTED,       // 加权
        FAILOVER,       // 故障转移
        PERFORMANCE     // 性能最优
    }

    /**
     * 切换结果
     */
    class SwitchResult {
        private boolean success;
        private String capabilityId;
        private String fromProviderId;
        private String toProviderId;
        private long switchTime;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getCapabilityId() {
            return capabilityId;
        }

        public void setCapabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
        }

        public String getFromProviderId() {
            return fromProviderId;
        }

        public void setFromProviderId(String fromProviderId) {
            this.fromProviderId = fromProviderId;
        }

        public String getToProviderId() {
            return toProviderId;
        }

        public void setToProviderId(String toProviderId) {
            this.toProviderId = toProviderId;
        }

        public long getSwitchTime() {
            return switchTime;
        }

        public void setSwitchTime(long switchTime) {
            this.switchTime = switchTime;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
