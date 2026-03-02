package net.ooder.skills.api;

import java.util.Map;

/**
 * 能力信息接口
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface CapabilityInfo {

    String getCapabilityId();

    String getCapabilityName();

    String getDescription();

    String getInterfaceId();

    String getEndpoint();

    String getProtocol();

    Map<String, Object> getConfig();

    CapabilityType getType();

    /**
     * 获取版本 (新增)
     *
     * @return 版本号
     */
    String getVersion();

    enum CapabilityType {
        SERVICE,
        STORAGE,
        COMMUNICATION,
        COMPUTATION
    }
}
