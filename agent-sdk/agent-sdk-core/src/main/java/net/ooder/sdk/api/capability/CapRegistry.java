package net.ooder.sdk.api.capability;

import java.util.List;
import java.util.Map;

/**
 * CAP 能力注册表接口
 *
 * <p>遵循 v0.8.0 架构,统一管理所有 Skill 的能力</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface CapRegistry {

    void register(Capability capability) throws CapRegistryException;

    void unregister(String capId) throws CapRegistryException;

    Capability findById(String capId);

    Capability findByAddress(CapAddress address);

    List<Capability> findByDomain(String domainId);

    List<Capability> findByType(CapabilityType type);

    List<Capability> findBySkill(String skillId);

    List<Capability> findByTag(String tag);

    List<Capability> findAll();

    boolean hasCapability(String capId);

    boolean isAddressOccupied(CapAddress address);

    void updateStatus(String capId, CapabilityStatus status) throws CapRegistryException;

    CapabilityStatus getStatus(String capId);

    CapAddress allocateAddress(String domainId) throws CapRegistryException;

    void releaseAddress(CapAddress address);

    DomainStats getDomainStats(String domainId);

    List<String> getAllDomains();

    void addListener(CapRegistryListener listener);

    void removeListener(CapRegistryListener listener);

    void clear();

    int size();

    class DomainStats {
        private String domainId;
        private int totalCapabilities;
        private int enabledCapabilities;
        private int healthyCapabilities;
        private Map<String, Integer> typeDistribution;

        public String getDomainId() { return domainId; }
        public void setDomainId(String domainId) { this.domainId = domainId; }
        public int getTotalCapabilities() { return totalCapabilities; }
        public void setTotalCapabilities(int totalCapabilities) { this.totalCapabilities = totalCapabilities; }
        public int getEnabledCapabilities() { return enabledCapabilities; }
        public void setEnabledCapabilities(int enabledCapabilities) { this.enabledCapabilities = enabledCapabilities; }
        public int getHealthyCapabilities() { return healthyCapabilities; }
        public void setHealthyCapabilities(int healthyCapabilities) { this.healthyCapabilities = healthyCapabilities; }
        public Map<String, Integer> getTypeDistribution() { return typeDistribution; }
        public void setTypeDistribution(Map<String, Integer> typeDistribution) { this.typeDistribution = typeDistribution; }
    }
}
