package net.ooder.skill.role.controller;

import net.ooder.skill.role.dto.*;
import net.ooder.skill.role.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/role-management")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class RoleManagementController {

    private static final Logger log = LoggerFactory.getLogger(RoleManagementController.class);

    private final Map<String, RoleDTO> roleStore = new HashMap<>();
    private final Map<String, UserInfoDTO> userStore = new HashMap<>();

    @GetMapping("/roles")
    public ResultModel<List<RoleDTO>> getAllRoles() {
        log.info("[RoleManagementController] Get all roles");
        
        if (roleStore.isEmpty()) {
            RoleDTO adminRole = new RoleDTO();
            adminRole.setId("admin");
            adminRole.setName("Administrator");
            adminRole.setDescription("Full access to all features");
            adminRole.setPermissions(Arrays.asList("read", "write", "delete", "admin"));
            roleStore.put("admin", adminRole);
            
            RoleDTO userRole = new RoleDTO();
            userRole.setId("user");
            userRole.setName("User");
            userRole.setDescription("Standard user access");
            userRole.setPermissions(Arrays.asList("read", "write"));
            roleStore.put("user", userRole);
        }
        
        return ResultModel.success(new ArrayList<>(roleStore.values()));
    }

    @PostMapping("/roles")
    public ResultModel<RoleDTO> createRole(@RequestBody CreateRoleRequest request) {
        log.info("[RoleManagementController] Create role: {}", request.getName());
        String id = UUID.randomUUID().toString();
        
        RoleDTO role = new RoleDTO();
        role.setId(id);
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        role.setType(request.getType());
        role.setPermissions(request.getPermissions());
        role.setCreateTime(System.currentTimeMillis());
        roleStore.put(id, role);
        return ResultModel.success(role);
    }

    @GetMapping("/roles/{id}")
    public ResultModel<RoleDTO> getRole(@PathVariable String id) {
        log.info("[RoleManagementController] Get role: {}", id);
        RoleDTO role = roleStore.get(id);
        if (role == null) {
            return ResultModel.notFound("Role not found: " + id);
        }
        return ResultModel.success(role);
    }

    @PutMapping("/roles/{id}")
    public ResultModel<RoleDTO> updateRole(@PathVariable String id, @RequestBody UpdateRoleRequest request) {
        log.info("[RoleManagementController] Update role: {}", id);
        RoleDTO role = roleStore.get(id);
        if (role == null) {
            return ResultModel.notFound("Role not found: " + id);
        }
        if (request.getName() != null) role.setName(request.getName());
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        if (request.getStatus() != null) role.setStatus(request.getStatus());
        if (request.getPermissions() != null) role.setPermissions(request.getPermissions());
        role.setUpdateTime(System.currentTimeMillis());
        roleStore.put(id, role);
        return ResultModel.success(role);
    }

    @DeleteMapping("/roles/{id}")
    public ResultModel<Boolean> deleteRole(@PathVariable String id) {
        log.info("[RoleManagementController] Delete role: {}", id);
        if (!roleStore.containsKey(id)) {
            return ResultModel.notFound("Role not found: " + id);
        }
        roleStore.remove(id);
        return ResultModel.success(true);
    }

    @GetMapping("/users")
    public ResultModel<List<UserInfoDTO>> getAllUsers() {
        log.info("[RoleManagementController] Get all users");
        
        if (userStore.isEmpty()) {
            UserInfoDTO user1 = new UserInfoDTO();
            user1.setUserId("user-001");
            user1.setUsername("admin");
            user1.setNickname("Admin");
            user1.setEmail("admin@ooder.net");
            user1.setRoles(Arrays.asList("admin"));
            userStore.put("user-001", user1);
            
            UserInfoDTO user2 = new UserInfoDTO();
            user2.setUserId("user-002");
            user2.setUsername("user");
            user2.setNickname("User");
            user2.setEmail("user@ooder.net");
            user2.setRoles(Arrays.asList("user"));
            userStore.put("user-002", user2);
        }
        
        return ResultModel.success(new ArrayList<>(userStore.values()));
    }

    @GetMapping("/users/{id}")
    public ResultModel<UserInfoDTO> getUser(@PathVariable String id) {
        log.info("[RoleManagementController] Get user: {}", id);
        UserInfoDTO user = userStore.get(id);
        if (user == null) {
            return ResultModel.notFound("User not found: " + id);
        }
        return ResultModel.success(user);
    }

    @PutMapping("/users/{id}")
    public ResultModel<UserInfoDTO> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        log.info("[RoleManagementController] Update user: {}", id);
        UserInfoDTO user = userStore.get(id);
        if (user == null) {
            return ResultModel.notFound("User not found: " + id);
        }
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getDepartmentName() != null) user.setDepartmentName(request.getDepartmentName());
        userStore.put(id, user);
        return ResultModel.success(user);
    }

    @DeleteMapping("/users/{id}")
    public ResultModel<Boolean> deleteUser(@PathVariable String id) {
        log.info("[RoleManagementController] Delete user: {}", id);
        if (!userStore.containsKey(id)) {
            return ResultModel.notFound("User not found: " + id);
        }
        userStore.remove(id);
        return ResultModel.success(true);
    }

    @PostMapping("/users/{userId}/bind-role/{roleId}")
    public ResultModel<UserInfoDTO> bindUserToRole(@PathVariable String userId, @PathVariable String roleId) {
        log.info("[RoleManagementController] Bind role {} to user {}", roleId, userId);
        
        UserInfoDTO user = userStore.get(userId);
        if (user == null) {
            user = new UserInfoDTO();
            user.setUserId(userId);
            userStore.put(userId, user);
        }
        
        List<String> roles = user.getRoles();
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(roleId)) {
            roles.add(roleId);
        }
        user.setRoles(roles);
        
        return ResultModel.success(user);
    }

    @GetMapping("/roles/{roleId}/menus")
    public ResultModel<List<String>> getRoleMenus(@PathVariable String roleId) {
        log.info("[RoleManagementController] Get menus for role: {}", roleId);
        
        List<String> menus = new ArrayList<>();
        RoleDTO role = roleStore.get(roleId);
        if (role != null && role.getMenuIds() != null) {
            menus = role.getMenuIds();
        }
        
        return ResultModel.success(menus);
    }

    @PutMapping("/roles/{roleId}/menus")
    public ResultModel<Boolean> setRoleMenus(@PathVariable String roleId, @RequestBody List<String> menuIds) {
        log.info("[RoleManagementController] Set menus for role: {}, count: {}", roleId, menuIds != null ? menuIds.size() : 0);
        
        RoleDTO role = roleStore.get(roleId);
        if (role == null) {
            return ResultModel.notFound("Role not found: " + roleId);
        }
        
        role.setMenuIds(menuIds);
        role.setUpdateTime(System.currentTimeMillis());
        
        return ResultModel.success(true);
    }

    @GetMapping("/roles/{roleId}/users")
    public ResultModel<List<UserInfoDTO>> getUsersByRole(@PathVariable String roleId) {
        log.info("[RoleManagementController] Get users by role: {}", roleId);
        
        List<UserInfoDTO> users = new ArrayList<>();
        for (UserInfoDTO user : userStore.values()) {
            if (user.getRoles() != null && user.getRoles().contains(roleId)) {
                users.add(user);
            }
        }
        
        return ResultModel.success(users);
    }

    @PostMapping("/users")
    public ResultModel<UserInfoDTO> createUser(@RequestBody CreateUserRequest request) {
        log.info("[RoleManagementController] Create user: {}", request.getUsername());
        String id = UUID.randomUUID().toString();
        
        UserInfoDTO user = new UserInfoDTO();
        user.setUserId(id);
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setCreateTime(System.currentTimeMillis());
        userStore.put(id, user);
        return ResultModel.success(user);
    }

    @GetMapping("/types")
    public ResultModel<List<RoleTypeDTO>> listRoleTypes() {
        log.info("[RoleManagementController] List role types");
        
        List<RoleTypeDTO> types = new ArrayList<>();
        
        RoleTypeDTO type1 = new RoleTypeDTO();
        type1.setCode("system");
        type1.setName("系统角色");
        type1.setDescription("系统内置角色");
        types.add(type1);
        
        RoleTypeDTO type2 = new RoleTypeDTO();
        type2.setCode("custom");
        type2.setName("自定义角色");
        type2.setDescription("用户自定义角色");
        types.add(type2);
        
        RoleTypeDTO type3 = new RoleTypeDTO();
        type3.setCode("business");
        type3.setName("业务角色");
        type3.setDescription("业务相关角色");
        types.add(type3);
        
        return ResultModel.success(types);
    }

    @GetMapping("/statuses")
    public ResultModel<List<RoleStatusDTO>> listRoleStatuses() {
        log.info("[RoleManagementController] List role statuses");
        
        List<RoleStatusDTO> statuses = new ArrayList<>();
        
        RoleStatusDTO status1 = new RoleStatusDTO();
        status1.setCode("active");
        status1.setName("启用");
        status1.setDescription("角色已启用");
        statuses.add(status1);
        
        RoleStatusDTO status2 = new RoleStatusDTO();
        status2.setCode("inactive");
        status2.setName("禁用");
        status2.setDescription("角色已禁用");
        statuses.add(status2);
        
        return ResultModel.success(statuses);
    }
}
