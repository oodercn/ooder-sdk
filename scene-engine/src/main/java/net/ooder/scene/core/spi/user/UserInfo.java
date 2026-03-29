package net.ooder.scene.core.spi.user;

import java.util.Map;

/**
 * 用户信息接口
 * 
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface UserInfo {

    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    String getUserId();

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    String getUsername();

    /**
     * 获取显示名称
     * 
     * @return 显示名称
     */
    String getDisplayName();

    /**
     * 获取邮箱
     * 
     * @return 邮箱
     */
    String getEmail();

    /**
     * 获取电话
     * 
     * @return 电话
     */
    String getPhone();

    /**
     * 获取部门ID
     * 
     * @return 部门ID
     */
    String getDepartmentId();

    /**
     * 获取组织ID
     * 
     * @return 组织ID
     */
    String getOrganizationId();

    /**
     * 获取扩展属性
     * 
     * @return 扩展属性Map
     */
    Map<String, Object> getAttributes();
}
