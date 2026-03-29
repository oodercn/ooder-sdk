package net.ooder.scene.core.spi.org;

import java.util.List;
import java.util.Map;

/**
 * 组织服务接口 - MVP实现此接口
 * 
 * <p>提供组织架构信息查询功能，供执行器调用。</p>
 * 
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface OrganizationService {

    /**
     * 获取部门信息
     * 
     * @param departmentId 部门ID
     * @return 部门信息
     */
    DepartmentInfo getDepartment(String departmentId);

    /**
     * 获取部门成员
     * 
     * @param departmentId 部门ID
     * @return 成员用户ID列表
     */
    List<String> getDepartmentMembers(String departmentId);

    /**
     * 获取部门及子部门所有成员
     * 
     * @param departmentId 部门ID
     * @return 所有成员用户ID列表
     */
    List<String> getAllDepartmentMembers(String departmentId);

    /**
     * 获取组织信息
     * 
     * @param organizationId 组织ID
     * @return 组织信息
     */
    OrganizationInfo getOrganization(String organizationId);

    /**
     * 获取组织下所有部门
     * 
     * @param organizationId 组织ID
     * @return 部门ID列表
     */
    List<String> getOrganizationDepartments(String organizationId);
}
