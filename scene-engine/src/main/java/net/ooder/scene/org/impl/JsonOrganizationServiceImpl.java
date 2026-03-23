package net.ooder.scene.org.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.SceneEventType;
import net.ooder.scene.event.org.OrganizationEvent;
import net.ooder.scene.org.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 组织架构服务 - JSON 文件实现
 * 
 * <p>基于 JSON 文件存储，适用于轻量级部署场景。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JsonOrganizationServiceImpl implements OrganizationService {
    
    private static final Logger log = LoggerFactory.getLogger(JsonOrganizationServiceImpl.class);
    
    private final ObjectMapper objectMapper;
    private final File dataDir;
    private final SceneEventPublisher eventPublisher;
    
    private final Map<String, OrgCompany> companies = new ConcurrentHashMap<>();
    private final Map<String, OrgDepartment> departments = new ConcurrentHashMap<>();
    private final Map<String, OrgUser> users = new ConcurrentHashMap<>();
    
    public JsonOrganizationServiceImpl(String dataPath) {
        this(dataPath, null);
    }
    
    public JsonOrganizationServiceImpl(String dataPath, SceneEventPublisher eventPublisher) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.dataDir = new File(dataPath);
        this.eventPublisher = eventPublisher;
        
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        loadData();
    }
    
    private void loadData() {
        loadCompanies();
        loadDepartments();
        loadUsers();
    }
    
    private void loadCompanies() {
        File file = new File(dataDir, "companies.json");
        if (file.exists()) {
            try {
                OrgCompany[] array = objectMapper.readValue(file, OrgCompany[].class);
                for (OrgCompany company : array) {
                    companies.put(company.getCompanyId(), company);
                }
                log.info("Loaded {} companies", companies.size());
            } catch (IOException e) {
                log.warn("Failed to load companies: {}", e.getMessage());
            }
        }
    }
    
    private void loadDepartments() {
        File file = new File(dataDir, "departments.json");
        if (file.exists()) {
            try {
                OrgDepartment[] array = objectMapper.readValue(file, OrgDepartment[].class);
                for (OrgDepartment dept : array) {
                    departments.put(dept.getDepartmentId(), dept);
                }
                log.info("Loaded {} departments", departments.size());
            } catch (IOException e) {
                log.warn("Failed to load departments: {}", e.getMessage());
            }
        }
    }
    
    private void loadUsers() {
        File file = new File(dataDir, "users.json");
        if (file.exists()) {
            try {
                OrgUser[] array = objectMapper.readValue(file, OrgUser[].class);
                for (OrgUser user : array) {
                    users.put(user.getUserId(), user);
                }
                log.info("Loaded {} users", users.size());
            } catch (IOException e) {
                log.warn("Failed to load users: {}", e.getMessage());
            }
        }
    }
    
    private void saveCompanies() {
        try {
            File file = new File(dataDir, "companies.json");
            objectMapper.writeValue(file, companies.values());
        } catch (IOException e) {
            log.error("Failed to save companies: {}", e.getMessage());
        }
    }
    
    private void saveDepartments() {
        try {
            File file = new File(dataDir, "departments.json");
            objectMapper.writeValue(file, departments.values());
        } catch (IOException e) {
            log.error("Failed to save departments: {}", e.getMessage());
        }
    }
    
    private void saveUsers() {
        try {
            File file = new File(dataDir, "users.json");
            objectMapper.writeValue(file, users.values());
        } catch (IOException e) {
            log.error("Failed to save users: {}", e.getMessage());
        }
    }
    
    // ========== 公司管理 ==========
    
    @Override
    public CompletableFuture<OrgCompany> createCompany(CreateCompanyRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String companyId = "company-" + System.currentTimeMillis();
            OrgCompany company = new OrgCompany(companyId, request.getName());
            company.setCode(request.getCode());
            company.setIndustry(request.getIndustry());
            company.setContactEmail(request.getContactEmail());
            company.setContactPhone(request.getContactPhone());
            company.setAddress(request.getAddress());
            company.setMaxUsers(request.getMaxUsers());
            company.setMaxDepartments(request.getMaxDepartments());
            
            companies.put(companyId, company);
            saveCompanies();
            
            log.info("Created company: {} ({})", company.getName(), companyId);
            publishAuditEvent(SceneEventType.ORG_COMPANY_CREATED, companyId, company.getName(), null, null, null, true);
            return company;
        });
    }
    
    @Override
    public CompletableFuture<OrgCompany> getCompany(String companyId) {
        return CompletableFuture.completedFuture(companies.get(companyId));
    }
    
    @Override
    public CompletableFuture<OrgCompany> updateCompany(String companyId, UpdateCompanyRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            OrgCompany company = companies.get(companyId);
            if (company == null) return null;
            
            if (request.getName() != null) company.setName(request.getName());
            if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
            if (request.getContactEmail() != null) company.setContactEmail(request.getContactEmail());
            if (request.getContactPhone() != null) company.setContactPhone(request.getContactPhone());
            if (request.getAddress() != null) company.setAddress(request.getAddress());
            company.setUpdateTime(System.currentTimeMillis());
            
            saveCompanies();
            publishAuditEvent(SceneEventType.ORG_COMPANY_UPDATED, companyId, company.getName(), null, null, null, true);
            return company;
        });
    }
    
    @Override
    public CompletableFuture<Void> deleteCompany(String companyId) {
        return CompletableFuture.runAsync(() -> {
            OrgCompany company = companies.get(companyId);
            String companyName = company != null ? company.getName() : null;
            
            companies.remove(companyId);
            departments.values().removeIf(d -> companyId.equals(d.getCompanyId()));
            users.values().removeIf(u -> companyId.equals(u.getCompanyId()));
            
            saveCompanies();
            saveDepartments();
            saveUsers();
            
            log.info("Deleted company: {}", companyId);
            publishAuditEvent(SceneEventType.ORG_COMPANY_DELETED, companyId, companyName, null, null, null, true);
        });
    }
    
    @Override
    public CompletableFuture<List<OrgCompany>> listCompanies(CompanyQuery query) {
        return CompletableFuture.completedFuture(new ArrayList<>(companies.values()));
    }
    
    // ========== 部门管理 ==========
    
    @Override
    public CompletableFuture<OrgDepartment> createDepartment(CreateDepartmentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String deptId = "dept-" + System.currentTimeMillis();
            OrgDepartment dept = new OrgDepartment(deptId, request.getCompanyId(), request.getName());
            dept.setDescription(request.getDescription());
            dept.setParentId(request.getParentId());
            dept.setManagerId(request.getManagerId());
            
            departments.put(deptId, dept);
            saveDepartments();
            
            log.info("Created department: {} ({})", dept.getName(), deptId);
            publishAuditEvent(SceneEventType.ORG_DEPARTMENT_CREATED, request.getCompanyId(), null, deptId, dept.getName(), null, true);
            return dept;
        });
    }
    
    @Override
    public CompletableFuture<OrgDepartment> getDepartment(String departmentId) {
        return CompletableFuture.completedFuture(departments.get(departmentId));
    }
    
    @Override
    public CompletableFuture<OrgDepartment> updateDepartment(String departmentId, UpdateDepartmentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            OrgDepartment dept = departments.get(departmentId);
            if (dept == null) return null;
            
            if (request.getName() != null) dept.setName(request.getName());
            if (request.getDescription() != null) dept.setDescription(request.getDescription());
            if (request.getManagerId() != null) dept.setManagerId(request.getManagerId());
            dept.setUpdateTime(System.currentTimeMillis());
            
            saveDepartments();
            publishAuditEvent(SceneEventType.ORG_DEPARTMENT_UPDATED, dept.getCompanyId(), null, departmentId, dept.getName(), null, true);
            return dept;
        });
    }
    
    @Override
    public CompletableFuture<Void> deleteDepartment(String departmentId) {
        return CompletableFuture.runAsync(() -> {
            OrgDepartment dept = departments.get(departmentId);
            String companyId = dept != null ? dept.getCompanyId() : null;
            String deptName = dept != null ? dept.getName() : null;
            
            departments.remove(departmentId);
            users.values().forEach(u -> {
                if (departmentId.equals(u.getDepartmentId())) {
                    u.setDepartmentId(null);
                }
            });
            
            saveDepartments();
            saveUsers();
            
            log.info("Deleted department: {}", departmentId);
            publishAuditEvent(SceneEventType.ORG_DEPARTMENT_DELETED, companyId, null, departmentId, deptName, null, true);
        });
    }
    
    @Override
    public CompletableFuture<List<OrgDepartment>> getDepartmentTree(String companyId) {
        return CompletableFuture.supplyAsync(() -> {
            List<OrgDepartment> result = new ArrayList<>();
            for (OrgDepartment dept : departments.values()) {
                if (companyId.equals(dept.getCompanyId())) {
                    result.add(dept);
                }
            }
            return result;
        });
    }
    
    @Override
    public CompletableFuture<List<OrgUser>> getDepartmentMembers(String departmentId) {
        return CompletableFuture.supplyAsync(() -> {
            List<OrgUser> result = new ArrayList<>();
            for (OrgUser user : users.values()) {
                if (departmentId.equals(user.getDepartmentId())) {
                    result.add(user);
                }
            }
            return result;
        });
    }
    
    // ========== 用户管理 ==========
    
    @Override
    public CompletableFuture<OrgUser> createUser(CreateUserRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String userId = "user-" + System.currentTimeMillis();
            OrgUser user = new OrgUser(userId, request.getName());
            user.setCompanyId(request.getCompanyId());
            user.setDepartmentId(request.getDepartmentId());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole(request.getRole());
            user.setTitle(request.getTitle());
            
            users.put(userId, user);
            saveUsers();
            
            log.info("Created user: {} ({})", user.getName(), userId);
            publishAuditEvent(SceneEventType.ORG_USER_CREATED, request.getCompanyId(), null, request.getDepartmentId(), null, userId, true);
            return user;
        });
    }
    
    @Override
    public CompletableFuture<OrgUser> getUser(String userId) {
        return CompletableFuture.completedFuture(users.get(userId));
    }
    
    @Override
    public CompletableFuture<OrgUser> updateUser(String userId, UpdateUserRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            OrgUser user = users.get(userId);
            if (user == null) return null;
            
            if (request.getName() != null) user.setName(request.getName());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getPhone() != null) user.setPhone(request.getPhone());
            if (request.getRole() != null) user.setRole(request.getRole());
            if (request.getTitle() != null) user.setTitle(request.getTitle());
            if (request.getDepartmentId() != null) user.setDepartmentId(request.getDepartmentId());
            user.setUpdateTime(System.currentTimeMillis());
            
            saveUsers();
            publishAuditEvent(SceneEventType.ORG_USER_UPDATED, user.getCompanyId(), null, user.getDepartmentId(), null, userId, true);
            return user;
        });
    }
    
    @Override
    public CompletableFuture<Void> deleteUser(String userId) {
        return CompletableFuture.runAsync(() -> {
            OrgUser user = users.get(userId);
            String companyId = user != null ? user.getCompanyId() : null;
            String deptId = user != null ? user.getDepartmentId() : null;
            
            users.remove(userId);
            saveUsers();
            log.info("Deleted user: {}", userId);
            publishAuditEvent(SceneEventType.ORG_USER_DELETED, companyId, null, deptId, null, userId, true);
        });
    }
    
    @Override
    public CompletableFuture<List<OrgUser>> listUsers(UserQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<OrgUser> result = new ArrayList<>();
            for (OrgUser user : users.values()) {
                if (query == null) {
                    result.add(user);
                } else {
                    if (query.getCompanyId() != null && !query.getCompanyId().equals(user.getCompanyId())) continue;
                    if (query.getDepartmentId() != null && !query.getDepartmentId().equals(user.getDepartmentId())) continue;
                    result.add(user);
                }
            }
            return result;
        });
    }
    
    @Override
    public CompletableFuture<OrgCompany> getUserCompany(String userId) {
        OrgUser user = users.get(userId);
        if (user == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(companies.get(user.getCompanyId()));
    }
    
    @Override
    public CompletableFuture<OrgDepartment> getUserDepartment(String userId) {
        OrgUser user = users.get(userId);
        if (user == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(departments.get(user.getDepartmentId()));
    }
    
    private void publishAuditEvent(SceneEventType eventType, String companyId, String companyName,
                                   String departmentId, String departmentName, String userId, boolean success) {
        if (eventPublisher != null) {
            OrganizationEvent event = OrganizationEvent.builder()
                .source(this)
                .eventType(eventType)
                .companyId(companyId)
                .companyName(companyName)
                .departmentId(departmentId)
                .departmentName(departmentName)
                .userId(userId)
                .success(success)
                .build();
            eventPublisher.publish(event);
        }
    }
}
