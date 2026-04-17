# 第五册：安全与权限

## 目录

1. [权限模型](#1-权限模型)
2. [命令透传安全](#2-命令透传安全)
3. [审计与监控](#3-审计与监控)
4. [安全最佳实践](#4-安全最佳实践)

---

## 1. 权限模型

### 1.1 角色体系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          角色权限体系                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐                                                            │
│  │  installer  │  系统安装者                                                │
│  │  ─────────  │  ─────────────────────────────────────────────────────    │
│  │             │  • skill:install    - 安装基础技能包                       │
│  │             │  • skill:view       - 查看技能信息                         │
│  │             │  • system:init      - 初始化系统环境                       │
│  └─────────────┘                                                            │
│         │                                                                   │
│         ▼                                                                   │
│  ┌─────────────┐                                                            │
│  │    admin    │  系统管理员                                                │
│  │  ─────────  │  ─────────────────────────────────────────────────────    │
│  │             │  • capability:discover   - 发现场景技能                    │
│  │             │  • capability:install    - 安装能力                        │
│  │             │  • capability:distribute - 配置分发                        │
│  │             │  • scene:create          - 创建场景                        │
│  │             │  • scene:manage          - 管理场景                        │
│  │             │  • user:assign           - 分配用户                        │
│  │             │  • capability:view       - 查看能力                        │
│  │             │  • scene:view            - 查看场景                        │
│  └─────────────┘                                                            │
│         │                                                                   │
│         ▼                                                                   │
│  ┌─────────────┐                                                            │
│  │    leader   │  主导者                                                    │
│  │  ─────────  │  ─────────────────────────────────────────────────────    │
│  │             │  • scene:activate        - 激活场景                        │
│  │             │  • scene:manage          - 管理场景                        │
│  │             │  • scene:view            - 查看场景                        │
│  │             │  • key:generate          - 生成密钥                        │
│  │             │  • participant:manage    - 管理参与者                     │
│  │             │  • task:assign           - 分配任务                        │
│  └─────────────┘                                                            │
│         │                                                                   │
│         ▼                                                                   │
│  ┌─────────────┐                                                            │
│  │ collaborator│  协作者                                                    │
│  │  ─────────  │  ─────────────────────────────────────────────────────    │
│  │             │  • task:view     - 查看任务                                │
│  │             │  • task:execute  - 执行任务                                │
│  │             │  • task:submit   - 提交任务                                │
│  │             │  • scene:view    - 查看场景                                │
│  │             │  • todo:view     - 查看待办                                │
│  └─────────────┘                                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 权限粒度

```java
/**
 * 权限定义
 */
public final class Permissions {
    
    // Skill 权限
    public static final String SKILL_INSTALL = "skill:install";
    public static final String SKILL_UNINSTALL = "skill:uninstall";
    public static final String SKILL_START = "skill:start";
    public static final String SKILL_STOP = "skill:stop";
    public static final String SKILL_UPDATE = "skill:update";
    public static final String SKILL_VIEW = "skill:view";
    public static final String SKILL_EXECUTE = "skill:execute";
    
    // Capability 权限
    public static final String CAPABILITY_DISCOVER = "capability:discover";
    public static final String CAPABILITY_INSTALL = "capability:install";
    public static final String CAPABILITY_DISTRIBUTE = "capability:distribute";
    public static final String CAPABILITY_VIEW = "capability:view";
    public static final String CAPABILITY_EXECUTE = "capability:execute";
    
    // Scene 权限
    public static final String SCENE_CREATE = "scene:create";
    public static final String SCENE_ACTIVATE = "scene:activate";
    public static final String SCENE_MANAGE = "scene:manage";
    public static final String SCENE_VIEW = "scene:view";
    public static final String SCENE_DELETE = "scene:delete";
    
    // Task 权限
    public static final String TASK_VIEW = "task:view";
    public static final String TASK_EXECUTE = "task:execute";
    public static final String TASK_ASSIGN = "task:assign";
    public static final String TASK_SUBMIT = "task:submit";
    
    // System 权限
    public static final String SYSTEM_INIT = "system:init";
    public static final String SYSTEM_CONFIG = "system:config";
    public static final String SYSTEM_AUDIT = "system:audit";
}
```

### 1.3 权限校验流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          权限校验流程                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  用户请求                                                                    │
│     │                                                                       │
│     ▼                                                                       │
│  ┌─────────────┐                                                           │
│  │  提取 Token  │                                                           │
│  │  从 Header   │                                                           │
│  └──────┬──────┘                                                           │
│         │                                                                   │
│         ▼                                                                   │
│  ┌─────────────┐                                                           │
│  │  验证 Token  │                                                           │
│  │  有效性      │                                                           │
│  └──────┬──────┘                                                           │
│         │                                                                   │
│         ▼                                                                   │
│  ┌─────────────┐                                                           │
│  │  获取用户    │                                                           │
│  │  权限列表    │                                                           │
│  └──────┬──────┘                                                           │
│         │                                                                   │
│         ▼                                                                   │
│  ┌─────────────┐                                                           │
│  │  匹配所需    │                                                           │
│  │  权限        │                                                           │
│  └──────┬──────┘                                                           │
│         │                                                                   │
│    ┌────┴────┐                                                              │
│    ▼         ▼                                                              │
│  ┌─────┐   ┌─────┐                                                          │
│  │ 有  │   │ 无  │                                                          │
│  │权限 │   │权限 │                                                          │
│  └──┬──┘   └──┬──┘                                                          │
│     │         │                                                             │
│     ▼         ▼                                                             │
│  ┌─────────┐ ┌─────────┐                                                    │
│  │ 继续执行 │ │ 拒绝访问 │                                                    │
│  │         │ │ 403     │                                                    │
│  └─────────┘ └─────────┘                                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. 命令透传安全

### 2.1 安全风险

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          命令透传安全风险                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  风险 1: 权限提升攻击                                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 攻击者 (collaborator 角色)                                          │   │
│  │   │                                                                 │   │
│  │   ▼                                                                 │   │
│  │ skill exec admin-skill create-user --role=admin                     │   │
│  │   │                                                                 │   │
│  │   ▼                                                                 │   │
│  │ 如果没有白名单限制，collaborator 可以执行 admin 专属命令            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  风险 2: 命令注入攻击                                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ skill exec db-skill query --sql="SELECT * FROM users; DROP TABLE"   │   │
│  │   │                                                                 │   │
│  │   ▼                                                                 │   │
│  │ 如果没有参数过滤，SQL 注入可以破坏数据库                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  风险 3: 脚本注入攻击                                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ skill exec report-skill generate --template="<script>alert(1)</script>"│  │
│  │   │                                                                 │   │
│  │   ▼                                                                 │   │
│  │ XSS 攻击可以窃取用户会话                                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  风险 4: 路径遍历攻击                                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ skill exec file-skill read --path="../../../etc/passwd"             │   │
│  │   │                                                                 │   │
│  │   ▼                                                                 │   │
│  │ 可以读取系统敏感文件                                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 安全防护机制

```java
/**
 * 安全命令代理 - 完整实现
 */
@Component
public class SecureCommandProxy {
    
    private static final Logger log = LoggerFactory.getLogger(SecureCommandProxy.class);
    
    @Autowired
    private AuditLogService auditLog;
    
    /**
     * 命令白名单
     */
    private static final Map<String, Set<String>> COMMAND_WHITELIST = Map.of(
        "rag-skill", Set.of("reindex", "search", "upload", "delete"),
        "chart-skill", Set.of("refresh", "export", "config"),
        "db-skill", Set.of("migrate", "backup", "restore"),
        "report-skill", Set.of("generate", "schedule", "download")
    );
    
    /**
     * 参数白名单 - 每个命令允许的参数
     */
    private static final Map<String, Set<String>> PARAM_WHITELIST = Map.of(
        "rag-skill:reindex", Set.of("knowledgeBase", "incremental"),
        "rag-skill:search", Set.of("query", "limit", "filters"),
        "chart-skill:refresh", Set.of("chartId", "dataSource"),
        "db-skill:migrate", Set.of("version", "dryRun")
    );
    
    /**
     * 危险字符模式
     */
    private static final List<Pattern> DANGEROUS_PATTERNS = List.of(
        // 命令注入
        Pattern.compile("[;|&$`\\{}\\[\\]\\(\\)]"),
        // 路径遍历
        Pattern.compile("\\.\\./|\\.\\\\.\\\\|~\\/|%~"),
        // 空字节
        Pattern.compile("\\x00"),
        // 控制字符
        Pattern.compile("[\\x00-\\x1F\\x7F]")
    );
    
    /**
     * SQL 注入检测
     */
    private static final Pattern SQL_INJECTION = Pattern.compile(
        "\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|" +
        "TRUNCATE|REPLACE|MERGE|GRANT|REVOKE)\\b|(--|#|/\\*|;)",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 脚本注入检测
     */
    private static final Pattern SCRIPT_INJECTION = Pattern.compile(
        "<script|javascript:|on\\w+\\s*=|eval\\s*\\(|expression\\s*\\(",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 执行代理命令
     */
    public CommandResult<?> execute(String skillId, String command, 
                                     Map<String, Object> params,
                                     UserSession user) {
        String fullCommand = skillId + ":" + command;
        
        try {
            // 1. 白名单校验
            validateWhitelist(skillId, command);
            
            // 2. 参数白名单校验
            validateParamWhitelist(fullCommand, params.keySet());
            
            // 3. 参数值过滤
            Map<String, Object> filteredParams = filterParams(params);
            
            // 4. 权限校验
            validatePermission(user, skillId, command);
            
            // 5. 审计记录 - 执行前
            auditLog.record(AuditEvent.builder()
                .userId(user.getUserId())
                .action("COMMAND_PROXY_EXECUTE")
                .resource(fullCommand)
                .parameters(filteredParams)
                .status("STARTED")
                .timestamp(System.currentTimeMillis())
                .build()
            );
            
            // 6. 执行命令
            CommandResult<?> result = doExecute(skillId, command, filteredParams);
            
            // 7. 审计记录 - 执行成功
            auditLog.record(AuditEvent.builder()
                .userId(user.getUserId())
                .action("COMMAND_PROXY_EXECUTE")
                .resource(fullCommand)
                .status("SUCCESS")
                .timestamp(System.currentTimeMillis())
                .build()
            );
            
            return result;
            
        } catch (SecurityException e) {
            // 审计记录 - 安全拒绝
            auditLog.record(AuditEvent.builder()
                .userId(user.getUserId())
                .action("COMMAND_PROXY_EXECUTE")
                .resource(fullCommand)
                .status("DENIED")
                .error(e.getMessage())
                .timestamp(System.currentTimeMillis())
                .build()
            );
            
            logSecurityEvent(user, "SECURITY_VIOLATION", fullCommand, e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // 审计记录 - 执行失败
            auditLog.record(AuditEvent.builder()
                .userId(user.getUserId())
                .action("COMMAND_PROXY_EXECUTE")
                .resource(fullCommand)
                .status("FAILED")
                .error(e.getMessage())
                .timestamp(System.currentTimeMillis())
                .build()
            );
            
            throw e;
        }
    }
    
    /**
     * 白名单校验
     */
    private void validateWhitelist(String skillId, String command) {
        Set<String> allowedCommands = COMMAND_WHITELIST.get(skillId);
        if (allowedCommands == null || !allowedCommands.contains(command)) {
            throw new SecurityException(
                String.format("Command not in whitelist: %s:%s", skillId, command)
            );
        }
    }
    
    /**
     * 参数白名单校验
     */
    private void validateParamWhitelist(String fullCommand, Set<String> paramNames) {
        Set<String> allowedParams = PARAM_WHITELIST.get(fullCommand);
        if (allowedParams == null) {
            throw new SecurityException("No parameter whitelist for: " + fullCommand);
        }
        
        for (String param : paramNames) {
            if (!allowedParams.contains(param)) {
                throw new SecurityException("Parameter not allowed: " + param);
            }
        }
    }
    
    /**
     * 参数过滤
     */
    private Map<String, Object> filterParams(Map<String, Object> params) {
        Map<String, Object> filtered = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // 过滤键名
            for (Pattern pattern : DANGEROUS_PATTERNS) {
                if (pattern.matcher(key).find()) {
                    throw new SecurityException("Dangerous parameter key: " + key);
                }
            }
            
            // 过滤值
            if (value instanceof String) {
                String strValue = (String) value;
                
                // 检查危险字符
                for (Pattern pattern : DANGEROUS_PATTERNS) {
                    if (pattern.matcher(strValue).find()) {
                        throw new SecurityException("Dangerous characters in value: " + strValue);
                    }
                }
                
                // 检查 SQL 注入
                if (SQL_INJECTION.matcher(strValue).find()) {
                    throw new SecurityException("SQL injection detected");
                }
                
                // 检查脚本注入
                if (SCRIPT_INJECTION.matcher(strValue).find()) {
                    throw new SecurityException("Script injection detected");
                }
                
                filtered.put(key, strValue);
            } else {
                filtered.put(key, value);
            }
        }
        
        return filtered;
    }
    
    /**
     * 权限校验
     */
    private void validatePermission(UserSession user, String skillId, String command) {
        // 检查 Skill 执行权限
        String skillPermission = skillId + ":execute";
        String commandPermission = skillId + ":" + command;
        
        boolean hasPermission = user.getPermissions().contains(skillPermission) ||
                               user.getPermissions().contains(commandPermission) ||
                               user.getPermissions().contains("skill:execute:*");
        
        if (!hasPermission) {
            throw new SecurityException(
                String.format("No permission to execute: %s:%s", skillId, command)
            );
        }
    }
    
    /**
     * 记录安全事件
     */
    private void logSecurityEvent(UserSession user, String eventType, 
                                   String resource, String detail) {
        log.warn("[SECURITY] User: {}, Event: {}, Resource: {}, Detail: {}",
            user != null ? user.getUserId() : "anonymous",
            eventType,
            resource,
            detail
        );
    }
}
```

---

## 3. 审计与监控

### 3.1 审计日志

```java
/**
 * 审计事件
 */
public interface AuditEvent {
    
    String getEventId();
    
    String getUserId();
    
    String getAction();
    
    String getResource();
    
    Map<String, Object> getParameters();
    
    String getStatus();
    
    String getError();
    
    long getTimestamp();
    
    String getClientIp();
    
    String getUserAgent();
}

/**
 * 审计日志服务
 */
public interface AuditLogService {
    
    void record(AuditEvent event);
    
    List<AuditEvent> query(AuditQuery query);
    
    void export(AuditQuery query, OutputStream output);
}
```

### 3.2 监控指标

```java
/**
 * 安全监控指标
 */
public class SecurityMetrics {
    
    // 命令执行指标
    private final Counter commandExecutions;
    private final Counter commandFailures;
    private final Counter commandRejections;
    
    // 安全事件指标
    private final Counter securityViolations;
    private final Counter permissionDenials;
    private final Counter injectionAttempts;
    
    // 性能指标
    private final Timer commandExecutionTime;
    private final Gauge activeSessions;
    
    public void recordCommandExecution(String command, boolean success, long duration) {
        commandExecutions.increment();
        commandExecutionTime.record(duration, TimeUnit.MILLISECONDS);
        
        if (!success) {
            commandFailures.increment();
        }
    }
    
    public void recordSecurityViolation(String type, String userId) {
        securityViolations.increment();
        log.warn("Security violation: type={}, user={}", type, userId);
    }
}
```

---

## 4. 安全最佳实践

### 4.1 开发安全规范

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          安全开发规范                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 输入验证                                                                │
│     ✓ 所有用户输入必须经过验证                                              │
│     ✓ 使用白名单而非黑名单                                                  │
│     ✓ 验证数据类型、长度、格式                                              │
│     ✓ 对特殊字符进行转义                                                    │
│                                                                             │
│  2. 权限控制                                                                │
│     ✓ 最小权限原则                                                          │
│     ✓ 分层权限校验（CLI/Scene/Skill）                                       │
│     ✓ 定期审查权限配置                                                      │
│     ✓ 敏感操作需要二次确认                                                  │
│                                                                             │
│  3. 命令透传                                                                │
│     ✓ 严格的白名单机制                                                      │
│     ✓ 参数类型和范围校验                                                    │
│     ✓ 禁止直接执行系统命令                                                  │
│     ✓ 所有透传操作记录审计日志                                              │
│                                                                             │
│  4. 数据保护                                                                │
│     ✓ 敏感数据加密存储                                                      │
│     ✓ 传输层加密（TLS）                                                     │
│     ✓ 密钥安全存储（KMS）                                                   │
│     ✓ 定期轮换密钥                                                          │
│                                                                             │
│  5. 审计监控                                                                │
│     ✓ 记录所有敏感操作                                                      │
│     ✓ 实时告警异常行为                                                      │
│     ✓ 定期安全审计                                                          │
│     ✓ 保留审计日志至少 180 天                                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 安全配置检查清单

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 命令白名单已配置 | ☐ | 只允许必要的命令透传 |
| 参数过滤已启用 | ☐ | 过滤危险字符和注入攻击 |
| 权限校验已启用 | ☐ | 多层权限校验 |
| 审计日志已启用 | ☐ | 记录所有敏感操作 |
| 监控告警已配置 | ☐ | 实时告警安全事件 |
| TLS 已启用 | ☐ | 传输层加密 |
| 密钥安全存储 | ☐ | 使用 KMS 管理密钥 |
| 会话超时已配置 | ☐ | 30 分钟无操作自动退出 |

---

## 下一册预告

**第六册：集成指南**

将提供：
- Skill 开发指南
- CLI 扩展指南
- 最佳实践
- 示例代码

请继续阅读第六册了解如何扩展和集成。
