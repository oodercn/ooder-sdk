package net.ooder.scene.core.spi.org;

import java.util.Map;

/**
 * 组织信息接口
 * 
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface OrganizationInfo {

    /**
     * 获取组织ID
     * 
     * @return 组织ID
     */
    String getOrganizationId();

    /**
     * 获取组织名称
     * 
     * @return 组织名称
     */
    String getName();

    /**
     * 获取组织编码
     * 
     * @return 组织编码
     */
    String getCode();

    /**
     * 获取扩展属性
     * 
     * @return 扩展属性Map
     */
    Map<String, Object> getAttributes();
}
