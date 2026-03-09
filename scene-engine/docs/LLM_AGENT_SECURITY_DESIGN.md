# LLM Agent 安全设计规范

**版本**: v2.4.0  
**日期**: 2026-03-07  
**状态**: 安全规范

---

## 一、概述

### 1.1 安全挑战

LLM Agent 在进行数据操作时面临以下安全挑战：

| 挑战 | 说明 |
|------|------|
| **双用户身份** | LLM-USER（AI身份）和 user（人类用户）同时存在 |
| **操作审计** | 需要记录两个用户的操作轨迹 |
| **权限控制** | 两个用户可能有不同的权限范围 |
| **跨场景通讯** | A2A（Agent-to-Agent）安全通讯 |
| **会话同步** | SSE 与 JDSServer 的状态同步 |

### 1.2 安全架构

```
┌─────────────────────────────────────────────────────────────┐
│  安全架构                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  用户层                                              │   │
│  │  ├── user (人类用户)                                 │   │
│  │  └── LLM-USER (AI 用户)                              │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  安全层                                              │   │
│  │  ├── 双用户认证 (DualUserAuth)                       │   │
│  │  ├── 权限控制 (PermissionControl)                    │   │
│  │  ├── 审计日志 (AuditLog)                             │   │
│  │  └── 会话同步 (SessionSync)                          │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  通讯层                                              │   │
│  │  ├── SSE (Server-Sent Events)                        │   │
│  │  ├── JDSServer (Java Data Sync Server)               │   │
│  │  └── Agent-Command (A2A 安全通道)                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、双用户安全模型

### 2.1 用户身份定义

| 身份 | 说明 | ID 格式 |
|------|------|---------|
| **user** | 人类用户 | `user-{userId}` |
| **LLM-USER** | AI 用户 | `llm-{modelId}-{sessionId}` |

### 2.2 双用户上下文

```java
/**
 * 双用户安全上下文
 */
public class DualUserContext {
    
    /**
     * 人类用户信息
     */
    private UserInfo user;
    
    /**
     * LLM 用户信息
     */
    private LlmUserInfo llmUser;
    
    /**
     * 会话信息
     */
    private SessionInfo session;
    
    /**
     * 操作来源
     */
    private OperationSource source;
    
    public enum OperationSource {
        USER_DIRECT,        // 用户直接操作
        USER_VIA_LLM,       // 用户通过 LLM 操作
        LLM_AUTONOMOUS,     // LLM 自主操作
        LLM_DELEGATED       // LLM 代理操作
    }
}

/**
 * 用户信息
 */
@Data
public class UserInfo {
    
    private String userId;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private String department;
    private String sessionId;
}

/**
 * LLM 用户信息
 */
@Data
public class LlmUserInfo {
    
    private String llmUserId;           // llm-{modelId}-{sessionId}
    private String modelId;             // 模型ID
    private String modelName;           // 模型名称
    private String provider;            // 提供商
    private String sessionId;           // 会话ID
    private String conversationId;      // 对话ID
    private List<String> capabilities;  // 授权能力
    private SecurityLevel securityLevel; // 安全级别
    private long tokenLimit;            // Token 限制
    private long tokenUsed;             // 已用 Token
    
    public enum SecurityLevel {
        LOW,        // 低安全级别：仅读取公开数据
        MEDIUM,     // 中安全级别：可操作用户授权数据
        HIGH        // 高安全级别：可执行敏感操作（需二次确认）
    }
}
```

### 2.3 双用户认证注解

```java
/**
 * 双用户认证注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DualUserAuth {
    
    /**
     * 允许的操作来源
     */
    OperationSource[] allowedSources() default {
        OperationSource.USER_DIRECT,
        OperationSource.USER_VIA_LLM
    };
    
    /**
     * 最小安全级别
     */
    LlmUserInfo.SecurityLevel minSecurityLevel() default LlmUserInfo.SecurityLevel.MEDIUM;
    
    /**
     * 是否需要用户确认
     */
    boolean requireUserConfirm() default false;
    
    /**
     * 是否记录审计日志
     */
    boolean auditLog() default true;
    
    /**
     * 敏感操作标记
     */
    boolean sensitive() default false;
}

/**
 * 权限检查注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * 用户所需权限
     */
    String[] userPermissions() default {};
    
    /**
     * LLM 所需能力
     */
    String[] llmCapabilities() default {};
    
    /**
     * 数据范围检查
     */
    String dataScopeCheck() default "";
}
```

### 2.4 双用户认证拦截器

```java
/**
 * 双用户认证拦截器
 */
@Component
public class DualUserAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private DualUserAuthService dualUserAuthService;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Override
    public boolean preHandle(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        DualUserAuth auth = handlerMethod.getMethodAnnotation(DualUserAuth.class);
        RequirePermission perm = handlerMethod.getMethodAnnotation(RequirePermission.class);
        
        if (auth == null && perm == null) {
            return true;
        }
        
        // 1. 解析双用户上下文
        DualUserContext context = dualUserAuthService.resolveContext(request);
        
        // 2. 验证操作来源
        if (auth != null) {
            if (!Arrays.asList(auth.allowedSources()).contains(context.getSource())) {
                throw new SecurityException("Operation source not allowed: " + context.getSource());
            }
        }
        
        // 3. 验证权限
        if (perm != null) {
            validatePermissions(context, perm);
        }
        
        // 4. 敏感操作确认
        if (auth != null && auth.requireUserConfirm()) {
            boolean confirmed = requestUserConfirm(context, request);
            if (!confirmed) {
                throw new SecurityException("User confirmation required");
            }
        }
        
        // 5. 存储上下文
        request.setAttribute("dualUserContext", context);
        
        return true;
    }
    
    @Override
    public void afterCompletion(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler, 
            Exception ex) {
        
        DualUserAuth auth = ((HandlerMethod) handler).getMethodAnnotation(DualUserAuth.class);
        
        if (auth != null && auth.auditLog()) {
            DualUserContext context = (DualUserContext) request.getAttribute("dualUserContext");
            auditLogService.log(context, request, response, ex);
        }
    }
    
    private void validatePermissions(DualUserContext context, RequirePermission perm) {
        // 验证用户权限
        for (String permission : perm.userPermissions()) {
            if (!context.getUser().getPermissions().contains(permission)) {
                throw new SecurityException("User permission denied: " + permission);
            }
        }
        
        // 验证 LLM 能力
        if (context.getLlmUser() != null) {
            for (String capability : perm.llmCapabilities()) {
                if (!context.getLlmUser().getCapabilities().contains(capability)) {
                    throw new SecurityException("LLM capability denied: " + capability);
                }
            }
        }
    }
}
```

---

## 三、审计日志机制

### 3.1 审计日志结构

```java
/**
 * 审计日志实体
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {
    
    @Id
    private String logId;
    
    // 双用户信息
    private String userId;              // 人类用户ID
    private String userName;            // 人类用户名
    private String llmUserId;           // LLM 用户ID
    private String llmModelId;          // LLM 模型ID
    
    // 操作信息
    private String operation;           // 操作类型
    private String operationSource;     // 操作来源
    private String resourceType;        // 资源类型
    private String resourceId;          // 资源ID
    private String action;              // 具体动作
    
    // 请求信息
    private String requestPath;         // 请求路径
    private String requestMethod;       // 请求方法
    private String requestParams;       // 请求参数（脱敏）
    private String requestBody;         // 请求体（脱敏）
    
    // 响应信息
    private int responseStatus;         // 响应状态
    private String responseSummary;     // 响应摘要
    
    // 安全信息
    private String sessionId;           // 会话ID
    private String clientIp;            // 客户端IP
    private String userAgent;           // 用户代理
    private String securityLevel;       // 安全级别
    
    // 结果信息
    private boolean success;            // 是否成功
    private String errorMessage;        // 错误信息
    private long durationMs;            // 耗时
    
    // 时间信息
    private LocalDateTime timestamp;    // 时间戳
    private String traceId;             // 链路追踪ID
}
```

### 3.2 审计日志服务

```java
/**
 * 审计日志服务
 */
@Service
public class AuditLogService {
    
    @Autowired
    private AuditLogRepository repository;
    
    @Autowired
    private DataMaskService maskService;
    
    /**
     * 记录审计日志
     */
    @Async
    public void log(
            DualUserContext context,
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception) {
        
        AuditLog auditLog = new AuditLog();
        auditLog.setLogId(UUID.randomUUID().toString());
        
        // 双用户信息
        if (context.getUser() != null) {
            auditLog.setUserId(context.getUser().getUserId());
            auditLog.setUserName(context.getUser().getUsername());
        }
        
        if (context.getLlmUser() != null) {
            auditLog.setLlmUserId(context.getLlmUser().getLlmUserId());
            auditLog.setLlmModelId(context.getLlmUser().getModelId());
        }
        
        // 操作信息
        auditLog.setOperationSource(context.getSource().name());
        auditLog.setRequestPath(request.getRequestURI());
        auditLog.setRequestMethod(request.getMethod());
        
        // 请求参数（脱敏）
        Map<String, String[]> params = request.getParameterMap();
        auditLog.setRequestParams(maskService.maskParams(params));
        
        // 请求体（脱敏）
        String body = request.getAttribute("requestBody") != null 
            ? request.getAttribute("requestBody").toString() 
            : "";
        auditLog.setRequestBody(maskService.maskBody(body));
        
        // 响应信息
        auditLog.setResponseStatus(response.getStatus());
        
        // 安全信息
        auditLog.setSessionId(context.getSession().getSessionId());
        auditLog.setClientIp(getClientIp(request));
        auditLog.setUserAgent(request.getHeader("User-Agent"));
        
        // 结果信息
        auditLog.setSuccess(exception == null);
        if (exception != null) {
            auditLog.setErrorMessage(exception.getMessage());
        }
        
        // 时间信息
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setTraceId(getTraceId(request));
        
        // 保存
        repository.save(auditLog);
    }
    
    /**
     * 查询用户操作日志
     */
    public List<AuditLog> queryByUser(String userId, LocalDateTime start, LocalDateTime end) {
        return repository.findByUserIdAndTimestampBetween(userId, start, end);
    }
    
    /**
     * 查询 LLM 操作日志
     */
    public List<AuditLog> queryByLlmUser(String llmUserId, LocalDateTime start, LocalDateTime end) {
        return repository.findByLlmUserIdAndTimestampBetween(llmUserId, start, end);
    }
    
    /**
     * 查询双用户联合操作日志
     */
    public List<AuditLog> queryByDualUser(
            String userId, 
            String llmUserId, 
            LocalDateTime start, 
            LocalDateTime end) {
        return repository.findByUserIdAndLlmUserIdAndTimestampBetween(
            userId, llmUserId, start, end);
    }
}
```

### 3.3 审计日志注解

```java
/**
 * 审计日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {
    
    /**
     * 操作名称
     */
    String operation();
    
    /**
     * 资源类型
     */
    String resourceType();
    
    /**
     * 是否记录请求参数
     */
    boolean logParams() default true;
    
    /**
     * 是否记录请求体
     */
    boolean logBody() default false;
    
    /**
     * 是否记录响应
     */
    boolean logResponse() default false;
    
    /**
     * 敏感字段（需要脱敏）
     */
    String[] sensitiveFields() default {};
}

// 使用示例
@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    
    @Audit(
        operation = "简历创建",
        resourceType = "Resume",
        sensitiveFields = {"phone", "email", "idCard"}
    )
    @DualUserAuth(allowedSources = {OperationSource.USER_DIRECT, OperationSource.USER_VIA_LLM})
    @PostMapping
    public Resume create(@RequestBody ResumeCreateRequest request) {
        // 业务逻辑
    }
}
```

---

## 四、SSE 与 JDSServer 同步机制

### 4.1 同步架构

```
┌─────────────────────────────────────────────────────────────┐
│  SSE 与 JDSServer 同步                                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │  LLM Session    │              │  JDSServer      │       │
│  │  (SSE)          │◄────────────►│  (Java Data     │       │
│  │                 │   双向同步    │   Sync Server)  │       │
│  └─────────────────┘              └─────────────────┘       │
│         │                                  │                 │
│         │                                  │                 │
│         ▼                                  ▼                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  同步内容                                            │   │
│  │  ├── 会话状态 (Session State)                        │   │
│  │  ├── 用户上下文 (User Context)                       │   │
│  │  ├── LLM 上下文 (LLM Context)                        │   │
│  │  ├── 安全令牌 (Security Token)                       │   │
│  │  └── 操作历史 (Operation History)                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 同步服务接口

```java
/**
 * 会话同步服务
 */
public interface SessionSyncService {
    
    /**
     * 创建会话并同步
     */
    SessionSyncResult createSession(DualUserContext context);
    
    /**
     * 更新会话状态
     */
    void updateSessionState(String sessionId, SessionState state);
    
    /**
     * 同步 LLM 上下文
     */
    void syncLlmContext(String sessionId, LlmUserInfo llmUser);
    
    /**
     * 同步用户上下文
     */
    void syncUserContext(String sessionId, UserInfo user);
    
    /**
     * 验证会话有效性
     */
    boolean validateSession(String sessionId);
    
    /**
     * 销毁会话
     */
    void destroySession(String sessionId);
}

/**
 * SSE 会话同步实现
 */
@Service
public class SseSessionSyncService implements SessionSyncService {
    
    @Autowired
    private JDSServerClient jdsClient;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Override
    public SessionSyncResult createSession(DualUserContext context) {
        // 1. 创建本地会话
        SessionInfo localSession = sessionManager.createSession(
            context.getUser().getUserId(),
            context.getUser().getUsername(),
            context.getSession().getClientIp(),
            context.getSession().getUserAgent()
        );
        
        // 2. 同步到 JDSServer
        JdsSyncRequest syncRequest = JdsSyncRequest.builder()
            .sessionId(localSession.getSessionId())
            .userId(context.getUser().getUserId())
            .llmUserId(context.getLlmUser() != null ? context.getLlmUser().getLlmUserId() : null)
            .timestamp(System.currentTimeMillis())
            .build();
        
        JdsSyncResult jdsResult = jdsClient.syncSession(syncRequest);
        
        // 3. 建立 SSE 连接
        SseConnection sseConnection = sseManager.createConnection(
            localSession.getSessionId(),
            context.getUser().getUserId()
        );
        
        return SessionSyncResult.builder()
            .sessionId(localSession.getSessionId())
            .sseEndpoint(sseConnection.getEndpoint())
            .jdsSynced(jdsResult.isSuccess())
            .build();
    }
    
    @Override
    public void syncLlmContext(String sessionId, LlmUserInfo llmUser) {
        // 同步 LLM 上下文到 JDSServer
        jdsClient.syncLlmContext(sessionId, llmUser);
        
        // 通过 SSE 推送更新
        sseManager.pushEvent(sessionId, "llm_context_update", llmUser);
    }
}
```

### 4.3 JDSServer 客户端

```java
/**
 * JDSServer 客户端
 */
@Component
public class JDSServerClient {
    
    @Value("${jds.server.url}")
    private String jdsServerUrl;
    
    private final RestTemplate restTemplate;
    
    /**
     * 同步会话
     */
    public JdsSyncResult syncSession(JdsSyncRequest request) {
        String url = jdsServerUrl + "/api/sync/session";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Auth-Token", generateSyncToken(request));
        
        HttpEntity<JdsSyncRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<JdsSyncResult> response = restTemplate.postForEntity(
            url, entity, JdsSyncResult.class);
        
        return response.getBody();
    }
    
    /**
     * 同步 LLM 上下文
     */
    public void syncLlmContext(String sessionId, LlmUserInfo llmUser) {
        String url = jdsServerUrl + "/api/sync/llm-context";
        
        Map<String, Object> request = new HashMap<>();
        request.put("sessionId", sessionId);
        request.put("llmUser", llmUser);
        request.put("timestamp", System.currentTimeMillis());
        
        restTemplate.postForEntity(url, request, Void.class);
    }
    
    /**
     * 验证会话
     */
    public boolean validateSession(String sessionId) {
        String url = jdsServerUrl + "/api/sync/validate?sessionId=" + sessionId;
        
        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }
    
    /**
     * 生成同步令牌
     */
    private String generateSyncToken(JdsSyncRequest request) {
        String payload = request.getSessionId() + ":" + request.getTimestamp();
        return SecurityUtils.sign(payload);
    }
}
```

---

## 五、跨场景 A2A 安全通讯

### 5.1 A2A 通讯架构

```
┌─────────────────────────────────────────────────────────────┐
│  跨场景 A2A 安全通讯                                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  场景A (招聘)                        场景B (培训)            │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │ Agent-A         │              │ Agent-B         │       │
│  │ ├── LLM-USER-A  │              │ ├── LLM-USER-B  │       │
│  │ ├── user-A      │              │ ├── user-B      │       │
│  │ └── Agent-Cmd   │◄────────────►│ └── Agent-Cmd   │       │
│  └─────────────────┘   安全通道   └─────────────────┘       │
│         │                                  │                 │
│         ▼                                  ▼                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Agent-Command 协议                                  │   │
│  │  ├── 身份认证 (ECC + 证书)                           │   │
│  │  ├── 传输加密 (TLS 1.3)                              │   │
│  │  ├── 端到端加密 (AES-256)                            │   │
│  │  ├── 消息签名 (ECDSA)                                │   │
│  │  └── 会话管理 (Token + Refresh)                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 Agent-Command 协议

```java
/**
 * Agent 命令消息
 */
@Data
@Builder
public class AgentCommand {
    
    /**
     * 协议版本
     */
    private String protocolVersion;
    
    /**
     * 命令ID
     */
    private String commandId;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    /**
     * 来源 Agent
     */
    private AgentInfo source;
    
    /**
     * 目标 Agent
     */
    private AgentInfo destination;
    
    /**
     * 操作类型
     */
    private String operation;
    
    /**
     * 载荷
     */
    private Map<String, Object> payload;
    
    /**
     * 元数据
     */
    private CommandMetadata metadata;
    
    /**
     * 签名
     */
    private String signature;
    
    /**
     * 安全令牌
     */
    private String securityToken;
    
    @Data
    @Builder
    public static class AgentInfo {
        private String agentId;
        private String agentType;
        private String sceneId;
        private String llmUserId;
        private String userId;
    }
    
    @Data
    @Builder
    public static class CommandMetadata {
        private String priority;
        private long timeout;
        private int retryCount;
        private String securityLevel;
        private String traceId;
        private boolean offlineMode;
        private boolean requireAck;
    }
}
```

### 5.3 Agent 安全通道

```java
/**
 * Agent 安全通道
 */
@Component
public class AgentSecureChannel {
    
    @Autowired
    private AgentCertificateManager certManager;
    
    @Autowired
    private AgentTokenManager tokenManager;
    
    /**
     * 建立安全连接
     */
    public SecureConnection establishConnection(
            AgentInfo source,
            AgentInfo destination) {
        
        // 1. 验证双方身份
        if (!certManager.verifyAgent(source.getAgentId())) {
            throw new SecurityException("Source agent not verified");
        }
        if (!certManager.verifyAgent(destination.getAgentId())) {
            throw new SecurityException("Destination agent not verified");
        }
        
        // 2. 协商加密密钥
        KeyPair keyPair = certManager.getKeyPair(source.getAgentId());
        PublicKey remotePublicKey = certManager.getPublicKey(destination.getAgentId());
        SecretKey sessionKey = generateSessionKey(keyPair, remotePublicKey);
        
        // 3. 创建安全连接
        return SecureConnection.builder()
            .connectionId(UUID.randomUUID().toString())
            .source(source)
            .destination(destination)
            .sessionKey(sessionKey)
            .createdAt(System.currentTimeMillis())
            .expiresAt(System.currentTimeMillis() + 3600000) // 1小时
            .build();
    }
    
    /**
     * 发送安全命令
     */
    public AgentCommandResponse sendCommand(
            SecureConnection connection,
            AgentCommand command) {
        
        // 1. 验证连接有效性
        if (connection.isExpired()) {
            throw new SecurityException("Connection expired");
        }
        
        // 2. 签名命令
        String signature = signCommand(command, connection.getSessionKey());
        command.setSignature(signature);
        
        // 3. 加密载荷
        Map<String, Object> encryptedPayload = encryptPayload(
            command.getPayload(), 
            connection.getSessionKey()
        );
        command.setPayload(encryptedPayload);
        
        // 4. 发送命令
        AgentCommandResponse response = doSendCommand(connection, command);
        
        // 5. 验证响应签名
        if (!verifyResponse(response, connection.getSessionKey())) {
            throw new SecurityException("Response signature invalid");
        }
        
        return response;
    }
    
    /**
     * 生成会话密钥
     */
    private SecretKey generateSessionKey(KeyPair localKeyPair, PublicKey remotePublicKey) {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(localKeyPair.getPrivate());
            keyAgreement.doPhase(remotePublicKey, true);
            
            byte[] sharedSecret = keyAgreement.generateSecret();
            return new SecretKeySpec(sharedSecret, 0, 32, "AES");
        } catch (Exception e) {
            throw new SecurityException("Failed to generate session key", e);
        }
    }
}
```

### 5.4 LLM-LLM 安全通讯

```java
/**
 * LLM 到 LLM 安全通讯服务
 */
@Service
public class LlmToLlmCommunicationService {
    
    @Autowired
    private AgentSecureChannel secureChannel;
    
    @Autowired
    private AuditLogService auditLogService;
    
    /**
     * 发送跨场景数据
     */
    public CrossSceneResult sendCrossSceneData(
            DualUserContext sourceContext,
            String targetSceneId,
            String operation,
            Map<String, Object> data) {
        
        // 1. 构建源 Agent 信息
        AgentCommand.AgentInfo sourceAgent = AgentCommand.AgentInfo.builder()
            .agentId(sourceContext.getSession().getAgentId())
            .agentType("end")
            .sceneId(sourceContext.getSession().getSceneId())
            .llmUserId(sourceContext.getLlmUser().getLlmUserId())
            .userId(sourceContext.getUser().getUserId())
            .build();
        
        // 2. 构建目标 Agent 信息
        AgentCommand.AgentInfo targetAgent = AgentCommand.AgentInfo.builder()
            .agentId("agent-" + targetSceneId)
            .agentType("end")
            .sceneId(targetSceneId)
            .build();
        
        // 3. 建立安全通道
        SecureConnection connection = secureChannel.establishConnection(
            sourceAgent, targetAgent);
        
        // 4. 构建命令
        AgentCommand command = AgentCommand.builder()
            .protocolVersion("2.4")
            .commandId(UUID.randomUUID().toString())
            .timestamp(System.currentTimeMillis())
            .source(sourceAgent)
            .destination(targetAgent)
            .operation(operation)
            .payload(data)
            .metadata(AgentCommand.CommandMetadata.builder()
                .priority("medium")
                .timeout(30000)
                .securityLevel("high")
                .traceId(generateTraceId())
                .requireAck(true)
                .build())
            .build();
        
        // 5. 发送命令
        AgentCommandResponse response = secureChannel.sendCommand(connection, command);
        
        // 6. 记录审计日志
        auditLogService.logCrossSceneCommunication(
            sourceContext, targetSceneId, operation, response);
        
        return CrossSceneResult.builder()
            .success(response.isSuccess())
            .data(response.getData())
            .traceId(command.getMetadata().getTraceId())
            .build();
    }
}

/**
 * 跨场景数据交互（A2A 模式）
 */
@Service
public class CrossSceneA2AService {
    
    @Autowired
    private LlmToLlmCommunicationService llmCommunication;
    
    /**
     * 招聘 → 培训：新员工入职
     */
    public void onEmployeeHired(Candidate candidate, DualUserContext context) {
        // 构建传输数据
        Map<String, Object> data = new HashMap<>();
        data.put("name", candidate.getName());
        data.put("position", candidate.getPosition());
        data.put("skills", candidate.getSkills());
        data.put("interviewComment", candidate.getInterviewComment());
        data.put("hireDate", LocalDate.now());
        
        // 通过 A2A 安全通道发送
        llmCommunication.sendCrossSceneData(
            context,
            "training-scene",
            "employee.onboard",
            data
        );
    }
}
```

---

## 六、安全配置规范

### 6.1 安全配置注解

```java
/**
 * 安全配置注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityConfig {
    
    /**
     * 安全级别
     */
    SecurityLevel level() default SecurityLevel.MEDIUM;
    
    /**
     * 是否启用审计
     */
    boolean auditEnabled() default true;
    
    /**
     * 是否启用双用户认证
     */
    boolean dualUserAuth() default true;
    
    /**
     * 是否启用 A2A 加密
     */
    boolean a2aEncryption() default true;
    
    /**
     * 会话超时（秒）
     */
    int sessionTimeout() default 3600;
    
    enum SecurityLevel {
        LOW,        // 基础安全
        MEDIUM,     // 标准安全
        HIGH,       // 高级安全
        CRITICAL    // 关键安全
    }
}

/**
 * LLM 安全配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LlmSecurityConfig {
    
    /**
     * LLM 用户 ID 前缀
     */
    String userIdPrefix() default "llm";
    
    /**
     * Token 限制
     */
    long tokenLimit() default 100000;
    
    /**
     * 允许的操作
     */
    String[] allowedOperations() default {};
    
    /**
     * 禁止的操作
     */
    String[] deniedOperations() default {};
    
    /**
     * 数据访问范围
     */
    String[] dataScopes() default {};
    
    /**
     * 是否需要用户确认敏感操作
     */
    boolean confirmSensitive() default true;
}
```

### 6.2 配置示例

```java
/**
 * 招聘场景安全配置
 */
@Service
@SecurityConfig(
    level = SecurityConfig.SecurityLevel.HIGH,
    auditEnabled = true,
    dualUserAuth = true,
    a2aEncryption = true,
    sessionTimeout = 7200
)
@LlmSecurityConfig(
    userIdPrefix = "llm-recruit",
    tokenLimit = 50000,
    allowedOperations = {
        "resume.read",
        "resume.create",
        "interview.schedule",
        "candidate.search"
    },
    deniedOperations = {
        "resume.delete",
        "offer.approve"
    },
    dataScopes = {
        "department:${user.department}",
        "position:${user.position}"
    },
    confirmSensitive = true
)
public class RecruitmentSecurityConfig {
    // 安全配置实现
}
```

---

## 七、安全检查清单

### 7.1 双用户安全检查

| 检查项 | 说明 | 状态 |
|--------|------|------|
| 双用户上下文解析 | 正确解析 user 和 LLM-USER | ☐ |
| 操作来源验证 | 验证操作来源是否允许 | ☐ |
| 权限双重检查 | 同时检查用户权限和 LLM 能力 | ☐ |
| 敏感操作确认 | 敏感操作需要用户确认 | ☐ |

### 7.2 审计日志检查

| 检查项 | 说明 | 状态 |
|--------|------|------|
| 双用户记录 | 同时记录两个用户信息 | ☐ |
| 数据脱敏 | 敏感数据已脱敏 | ☐ |
| 操作追溯 | 可追溯完整操作链 | ☐ |
| 日志完整性 | 日志不可篡改 | ☐ |

### 7.3 A2A 安全检查

| 检查项 | 说明 | 状态 |
|--------|------|------|
| 身份认证 | 双方 Agent 身份已验证 | ☐ |
| 传输加密 | 使用 TLS 1.3 | ☐ |
| 端到端加密 | 敏感数据 AES-256 加密 | ☐ |
| 消息签名 | ECDSA 签名验证 | ☐ |

---

## 八、总结

### 8.1 安全架构要点

| 组件 | 安全措施 |
|------|----------|
| **双用户模型** | user + LLM-USER 独立认证和授权 |
| **审计日志** | 双用户操作全程记录 |
| **SSE 同步** | 与 JDSServer 实时同步会话状态 |
| **A2A 通讯** | Agent-Command 安全通道 |

### 8.2 安全原则

1. **最小权限原则**：LLM-USER 仅拥有必要的能力
2. **审计追踪原则**：所有操作可追溯
3. **加密传输原则**：跨场景通讯全程加密
4. **用户确认原则**：敏感操作需用户确认

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
