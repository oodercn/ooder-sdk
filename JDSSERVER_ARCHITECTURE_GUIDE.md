# JDSServer 架构注意事项文档

## 概述

本文档详细说明 JDSServer 的核心架构，作为 Skills 重构的参考依据。**每次重构前必须阅读本文档**。

---

## 1. 核心定位

### 1.1 JDSServer 是什么

JDSServer 是 ooder 框架的**核心连接管理组件**，职责包括：
- **Session 管理**：用户会话生命周期管理
- **ConnectInfo 管理**：全局唯一的用户连接信息管理
- **缓存管理**：多级缓存体系维护
- **集群协调**：分布式环境下的节点通信
- **事件分发**：服务器生命周期事件管理

### 1.2 关键设计原则

```
┌─────────────────────────────────────────────────────────────┐
│                    JDSServer 设计原则                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 单例模式：全局唯一实例                                    │
│     JDSServer.getInstance()                                 │
│                                                             │
│  2. 有状态服务：维护大量运行时状态                             │
│     - Session 状态                                           │
│     - ConnectInfo 状态                                       │
│     - 缓存状态                                               │
│                                                             │
│  3. 缓存驱动：大量使用缓存提升性能                             │
│     - 5个核心缓存                                            │
│     - 分布式缓存支持                                         │
│                                                             │
│  4. 事件驱动：通过事件机制解耦                                 │
│     - 服务器生命周期事件                                      │
│     - 集群事件                                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 核心组件详解

### 2.1 五大核心缓存（极其重要）

```java
// 位置：JDSServer.java 第173-184行

// 1. Session 与 ConnectInfo 映射（最关键）
private static Cache<String, ConnectInfo> sessionhandleConnectInfoCache;
// key: sessionID
// value: ConnectInfo (userID, loginName, password)

// 2. Session 与 SystemCode 映射
private static Cache<String, String> sessionhandleSystemCodeCache;
// key: sessionID
// value: systemCode

// 3. SessionHandle 实例缓存
private static Cache<String, JDSSessionHandle> sessionHandleCache;
// key: sessionID
// value: JDSSessionHandle

// 4. 用户到 Session 列表映射（JSON序列化）
private static Cache<String, String> connectHandleCache;
// key: userID
// value: JSONArray<JDSSessionHandle> (JSON字符串)

// 5. 连接时间戳缓存
protected static Cache<String, Long> connectTimeCache;
// key: sessionID
// value: 连接时间戳
```

**缓存关系图**：

```
User Login
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│  1. 创建 JDSSessionHandle                                   │
│     └── sessionHandleCache.put(sessionID, handle)           │
│                                                             │
│  2. 存储 ConnectInfo                                        │
│     └── sessionhandleConnectInfoCache.put(sessionID, info)  │
│                                                             │
│  3. 记录连接时间                                             │
│     └── connectTimeCache.put(sessionID, currentTime)        │
│                                                             │
│  4. 更新用户 Session 列表                                    │
│     └── connectHandleCache.put(userID, jsonArray)           │
│         (JSON序列化存储多个 SessionHandle)                   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 ConnectInfo 详解

**类位置**: `net.ooder.engine.ConnectInfo`

**核心属性**:
```java
public class ConnectInfo implements Serializable {
    private String userID;      // 用户ID
    private String loginName;   // 登录名
    private String password;    // 密码
}
```

**重要特性**:
1. **全局唯一**：通过 userID + loginName 标识一个用户
2. **可序列化**：支持分布式缓存存储
3. **equals/hashCode**：基于 userID 和 loginName 实现

**获取方式**:
```java
// 从 JDSServer 获取
JDSServer server = JDSServer.getInstance();
ConnectInfo info = server.getConnectInfo(sessionHandle);

// 从缓存获取
ConnectInfo info = sessionhandleConnectInfoCache.get(sessionID);
```

---

## 3. Session 生命周期管理

### 3.1 完整生命周期流程

```
┌─────────────┐
│   开始     │
└──────┬──────┘
       │
       ▼ 用户调用 connect()
┌─────────────────────────────────────────────────────────────┐
│ 1. 参数校验                                                  │
│    - 检查服务器是否启动                                       │
│    - 获取 sessionHandle 和 connectInfo                       │
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. ConnectInfo 恢复策略（重要）                               │
│    优先级：                                                   │
│    1) clientService.getConnectInfo()                         │
│    2) sessionhandleConnectInfoCache.get(sessionID)           │
│    3) clientService.getConnectionHandle().getConnectInfo()   │
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. 单点登录检查（如果启用）                                    │
│    Config.singleLogin() == true                              │
│    └── 检查同一用户是否已有其他 Session                        │
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Session 处理分支                                          │
│    ├── 首次登录：创建新的 SessionHandleList                   │
│    ├── 重复登录：更新 clientServiceMap                        │
│    └── 新Session登录：invalidate旧Session -> 添加到新列表     │
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. 缓存更新（5个缓存都要更新）                                 │
│    ├── sessionHandleCache.put(sessionID, sessionHandle)      │
│    ├── sessionhandleConnectInfoCache.put(sessionID, connectInfo)
│    ├── connectTimeCache.put(sessionID, currentTime)          │
│    └── updateHandle(connectInfo, sessionHandleList)          │
│        (更新 connectHandleCache)                             │
└─────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────┐
│   活跃状态  │
└─────────────┘
```

### 3.2 Session 过期检查机制

```java
// SessionCheckTask 定时任务
public class SessionCheckTask implements Runnable {
    public void run() {
        // 1. 遍历 sessionHandleCache
        // 2. 检查 (currentTime - loginTime) > expireTime
        // 3. 收集过期 Session
        // 4. 调用 invalidateSession(List) 批量失效
    }
}

// 调度配置（JDSServer.java 第403-405行）
SessionCheckTask sessionCheckTask = new SessionCheckTask(expireTime);
Executors.newSingleThreadScheduledExecutor()
    .scheduleWithFixedDelay(sessionCheckTask, 15000, checkInterval, TimeUnit.MILLISECONDS);
```

---

## 4. JDSClientService 体系

### 4.1 双重 Map 结构（重要）

```java
// JDSServer.java 第170行
private static ConcurrentMap<String, ConcurrentMap<ConfigCode, JDSClientService>> clientServiceMap;

// 结构说明：
// 第一层 Key: sessionID (用户会话标识)
// 第二层 Key: ConfigCode (应用/系统代码，如 "org", "vfs", "msg")
// Value: JDSClientService 实例
```

**示例**:
```
clientServiceMap = {
    "session-001": {
        "org" -> JDSClientServiceImpl@1a2b3c,
        "vfs" -> JDSClientServiceImpl@4d5e6f,
        "msg" -> JDSClientServiceImpl@7g8h9i
    },
    "session-002": {
        "org" -> JDSClientServiceImpl@2b3c4d
    }
}
```

### 4.2 创建流程

```java
// newJDSClientService 方法（第634-715行）
public JDSClientService newJDSClientService(JDSSessionHandle sessionHandle, ConfigCode configCode) {
    // 1. 检查服务器状态
    
    // 2. 获取 CApplication 配置
    CApplication app = this.getClusterClient().getApplication(configCode);
    
    // 3. 分支处理
    if (app == null) {
        // 无配置：创建默认实现
        jdsService = new JDSClientServiceImpl(sessionHandle, configCode);
    } else if (app.getJdsService() != null) {
        // 有自定义配置：反射加载
        String jdsServiceStr = app.getJdsService().getImplementation();
        Class clazz = ClassUtility.loadClass(jdsServiceStr);
        Constructor constructor = clazz.getConstructor(ConnectInfo.class, ConfigCode.class);
        jdsService = (JDSClientService) constructor.newInstance(connectInfo, configCode);
    }
    
    // 4. 创建 ConnectionHandle
    ConnectionHandle handle = new DefaultConnectionHandle(jdsService, sessionHandle, configCode);
    jdsService.setConnectionHandle(handle);
    
    return jdsService;
}
```

---

## 5. 事件机制

### 5.1 双事件系统

```
┌─────────────────────────────────────────────────────────────┐
│                    JDS 双事件系统                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────┐                                │
│  │     EventControl        │  <-- 本地服务器事件             │
│  │  (单例，本地JVM内)       │                                │
│  │                         │                                │
│  │  事件类型：               │                                │
│  │  - serverStarting       │                                │
│  │  - serverStarted        │                                │
│  │  - serverStopping       │                                │
│  │  - serverStopped        │                                │
│  │  - systemSaving         │                                │
│  │  - ...                  │                                │
│  └─────────────────────────┘                                │
│                                                             │
│  ┌─────────────────────────┐                                │
│  │   ClusterEventControl   │  <-- 集群分布式事件             │
│  │  (UDP广播，跨节点)        │                                │
│  │                         │                                │
│  │  通过 UDP 发送到集群所有节点                              │
│  │  支持事件防重（1秒内重复过滤）                             │
│  └─────────────────────────┘                                │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 事件触发点

| 位置 | 事件 | 说明 |
|-----|------|------|
| `_start()` 第460行 | `serverStarting` | 服务器开始启动 |
| `_start()` 第481行 | `serverStarted` | 服务器启动完成 |
| `_stop()` 第493行 | `serverStopping` | 服务器开始停止 |
| `_stop()` 第530行 | `serverStopped` | 服务器停止完成 |

---

## 6. 集群功能

### 6.1 ClusterClient 架构

```java
public interface ClusterClient {
    void login();              // 登录集群
    void start();              // 启动集群客户端
    void stop();               // 停止集群客户端
    UDPClient getUDPClient();  // 获取UDP客户端
    boolean isLogin();         // 是否已登录
    
    // 服务器节点管理
    ServerNode getServerNodeById(String nodeId);
    List<ServerNode> getAllServer();
    
    // 应用管理
    CApplication getApplication(ConfigCode systemCode);
    
    // 子系统管理
    SubSystem getSystem(String systemCode);
}
```

### 6.2 集群初始化

```java
// JDSServer 构造函数第196-201行
private JDSServer() throws JDSException {
    init();                          // 1. 初始化服务器
    this.getClusterClient().start(); // 2. 启动集群客户端
    _start();                        // 3. 启动服务器业务
}
```

---

## 7. 配置体系

### 7.1 关键配置项

| 配置项 | 默认值 | 说明 | 位置 |
|-------|-------|------|------|
| `session.ExpireTime` | 30 | Session过期时间(分钟) | engine_config.xml |
| `session.CheckInterval` | 5 | Session检查间隔(分钟) | engine_config.xml |
| `session.enabled` | true | 是否启用Session检查 | engine_config.xml |
| `singleLogin` | false | 是否单点登录 | engine_config.xml |
| `admin.StartAdminThread` | false | 是否启动管理线程 | engine_config.xml |
| `admin.port` | 10523 | 管理端口 | engine_config.xml |
| `udpServer.enabled` | - | UDP服务器启用 | engine_config.xml |
| `udpServer.port` | 8087 | UDP端口 | engine_config.xml |

### 7.2 配置加载优先级

1. `engine_config.xml` - 主配置文件
2. `application.properties` - Spring配置
3. `jds_init.properties` - 初始化配置
4. `jdsclient_init.properties` - 客户端配置

---

## 8. 线程安全机制

### 8.1 同步机制一览

```java
// 1. 单例创建锁
private static final Object THREAD_LOCK = new Object();
synchronized (THREAD_LOCK) {
    if (instance == null) {
        instance = new JDSServer();
    }
}

// 2. 读写锁
public static final ReadWriteLock lock = new ReentrantReadWriteLock(false);

// 3. 并发容器
private static ConcurrentMap<String, ConcurrentMap<ConfigCode, JDSClientService>> clientServiceMap;

// 4. 线程池
ExecutorService clearSensorservice = Executors.newFixedThreadPool(50);
```

### 8.2 线程池用途

| 线程池 | 用途 | 大小 |
|-------|------|------|
| `clearSensorservice` | 异步清理Session | 50线程 |
| `heartService` | UDP心跳处理 | 150线程 |
| 定时任务线程池 | Session检查 | 1线程 |

---

## 9. Skills 重构注意事项

### 9.1 ConnectInfo 使用规范

**必须遵守**:
1. 使用 `net.ooder.engine.ConnectInfo`，不创建新的 ConnectInfo 类
2. 从 JDSServer 获取：`JDSServer.getInstance().getConnectInfo(sessionHandle)`
3. 不包装 ConnectInfo，直接使用原始对象
4. ConnectInfo 随 Session 生命周期管理，不单独维护

### 9.2 Session 管理规范

**禁止**:
- 在 Skills 中单独维护 Session 状态
- 绕过 JDSServer 直接操作 Session 缓存
- 创建新的 Session 管理机制

**必须**:
- 通过 JDSServer 接口操作 Session
- 使用 JDSServer 提供的 ConnectInfo
- 遵循 JDSServer 的生命周期管理

### 9.3 缓存使用规范

**禁止**:
- 直接访问 JDSServer 的私有缓存字段
- 在 Skills 中创建与 JDSServer 功能重复的缓存

**推荐**:
- 使用 JDSServer 提供的公共方法获取数据
- 如需缓存，使用独立的缓存命名空间

### 9.4 事件规范

**Skills 应该**:
- 监听 JDSServer 的事件（serverStarting, serverStarted等）
- 在 serverStarting 时初始化
- 在 serverStopping 时清理资源

**Skills 不应该**:
- 发送 JDSServer 级别的生命周期事件
- 直接操作 EventControl

---

## 10. 重构检查清单

每次重构前，必须确认以下事项：

### 10.1 代码检查

- [ ] 是否使用了 `net.ooder.engine.ConnectInfo`？
- [ ] 是否从 JDSServer 获取 ConnectInfo？
- [ ] 是否遵循 Session 生命周期管理？
- [ ] 是否使用了 JDSServer 提供的缓存？
- [ ] 是否正确处理了集群环境？

### 10.2 功能检查

- [ ] Session 创建是否正常？
- [ ] Session 过期是否正常？
- [ ] 用户注销是否正常？
- [ ] 单点登录是否正常（如果启用）？
- [ ] 集群事件是否正常？

### 10.3 性能检查

- [ ] 缓存命中率是否正常？
- [ ] Session 检查任务是否正常运行？
- [ ] 线程池是否正常工作？
- [ ] 内存使用是否正常？

---

## 11. 关键代码位置速查

| 功能 | 类 | 方法 | 行号 |
|-----|-----|------|------|
| 单例获取 | JDSServer | getInstance() | 210-221 |
| Connect 流程 | JDSServer | connect() | 850-934 |
| Disconnect 流程 | JDSServer | disconnect() | 942-966 |
| Session 失效 | JDSServer | invalidateSession() | 795-816 |
| ConnectInfo 获取 | JDSServer | getConnectInfo() | 1244-1247 |
| ClientService 创建 | JDSServer | newJDSClientService() | 634-715 |
| ClientService 获取 | JDSServer | getJDSClientService() | 729-763 |
| Session 激活 | JDSServer | activeSession() | 768-774 |
| 缓存初始化 | JDSServer | init() | 338-436 |
| 服务器启动 | JDSServer | _start() | 454-486 |
| 服务器停止 | JDSServer | _stop() | 488-538 |
| Session 检查任务 | SessionCheckTask | run() | - |
| 事件分发 | EventControl | dispatchEvent() | - |
| 集群客户端获取 | JDSServer | getClusterClient() | 1273-1283 |

---

## 12. 常见问题

### Q1: 如何获取当前用户的 ConnectInfo？

```java
JDSServer server = JDSServer.getInstance();
JDSSessionHandle handle = ...; // 从上下文获取
ConnectInfo info = server.getConnectInfo(handle);
```

### Q2: 如何检查用户是否已登录？

```java
JDSServer server = JDSServer.getInstance();
Set<JDSSessionHandle> handles = server.getSessionHandleList(connectInfo);
boolean isLogined = !handles.isEmpty();
```

### Q3: 如何获取所有活跃用户？

```java
List<ConnectInfo> allUsers = JDSServer.getAllConnectInfo();
```

### Q4: Skills 如何监听服务器启动？

```java
// 实现 EIServerListener 接口
public class SkillInitializer implements EIServerListener {
    public void onServerStarted(EIServerEvent event) {
        // 初始化 Skill
    }
}

// 注册监听器
EventControl.getInstance().addListener(new SkillInitializer());
```

---

**文档版本**: v1.0  
**最后更新**: 2026-02-26  
**适用版本**: ooder-common 2.3+
