# 深入ooderAgent南向协议：以agent-SDK技术原理为核心的分布式Agent安全交互机制

> **作者**: Ooder Team  
> **日期**: 2026-03-07  
> **关键词**: 南向协议、agent-SDK、Capability、Provider、Discovery、安全交互

---

## 引言：南向协议的核心价值

在分布式Agent系统中，**南向协议**（Southbound Protocol）是Agent与底层能力、资源、服务交互的核心桥梁。ooderAgent通过精心设计的南向协议架构，实现了企业LLM与个人LLM之间安全、可信、可控的交互。

**南向协议的核心价值**：

```
┌─────────────────────────────────────────────────────────────┐
│                    北向协议 (Northbound)                     │
│              应用层接口、用户交互、业务编排                   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│                      Agent Core                              │
│                   (agent-SDK核心)                            │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│                    南向协议 (Southbound)                     │
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Capability  │  │  Provider   │  │  Discovery  │         │
│  │  能力系统    │  │  提供者系统  │  │   发现系统   │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Session   │  │   Security  │  │    Event    │         │
│  │  会话管理    │  │   安全机制   │  │   事件系统   │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    底层资源与服务
         (LLM、向量库、存储、网络、第三方API...)
```

---

## 一、agent-SDK核心架构：南向协议的技术基座

### 1.1 三层架构设计

agent-SDK采用三层架构设计，清晰分离关注点：

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (Application)                      │
│                                                              │
│  Scene API │ Skill API │ Cap API │ Discovery API            │
│                                                              │
│  职责：业务编排、场景管理、技能调用                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    服务层 (Service)                          │
│                                                              │
│  SessionManager │ SkillService │ DiscoveryService           │
│  ProviderRegistry │ SecurityInterceptor                      │
│                                                              │
│  职责：核心业务逻辑、状态管理、安全控制                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    协议层 (Protocol)                         │
│                                                              │
│  UDP Broadcast │ mDNS │ HTTP/HTTPS │ WebSocket │ P2P        │
│                                                              │
│  职责：网络通信、协议适配、数据传输                            │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 核心组件关系图

```
┌─────────────────────────────────────────────────────────────┐
│                      SceneAgentBridge                        │
│                    (Agent核心实现)                            │
│                                                              │
│  实现: SceneAgentCore + SceneAgent + Agent                   │
│  职责: 桥接scene-engine与agent-sdk                            │
└───────────────┬─────────────────────────┬───────────────────┘
                │                         │
        ┌───────▼────────┐        ┌──────▼─────────┐
        │  CapRegistry   │        │ ProviderRegistry│
        │  能力注册表     │        │  提供者注册表   │
        └───────┬────────┘        └──────┬─────────┘
                │                        │
        ┌───────▼────────┐        ┌──────▼─────────┐
        │   CapRouter    │        │    Providers   │
        │   能力路由器    │        │  能力提供者    │
        └────────────────┘        └────────────────┘
                │
        ┌───────▼────────┐
        │ DiscoveryService│
        │   发现服务      │
        └───────┬────────┘
                │
        ┌───────▼────────┐
        │DiscoveryProvider│
        │   发现提供者    │
        │  (UDP/mDNS/...) │
        └────────────────┘
```

---

## 二、Capability系统：南向协议的能力抽象

### 2.1 CAP能力地址协议

CAP（Capability Address Protocol）是南向协议的核心，定义了统一的能力寻址方案：

```java
/**
 * CAP能力地址空间
 * 
 * 地址范围：
 * - 00-3F (0-63): System - 系统能力
 * - 40-9F (64-159): Common - 通用能力
 * - A0-FF (160-255): Extension - 扩展能力
 */
public class CapAddress {
    
    private final int address;
    
    public CapAddress(int address) {
        if (address < 0 || address > 255) {
            throw new IllegalArgumentException("Address must be 0-255");
        }
        this.address = address;
    }
    
    public String toHex() {
        return String.format("%02X", address);
    }
    
    public CapCategory getCategory() {
        if (address <= 0x3F) return CapCategory.SYSTEM;
        if (address <= 0x9F) return CapCategory.COMMON;
        return CapCategory.EXTENSION;
    }
}
```

### 2.2 CapRouter能力路由器

CapRouter是能力调用的核心路由器，负责将能力请求路由到对应的处理器：

```java
/**
 * CAP能力路由器
 * 
 * 核心功能：
 * 1. 能力注册表管理
 * 2. 请求路由分发
 * 3. 事件发布
 * 4. 默认处理
 */
public class CapRouter {
    
    private final CapRegistry registry;
    private final Map<String, CapHandler> handlers = new ConcurrentHashMap<>();
    private SceneEventPublisher eventPublisher;
    
    /**
     * 路由能力请求
     * 
     * 流程：
     * 1. 检查能力是否存在
     * 2. 查找处理器
     * 3. 执行处理
     * 4. 发布事件
     */
    public CapResponse routeRequest(String capId, CapRequest request) {
        // 1. 检查能力是否存在
        if (!registry.hasCapability(capId)) {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(
                this, capId, request.getRequestId(), "Capability not found"));
            return CapResponse.failure(request.getRequestId(), capId, "Capability not found");
        }
        
        // 2. 查找处理器
        CapHandler handler = handlers.get(capId);
        CapResponse response;
        
        if (handler != null) {
            // 3. 执行处理
            response = handler.handle(request);
        } else {
            response = handleDefault(request);
        }
        
        // 4. 发布事件
        if (response.isSuccess()) {
            Capability capability = registry.findById(capId);
            String capName = capability != null ? capability.getName() : capId;
            publishCapabilityEvent(CapabilityEvent.invoked(
                this, capId, capName, request.getRequestId()));
        } else {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(
                this, capId, request.getRequestId(), response.getErrorMessage()));
        }
        
        return response;
    }
    
    /**
     * 能力处理器接口
     */
    public interface CapHandler {
        CapResponse handle(CapRequest request);
    }
}
```

### 2.3 能力调用流程

```
┌─────────────────────────────────────────────────────────────┐
│                    能力调用完整流程                           │
└─────────────────────────────────────────────────────────────┘

1. 客户端发起请求
   │
   ├── 构建CapRequest
   │   ├── requestId: UUID
   │   ├── capId: "40" (消息发送能力)
   │   └── parameters: Map<String, Object>
   │
   ▼
2. CapRouter路由
   │
   ├── 检查能力是否存在
   │   └── registry.hasCapability(capId)
   │
   ├── 查找处理器
   │   └── handlers.get(capId)
   │
   ▼
3. CapHandler执行
   │
   ├── 参数验证
   │   └── validateParameters(request)
   │
   ├── 业务逻辑
   │   └── executeBusinessLogic(request)
   │
   ├── 构建响应
   │   └── CapResponse.success(requestId, capId, result)
   │
   ▼
4. 事件发布
   │
   ├── 成功事件
   │   └── CapabilityEvent.invoked(...)
   │
   └── 失败事件
       └── CapabilityEvent.invocationFailed(...)
   │
   ▼
5. 返回响应
   └── CapResponse
```

---

## 三、Provider系统：南向协议的能力提供机制

### 3.1 Provider架构设计

Provider系统采用接口抽象和实现分离的设计模式：

```java
/**
 * Provider基础接口
 */
public interface BaseProvider {
    
    /**
     * 获取Provider名称
     */
    String getProviderName();
    
    /**
     * 获取Provider版本
     */
    String getProviderVersion();
    
    /**
     * 初始化
     */
    void initialize();
    
    /**
     * 启动
     */
    void start();
    
    /**
     * 停止
     */
    void stop();
    
    /**
     * 检查是否健康
     */
    boolean isHealthy();
}
```

### 3.2 ProviderRegistry注册中心

ProviderRegistry统一管理所有Provider的生命周期：

```java
/**
 * Provider注册中心
 * 
 * 职责：
 * 1. Provider注册与注销
 * 2. Provider查找与获取
 * 3. 生命周期管理
 */
public interface ProviderRegistry {
    
    /**
     * 注册Provider
     */
    <T extends BaseProvider> void register(Class<T> providerType, T provider);
    
    /**
     * 获取Provider
     */
    <T extends BaseProvider> T getProvider(Class<T> providerType);
    
    /**
     * 检查Provider是否存在
     */
    boolean hasProvider(Class<? extends BaseProvider> providerType);
    
    /**
     * 获取所有已注册的Provider类型
     */
    Set<Class<? extends BaseProvider>> getProviderTypes();
    
    /**
     * 启动所有Provider
     */
    void startAll();
    
    /**
     * 停止所有Provider
     */
    void stopAll();
    
    /**
     * 获取Provider数量
     */
    int getProviderCount();
}
```

### 3.3 核心Provider实现

#### 3.3.1 AgentProvider - Agent管理

```java
/**
 * Agent提供者接口
 */
public interface AgentProvider extends BaseProvider {
    
    /**
     * 获取所有EndAgent
     */
    Result<List<EndAgent>> getEndAgents();
    
    /**
     * 添加EndAgent
     */
    Result<EndAgent> addEndAgent(Map<String, Object> agentData);
    
    /**
     * 编辑EndAgent
     */
    Result<EndAgent> editEndAgent(String agentId, Map<String, Object> agentData);
    
    /**
     * 删除EndAgent
     */
    Result<EndAgent> deleteEndAgent(String agentId);
    
    /**
     * 获取EndAgent详情
     */
    Result<EndAgent> getEndAgentDetails(String agentId);
    
    /**
     * 获取网络状态
     */
    Result<NetworkStatusData> getNetworkStatus();
    
    /**
     * 获取命令统计
     */
    Result<CommandStatsData> getCommandStats();
    
    /**
     * 测试命令
     */
    Result<TestCommandResult> testCommand(Map<String, Object> commandData);
}
```

#### 3.3.2 LlmProvider - LLM能力提供

```java
/**
 * LLM提供者接口
 */
public interface LlmProvider {
    
    /**
     * 同步对话
     */
    String chat(String model, List<Message> messages, Map<String, Object> options);
    
    /**
     * 异步对话
     */
    CompletableFuture<String> chatAsync(String model, List<Message> messages, 
                                        Map<String, Object> options);
    
    /**
     * 流式对话
     */
    void chatStream(String model, List<Message> messages, 
                   Map<String, Object> options, StreamHandler handler);
    
    /**
     * 文本向量化
     */
    float[] embed(String text);
    
    /**
     * 批量向量化
     */
    List<float[]> embedBatch(List<String> texts);
    
    /**
     * 工具调用
     */
    String chatWithTools(String model, List<Message> messages, 
                        List<ToolDefinition> tools, Map<String, Object> options);
}
```

### 3.4 Provider生命周期管理

```
┌─────────────────────────────────────────────────────────────┐
│                    Provider生命周期                          │
└─────────────────────────────────────────────────────────────┘

1. 创建阶段
   │
   ├── 实例化Provider
   │   └── new AgentProviderImpl()
   │
   ▼
2. 注册阶段
   │
   ├── 注册到ProviderRegistry
   │   └── registry.register(AgentProvider.class, provider)
   │
   ▼
3. 初始化阶段
   │
   ├── 调用initialize()
   │   └── 加载配置、建立连接
   │
   ▼
4. 运行阶段
   │
   ├── 调用start()
   │   └── 启动服务、监听端口
   │
   ├── 处理请求
   │   └── provider.handleRequest()
   │
   ├── 健康检查
   │   └── provider.isHealthy()
   │
   ▼
5. 停止阶段
   │
   ├── 调用stop()
   │   └── 关闭连接、释放资源
   │
   └── 从Registry注销
       └── registry.unregister(AgentProvider.class)
```

---

## 四、Discovery系统：南向协议的发现机制

### 4.1 发现服务架构

Discovery系统支持多种发现方式，实现灵活的能力发现：

```
┌─────────────────────────────────────────────────────────────┐
│              CapabilityDiscoveryService                      │
│                   能力发现服务层                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ UDP         │  │   mDNS      │  │ SkillCenter │         │
│  │ Provider    │  │  Provider   │  │  Provider   │         │
│  │ (优先级100) │  │ (优先级90)  │  │ (优先级80)  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Local FS    │  │    DHT      │  │   GitHub    │         │
│  │ Provider    │  │  Provider   │  │  Provider   │         │
│  │ (优先级50)  │  │ (优先级70)  │  │ (优先级60)  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 DiscoveryProvider接口

```java
/**
 * 发现提供者接口
 */
public interface DiscoveryProvider {
    
    /**
     * 获取提供者名称
     */
    String getProviderName();
    
    /**
     * 初始化
     */
    void initialize(DiscoveryConfig config);
    
    /**
     * 启动
     */
    void start();
    
    /**
     * 停止
     */
    void stop();
    
    /**
     * 是否运行中
     */
    boolean isRunning();
    
    /**
     * 执行发现
     */
    CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query);
    
    /**
     * 获取优先级（数值越大优先级越高）
     */
    int getPriority();
    
    /**
     * 是否适用于指定范围
     */
    boolean isApplicable(DiscoveryScope scope);
}
```

### 4.3 发现范围与适用性

| 范围 | 说明 | 适用发现方式 |
|------|------|-------------|
| PERSONAL | 个人设备 | Local FS, UDP |
| DEPARTMENT | 部门分享 | UDP, mDNS, SkillCenter, DHT |
| COMPANY | 公司管理 | SkillCenter API, Git Repository |
| PUBLIC | 公共社区 | SkillCenter API, GitHub, Gitee |

### 4.4 UDP广播发现实现

```java
/**
 * UDP广播发现提供者
 * 
 * 特点：
 * - 局域网快速发现
 * - 低延迟
 * - 零配置
 */
public class UdpBroadcastDiscoveryProvider implements DiscoveryProvider {
    
    private static final int BROADCAST_PORT = 48888;
    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    
    private DatagramSocket socket;
    private volatile boolean running;
    
    @Override
    public void start() {
        try {
            socket = new DatagramSocket(BROADCAST_PORT);
            running = true;
            
            // 启动接收线程
            new Thread(this::receivePackets).start();
        } catch (IOException e) {
            throw new DiscoveryException("UDP启动失败", e);
        }
    }
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            
            // 发送发现请求
            sendDiscoveryRequest(query);
            
            // 等待响应（超时2秒）
            List<ResponsePacket> responses = waitForResponses(2000);
            
            // 解析响应
            for (ResponsePacket response : responses) {
                DiscoveredItem item = parseResponse(response);
                if (item != null) {
                    results.add(item);
                }
            }
            
            return results;
        });
    }
    
    private void sendDiscoveryRequest(DiscoveryQuery query) {
        DiscoveryPacket packet = new DiscoveryPacket();
        packet.setType(MSG_AGENT_ANNOUNCE);
        packet.setQuery(query.getQuery());
        packet.setScope(query.getScope().name());
        
        byte[] data = serialize(packet);
        DatagramPacket datagram = new DatagramPacket(
            data, data.length,
            InetAddress.getByName(BROADCAST_ADDRESS),
            BROADCAST_PORT
        );
        
        socket.send(datagram);
    }
}
```

### 4.5 发现流程详解

```
┌─────────────────────────────────────────────────────────────┐
│                    发现流程详解                               │
└─────────────────────────────────────────────────────────────┘

1. 用户设置发现范围
   │
   └── DiscoveryScope scope = DiscoveryScope.DEPARTMENT;
       discoveryService.setDiscoveryScope(scope);

2. 执行发现查询
   │
   └── DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, "messaging");
       CompletableFuture<List<DiscoveredItem>> future = 
           discoveryService.searchScenes("messaging");

3. 系统选择适用的发现提供者
   │
   ├── 根据 scope 过滤提供者
   │   ├── LocalFsProvider (PERSONAL ✓)
   │   ├── UdpProvider (PERSONAL ✓, DEPARTMENT ✓)
   │   ├── MdnsProvider (DEPARTMENT ✓)
   │   └── SkillCenterProvider (DEPARTMENT ✓, COMPANY ✓, PUBLIC ✓)
   │
   └── 按优先级排序
       ├── UdpProvider (100)
       ├── MdnsProvider (90)
       └── SkillCenterProvider (80)

4. 并行执行发现
   │
   ├── UdpProvider.discover(query)
   │   └── 发现结果 A
   │
   ├── MdnsProvider.discover(query)
   │   └── 发现结果 B
   │
   └── SkillCenterProvider.discover(query)
       └── 发现结果 C

5. 聚合结果
   │
   ├── 去重
   │   └── 根据item.id去重
   │
   ├── 排序
   │   └── 按相关度排序
   │
   └── 返回
       └── List<DiscoveredItem>
```

---

## 五、Session系统：南向协议的会话管理

### 5.1 SessionManager接口

```java
/**
 * Session管理器接口
 */
public interface SessionManager {
    
    /**
     * 创建会话
     */
    SessionInfo createSession(String userId, String username, 
                             String clientIp, String userAgent);
    
    /**
     * 获取会话
     */
    SessionInfo getSession(String sessionId);
    
    /**
     * 验证会话
     */
    boolean validateSession(String sessionId);
    
    /**
     * 刷新会话
     */
    SessionInfo refreshSession(String sessionId);
    
    /**
     * 销毁会话
     */
    void destroySession(String sessionId);
    
    /**
     * 触摸会话（更新活跃时间）
     */
    void touchSession(String sessionId);
    
    /**
     * 获取用户活跃会话
     */
    List<SessionInfo> getActiveSessions(String userId);
    
    /**
     * 销毁用户所有会话
     */
    void destroyUserSessions(String userId);
}
```

### 5.2 SessionManagerImpl实现

```java
/**
 * Session管理器实现
 */
public class SessionManagerImpl implements SessionManager {
    
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userSessions = new ConcurrentHashMap<>();
    
    private long sessionTimeout = 1800000L; // 30分钟
    private int maxSessionsPerUser = 10;
    private SceneEventPublisher eventPublisher;
    
    @Override
    public SessionInfo createSession(String userId, String username, 
                                     String clientIp, String userAgent) {
        String sessionId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        
        SessionInfo session = new SessionInfo();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setUsername(username);
        session.setClientIp(clientIp);
        session.setUserAgent(userAgent);
        session.setCreatedAt(now);
        session.setExpiresAt(now + sessionTimeout);
        session.setLastActiveAt(now);
        session.setStatus("ACTIVE");
        
        sessions.put(sessionId, session);
        
        // 维护用户会话列表
        List<String> userSessionList = userSessions.computeIfAbsent(
            userId, k -> new ArrayList<>());
        userSessionList.add(sessionId);
        
        // 限制用户会话数量
        if (userSessionList.size() > maxSessionsPerUser) {
            String oldestSessionId = userSessionList.remove(0);
            sessions.remove(oldestSessionId);
        }
        
        // 发布会话创建事件
        publishSessionEvent(SessionEvent.created(this, sessionId, userId));
        
        return session;
    }
    
    @Override
    public boolean validateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }
        
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        // 检查是否过期
        if (session.isExpired()) {
            destroySession(sessionId);
            return false;
        }
        
        return "ACTIVE".equals(session.getStatus());
    }
    
    /**
     * 清理过期会话
     */
    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, SessionInfo>> iterator = 
            sessions.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, SessionInfo> entry = iterator.next();
            SessionInfo session = entry.getValue();
            
            if (session.isExpired()) {
                iterator.remove();
                
                String userId = session.getUserId();
                List<String> userSessionList = userSessions.get(userId);
                if (userSessionList != null) {
                    userSessionList.remove(entry.getKey());
                    if (userSessionList.isEmpty()) {
                        userSessions.remove(userId);
                    }
                }
                
                publishSessionEvent(SessionEvent.expired(
                    this, entry.getKey(), userId));
            }
        }
    }
}
```

### 5.3 会话生命周期

```
┌─────────────────────────────────────────────────────────────┐
│                    会话生命周期                              │
└─────────────────────────────────────────────────────────────┘

1. 创建 (Created)
   │
   ├── 用户登录
   ├── 生成SessionId
   ├── 设置过期时间
   └── 发布SessionCreatedEvent

2. 活跃 (Active)
   │
   ├── 请求验证
   ├── 刷新过期时间
   └── 发布SessionRefreshedEvent

3. 过期 (Expired)
   │
   ├── 定时检查
   ├── 清理过期会话
   └── 发布SessionExpiredEvent

4. 销毁 (Destroyed)
   │
   ├── 用户登出
   ├── 从Registry移除
   └── 发布SessionDestroyedEvent
```

---

## 六、Security系统：南向协议的安全保障

### 6.1 SecurityInterceptor安全拦截器

```java
/**
 * 安全拦截器接口
 */
public interface SecurityInterceptor {
    
    /**
     * 前置拦截
     * 
     * @param context 操作上下文
     * @param request 技能请求
     * @return 是否允许执行
     */
    InterceptorResult beforeExecute(OperationContext context, SkillRequest request);
    
    /**
     * 后置拦截
     */
    void afterExecute(OperationContext context, SkillRequest request, SkillResponse response);
    
    /**
     * 异常拦截
     */
    void onError(OperationContext context, SkillRequest request, Throwable error);
    
    /**
     * 获取拦截器优先级
     */
    int getOrder();
}
```

### 6.2 拦截器链实现

```java
/**
 * 安全配置类
 */
public class SecurityConfig {
    
    /**
     * 配置安全拦截器链
     */
    private static void configureSecurityInterceptors() {
        List<SecurityInterceptor> interceptors = new ArrayList<>();
        
        // 1. 输入验证拦截器 (Order: 50)
        interceptors.add(new InputValidationInterceptor());
        
        // 2. 速率限制拦截器 (Order: 25)
        interceptors.add(new RateLimitInterceptor());
        
        // 3. 权限检查拦截器 (Order: 100)
        interceptors.add(new PermissionCheckInterceptor());
        
        // 4. 审计日志拦截器 (Order: 200)
        interceptors.add(new AuditLogInterceptor());
        
        // 按优先级排序
        interceptors.sort(Comparator.comparingInt(SecurityInterceptor::getOrder));
    }
}
```

### 6.3 权限检查拦截器

```java
/**
 * 权限检查拦截器
 */
public class PermissionCheckInterceptor implements SecurityInterceptor {
    
    @Override
    public InterceptorResult beforeExecute(OperationContext context, SkillRequest request) {
        InterceptorResult result = new InterceptorResult();
        
        // 1. 验证TOKEN
        TokenInfo tokenInfo = tokenManager.validateToken(request.getToken());
        if (tokenInfo == null) {
            result.setAllowed(false);
            result.setErrorCode(1001);
            result.setErrorMessage("认证失败：TOKEN无效或已过期");
            return result;
        }
        
        // 2. 检查权限
        Permission permission = permissionService.checkPermission(
            tokenInfo.getUserId(),
            request.getResource(),
            request.getAction()
        );
        
        if (permission == null || !"ALLOW".equals(permission.getEffect())) {
            result.setAllowed(false);
            result.setErrorCode(1002);
            result.setErrorMessage("权限不足：无权执行此操作");
            return result;
        }
        
        // 3. 验证数据访问权限
        if (!validateDataAccess(context, request, permission)) {
            result.setAllowed(false);
            result.setErrorCode(1002);
            result.setErrorMessage("数据访问权限不足");
            return result;
        }
        
        // 4. 检查约束条件
        if (!validateConstraints(request.getConstraints(), permission.getConditions())) {
            result.setAllowed(false);
            result.setErrorCode(1003);
            result.setErrorMessage("违反安全约束条件");
            return result;
        }
        
        result.setAllowed(true);
        return result;
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
}
```

### 6.4 安全拦截流程

```
┌─────────────────────────────────────────────────────────────┐
│                    安全拦截流程                              │
└─────────────────────────────────────────────────────────────┘

请求进入
    │
    ▼
┌─────────────────┐
│ InputValidation │ (Order: 50)
│   输入验证       │
└────────┬────────┘
         │ ✅ 通过
         ▼
┌─────────────────┐
│   RateLimit     │ (Order: 25)
│   速率限制       │
└────────┬────────┘
         │ ✅ 通过
         ▼
┌─────────────────┐
│ PermissionCheck │ (Order: 100)
│   权限检查       │
└────────┬────────┘
         │ ✅ 通过
         ▼
┌─────────────────┐
│   AuditLog      │ (Order: 200)
│   审计日志       │
└────────┬────────┘
         │ ✅ 通过
         ▼
    执行业务逻辑
         │
         ▼
      返回响应
```

---

## 七、Event系统：南向协议的事件驱动

### 7.1 SceneEventPublisher事件发布器

```java
/**
 * 场景事件发布器
 */
@Component
public class SceneEventPublisher {
    
    private final ApplicationEventPublisher publisher;
    
    /**
     * 发布事件
     */
    public void publish(SceneEvent event) {
        publisher.publishEvent(event);
    }
    
    /**
     * 异步发布事件
     */
    public void publishAsync(SceneEvent event) {
        CompletableFuture.runAsync(() -> publish(event));
    }
}
```

### 7.2 核心事件类型

| 事件类型 | 说明 | 触发条件 |
|---------|------|---------|
| CapabilityInvokedEvent | 能力被调用 | 能力调用成功 |
| CapabilityFailedEvent | 能力调用失败 | 能力调用异常 |
| SessionCreatedEvent | 会话创建 | 用户登录成功 |
| SessionDestroyedEvent | 会话销毁 | 用户登出或过期 |
| SkillInstalledEvent | 技能安装 | 技能安装完成 |
| SkillStartedEvent | 技能启动 | 技能启动成功 |

### 7.3 事件驱动架构

```
┌─────────────────────────────────────────────────────────────┐
│                    事件驱动架构                              │
└─────────────────────────────────────────────────────────────┘

事件源 (Event Source)
    │
    ├── Capability调用
    │   └── CapabilityEvent
    │
    ├── Session管理
    │   └── SessionEvent
    │
    ├── Skill操作
    │   └── SkillEvent
    │
    └── Security事件
        └── SecurityEvent
    │
    ▼
SceneEventPublisher
    │
    ├── 同步发布
    │   └── publish(event)
    │
    └── 异步发布
        └── publishAsync(event)
    │
    ▼
事件监听器 (Event Listeners)
    │
    ├── AuditEventListener
    │   └── 记录审计日志
    │
    ├── MetricsEventListener
    │   └── 更新监控指标
    │
    └── NotificationEventListener
        └── 发送通知
```

---

## 八、企业LLM与个人LLM交互实战

### 8.1 场景：知识检索协作

**业务需求**: 个人LLM需要查询企业知识库

#### 完整交互流程

```
┌─────────────────────────────────────────────────────────────┐
│              企业LLM与个人LLM交互完整流程                     │
└─────────────────────────────────────────────────────────────┘

1. 建立会话
   │
   ├── Personal Agent发起会话请求
   │   └── SessionManager.createSession()
   │
   ├── Enterprise Agent验证身份
   │   └── TokenManager.validateToken()
   │
   └── 建立安全通道
       └── ECDH密钥交换

2. 发现能力
   │
   ├── DiscoveryService.searchCapabilities("knowledge")
   │   ├── UDP Provider发现
   │   ├── mDNS Provider发现
   │   └── SkillCenter Provider发现
   │
   └── 返回可用能力列表

3. 调用能力
   │
   ├── 构建CapRequest
   │   ├── capId: "40" (知识检索)
   │   ├── parameters: {query, kbId, topK}
   │   └── token: sessionToken
   │
   ├── CapRouter路由
   │   ├── 检查能力存在
   │   ├── 查找处理器
   │   └── 执行处理
   │
   └── SecurityInterceptor拦截
       ├── InputValidation
       ├── RateLimit
       ├── PermissionCheck
       └── AuditLog

4. 返回结果
   │
   ├── 数据脱敏
   │   └── DataMaskingService.mask()
   │
   ├── 加密传输
   │   └── AES-256-GCM加密
   │
   └── 发布事件
       └── CapabilityInvokedEvent

5. 会话管理
   │
   ├── 刷新会话
   │   └── SessionManager.refreshSession()
   │
   └── 会话超时
       └── SessionManager.cleanupExpiredSessions()
```

### 8.2 代码实现示例

```java
/**
 * 知识检索协作示例
 */
public class KnowledgeCollaborationExample {
    
    private SessionManager sessionManager;
    private DiscoveryService discoveryService;
    private CapRouter capRouter;
    private SecurityInterceptor securityInterceptor;
    
    /**
     * 个人LLM查询企业知识库
     */
    public Object queryEnterpriseKnowledge(String userId, String query, String kbId) {
        
        // 1. 验证会话
        SessionInfo session = sessionManager.getSession(sessionId);
        if (session == null || !sessionManager.validateSession(sessionId)) {
            throw new AuthenticationException("会话无效或已过期");
        }
        
        // 2. 发现知识检索能力
        DiscoveryQuery discoveryQuery = new DiscoveryQuery(
            DiscoveryType.CAPABILITY, 
            "knowledge"
        );
        List<DiscoveredItem> capabilities = discoveryService
            .searchCapabilities("knowledge").join();
        
        if (capabilities.isEmpty()) {
            throw new CapabilityNotFoundException("未找到知识检索能力");
        }
        
        // 3. 构建能力请求
        CapRequest request = new CapRequest(UUID.randomUUID().toString(), "40");
        request.setParameter("query", query);
        request.setParameter("kbId", kbId);
        request.setParameter("topK", 5);
        request.setToken(session.getToken());
        
        // 4. 安全拦截
        OperationContext context = new OperationContext(userId, sessionId);
        SkillRequest skillRequest = new SkillRequest(request);
        
        InterceptorResult interceptorResult = securityInterceptor.beforeExecute(
            context, skillRequest);
        
        if (!interceptorResult.isAllowed()) {
            throw new SecurityException(interceptorResult.getErrorMessage());
        }
        
        // 5. 路由能力请求
        CapResponse response = capRouter.routeRequest("40", request);
        
        // 6. 后置拦截
        securityInterceptor.afterExecute(context, skillRequest, 
            new SkillResponse(response));
        
        // 7. 返回结果
        return response.isSuccess() ? response.getResult() : null;
    }
}
```

---

## 九、南向协议的安全保障机制

### 9.1 多层安全防护

```
┌─────────────────────────────────────────────────────────────┐
│                    多层安全防护体系                          │
└─────────────────────────────────────────────────────────────┘

第一层：网络层安全
├── TLS 1.3传输加密
├── 证书双向验证
└── 网络隔离

第二层：会话层安全
├── Session管理
├── Token认证
├── 会话超时
└── 并发控制

第三层：能力层安全
├── Capability权限控制
├── 参数验证
├── 数据脱敏
└── 审计日志

第四层：应用层安全
├── 业务逻辑验证
├── 数据完整性检查
├── 异常行为检测
└── 安全告警
```

### 9.2 安全威胁防护

| 威胁类型 | 防护措施 | 实现位置 |
|---------|---------|---------|
| 中间人攻击 | TLS 1.3 + 证书验证 | 传输层 |
| 重放攻击 | SessionId + Timestamp + Nonce | 会话层 |
| 身份伪造 | Token + 证书验证 | 认证层 |
| 权限提升 | RBAC + 数据分类 | 权限层 |
| 数据泄露 | 端到端加密 + 数据脱敏 | 数据层 |
| 拒绝服务 | 速率限制 + 熔断 | 网络层 |

---

## 十、总结与展望

### 10.1 核心价值总结

ooderAgent南向协议通过agent-SDK的技术架构，实现了：

```
┌─────────────────────────────────────────────────────────────┐
│                    南向协议核心价值                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  🔧 Capability系统                                           │
│  • 统一的能力抽象                                            │
│  • 灵活的路由机制                                            │
│  • 标准化的调用接口                                          │
│                                                              │
│  🏭 Provider系统                                             │
│  • 可插拔的能力提供                                          │
│  • 统一的生命周期管理                                        │
│  • 灵活的扩展机制                                            │
│                                                              │
│  🔍 Discovery系统                                            │
│  • 多协议发现支持                                            │
│  • 分层发现范围                                              │
│  • 智能结果聚合                                              │
│                                                              │
│  🔐 Security系统                                             │
│  • 多层安全防护                                              │
│  • 细粒度权限控制                                            │
│  • 完整审计追溯                                              │
│                                                              │
│  📊 Event系统                                                │
│  • 事件驱动架构                                              │
│  • 异步处理机制                                              │
│  • 松耦合设计                                                │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 10.2 技术优势

| 优势 | 说明 |
|------|------|
| **架构清晰** | 三层架构，职责明确 |
| **扩展性强** | Provider机制，易于扩展 |
| **安全可靠** | 多层防护，完整审计 |
| **性能优异** | 异步处理，事件驱动 |
| **易于维护** | 接口抽象，松耦合 |

### 10.3 未来展望

1. **联邦学习支持**: 支持在不共享数据的情况下进行模型训练
2. **隐私计算集成**: 集成MPC、TEE等技术
3. **AI治理**: 增加伦理、公平性、可解释性治理
4. **量子安全**: 研究抗量子密码算法
5. **边缘计算**: 支持边缘设备的轻量级部署

---

## 参考文献

1. [ooder Agent Protocol v2.3](docs/protocol/v2.3/agent-protocol.md)
2. [技能发现协议 v2.3](docs/protocol/v2.3/skill-discovery-protocol.md)
3. [协议主文档 v2.3](docs/protocol/v2.3/protocol-main.md)
4. [ooder A2A安全交互机制](docs/A2A_SECURITY_BLOG.md)

---

**关于作者**: Ooder Team致力于构建安全、可信、可控的AI Agent协作平台。

**联系方式**: https://gitee.com/ooderCN

---

*本文版权归Ooder Team所有，转载请注明出处。*
