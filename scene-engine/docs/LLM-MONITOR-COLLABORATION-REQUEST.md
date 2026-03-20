# SE SDK 协作需求: LLM 监控四级统计

## 1. 协作概述

**发起方**: MVP 团队  
**接收方**: SE 团队  
**主题**: LLM 监控四级统计 - SE SDK 接口支持  
**优先级**: P1  
**日期**: 2026-03-20  
**状态**: 🔴 待确认

---

## 2. 背景说明

### 2.1 目标需求

实现**公司-部门-人员-模块**四级统计体系：

```
公司级 (Company Level)
    └── 部门级 (Department Level)
            └── 人员级 (User Level)
                    └── 模块级 (Module Level)
```

### 2.2 现有问题

1. 缺少组织架构维度（公司、部门）
2. 缺少模块/能力维度的深度统计
3. 接口统计与自行统计未整合
4. 无多租户隔离能力

---

## 3. SE SDK 需求清单

### 3.1 需求概览

| 需求ID | 需求描述 | 优先级 | 说明 |
|--------|----------|--------|------|
| LLM-REQ-001 | LLM 调用审计接口扩展 | P0 | 支持四级维度记录 |
| LLM-REQ-002 | 组织架构管理接口 | P0 | 公司、部门、用户管理 |
| LLM-REQ-003 | 四级统计聚合接口 | P0 | 按维度聚合统计 |
| LLM-REQ-004 | 多租户隔离支持 | P1 | 数据隔离能力 |

---

### 3.2 LLM-REQ-001: LLM 调用审计接口扩展

**需求描述**:

扩展现有 `AuditService`，支持 LLM 调用的四级维度记录。

**接口定义**:

```java
package net.ooder.scene.llm.audit;

/**
 * LLM 调用审计服务
 */
public interface LlmAuditService {
    
    /**
     * 记录 LLM 调用
     */
    void logLlmCall(LlmCallContext context, LlmCallResult result);
    
    /**
     * 查询 LLM 调用日志
     */
    CompletableFuture<List<LlmCallLog>> queryLlmLogs(LlmLogQuery query);
    
    /**
     * 获取用户 LLM 统计
     */
    CompletableFuture<UserLlmStats> getUserLlmStats(String userId, long startTime, long endTime);
    
    /**
     * 获取部门 LLM 统计
     */
    CompletableFuture<DepartmentLlmStats> getDepartmentLlmStats(String departmentId, long startTime, long endTime);
    
    /**
     * 获取公司 LLM 统计
     */
    CompletableFuture<CompanyLlmStats> getCompanyLlmStats(String companyId, long startTime, long endTime);
    
    /**
     * 获取模块 LLM 统计
     */
    CompletableFuture<ModuleLlmStats> getModuleLlmStats(String moduleId, String userId, long startTime, long endTime);
}

/**
 * LLM 调用上下文
 */
public class LlmCallContext {
    private String companyId;
    private String companyName;
    private String departmentId;
    private String departmentName;
    private String userId;
    private String userName;
    private String sceneId;
    private String sceneName;
    private String capabilityId;
    private String capabilityName;
    private String moduleId;
    private String moduleName;
    private String businessType;
    private String clientIp;
    private String sessionId;
    private String requestId;
}

/**
 * LLM 调用结果
 */
public class LlmCallResult {
    private String providerId;
    private String providerName;
    private String model;
    private String requestType;
    private int inputTokens;
    private int outputTokens;
    private int totalTokens;
    private double cost;
    private long latency;
    private String status;
    private String errorMessage;
    private Map<String, Object> metadata;
}
```

---

### 3.3 LLM-REQ-002: 组织架构管理接口

**需求描述**:

提供公司、部门、用户的 CRUD 接口，支持多租户。

**接口定义**:

```java
package net.ooder.scene.org;

/**
 * 组织架构服务
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

/**
 * 公司实体
 */
public class OrgCompany {
    private String companyId;
    private String name;
    private String code;
    private String industry;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private int maxUsers;
    private int maxDepartments;
    private long createTime;
    private long updateTime;
    private boolean active;
    private Map<String, Object> settings;
}

/**
 * 部门实体
 */
public class OrgDepartment {
    private String departmentId;
    private String companyId;
    private String name;
    private String description;
    private String parentId;
    private String managerId;
    private List<String> memberIds;
    private int level;
    private String fullPath;
    private long createTime;
    private long updateTime;
}

/**
 * 用户实体
 */
public class OrgUser {
    private String userId;
    private String companyId;
    private String departmentId;
    private String departmentName;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String title;
    private String avatar;
    private List<String> permissions;
    private long createTime;
    private long updateTime;
    private boolean active;
}
```

---

### 3.4 LLM-REQ-003: 四级统计聚合接口

**需求描述**:

提供四级维度的统计聚合能力。

**接口定义**:

```java
package net.ooder.scene.llm.stats;

/**
 * LLM 统计聚合服务
 */
public interface LlmStatsAggregationService {
    
    /**
     * 获取公司级统计
     */
    CompletableFuture<CompanyLlmStats> getCompanyStats(String companyId, StatsTimeRange timeRange);
    
    /**
     * 获取部门级统计
     */
    CompletableFuture<DepartmentLlmStats> getDepartmentStats(String departmentId, StatsTimeRange timeRange);
    
    /**
     * 获取用户级统计
     */
    CompletableFuture<UserLlmStats> getUserStats(String userId, StatsTimeRange timeRange);
    
    /**
     * 获取模块级统计
     */
    CompletableFuture<ModuleLlmStats> getModuleStats(String moduleId, String userId, StatsTimeRange timeRange);
    
    /**
     * 获取公司下所有部门排名
     */
    CompletableFuture<List<DepartmentLlmStats>> getDepartmentRanking(String companyId, StatsTimeRange timeRange, int limit);
    
    /**
     * 获取部门下所有用户排名
     */
    CompletableFuture<List<UserLlmStats>> getUserRanking(String departmentId, StatsTimeRange timeRange, int limit);
    
    /**
     * 获取用户下所有模块排名
     */
    CompletableFuture<List<ModuleLlmStats>> getModuleRanking(String userId, StatsTimeRange timeRange, int limit);
    
    /**
     * 刷新统计缓存
     */
    CompletableFuture<Void> refreshStats(String companyId);
}

/**
 * 统计时间范围
 */
public class StatsTimeRange {
    private long startTime;
    private long endTime;
    private StatsGranularity granularity; // HOUR, DAY, WEEK, MONTH
}

/**
 * 公司级统计
 */
public class CompanyLlmStats {
    private String companyId;
    private String companyName;
    
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    private long totalInputTokens;
    private long totalOutputTokens;
    private long totalTokens;
    
    private double totalCost;
    private double monthToDateCost;
    private double budgetLimit;
    private double budgetUsedPercent;
    
    private double avgLatency;
    private long maxLatency;
    private long minLatency;
    
    private long todayCalls;
    private long weekCalls;
    private long monthCalls;
    
    private List<DepartmentLlmStats> topDepartments;
    
    private long statsTime;
    private long startTime;
    private long endTime;
}

/**
 * 部门级统计
 */
public class DepartmentLlmStats {
    private String departmentId;
    private String departmentName;
    private String companyId;
    
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    private double totalCost;
    private double budgetLimit;
    
    private List<UserLlmStats> topUsers;
    
    private long statsTime;
}

/**
 * 用户级统计
 */
public class UserLlmStats {
    private String userId;
    private String userName;
    private String departmentId;
    private String departmentName;
    
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    private double totalCost;
    private double quotaLimit;
    private double quotaUsed;
    
    private List<ModuleLlmStats> moduleStats;
    
    private long statsTime;
}

/**
 * 模块级统计
 */
public class ModuleLlmStats {
    private String moduleId;
    private String moduleName;
    private String moduleType; // SCENE, CAPABILITY, TOOL
    private String userId;
    
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    
    private long totalTokens;
    private long totalInputTokens;
    private long totalOutputTokens;
    
    private double totalCost;
    private double avgLatency;
    
    private Map<String, Long> providerDistribution;
    private Map<String, Long> modelDistribution;
    
    private long statsTime;
}
```

---

### 3.5 LLM-REQ-004: 多租户隔离支持

**需求描述**:

支持多租户数据隔离，确保不同公司的数据互不可见。

**接口定义**:

```java
package net.ooder.scene.tenant;

/**
 * 租户上下文
 */
public class TenantContext {
    
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    
    /**
     * 设置当前租户
     */
    public static void setCurrentTenant(String companyId) {
        CURRENT_TENANT.set(companyId);
    }
    
    /**
     * 获取当前租户
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    
    /**
     * 清除租户上下文
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}

/**
 * 租户隔离拦截器
 */
public interface TenantIsolationInterceptor {
    
    /**
     * 检查数据访问权限
     */
    boolean checkAccess(String userId, String resourceCompanyId);
    
    /**
     * 过滤租户数据
     */
    <T> List<T> filterByTenant(List<T> data, String companyId);
}
```

---

## 4. 数据模型扩展

### 4.1 LLM 调用日志扩展

需要在现有 `LlmCallLog` 中添加以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `companyId` | String | 公司ID |
| `companyName` | String | 公司名称 |
| `departmentId` | String | 部门ID |
| `departmentName` | String | 部门名称 |
| `userId` | String | 用户ID |
| `userName` | String | 用户名称 |
| `sceneId` | String | 场景ID |
| `sceneName` | String | 场景名称 |
| `capabilityId` | String | 能力ID |
| `capabilityName` | String | 能力名称 |
| `moduleId` | String | 模块ID |
| `moduleName` | String | 模块名称 |
| `businessType` | String | 业务类型 |
| `clientIp` | String | 客户端IP |
| `sessionId` | String | 会话ID |
| `requestId` | String | 请求ID |

---

## 5. 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                     MVP 应用层                               │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ LlmMonitorController                                     ││
│  │ ├── /api/llm-stats/company/{id}                         ││
│  │ ├── /api/llm-stats/department/{id}                      ││
│  │ ├── /api/llm-stats/user/{id}                            ││
│  │ └── /api/llm-stats/module/{id}                          ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     SE SDK 层                                │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ LlmAuditService │  │OrganizationService│                 │
│  │ - logLlmCall()  │  │ - createCompany()│                 │
│  │ - queryLlmLogs()│  │ - createDepartment()               │
│  │ - getStats()    │  │ - createUser()   │                 │
│  └─────────────────┘  └─────────────────┘                  │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ LlmStatsAggregationService                               ││
│  │ - getCompanyStats()                                      ││
│  │ - getDepartmentStats()                                   ││
│  │ - getUserStats()                                         ││
│  │ - getModuleStats()                                       ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     数据存储层                               │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ 实时统计 (Redis) │  │ 历史统计 (DB)   │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. 时间计划

| 阶段 | 任务 | 预计时间 | 状态 |
|------|------|----------|------|
| Phase 1 | 组织架构接口实现 | 3-5 天 | 待开始 |
| Phase 2 | LLM 审计接口扩展 | 2-3 天 | 待开始 |
| Phase 3 | 统计聚合服务实现 | 3-5 天 | 待开始 |
| Phase 4 | 多租户隔离支持 | 2-3 天 | 待开始 |
| **总计** | - | **10-16 天** | - |

---

## 7. 验收标准

### 7.1 功能验收

- [ ] 组织架构 CRUD 接口可用
- [ ] LLM 调用日志包含四级维度
- [ ] 四级统计聚合接口可用
- [ ] 多租户数据隔离验证通过

### 7.2 性能验收

- [ ] 统计查询响应时间 < 500ms
- [ ] 日志写入不影响主流程性能
- [ ] 聚合计算支持增量更新

---

## 8. 状态

- [x] 需求文档完成
- [ ] SE 团队确认
- [ ] 开始实施

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**最后更新**: 2026-03-20
