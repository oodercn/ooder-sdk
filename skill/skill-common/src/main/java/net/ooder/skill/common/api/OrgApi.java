package net.ooder.skill.common.api;

import net.ooder.skill.common.model.OrgDepartment;
import net.ooder.skill.common.model.OrgUser;
import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.common.service.OrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/org")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class OrgApi {

    private static final Logger log = LoggerFactory.getLogger(OrgApi.class);

    @Autowired
    private OrgService orgService;

    @GetMapping("/users/current")
    public ResultModel<OrgUser> getCurrentUser() {
        log.info("[OrgApi] Getting current user");
        try {
            OrgUser user = orgService.getCurrentUser();
            if (user != null) {
                return ResultModel.success(user);
            }
            return ResultModel.notFound("用户不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error getting current user", e);
            return ResultModel.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResultModel<List<OrgUser>> getAllUsers() {
        log.info("[OrgApi] Getting all users");
        try {
            return ResultModel.success(orgService.getAllUsers());
        } catch (Exception e) {
            log.error("[OrgApi] Error getting users", e);
            return ResultModel.error(500, "获取用户列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/users/{userId}")
    public ResultModel<OrgUser> getUser(@PathVariable String userId) {
        log.info("[OrgApi] Getting user: {}", userId);
        try {
            OrgUser user = orgService.getUser(userId);
            if (user != null) {
                return ResultModel.success(user);
            }
            return ResultModel.notFound("用户不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error getting user", e);
            return ResultModel.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/users")
    public ResultModel<OrgUser> createUser(@RequestBody OrgUser user) {
        log.info("[OrgApi] Creating user: {}", user.getUserId());
        try {
            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                return ResultModel.badRequest("用户ID不能为空");
            }
            if (orgService.getUser(user.getUserId()) != null) {
                return ResultModel.conflict("用户已存在");
            }
            user.setCreateTime(System.currentTimeMillis());
            user.setUpdateTime(System.currentTimeMillis());
            orgService.addUser(user);
            return ResultModel.success(user);
        } catch (Exception e) {
            log.error("[OrgApi] Error creating user", e);
            return ResultModel.error(500, "创建用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/users/{userId}")
    public ResultModel<OrgUser> updateUser(@PathVariable String userId, @RequestBody OrgUser user) {
        log.info("[OrgApi] Updating user: {}", userId);
        try {
            OrgUser existing = orgService.getUser(userId);
            if (existing == null) {
                return ResultModel.notFound("用户不存在");
            }
            user.setUserId(userId);
            user.setCreateTime(existing.getCreateTime());
            user.setUpdateTime(System.currentTimeMillis());
            orgService.updateUser(user);
            return ResultModel.success(user);
        } catch (Exception e) {
            log.error("[OrgApi] Error updating user", e);
            return ResultModel.error(500, "更新用户失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResultModel<Boolean> deleteUser(@PathVariable String userId) {
        log.info("[OrgApi] Deleting user: {}", userId);
        try {
            boolean deleted = orgService.deleteUser(userId);
            if (deleted) {
                return ResultModel.success(true);
            }
            return ResultModel.notFound("用户不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error deleting user", e);
            return ResultModel.error(500, "删除用户失败: " + e.getMessage());
        }
    }

    @GetMapping("/users/current/stats")
    public ResultModel<Map<String, Object>> getCurrentUserStats() {
        log.info("[OrgApi] Getting current user stats");
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalScenes", 0);
        stats.put("activeTasks", 0);
        stats.put("completedTasks", 0);
        stats.put("pendingTodos", 0);
        return ResultModel.success(stats);
    }

    @GetMapping("/departments")
    public ResultModel<List<OrgDepartment>> getAllDepartments() {
        log.info("[OrgApi] Getting all departments");
        try {
            return ResultModel.success(orgService.getAllDepartments());
        } catch (Exception e) {
            log.error("[OrgApi] Error getting departments", e);
            return ResultModel.error(500, "获取部门列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/departments/{departmentId}")
    public ResultModel<OrgDepartment> getDepartment(@PathVariable String departmentId) {
        log.info("[OrgApi] Getting department: {}", departmentId);
        try {
            OrgDepartment dept = orgService.getDepartment(departmentId);
            if (dept != null) {
                return ResultModel.success(dept);
            }
            return ResultModel.notFound("部门不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error getting department", e);
            return ResultModel.error(500, "获取部门信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/departments")
    public ResultModel<OrgDepartment> createDepartment(@RequestBody OrgDepartment department) {
        log.info("[OrgApi] Creating department: {}", department.getDepartmentId());
        try {
            if (department.getDepartmentId() == null || department.getDepartmentId().isEmpty()) {
                return ResultModel.badRequest("部门ID不能为空");
            }
            if (orgService.getDepartment(department.getDepartmentId()) != null) {
                return ResultModel.conflict("部门已存在");
            }
            department.setCreateTime(System.currentTimeMillis());
            orgService.addDepartment(department);
            return ResultModel.success(department);
        } catch (Exception e) {
            log.error("[OrgApi] Error creating department", e);
            return ResultModel.error(500, "创建部门失败: " + e.getMessage());
        }
    }

    @PutMapping("/departments/{departmentId}")
    public ResultModel<OrgDepartment> updateDepartment(@PathVariable String departmentId, @RequestBody OrgDepartment department) {
        log.info("[OrgApi] Updating department: {}", departmentId);
        try {
            OrgDepartment existing = orgService.getDepartment(departmentId);
            if (existing == null) {
                return ResultModel.notFound("部门不存在");
            }
            department.setDepartmentId(departmentId);
            department.setCreateTime(existing.getCreateTime());
            orgService.updateDepartment(department);
            return ResultModel.success(department);
        } catch (Exception e) {
            log.error("[OrgApi] Error updating department", e);
            return ResultModel.error(500, "更新部门失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/departments/{departmentId}")
    public ResultModel<Boolean> deleteDepartment(@PathVariable String departmentId) {
        log.info("[OrgApi] Deleting department: {}", departmentId);
        try {
            boolean deleted = orgService.deleteDepartment(departmentId);
            if (deleted) {
                return ResultModel.success(true);
            }
            return ResultModel.notFound("部门不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error deleting department", e);
            return ResultModel.error(500, "删除部门失败: " + e.getMessage());
        }
    }

    @GetMapping("/tree")
    public ResultModel<List<Map<String, Object>>> getOrgTree() {
        log.info("[OrgApi] Getting org tree");
        try {
            return ResultModel.success(orgService.getOrgTree());
        } catch (Exception e) {
            log.error("[OrgApi] Error getting org tree", e);
            return ResultModel.error(500, "获取组织树失败: " + e.getMessage());
        }
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> getAvailableRoles() {
        log.info("[OrgApi] Getting available roles");
        try {
            return ResultModel.success(orgService.getAvailableRoles());
        } catch (Exception e) {
            log.error("[OrgApi] Error getting roles", e);
            return ResultModel.error(500, "获取角色列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/departments/{departmentId}/members")
    public ResultModel<List<OrgUser>> getDepartmentMembers(@PathVariable String departmentId) {
        log.info("[OrgApi] Getting members of department: {}", departmentId);
        try {
            OrgDepartment dept = orgService.getDepartment(departmentId);
            if (dept == null) {
                return ResultModel.notFound("部门不存在");
            }
            return ResultModel.success(orgService.getDepartmentMembers(departmentId));
        } catch (Exception e) {
            log.error("[OrgApi] Error getting department members", e);
            return ResultModel.error(500, "获取部门成员失败: " + e.getMessage());
        }
    }

    @PostMapping("/departments/{departmentId}/members/{userId}")
    public ResultModel<Boolean> addMemberToDepartment(@PathVariable String departmentId, @PathVariable String userId) {
        log.info("[OrgApi] Adding member {} to department {}", userId, departmentId);
        try {
            boolean added = orgService.addMemberToDepartment(departmentId, userId);
            if (added) {
                return ResultModel.success(true);
            }
            return ResultModel.badRequest("添加成员失败，部门或用户不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error adding member to department", e);
            return ResultModel.error(500, "添加成员失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/departments/{departmentId}/members/{userId}")
    public ResultModel<Boolean> removeMemberFromDepartment(@PathVariable String departmentId, @PathVariable String userId) {
        log.info("[OrgApi] Removing member {} from department {}", userId, departmentId);
        try {
            boolean removed = orgService.removeMemberFromDepartment(departmentId, userId);
            if (removed) {
                return ResultModel.success(true);
            }
            return ResultModel.badRequest("移除成员失败，部门或用户不存在");
        } catch (Exception e) {
            log.error("[OrgApi] Error removing member from department", e);
            return ResultModel.error(500, "移除成员失败: " + e.getMessage());
        }
    }
}
