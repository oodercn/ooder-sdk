package net.ooder.scene.org;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 组织架构服务接口
 * 
 * <p>提供公司、部门、用户的 CRUD 接口，支持多租户。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface OrganizationService {
    
    // ========== 公司管理 ==========
    
    /**
     * 创建公司
     */
    CompletableFuture<OrgCompany> createCompany(CreateCompanyRequest request);
    
    /**
     * 获取公司
     */
    CompletableFuture<OrgCompany> getCompany(String companyId);
    
    /**
     * 更新公司
     */
    CompletableFuture<OrgCompany> updateCompany(String companyId, UpdateCompanyRequest request);
    
    /**
     * 删除公司
     */
    CompletableFuture<Void> deleteCompany(String companyId);
    
    /**
     * 获取公司列表
     */
    CompletableFuture<List<OrgCompany>> listCompanies(CompanyQuery query);
    
    // ========== 部门管理 ==========
    
    /**
     * 创建部门
     */
    CompletableFuture<OrgDepartment> createDepartment(CreateDepartmentRequest request);
    
    /**
     * 获取部门
     */
    CompletableFuture<OrgDepartment> getDepartment(String departmentId);
    
    /**
     * 更新部门
     */
    CompletableFuture<OrgDepartment> updateDepartment(String departmentId, UpdateDepartmentRequest request);
    
    /**
     * 删除部门
     */
    CompletableFuture<Void> deleteDepartment(String departmentId);
    
    /**
     * 获取部门树
     */
    CompletableFuture<List<OrgDepartment>> getDepartmentTree(String companyId);
    
    /**
     * 获取部门成员
     */
    CompletableFuture<List<OrgUser>> getDepartmentMembers(String departmentId);
    
    // ========== 用户管理 ==========
    
    /**
     * 创建用户
     */
    CompletableFuture<OrgUser> createUser(CreateUserRequest request);
    
    /**
     * 获取用户
     */
    CompletableFuture<OrgUser> getUser(String userId);
    
    /**
     * 更新用户
     */
    CompletableFuture<OrgUser> updateUser(String userId, UpdateUserRequest request);
    
    /**
     * 删除用户
     */
    CompletableFuture<Void> deleteUser(String userId);
    
    /**
     * 获取用户列表
     */
    CompletableFuture<List<OrgUser>> listUsers(UserQuery query);
    
    /**
     * 获取用户所属公司
     */
    CompletableFuture<OrgCompany> getUserCompany(String userId);
    
    /**
     * 获取用户所属部门
     */
    CompletableFuture<OrgDepartment> getUserDepartment(String userId);
}
