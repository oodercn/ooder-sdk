package net.ooder.skill.common.repository;

import net.ooder.skill.common.model.OrgUser;

import java.util.List;
import java.util.Optional;

public interface OrgUserRepository {
    
    OrgUser save(OrgUser user);
    
    Optional<OrgUser> findById(String userId);
    
    Optional<OrgUser> findByPlatformAndExternalId(String platform, String externalId);
    
    List<OrgUser> findByDepartmentId(String departmentId);
    
    List<OrgUser> findAll();
    
    List<OrgUser> search(String keyword);
    
    void deleteById(String userId);
    
    void deleteByPlatform(String platform);
    
    long count();
    
    long countByDepartmentId(String departmentId);
    
    void updateDepartment(String userId, String newDepartmentId);
}
