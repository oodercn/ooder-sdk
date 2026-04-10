package net.ooder.skill.common.spi;

import net.ooder.skill.common.spi.org.DepartmentInfo;

import java.util.List;

public interface OrganizationService {
    
    DepartmentInfo getDepartment(String departmentId);
    
    List<DepartmentInfo> getChildDepartments(String parentDepartmentId);
    
    List<String> getDepartmentMembers(String departmentId);
    
    String getDepartmentManager(String departmentId);
    
    List<DepartmentInfo> getUserHierarchy(String userId);
}
