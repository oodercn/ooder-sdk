package net.ooder.skill.role.service;

import java.util.List;
import java.util.Map;
import net.ooder.skill.role.dto.*;

public interface RoleService {
    
    RoleDTO create(RoleDTO role);
    
    RoleDTO createRole(RoleDTO role);
    
    RoleDTO update(RoleDTO role);
    
    RoleDTO updateRole(String roleId, RoleDTO role);
    
    void delete(String roleId);
    
    boolean deleteRole(String roleId);
    
    RoleDTO findById(String roleId);
    
    RoleDTO getRole(String roleId);
    
    List<RoleDTO> findAll();
    
    List<RoleDTO> getAllRoles();
    
    List<RoleDTO> findByOrgId(String orgId);
    
    List<RoleDTO> findByType(String type);
    
    void updatePermissions(String roleId, List<String> permissionIds);
    
    void assignRoleToUser(String userId, String roleId);
    
    UserInfoDTO bindUserToRole(String userId, String roleId);
    
    void removeRoleFromUser(String userId, String roleId);
    
    List<RoleDTO> getUserRoles(String userId);
    
    List<Map<String, Object>> getRoleUsers(String roleId);
    
    List<UserInfoDTO> getUsersByRole(String roleId);
    
    UserInfoDTO createUser(String name, String email, String orgRole, String departmentId);
    
    UserInfoDTO setUserPassword(String userId, String password);
    
    List<UserInfoDTO> getAllUsers();
    
    UserInfoDTO getUserById(String userId);
    
    Map<String, Object> getRoleWithUsers(String roleId);
    
    Map<String, Object> getFullConfig();
}
