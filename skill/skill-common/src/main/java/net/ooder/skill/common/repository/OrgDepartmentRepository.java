package net.ooder.skill.common.repository;

import net.ooder.skill.common.model.OrgDepartment;

import java.util.List;
import java.util.Optional;

public interface OrgDepartmentRepository {
    
    OrgDepartment save(OrgDepartment department);
    
    Optional<OrgDepartment> findById(String departmentId);
    
    Optional<OrgDepartment> findByPlatformAndExternalId(String platform, String externalId);
    
    List<OrgDepartment> findByParentId(String parentId);
    
    List<OrgDepartment> findAll();
    
    List<OrgDepartment> findRootDepartments();
    
    void deleteById(String departmentId);
    
    void deleteByPlatform(String platform);
    
    long count();
    
    long countByParentId(String parentId);
}
