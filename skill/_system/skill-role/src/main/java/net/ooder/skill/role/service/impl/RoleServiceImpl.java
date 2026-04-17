package net.ooder.skill.role.service.impl;

import net.ooder.skill.role.dto.*;
import net.ooder.skill.role.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {

    private final Map<String, RoleDTO> roleStore = new HashMap<>();
    private final Map<String, UserInfoDTO> userStore = new HashMap<>();
    private final Map<String, Set<String>> userRoles = new HashMap<>();

    @Override
    public RoleDTO create(RoleDTO role) {
        return createRole(role);
    }

    @Override
    public RoleDTO createRole(RoleDTO role) {
        if (role.getRoleId() == null) {
            role.setRoleId(UUID.randomUUID().toString());
        }
        roleStore.put(role.getRoleId(), role);
        return role;
    }

    @Override
    public RoleDTO update(RoleDTO role) {
        return updateRole(role.getRoleId(), role);
    }

    @Override
    public RoleDTO updateRole(String roleId, RoleDTO role) {
        roleStore.put(roleId, role);
        return role;
    }

    @Override
    public void delete(String roleId) {
        deleteRole(roleId);
    }

    @Override
    public boolean deleteRole(String roleId) {
        roleStore.remove(roleId);
        return true;
    }

    @Override
    public RoleDTO findById(String roleId) {
        return getRole(roleId);
    }

    @Override
    public RoleDTO getRole(String roleId) {
        return roleStore.get(roleId);
    }

    @Override
    public List<RoleDTO> findAll() {
        return getAllRoles();
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return new ArrayList<>(roleStore.values());
    }

    @Override
    public List<RoleDTO> findByOrgId(String orgId) {
        return new ArrayList<>();
    }

    @Override
    public List<RoleDTO> findByType(String type) {
        return new ArrayList<>();
    }

    @Override
    public void updatePermissions(String roleId, List<String> permissionIds) {
    }

    @Override
    public void assignRoleToUser(String userId, String roleId) {
        bindUserToRole(userId, roleId);
    }

    @Override
    public UserInfoDTO bindUserToRole(String userId, String roleId) {
        userRoles.computeIfAbsent(userId, k -> new HashSet<>()).add(roleId);
        return userStore.get(userId);
    }

    @Override
    public void removeRoleFromUser(String userId, String roleId) {
        Set<String> roles = userRoles.get(userId);
        if (roles != null) {
            roles.remove(roleId);
        }
    }

    @Override
    public List<RoleDTO> getUserRoles(String userId) {
        Set<String> roles = userRoles.get(userId);
        if (roles == null) return new ArrayList<>();
        List<RoleDTO> result = new ArrayList<>();
        for (String roleId : roles) {
            RoleDTO role = roleStore.get(roleId);
            if (role != null) result.add(role);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRoleUsers(String roleId) {
        return new ArrayList<>();
    }

    @Override
    public List<UserInfoDTO> getUsersByRole(String roleId) {
        return new ArrayList<>();
    }

    @Override
    public UserInfoDTO createUser(String name, String email, String orgRole, String departmentId) {
        UserInfoDTO user = new UserInfoDTO();
        user.setUserId(UUID.randomUUID().toString());
        user.setNickname(name);
        user.setEmail(email);
        userStore.put(user.getUserId(), user);
        return user;
    }

    @Override
    public UserInfoDTO setUserPassword(String userId, String password) {
        return userStore.get(userId);
    }

    @Override
    public List<UserInfoDTO> getAllUsers() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public UserInfoDTO getUserById(String userId) {
        return userStore.get(userId);
    }

    @Override
    public Map<String, Object> getRoleWithUsers(String roleId) {
        Map<String, Object> result = new HashMap<>();
        result.put("role", roleStore.get(roleId));
        result.put("users", getUsersByRole(roleId));
        return result;
    }

    @Override
    public Map<String, Object> getFullConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("roles", getAllRoles());
        config.put("users", getAllUsers());
        return config;
    }
}
