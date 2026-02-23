# Agent 协议文档 - v0.7.3

## 1. 协议概述

Agent 协议是 SuperAgent 系统中 MCP Agent、Route Agent 和 End Agent 之间的通信协议，属于南向协议的具体实现。v0.7.3 版本的 Agent 协议在 v0.7.0 基础上进行了全面升级，新增节点发现、本地认证、场景组协作、离线支持和事件总线等核心特性。

### 1.1 协议目标

- 实现 Agent 之间的高效、安全通信
- 支持广域网环境下的 Agent 协同工作
- 提供标准化的 Agent 接口和消息格式
- 确保 Agent 身份的真实性和通信的安全性
- 支持 Agent 动态发现和网络自组织
- 提供灵活的扩展机制，适应不同的应用场景
- **v0.7.3 新增**：支持离线模式和自动同步
- **v0.7.3 新增**：统一事件管理机制

### 1.2 v0.7.3 升级内容

| 新增特性 | 说明 |
|---------|------|
| DiscoveryProtocol | 节点发现协议，支持 UDP/DHT/SkillCenter/mDNS |
| LoginProtocol | 本地认证协议，支持离线认证和会话管理 |
| CollaborationProtocol | 场景组协作协议，支持任务分配和状态同步 |
| OfflineService | 离线服务，支持网络断开时的场景运行 |
| EventBus | 事件总线，统一事件管理和模块解耦 |

### 1.3 协议适用范围

- 广域网内的 Agent 通信
- 跨网络、跨设备的 Agent 协同
- 企业级分布式 Agent 系统
- 个人设备间的安全 Agent 通信
- 跨组织的 Agent 网络协作
- 离线环境下的 Agent 运行

## 2. Agent 架构

### 2.1 Agent 类型

SuperAgent 系统包含三种类型的 Agent：

| Agent 类型 | 描述 | 职责 |
|------------|------|------|
| MCP Agent | 主控智能体 | 资源管理、任务调度、安全认证 |
| Route Agent | 路由智能体 | 消息路由、负载均衡、网络管理 |
| End Agent | 终端智能体 | 与外部设备和系统交互、数据采集和执行 |

### 2.2 通信模式

Agent 之间的通信模式包括：

- **星型通信**：所有 Agent 与 MCP Agent 直接通信
- **链式通信**：Agent 之间通过 Route Agent 进行链式通信
- **网状通信**：Agent 之间直接通信，形成网状网络
- **混合通信**：结合多种通信模式，适应不同场景

## 3. 协议格式

### 3.1 消息格式

Agent 协议采用 JSON 格式，确保数据结构的一致性和可解析性：

```json
{
  "protocol_version": "0.7.3",
  "command_id": "uuid",
  "timestamp": "2026-02-20T12:00:00Z",
  "source": {
    "component": "string",
    "id": "string",
    "type": "mcp|route|end"
  },
  "destination": {
    "component": "string",
    "id": "string",
    "type": "mcp|route|end"
  },
  "operation": "string",
  "payload": {},
  "metadata": {
    "priority": "high|medium|low",
    "timeout": "number",
    "retry_count": "number",
    "security_level": "high|medium|low",
    "trace_id": "string",
    "offline_mode": false
  },
  "signature": "digital_signature",
  "token": "session_token"
}
```

## 4. 操作类型

### 4.1 核心操作

| 操作类型 | 功能描述 | 目标组件 |
|----------|----------|----------|
| agent.discover | 发现网络中的 Agent | routeAgent |
| agent.register | 注册 Agent 到网络 | mcpAgent |
| agent.status | 获取 Agent 状态 | any |
| agent.command | 发送命令到 Agent | any |
| agent.heartbeat | 发送心跳消息 | any |
| agent.security.authenticate | Agent 身份认证 | mcpAgent |
| agent.network.join | 加入 Agent 网络 | routeAgent |

### 4.2 v0.7.3 新增操作

| 操作类型 | 功能描述 | 目标组件 |
|----------|----------|----------|
| agent.discovery.udp | UDP 广播发现 | routeAgent |
| agent.discovery.dht | DHT 节点发现 | routeAgent |
| agent.login.local | 本地认证 | mcpAgent |
| agent.login.session | 会话管理 | mcpAgent |
| agent.collaboration.task | 任务分配 | routeAgent |
| agent.collaboration.sync | 状态同步 | routeAgent |
| agent.offline.enable | 启用离线模式 | any |
| agent.offline.sync | 离线数据同步 | any |
| agent.event.subscribe | 事件订阅 | any |
| agent.event.publish | 事件发布 | any |

## 5. 安全机制

### 5.1 身份认证

- **身份生成**：每个 Agent 使用 ECC 算法生成密钥对
- **证书管理**：Agent 可以向信任的 CA 申请身份证书
- **证书验证**：Agent 之间通信时验证对方证书
- **证书链**：支持多级证书链
- **v0.7.3 新增**：本地认证支持，离线环境下可使用本地缓存的认证信息

### 5.2 数据加密

- **传输加密**：使用 TLS 1.3 加密所有 Agent 间通信
- **端到端加密**：对敏感数据使用 AES-256 进行端到端加密
- **密钥管理**：使用 ECDH 算法进行密钥交换

## 6. DiscoveryProtocol（节点发现协议）

### 6.1 发现方法

| 方法 | 适用场景 | 延迟 |
|------|---------|------|
| UDP Broadcast | 局域网 | 低 |
| DHT (Kademlia) | 广域网 | 中 |
| SkillCenter API | 中心化目录 | 低 |
| mDNS/DNS-SD | 服务发现 | 低 |

### 6.2 发现流程

```
1. 启动时执行多路发现
   ├── UDP Broadcast (局域网)
   ├── DHT 查询 (广域网)
   ├── SkillCenter API (中心化)
   └── mDNS/DNS-SD (服务发现)
   
2. 汇总发现结果
   └── 去重、验证、排序
   
3. 建立连接
   └── 按优先级建立 P2P 连接
```

### 6.3 API 接口

```java
public interface DiscoveryProtocolAdapter {

    CompletableFuture<DiscoveryResult> discoverPeers(DiscoveryRequest request);

    CompletableFuture<List<Peer>> listDiscoveredPeers();

    CompletableFuture<Peer> discoverMcp();

    CompletableFuture<Peer> getPeer(String peerId);

    CompletableFuture<Boolean> isPeerOnline(String peerId);

    void addDiscoveryListener(DiscoveryEventListener listener);

    void removeDiscoveryListener(DiscoveryEventListener listener);
}
```

### 6.4 数据模型

#### DiscoveryRequest

```java
public class DiscoveryRequest {
    private String discoveryType;     // 发现类型：UDP/DHT/SKILL_CENTER/MDNS
    private int timeout;              // 超时时间（毫秒）
    private int maxPeers;             // 最大返回节点数
    private List<String> peerTypes;   // 节点类型过滤
    private String filter;            // 自定义过滤条件
}
```

#### DiscoveryResult

```java
public class DiscoveryResult {
    private boolean success;          // 是否成功
    private String message;           // 结果消息
    private List<Peer> peers;         // 发现的节点列表
    private int totalFound;           // 总发现数量
    private long duration;            // 耗时（毫秒）
}
```

#### Peer

```java
public class Peer {
    private String peerId;            // 节点唯一标识
    private String peerName;          // 节点名称
    private String peerType;          // 节点类型：MCP/ROUTE/END
    private String address;           // 网络地址
    private int port;                 // 端口号
    private String status;            // 状态：ONLINE/OFFLINE
    private long lastSeen;            // 最后在线时间
    private long registeredAt;        // 注册时间
    private List<String> capabilities;// 能力列表
    private String version;           // 协议版本

    public boolean isOnline() {
        return "ONLINE".equals(status);
    }
}
```

## 7. LoginProtocol（登录协议）

### 7.1 认证模式

| 模式 | 说明 | 适用场景 |
|------|------|---------|
| 在线认证 | 通过 SkillCenter 认证 | 有网络连接 |
| 本地认证 | 使用本地缓存认证 | 离线环境 |
| 域认证 | 通过企业域认证 | 企业环境 |

### 7.2 API 接口

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

### 7.3 数据模型

#### LoginRequest

```java
public class LoginRequest {
    private String username;          // 用户名
    private String password;          // 密码
    private String loginType;         // 登录类型：ONLINE/LOCAL/DOMAIN
    private String deviceId;          // 设备ID
    private String clientIp;          // 客户端IP
    private String userAgent;         // 用户代理
}
```

#### LoginResult

```java
public class LoginResult {
    private boolean success;          // 是否成功
    private String message;           // 结果消息
    private Session session;          // 会话信息
    private String errorCode;         // 错误码
}
```

#### Session

```java
public class Session {
    private String sessionId;         // 会话唯一标识
    private String userId;            // 用户ID
    private String username;          // 用户名
    private String deviceId;          // 设备ID
    private String clientIp;          // 客户端IP
    private long createdAt;           // 创建时间
    private long expiresAt;           // 过期时间
    private long lastActiveAt;        // 最后活跃时间
    private String status;            // 状态：ACTIVE/EXPIRED/REVOKED

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) && !isExpired();
    }
}
```

## 8. CollaborationProtocol（协作协议）

### 8.1 任务分配

```java
public interface CollaborationProtocol {
    
    CompletableFuture<Void> joinSceneGroup(String groupId, JoinRequest request);
    
    CompletableFuture<TaskInfo> receiveTask(String groupId);
    
    CompletableFuture<Void> submitTaskResult(String groupId, String taskId, TaskResult result);
    
    CompletableFuture<Void> syncState(String groupId, SceneGroupState state);
}
```

### 8.2 状态同步

| 同步类型 | 触发条件 | 数据内容 |
|---------|---------|---------|
| 全量同步 | 成员加入 | 完整状态 |
| 增量同步 | 状态变更 | 变更部分 |
| 心跳同步 | 定时 | 状态摘要 |

## 9. OfflineService（离线服务）

### 9.1 离线模式

```java
public interface OfflineService {
    
    void enableOfflineMode();
    
    void disableOfflineMode();
    
    boolean isOfflineMode();
    
    CompletableFuture<SyncResult> syncNow();
    
    List<PendingChange> getPendingChanges();
}
```

### 9.2 离线流程

```
网络断开
    │
    ▼
启用离线模式
    │
    ├── 暂存状态变更
    ├── 使用本地缓存数据
    └── 记录操作日志
    │
    ▼
网络恢复
    │
    ▼
自动同步
    │
    ├── 冲突检测
    ├── 冲突解决
    └── 状态合并
```

## 10. EventBus（事件总线）

### 10.1 事件类型

| 事件类型 | 说明 |
|---------|------|
| SceneGroupCreatedEvent | 场景组创建 |
| MemberJoinedEvent | 成员加入 |
| MemberLeftEvent | 成员离开 |
| PrimaryChangedEvent | 主节点变更 |
| TaskAssignedEvent | 任务分配 |
| TaskCompletedEvent | 任务完成 |
| NetworkDisconnectedEvent | 网络断开 |
| NetworkConnectedEvent | 网络连接 |
| OfflineModeEnabledEvent | 离线模式启用 |
| SyncCompletedEvent | 同步完成 |

### 10.2 事件接口

```java
public interface EventBus {
    
    <T> void subscribe(Class<T> eventType, Consumer<T> handler);
    
    <T> void unsubscribe(Class<T> eventType, Consumer<T> handler);
    
    void publish(Object event);
    
    <T> CompletableFuture<Void> publishAsync(T event);
}
```

## 11. Driver 组件（驱动代理）

### 11.1 Driver 接口

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

### 11.2 InterfaceParser

InterfaceParser 用于解析 YAML/JSON 格式的接口定义文件：

```java
public class InterfaceParser {
    
    public InterfaceDefinition parse(InputStream input);
    
    public InterfaceDefinition parseFromYaml(String yamlContent);
}
```

## 12. 错误处理

### 12.1 错误码

| 错误码 | 错误描述 | 处理策略 |
|--------|----------|----------|
| 1000 | 参数错误 | 直接返回错误 |
| 1001 | 认证失败 | 引导重新认证 |
| 1002 | 权限不足 | 直接返回错误 |
| 1003 | 资源不存在 | 直接返回错误 |
| 1004 | 请求超时 | 指数退避重试 |
| 1005 | 网络错误 | 尝试其他路径 |
| 2000 | 安全验证失败 | 引导重新认证 |
| 2003 | 广域网连接失败 | 尝试 NAT 穿透 |
| 3001 | 离线模式限制 | 提示用户 |
| 3002 | 同步冲突 | 冲突解决 |
| 3003 | 离线数据过期 | 重新获取 |

## 13. 参考资料

- [SuperAgent 核心协议文档](../main/protocol-main.md)
- [P2P 协议文档](../p2p/p2p-protocol.md)
- [场景组协议文档](./scene-group-protocol.md)
- [技能发现协议文档](../skill/skill-discovery-protocol.md)
