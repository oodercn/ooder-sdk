package net.ooder.skills.api;

import java.util.List;

/**
 * 本地能力仓库接口
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface LocalCapabilityRepository {

    void scanLocalCapabilities();

    void registerCapability(CapabilityInfo capability);

    void unregisterCapability(String capabilityId);

    CapabilityInfo findCapability(String capabilityId);

    List<CapabilityInfo> findCapabilitiesByScene(String sceneName);

    List<CapabilityInfo> findCapabilitiesByType(CapabilityInfo.CapabilityType type);

    boolean hasCapability(String capabilityId);

    /**
     * 获取能力版本 (新增)
     *
     * @param capabilityId 能力ID
     * @return 版本号
     */
    String getCapabilityVersion(String capabilityId);

    List<CapabilityInfo> getAllCapabilities();

    int getCapabilityCount();
}
