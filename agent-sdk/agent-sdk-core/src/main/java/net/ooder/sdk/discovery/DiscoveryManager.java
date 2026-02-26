package net.ooder.sdk.discovery;

import net.ooder.sdk.api.capability.Capability;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 发现管理器接口
 *
 * <p>管理 Skill 的发现机制,支持多种发现方式</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface DiscoveryManager {

    /**
     * 启动发现服务
     */
    void start();

    /**
     * 停止发现服务
     */
    void stop();

    /**
     * 发现 Skill (异步)
     *
     * @param query 发现查询条件
     * @return CompletableFuture<发现结果>
     */
    CompletableFuture<DiscoveryResult> discover(DiscoveryQuery query);

    /**
     * 发现特定能力
     *
     * @param capId 能力ID
     * @return CompletableFuture<能力信息>
     */
    CompletableFuture<DiscoveredCapability> discoverCapability(String capId);

    /**
     * 广播自身能力
     *
     * @param capabilities 能力列表
     */
    void advertise(List<Capability> capabilities);

    /**
     * 添加发现监听器
     *
     * @param listener 监听器
     */
    void addListener(DiscoveryListener listener);

    /**
     * 移除发现监听器
     *
     * @param listener 监听器
     */
    void removeListener(DiscoveryListener listener);

    /**
     * 获取支持的发现机制
     *
     * @return 发现机制列表
     */
    List<DiscoveryMechanism> getSupportedMechanisms();

    /**
     * 发现查询条件
     */
    class DiscoveryQuery {
        private String domainId;
        private String skillId;
        private String capabilityType;
        private List<String> tags;
        private long timeout;

        public String getDomainId() { return domainId; }
        public void setDomainId(String domainId) { this.domainId = domainId; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getCapabilityType() { return capabilityType; }
        public void setCapabilityType(String capabilityType) { this.capabilityType = capabilityType; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }

        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
    }

    /**
     * 发现结果
     */
    class DiscoveryResult {
        private List<DiscoveredCapability> capabilities;
        private long discoveryTime;
        private boolean success;
        private String errorMessage;

        public List<DiscoveredCapability> getCapabilities() { return capabilities; }
        public void setCapabilities(List<DiscoveredCapability> capabilities) { this.capabilities = capabilities; }

        public long getDiscoveryTime() { return discoveryTime; }
        public void setDiscoveryTime(long discoveryTime) { this.discoveryTime = discoveryTime; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 发现的能力信息
     */
    class DiscoveredCapability {
        private String capId;
        private String skillId;
        private String name;
        private String version;
        private String endpoint;
        private String domainId;
        private long discoveredAt;

        public String getCapId() { return capId; }
        public void setCapId(String capId) { this.capId = capId; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public String getDomainId() { return domainId; }
        public void setDomainId(String domainId) { this.domainId = domainId; }

        public long getDiscoveredAt() { return discoveredAt; }
        public void setDiscoveredAt(long discoveredAt) { this.discoveredAt = discoveredAt; }
    }

    /**
     * 发现机制枚举
     */
    enum DiscoveryMechanism {
        MDNS,       // mDNS/DNS-SD
        DHT,        // 分布式哈希表
        CENTER,     // 中心化服务
        FILE,       // 文件系统
        MANUAL      // 手动配置
    }

    /**
     * 发现监听器
     */
    interface DiscoveryListener {
        /**
         * 发现新能力时触发
         *
         * @param capability 发现的能力
         */
        void onCapabilityDiscovered(DiscoveredCapability capability);

        /**
         * 能力消失时触发
         *
         * @param capId 能力ID
         */
        void onCapabilityLost(String capId);

        /**
         * 发现错误时触发
         *
         * @param error 错误信息
         */
        void onDiscoveryError(String error);
    }
}
