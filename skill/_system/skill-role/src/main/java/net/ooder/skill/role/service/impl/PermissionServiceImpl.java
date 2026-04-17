package net.ooder.skill.role.service.impl;

import net.ooder.skill.role.dto.*;
import net.ooder.skill.role.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final Map<String, PermissionDTO> permissionStore = new HashMap<>();

    public PermissionServiceImpl() {
        initDefaultPermissions();
    }

    private void initDefaultPermissions() {
        addPermission("user:read", "用户读取", "user", "read");
        addPermission("user:write", "用户写入", "user", "write");
        addPermission("role:read", "角色读取", "role", "read");
        addPermission("role:write", "角色写入", "role", "write");
        addPermission("menu:read", "菜单读取", "menu", "read");
        addPermission("menu:write", "菜单写入", "menu", "write");
    }

    private void addPermission(String id, String name, String resource, String action) {
        PermissionDTO perm = new PermissionDTO();
        perm.setPermissionId(id);
        perm.setName(name);
        perm.setResource(resource);
        perm.setAction(action);
        permissionStore.put(id, perm);
    }

    @Override
    public List<PermissionDTO> findAll() {
        return new ArrayList<>(permissionStore.values());
    }

    @Override
    public PermissionDTO findById(String permissionId) {
        return permissionStore.get(permissionId);
    }

    @Override
    public List<PermissionDTO> findByRoleId(String roleId) {
        return new ArrayList<>();
    }

    @Override
    public List<PermissionDTO> findByType(String type) {
        return new ArrayList<>();
    }

    @Override
    public List<PermissionDTO> findByResource(String resource) {
        List<PermissionDTO> result = new ArrayList<>();
        for (PermissionDTO perm : permissionStore.values()) {
            if (resource.equals(perm.getResource())) {
                result.add(perm);
            }
        }
        return result;
    }

    @Override
    public List<PermissionDTO> getPermissionTree() {
        return new ArrayList<>(permissionStore.values());
    }
}
