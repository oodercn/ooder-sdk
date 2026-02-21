# SceneEngine 设计文档

**文档版本**: v1.0  
**SDK版本**: 0.7.3  
**创建日期**: 2026-02-22  
**状态**: 正式版

---

## 一、概述

### 1.1 项目定位

SceneEngine 是 Ooder Agent 平台的场景引擎服务集群，作为企业级场景服务的核心协调层：

- **对外**：以场景方式提供统一服务入口，支持点对点通讯、垂直业务场景（HR、CRM、财务、审批等）
- **对内**：将各个 Skills 能力根据场景协作关系建立连接和管理

### 1.2 核心概念

| 概念 | 说明 |
|------|------|
| **Scene（场景）** | 服务组织单元，定义一组相关能力和协作关系 |
| **Engine（引擎）** | 技术能力分类管理器，负责特定领域能力的安装、初始化、运行监控 |
| **Driver（驱动）** | 场景驱动实现，封装特定场景的能力接口和降级逻辑 |
| **Skill（技能）** | 原子化服务能力，由 Engine 管理和提供 |
| **Provider（提供者）** | 能力提供接口，由各引擎核心实现 |

### 1.3 模块组成

```
scene-engine/
├── scene-engine/          # 场景引擎核心
├── drivers/               # 场景驱动实现
│   ├── msg/              # 消息驱动
│   ├── org/              # 组织驱动
│   ├── vfs/              # 文件驱动
│   └── mqtt/             # MQTT驱动
├── scene-gateway/         # 场景网关
├── skill-org/             # 组织服务技能
├── skill-vfs/             # 文件服务技能
├── skill-msg/             # 消息服务技能
├── skill-mqtt/            # MQTT服务技能
├── skill-agent/           # Agent服务技能
├── skill-security/        # 安全服务技能
└── skill-business/        # 业务服务技能
```

---

## 二、架构设计

### 2.1 分层架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           用户接入层 (User Access)                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  Web Client │  │ Mobile App  │  │  Desktop    │  │  Third-party│        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景网关层 (Scene Gateway)                            │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                      scene-gateway                                     │  │
│  │  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐            │  │
│  │  │ HTTP API       │ │ Comet长连接    │ │ WebSocket      │            │  │
│  │  └────────────────┘ └────────────────┘ └────────────────┘            │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景引擎层 (Scene Engine)                             │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                      scene-engine (核心)                               │  │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐         │  │
│  │  │EngineManager│ │DriverRegistry│ │SessionManager│ │SecurityInterceptor│  │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘         │  │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐         │  │
│  │  │ProviderRegistry│ │SkillService│ │AuditService│ │ProtocolAdapter│   │  │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘         │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        驱动能力层 (Drivers)                                  │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐         │  │
│  │  │ MsgDriver  │ │ OrgDriver  │ │ VfsDriver  │ │ MqttDriver │         │  │
│  │  │ (消息驱动)  │ │ (组织驱动)  │ │ (文件驱动) │ │ (MQTT驱动) │         │  │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘         │  │
│  │  ┌────────────┐ ┌────────────┐                                       │  │
│  │  │ MsgFallback│ │ OrgFallback│ │ VfsFallback│                       │  │
│  │  │ (消息降级)  │ │ (组织降级)  │ │ (文件降级) │                       │  │
│  │  └────────────┘ └────────────┘                                       │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技能服务层 (Skills)                                   │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐              │
│  │ skill-org  │ │ skill-msg  │ │ skill-vfs  │ │ skill-mqtt │              │
│  │ (组织服务)  │ │ (消息服务)  │ │ (文件服务) │ │ (MQTT服务) │              │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘              │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐              │
│  │skill-agent │ │skill-security│skill-business│   ...      │              │
│  │ (代理服务) │ │ (安全服务)  │ │ (业务服务) │             │              │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件职责

| 组件 | 层级 | 职责 |
|------|------|------|
| **scene-gateway** | 网关层 | HTTP API、Comet长连接、WebSocket接入 |
| **EngineManager** | 引擎层 | 引擎管理器，协调各 Engine 的安装、启动、监控 |
| **DriverRegistry** | 引擎层 | 驱动注册中心，管理场景驱动的注册和发现 |
| **SessionManager** | 引擎层 | 会话管理器，管理用户会话和Token |
| **ProviderRegistry** | 引擎层 | Provider注册中心，管理能力提供者 |
| **SecurityInterceptor** | 引擎层 | 安全拦截器，进行权限检查和审计 |
| **Driver** | 驱动层 | 场景驱动实现，封装特定场景的能力接口 |
| **Skill** | 技能层 | 原子化服务能力，提供具体业务功能 |

---

## 三、引擎设计

### 3.1 Engine 接口

```java
public interface Engine {
    EngineType getType();
    String getName();
    EngineStatus getStatus();
    
    void initialize();
    void start();
    void stop();
    void destroy();
    
    Map<String, Object> getConfig();
    void updateConfig(Map<String, Object> config);
    
    EngineStats getStats();
    boolean healthCheck();
    
    List<String> getCapabilities();
}
```

### 3.2 EngineType 引擎类型

```java
public enum EngineType {
    ORG("Org Engine", "User, department, role management"),
    MSG("Message Engine", "Message push, P2P, Topic subscription"),
    VFS("File Engine", "File upload, download, storage management"),
    AGENT("Agent Engine", "Agent registration, heartbeat, subnet management"),
    SKILL("Skill Engine", "Skill search, install, runtime monitoring"),
    SESSION("Session Engine", "Session, Token, connection management"),
    SECURITY("Security Engine", "Authentication, authorization, encryption management"),
    AUDIT("Audit Engine", "Log audit, operation records"),
    WORKFLOW("Workflow Engine", "Process orchestration, task scheduling"),
    STATE("State Engine", "State management, event driven"),
    CAPABILITY("Capability Engine", "Capability registration, discovery, invocation");
}
```

### 3.3 EngineManager 引擎管理器

```java
public interface EngineManager {
    void registerEngine(Engine engine);
    void unregisterEngine(EngineType engineType);
    Engine getEngine(EngineType engineType);
    List<Engine> getAllEngines();
    List<EngineType> getEngineTypes();
    boolean hasEngine(EngineType engineType);
    
    void initializeAll();
    void startAll();
    void stopAll();
    void destroyAll();
    
    Map<EngineType, EngineStatus> getAllStatus();
    List<EngineType> healthCheckAll();
}
```

---

## 四、驱动设计

### 4.1 Driver 接口

```java
public interface Driver {
    String getCategory();
    String getVersion();
    
    void initialize(DriverContext context);
    void shutdown();
    
    Object getSkill();
    Object getCapabilities();
    Object getFallback();
    boolean hasFallback();
    
    InterfaceDefinition getInterfaceDefinition();
    HealthStatus getHealthStatus();
}
```

### 4.2 DriverRegistry 驱动注册中心

```java
public class DriverRegistry {
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private final Map<String, InterfaceDefinition> interfaceDefinitions = new ConcurrentHashMap<>();
    
    public void register(Driver driver);
    public void unregister(String category);
    public Driver getDriver(String category);
    public InterfaceDefinition getInterfaceDefinition(String category);
    public Collection<Driver> getAllDrivers();
    public HealthStatus getHealthStatus(String category);
    public Map<String, HealthStatus> getAllHealthStatus();
}
```

### 4.3 内置驱动

| 驱动 | 类别 | 能力 | 降级实现 |
|------|------|------|----------|
| **MsgDriver** | MSG | 消息发送、队列管理、订阅发布 | MsgFallback (本地内存队列) |
| **OrgDriver** | ORG | 用户认证、组织架构、角色管理 | OrgFallback (JSON本地数据源) |
| **VfsDriver** | VFS | 文件存储、文件夹管理、版本控制 | VfsFallback (本地文件系统) |
| **MqttDriver** | MQTT | MQTT消息、Topic订阅、QoS支持 | MqttFallback (本地内存) |

### 4.4 场景描述规范

每个驱动通过 YAML 文件定义场景描述：

```yaml
apiVersion: agent.ooder.net/v1
kind: SceneDescription

metadata:
  category: ORG
  name: Organization Management
  version: 1.0.0
  author: ooder-team

spec:
  category: ORG
  description: 组织管理场景，提供用户认证、组织架构、角色管理能力
  
  capabilities:
    - name: user-auth
      description: 用户认证能力
      version: 1.0.0
    - name: user-manage
      description: 用户管理能力
      version: 1.0.0
      
  protocols:
    - type: http
      description: REST API
      
  roles:
    - name: admin
      capabilities: ["*"]
      description: 管理员角色
    - name: viewer
      capabilities: ["user-auth:read", "org-manage:read"]
      description: 只读角色
      
  fallback:
    enabled: true
    implementation: net.ooder.scene.drivers.org.OrgFallback
    description: JSON 本地数据源降级实现
    limitations:
      - 数据存储在本地 JSON 文件
      - 不支持多租户
```

---

## 五、接口定义规范

### 5.1 InterfaceDefinition

每个驱动通过 YAML 文件定义接口规范：

```yaml
apiVersion: agent.ooder.net/v1
kind: InterfaceDefinition

metadata:
  category: ORG
  version: 1.0.0
  interfaceHash: sha256:org-v1.0.0

spec:
  capabilities:
    user-auth:
      description: 用户认证能力
      methods:
        login:
          description: 用户登录
          input:
            type: object
            required: [username, password]
            properties:
              username:
                type: string
              password:
                type: string
          output:
            type: object
            properties:
              userId:
                type: string
              token:
                type: string
              roles:
                type: array
                items:
                  type: string
          errors:
            - code: AUTH_FAILED
              message: 认证失败
```

### 5.2 能力定义规范

| 场景 | 能力 | 方法 |
|------|------|------|
| **ORG** | user-auth | login, logout, validateToken, refreshToken |
| **ORG** | user-manage | getUser, registerUser, updateUser, deleteUser, listUsers |
| **ORG** | org-manage | getOrgTree, getOrg, getOrgUsers |
| **ORG** | role-manage | getUserRoles |
| **MSG** | message-operations | sendMessage, receiveMessage, acknowledgeMessage, batchSend |
| **MSG** | queue-operations | createQueue, deleteQueue, listQueues, getQueueInfo, purgeQueue |
| **MSG** | subscription-operations | subscribe, unsubscribe, listSubscriptions |
| **VFS** | file-operations | getFileInfo, createFile, deleteFile, copyFile, moveFile, renameFile, listFiles |
| **VFS** | folder-operations | getFolder, createFolder, deleteFolder, listFolders |
| **VFS** | stream-operations | downloadFile, uploadFile |
| **VFS** | version-operations | getFileVersions, createVersion |
| **VFS** | search-operations | searchFiles |

---

## 六、会话管理

### 6.1 SessionManager 接口

```java
public interface SessionManager {
    SessionInfo createSession(String userId, String username, String clientIp, String userAgent);
    SessionInfo getSession(String sessionId);
    boolean validateSession(String sessionId);
    SessionInfo refreshSession(String sessionId);
    void destroySession(String sessionId);
    void touchSession(String sessionId);
    List<SessionInfo> getActiveSessions(String userId);
    void destroyUserSessions(String userId);
}
```

### 6.2 TokenManager 接口

```java
public interface TokenManager {
    TokenInfo createToken(String userId, String sessionId);
    TokenInfo getToken(String tokenId);
    boolean validateToken(String tokenId);
    TokenInfo refreshToken(String tokenId);
    void revokeToken(String tokenId);
    void revokeUserTokens(String userId);
}
```

---

## 七、安全设计

### 7.1 SecurityInterceptor 安全拦截器

```java
public interface SecurityInterceptor {
    InterceptorResult beforeExecute(OperationContext context, SkillRequest request);
    void afterExecute(OperationContext context, SkillRequest request, SkillResponse response);
    void onError(OperationContext context, SkillRequest request, Throwable error);
    int getOrder();
}
```

### 7.2 权限模型

```java
public class Permission {
    private String resource;
    private String action;
    private String effect;  // ALLOW, DENY
    private Map<String, String> conditions;
}
```

### 7.3 审计服务

```java
public interface AuditService {
    void log(AuditLog auditLog);
    PageResult<AuditLog> query(AuditLogQuery query);
    AuditStats getStats(String userId, long startTime, long endTime);
    AuditExportResult export(AuditLogQuery query);
}
```

---

## 八、Provider 设计

### 8.1 BaseProvider 基础接口

```java
public interface BaseProvider {
    String getProviderName();
    void initialize();
    void start();
    void stop();
    boolean isRunning();
}
```

### 8.2 Provider 类型

| Provider | 说明 |
|----------|------|
| **SystemProvider** | 系统信息、服务状态、资源使用 |
| **ConfigProvider** | 配置管理、配置历史 |
| **UserProvider** | 用户管理、用户设置 |
| **AgentProvider** | Agent注册、心跳、命令 |
| **HealthProvider** | 健康检查、健康报告 |
| **NetworkProvider** | 网络配置、连接状态 |
| **SecurityProvider** | 安全策略、威胁检测 |
| **LogProvider** | 日志查询、日志统计 |
| **ProtocolProvider** | 协议处理、协议适配 |
| **SkillShareProvider** | 技能共享、技能接收 |

### 8.3 ProviderRegistry 注册中心

```java
public interface ProviderRegistry {
    <T extends BaseProvider> void register(Class<T> providerType, T provider);
    <T extends BaseProvider> T getProvider(Class<T> providerType);
    boolean hasProvider(Class<? extends BaseProvider> providerType);
    Set<Class<? extends BaseProvider>> getProviderTypes();
    void startAll();
    void stopAll();
    int getProviderCount();
}
```

---

## 九、协议适配

### 9.1 LoginProtocolAdapter 登录协议

```java
public interface LoginProtocolAdapter {
    CompletableFuture<LoginResult> login(LoginRequest request);
    CompletableFuture<Void> logout(String sessionId);
    CompletableFuture<Session> getSession(String sessionId);
    CompletableFuture<Boolean> validateSession(String sessionId);
    CompletableFuture<Session> refreshSession(String sessionId);
    CompletableFuture<String> getCurrentUserId(String sessionId);
}
```

### 9.2 DiscoveryProtocolAdapter 发现协议

```java
public interface DiscoveryProtocolAdapter {
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    void addListener(DiscoveryEventListener listener);
    void removeListener(DiscoveryEventListener listener);
}
```

---

## 十、技能服务

### 10.1 SkillService 接口

```java
public interface SkillService {
    SkillInfo findSkill(String skillId);
    List<SkillInfo> searchSkills(SkillQuery query);
    List<SkillInfo> discoverSkills(SkillQuery query);
    
    SkillInstallResult installSkill(String userId, String skillId);
    SkillInstallResult installSkill(String userId, String skillId, Map<String, Object> config);
    SkillInstallProgress getInstallProgress(String installId);
    SkillUninstallResult uninstallSkill(String userId, String skillId);
    
    List<InstalledSkillInfo> listInstalledSkills(String userId);
    List<CapabilityInfo> listCapabilities(String skillId);
    
    Object invokeCapability(String userId, String skillId, String capability, Map<String, Object> params);
    
    SkillRuntimeStatus getRuntimeStatus(String skillId);
    void startSkill(String userId, String skillId);
    void stopSkill(String userId, String skillId);
    void restartSkill(String userId, String skillId);
}
```

### 10.2 内置技能

| 技能模块 | 说明 |
|----------|------|
| **skill-org** | 组织服务技能，支持JSON本地数据源 |
| **skill-vfs** | 文件服务技能，支持本地文件系统 |
| **skill-msg** | 消息服务技能，支持内存队列 |
| **skill-mqtt** | MQTT服务技能，支持MQTT 3.1.1/5.0 |
| **skill-agent** | Agent服务技能，支持Agent注册和心跳 |
| **skill-security** | 安全服务技能，支持认证授权 |
| **skill-business** | 业务服务技能，支持自定义业务扩展 |

---

## 十一、包结构

```
net.ooder.scene/
├── core/
│   ├── driver/
│   │   ├── Driver.java
│   │   ├── DriverContext.java
│   │   ├── DriverRegistry.java
│   │   ├── HealthStatus.java
│   │   └── InterfaceParser.java
│   ├── provider/
│   │   └── *ProviderImpl.java
│   ├── security/
│   │   ├── SecurityInterceptor.java
│   │   ├── Permission.java
│   │   └── SecureSkillService.java
│   ├── skill/
│   │   ├── SkillService.java
│   │   └── SkillProviderRegistry.java
│   ├── ProviderRegistry.java
│   ├── CapabilityInfo.java
│   ├── SceneClient.java
│   └── Result.java
│
├── engine/
│   ├── Engine.java
│   ├── EngineManager.java
│   ├── EngineType.java
│   ├── EngineStatus.java
│   └── EngineStats.java
│
├── protocol/
│   ├── LoginProtocolAdapter.java
│   ├── DiscoveryProtocolAdapter.java
│   ├── LoginRequest.java
│   ├── LoginResult.java
│   └── Session.java
│
├── provider/
│   ├── BaseProvider.java
│   ├── SystemProvider.java
│   ├── ConfigProvider.java
│   ├── UserProvider.java
│   ├── AgentProvider.java
│   ├── HealthProvider.java
│   ├── NetworkProvider.java
│   ├── SecurityProvider.java
│   ├── LogProvider.java
│   ├── ProtocolProvider.java
│   └── SkillShareProvider.java
│
├── session/
│   ├── SessionManager.java
│   ├── TokenManager.java
│   ├── SessionInfo.java
│   └── TokenInfo.java
│
├── audit/
│   ├── AuditService.java
│   └── AuditStats.java
│
└── skill/
    ├── HttpClientProvider.java
    ├── LlmProvider.java
    ├── SchedulerProvider.java
    └── StorageProvider.java
```

---

## 十二、配置规范

### 12.1 场景配置示例

```yaml
# scene-dev.yaml
scene:
  id: dev-scene
  name: 开发环境场景
  version: 0.7.3
  
  engines:
    - type: ORG
      enabled: true
      config:
        dataSource: json
        dataPath: ./data/org
    - type: VFS
      enabled: true
      config:
        storageType: local
        basePath: ./data/vfs
    - type: MSG
      enabled: true
      config:
        queueType: memory
        
  security:
    enabled: true
    authType: local
    tokenExpire: 3600
    
  audit:
    enabled: true
    logPath: ./logs/audit
```

### 12.2 生产环境配置

```yaml
# scene-prod.yaml
scene:
  id: prod-scene
  name: 生产环境场景
  version: 0.7.3
  
  engines:
    - type: ORG
      enabled: true
      config:
        dataSource: feishu
        appId: ${FEISHU_APP_ID}
        appSecret: ${FEISHU_APP_SECRET}
    - type: VFS
      enabled: true
      config:
        storageType: minio
        endpoint: ${MINIO_ENDPOINT}
        accessKey: ${MINIO_ACCESS_KEY}
        secretKey: ${MINIO_SECRET_KEY}
    - type: MSG
      enabled: true
      config:
        queueType: rabbitmq
        host: ${RABBITMQ_HOST}
        port: 5672
        
  security:
    enabled: true
    authType: oauth2
    tokenExpire: 7200
    
  audit:
    enabled: true
    logPath: /var/log/ooder/audit
    retention: 30
```

---

## 十三、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|---------|
| v1.0 | 2026-02-22 | 正式版发布 |

---

**文档结束**
