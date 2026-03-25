# 审计服务适配器

## 概述

`AuditServiceAdapter` 是一个适配器类，将 `net.ooder.scene.audit.AuditService` 适配为 `net.ooder.scene.core.security.AuditService` 接口，解决两个模块审计服务接口不一致的问题。

**包路径**: `net.ooder.scene.core.security.AuditServiceAdapter`

**实现接口**: `net.ooder.scene.core.security.AuditService`

---

## 问题背景

SE SDK 中存在两个不同的 `AuditService` 接口：

| 接口 | 包路径 | 用途 |
|------|--------|------|
| `AuditService` | `net.ooder.scene.audit` | 审计模块的审计服务 |
| `AuditService` | `net.ooder.scene.core.security` | 安全模块的审计服务 |

`AuditLogPersistenceService` 实现的是 `audit` 包的接口，但 `SecureSkillService` 需要的是 `security` 包的接口。

---

## 解决方案

创建 `AuditServiceAdapter` 作为适配器：

```java
@Service
public class AuditServiceAdapter implements AuditService {

    private final net.ooder.scene.audit.AuditService delegate;

    public AuditServiceAdapter(net.ooder.scene.audit.AuditService auditService) {
        this.delegate = auditService;
    }
    
    // 适配方法实现...
}
```

---

## 核心方法

### logOperation

记录操作日志。

```java
@Override
public void logOperation(
    OperationContext context,
    String operation,
    String resource,
    String resourceId,
    OperationResult result,
    Map<String, Object> details
)
```

**参数**:
- `context` - 操作上下文（用户、会话、IP等）
- `operation` - 操作类型
- `resource` - 资源类型
- `resourceId` - 资源ID
- `result` - 操作结果（SUCCESS/FAILURE/DENIED/TIMEOUT）
- `details` - 扩展详情

### queryLogs

查询审计日志。

```java
@Override
public CompletableFuture<List<AuditLog>> queryLogs(AuditLogQuery query)
```

### exportLogs

导出审计日志。

```java
@Override
public CompletableFuture<AuditExportResult> exportLogs(AuditLogQuery query)
```

### getUserStats

获取用户操作统计。

```java
@Override
public CompletableFuture<UserOperationStats> getUserStats(
    String userId, 
    long startTime, 
    long endTime
)
```

### getResourceStats

获取资源访问统计。

```java
@Override
public CompletableFuture<ResourceAccessStats> getResourceStats(
    String resourceType, 
    String resourceId
)
```

---

## 使用示例

### Spring Boot 自动注入

```java
@Service
public class SecureSkillService {
    
    private final AuditService auditService;
    
    // 自动注入 AuditServiceAdapter
    public SecureSkillService(AuditService auditService) {
        this.auditService = auditService;
    }
    
    public void executeSecureOperation(String skillId, String userId) {
        OperationContext context = new OperationContext();
        context.setUserId(userId);
        context.setSkillId(skillId);
        context.setTimestamp(System.currentTimeMillis());
        
        try {
            // 执行操作...
            
            auditService.logOperation(
                context,
                "EXECUTE",
                "SKILL",
                skillId,
                OperationResult.SUCCESS,
                null
            );
        } catch (Exception e) {
            auditService.logOperation(
                context,
                "EXECUTE",
                "SKILL",
                skillId,
                OperationResult.FAILURE,
                Map.of("error", e.getMessage())
            );
            throw e;
        }
    }
}
```

---

## OperationContext 结构

```java
public class OperationContext {
    private String userId;          // 操作用户ID
    private String userName;        // 用户名
    private String sessionId;       // 会话ID
    private String ipAddress;       // 客户端IP
    private String userAgent;       // 客户端标识
    private String sceneId;         // 场景ID
    private String groupId;         // 场景组ID
    private String skillId;         // 技能ID
    private long timestamp;         // 操作时间
    private String requestId;       // 请求ID
    private Map<String, Object> extra;  // 扩展信息
}
```

---

## OperationResult 枚举

```java
public enum OperationResult {
    SUCCESS,   // 成功
    FAILURE,   // 失败
    DENIED,    // 拒绝
    TIMEOUT    // 超时
}
```

---

## AuditLogQuery 结构

```java
public class AuditLogQuery {
    private String userId;          // 用户ID
    private String operation;       // 操作类型
    private String resource;        // 资源类型
    private String resourceId;      // 资源ID
    private long startTime;         // 开始时间
    private long endTime;           // 结束时间
    private int pageNum;            // 页码
    private int pageSize;           // 每页大小
    private String format;          // 导出格式
}
```

---

## 适配逻辑

### 日志转换

```java
private net.ooder.scene.core.AuditLog convertToAuditLog(OperationContext context, ...) {
    net.ooder.scene.core.AuditLog auditLog = new net.ooder.scene.core.AuditLog();
    auditLog.setLogId(UUID.randomUUID().toString());
    auditLog.setUserId(context.getUserId());
    auditLog.setUserName(context.getUserName());
    auditLog.setEventType(resource);
    auditLog.setAction(operation);
    auditLog.setTarget(resourceId);
    auditLog.setResult(result.name());
    auditLog.setTimestamp(System.currentTimeMillis());
    auditLog.setSource(context.getSceneId());
    auditLog.setIpAddress(context.getIpAddress());
    return auditLog;
}
```

### 查询转换

```java
private AuditLogFilter convertFilter(AuditLogQuery query) {
    AuditLogFilter filter = new AuditLogFilter();
    filter.setUserId(query.getUserId());
    filter.setEventType(query.getOperation());
    filter.setStartTime(query.getStartTime());
    filter.setEndTime(query.getEndTime());
    return filter;
}
```

---

## 配置

### Spring Boot 配置

适配器已标注 `@Service` 注解，Spring Boot 会自动扫描并注册：

```java
@Service
public class AuditServiceAdapter implements AuditService {
    // ...
}
```

### 条件装配

如果需要条件装配：

```java
@Configuration
public class AuditConfig {
    
    @Bean
    @ConditionalOnBean(net.ooder.scene.audit.AuditService.class)
    public AuditService auditServiceAdapter(
            net.ooder.scene.audit.AuditService delegate) {
        return new AuditServiceAdapter(delegate);
    }
}
```

---

## 注意事项

1. **自动注入**: Spring Boot 会自动注入适配器，无需额外配置
2. **接口隔离**: 使用 `AuditServiceAdapter` 时，确保注入的是 `security` 包的接口
3. **日志转换**: 适配器会自动转换两个接口的数据结构
4. **异步操作**: `queryLogs`、`exportLogs` 等方法返回 `CompletableFuture`

---

## 相关文档

- [场景配置加载](../11-scene-config/01-scene-config-loader.md)
- [SPI服务暴露](../09-spi/01-service-provider.md)
