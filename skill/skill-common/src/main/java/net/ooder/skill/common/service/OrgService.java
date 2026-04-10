package net.ooder.skill.common.service;

import net.ooder.skill.common.model.OrgUser;
import net.ooder.skill.common.model.OrgDepartment;
import net.ooder.skill.common.storage.JsonStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrgService {

    private static final Logger log = LoggerFactory.getLogger(OrgService.class);

    private final JsonStorageService storage;
    private final Map<String, OrgUser> users = new ConcurrentHashMap<>();
    private final Map<String, OrgDepartment> departments = new ConcurrentHashMap<>();

    public OrgService(JsonStorageService storage) {
        this.storage = storage;
    }

    @PostConstruct
    public void init() {
        loadFromStorage();
        initDefaultData();
    }

    @SuppressWarnings("unchecked")
    private void loadFromStorage() {
        Map<String, OrgUser> storedUsers = storage.getAll("users");
        if (storedUsers != null && !storedUsers.isEmpty()) {
            users.putAll(storedUsers);
            log.info("Loaded {} users from storage", users.size());
        }

        Map<String, OrgDepartment> storedDepts = storage.getAll("departments");
        if (storedDepts != null && !storedDepts.isEmpty()) {
            departments.putAll(storedDepts);
            log.info("Loaded {} departments from storage", departments.size());
        }
    }

    private void initDefaultData() {
        if (users.isEmpty()) {
            OrgUser admin = new OrgUser();
            admin.setUserId("user-admin-001");
            admin.setName("系统管理员");
            admin.setEmail("admin@example.com");
            admin.setDepartmentId("dept-it");
            admin.setRole("admin");
            admin.setTitle("系统管理员");
            admin.setCreateTime(System.currentTimeMillis());
            admin.setActive(true);
            users.put(admin.getUserId(), admin);

            OrgUser user = new OrgUser();
            user.setUserId("user-default-001");
            user.setName("默认用户");
            user.setEmail("user@example.com");
            user.setDepartmentId("dept-rd");
            user.setRole("user");
            user.setTitle("工程师");
            user.setCreateTime(System.currentTimeMillis());
            user.setActive(true);
            users.put(user.getUserId(), user);

            users.forEach((id, u) -> storage.put("users", id, u));
            log.info("Initialized {} default users", users.size());
        }

        if (departments.isEmpty()) {
            OrgDepartment rd = new OrgDepartment();
            rd.setDepartmentId("dept-rd");
            rd.setName("研发部");
            rd.setDescription("负责产品研发和技术创新");
            rd.setParentId(null);
            rd.setManagerId("user-admin-001");
            rd.setMemberIds(Arrays.asList("user-admin-001", "user-default-001"));
            rd.setCreateTime(System.currentTimeMillis());
            departments.put(rd.getDepartmentId(), rd);

            OrgDepartment it = new OrgDepartment();
            it.setDepartmentId("dept-it");
            it.setName("信息技术部");
            it.setDescription("负责系统运维和技术支持");
            it.setParentId(null);
            it.setManagerId("user-admin-001");
            it.setMemberIds(Arrays.asList("user-admin-001"));
            it.setCreateTime(System.currentTimeMillis());
            departments.put(it.getDepartmentId(), it);

            departments.forEach((id, dept) -> storage.put("departments", id, dept));
            log.info("Initialized {} default departments", departments.size());
        }
    }

    public OrgUser getUser(String userId) {
        return users.get(userId);
    }

    public OrgUser getCurrentUser() {
        if (!users.isEmpty()) {
            OrgUser admin = users.get("user-admin-001");
            if (admin != null) {
                return admin;
            }
            return users.values().iterator().next();
        }
        OrgUser defaultUser = new OrgUser();
        defaultUser.setUserId("default");
        defaultUser.setName("默认用户");
        defaultUser.setRole("user");
        defaultUser.setActive(true);
        return defaultUser;
    }

    public List<OrgUser> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<OrgUser> getUsersByDepartment(String departmentId) {
        List<OrgUser> result = new ArrayList<>();
        for (OrgUser user : users.values()) {
            if (departmentId.equals(user.getDepartmentId())) {
                result.add(user);
            }
        }
        return result;
    }

    public List<OrgUser> getUsersByRole(String role) {
        List<OrgUser> result = new ArrayList<>();
        for (OrgUser user : users.values()) {
            if (role.equals(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    public OrgDepartment getDepartment(String departmentId) {
        return departments.get(departmentId);
    }

    public List<OrgDepartment> getAllDepartments() {
        return new ArrayList<>(departments.values());
    }

    public List<OrgUser> getDepartmentMembers(String departmentId) {
        OrgDepartment dept = departments.get(departmentId);
        if (dept != null && dept.getMemberIds() != null) {
            List<OrgUser> members = new ArrayList<>();
            for (String memberId : dept.getMemberIds()) {
                OrgUser user = users.get(memberId);
                if (user != null) {
                    members.add(user);
                }
            }
            return members;
        }
        return new ArrayList<>();
    }

    public OrgUser getDepartmentManager(String departmentId) {
        OrgDepartment dept = departments.get(departmentId);
        if (dept != null && dept.getManagerId() != null) {
            return users.get(dept.getManagerId());
        }
        return null;
    }

    public void addUser(OrgUser user) {
        users.put(user.getUserId(), user);
        storage.put("users", user.getUserId(), user);
        log.info("Added user: {}", user.getUserId());
    }

    public void updateUser(OrgUser user) {
        users.put(user.getUserId(), user);
        storage.put("users", user.getUserId(), user);
        log.info("Updated user: {}", user.getUserId());
    }

    public boolean deleteUser(String userId) {
        OrgUser removed = users.remove(userId);
        if (removed != null) {
            storage.remove("users", userId);
            log.info("Deleted user: {}", userId);
            return true;
        }
        return false;
    }

    public void addDepartment(OrgDepartment department) {
        departments.put(department.getDepartmentId(), department);
        storage.put("departments", department.getDepartmentId(), department);
        log.info("Added department: {}", department.getDepartmentId());
    }

    public void updateDepartment(OrgDepartment department) {
        departments.put(department.getDepartmentId(), department);
        storage.put("departments", department.getDepartmentId(), department);
        log.info("Updated department: {}", department.getDepartmentId());
    }

    public boolean deleteDepartment(String departmentId) {
        OrgDepartment removed = departments.remove(departmentId);
        if (removed != null) {
            storage.remove("departments", departmentId);
            log.info("Deleted department: {}", departmentId);
            return true;
        }
        return false;
    }

    public boolean addMemberToDepartment(String departmentId, String userId) {
        OrgDepartment dept = departments.get(departmentId);
        OrgUser user = users.get(userId);
        
        if (dept == null || user == null) {
            return false;
        }
        
        List<String> memberIds = dept.getMemberIds();
        if (memberIds == null) {
            memberIds = new ArrayList<>();
        }
        
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
            dept.setMemberIds(memberIds);
            user.setDepartmentId(departmentId);
            
            storage.put("departments", departmentId, dept);
            storage.put("users", userId, user);
            log.info("Added member {} to department {}", userId, departmentId);
        }
        
        return true;
    }

    public boolean removeMemberFromDepartment(String departmentId, String userId) {
        OrgDepartment dept = departments.get(departmentId);
        OrgUser user = users.get(userId);
        
        if (dept == null || user == null) {
            return false;
        }
        
        List<String> memberIds = dept.getMemberIds();
        if (memberIds != null && memberIds.contains(userId)) {
            memberIds.remove(userId);
            dept.setMemberIds(memberIds);
            user.setDepartmentId(null);
            
            storage.put("departments", departmentId, dept);
            storage.put("users", userId, user);
            log.info("Removed member {} from department {}", userId, departmentId);
        }
        
        return true;
    }

    public List<Map<String, Object>> getOrgTree() {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        for (OrgDepartment dept : departments.values()) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", dept.getDepartmentId());
            node.put("name", dept.getName());
            node.put("type", "department");
            node.put("parentId", dept.getParentId());
            node.put("managerId", dept.getManagerId());
            
            List<Map<String, Object>> children = new ArrayList<>();
            List<OrgUser> members = getDepartmentMembers(dept.getDepartmentId());
            for (OrgUser user : members) {
                Map<String, Object> userNode = new HashMap<>();
                userNode.put("id", user.getUserId());
                userNode.put("name", user.getName());
                userNode.put("type", "user");
                userNode.put("role", user.getRole());
                userNode.put("title", user.getTitle());
                userNode.put("email", user.getEmail());
                userNode.put("parentId", dept.getDepartmentId());
                children.add(userNode);
            }
            node.put("children", children);
            
            tree.add(node);
        }
        
        return tree;
    }

    public List<Map<String, Object>> getAvailableRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        String[][] roleData = {
            {"manager", "管理者", "场景管理者，拥有完整管理权限"},
            {"employee", "员工", "普通员工，参与场景执行"},
            {"hr", "HR", "人力资源，管理人事相关"},
            {"admin", "管理员", "系统运维、能力管理、用户管理"},
            {"user", "普通用户", "场景参与、任务执行、业务流转"},
            {"developer", "开发者", "能力开发、测试、发布"},
            {"llm-assistant", "LLM助手", "AI分析助手"},
            {"coordinator", "协调Agent", "任务协调Agent"}
        };
        
        for (String[] data : roleData) {
            Map<String, Object> role = new HashMap<>();
            role.put("id", data[0]);
            role.put("name", data[1]);
            role.put("description", data[2]);
            roles.add(role);
        }
        
        return roles;
    }
}
