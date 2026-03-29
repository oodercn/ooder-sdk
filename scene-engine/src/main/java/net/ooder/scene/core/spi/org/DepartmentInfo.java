package net.ooder.scene.core.spi.org;

import java.util.Map;

/**
 * 部门信息接口
 * 
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface DepartmentInfo {

    /**
     * 获取部门ID
     * 
     * @return 部门ID
     */
    String getDepartmentId();

    /**
     * 获取部门名称
     * 
     * @return 部门名称
     */
    String getName();

    /**
     * 获取父部门ID
     * 
     * @return 父部门ID
     */
    String getParentId();

    /**
     * 获取组织ID
     * 
     * @return 组织ID
     */
    String getOrganizationId();

    /**
     * 获取成员数量
     * 
     * @return 成员数量
     */
    int getMemberCount();

    /**
     * 获取扩展属性
     * 
     * @return 扩展属性Map
     */
    Map<String, Object> getAttributes();
}
