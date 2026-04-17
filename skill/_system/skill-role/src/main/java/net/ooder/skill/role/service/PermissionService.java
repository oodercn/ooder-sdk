package net.ooder.skill.role.service;

import java.util.List;
import net.ooder.skill.role.dto.*;

public interface PermissionService {
    
    List<PermissionDTO> findAll();
    
    PermissionDTO findById(String permissionId);
    
    List<PermissionDTO> findByRoleId(String roleId);
    
    List<PermissionDTO> findByType(String type);
    
    List<PermissionDTO> findByResource(String resource);
    
    List<PermissionDTO> getPermissionTree();
}
