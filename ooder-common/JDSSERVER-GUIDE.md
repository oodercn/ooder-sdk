# JDSServer 连接管理调用说明

## 一、概述

JDSServer 是 ooder 框架的核心连接管理组件，提供会话管理、缓存管理、健康检查等功能。本文档描述 JDSServer 2.2 版本的 API 调用方式。

## 二、模块依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.2</version>
</dependency>
```

## 三、核心组件

### 3.1 组件架构

```
JDSServerSupport (入口类)
├── SessionManager (会话管理)
├── SessionCacheManager (缓存管理)
├── SessionAdminService (管理服务)
└── SceneConfig (场景配置)
```

### 3.2 获取实例

```java
import net.ooder.server.JDSServerSupport;
import net.ooder.common.JDSException;

// 方式一：获取实例（可能抛出异常）
JDSServerSupport server = JDSServerSupport.getInstance();

// 方式二：安全获取（不抛出异常）
JDSServerSupport server = JDSServerSupport.getInstanceOrNull();
if (server == null) {
    // 初始化失败
    Throwable error = JDSServerSupport.getInitializationError();
}

// 方式三：检查可用性
if (JDSServerSupport.isAvailable()) {
    JDSServerSupport server = JDSServerSupport.getInstance();
}

// 重置实例
JDSServerSupport.reset();
```

## 四、会话管理 API

### 4.1 创建会话

```java
import net.ooder.server.session.SessionManager;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

SessionManager sessionManager = server.getSessionManager();

// 创建连接信息
ConnectInfo connectInfo = new ConnectInfo("userId", "loginName", "password");

// 创建会话
JDSSessionHandle handle = sessionManager.createSession(connectInfo);
String sessionId = handle.getSessionID();
```

### 4.2 会话操作

```java
// 获取会话
JDSSessionHandle handle = sessionManager.getSession(sessionId);

// 检查会话有效性
boolean valid = sessionManager.isSessionValid(sessionId);

// 保持会话活跃
sessionManager.keepAlive(sessionId);

// 使会话失效
sessionManager.invalidateSession(sessionId);
```

### 4.3 会话统计

```java
import net.ooder.server.session.SessionStats;

SessionStats stats = sessionManager.getStats();

System.out.println("活跃会话数: " + stats.getActiveSessions());
System.out.println("总登录数: " + stats.getTotalLogins());
System.out.println("总登出数: " + stats.getTotalLogouts());
System.out.println("过期会话数: " + stats.getExpiredSessions());
System.out.println("平均登录时间: " + stats.getAvgLoginTime() + "ms");
System.out.println("登出率: " + stats.getLogoutRate() + "%");
System.out.println("过期率: " + stats.getExpirationRate() + "%");
```

### 4.4 生命周期监听

```java
import net.ooder.server.session.SessionLifecycle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.engine.ConnectInfo;

SessionLifecycle listener = new SessionLifecycle() {
    @Override
    public void onCreated(JDSSessionHandle handle, ConnectInfo info) {
        System.out.println("会话创建: " + handle.getSessionID());
    }
    
    @Override
    public void onActivated(JDSSessionHandle handle) {
        System.out.println("会话激活: " + handle.getSessionID());
    }
    
    @Override
    public void onExpired(JDSSessionHandle handle) {
        System.out.println("会话过期: " + handle.getSessionID());
    }
    
    @Override
    public void onDestroyed(JDSSessionHandle handle) {
        System.out.println("会话销毁: " + handle.getSessionID());
    }
};

sessionManager.registerLifecycleListener(listener);
sessionManager.unregisterLifecycleListener(listener);
```

## 五、缓存管理 API

### 5.1 缓存操作

```java
import net.ooder.server.session.SessionCacheManager;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.engine.ConnectInfo;

SessionCacheManager cacheManager = server.getCacheManager();

// 存储会话
cacheManager.putSession(handle, connectInfo, "systemCode");

// 获取会话信息
ConnectInfo info = cacheManager.getConnectInfo(sessionId);
JDSSessionHandle handle = cacheManager.getSessionHandle(sessionId);
String systemCode = cacheManager.getSystemCode(sessionId);
Long connectTime = cacheManager.getConnectTime(sessionId);

// 更新连接时间
cacheManager.updateConnectTime(sessionId);

// 检查会话是否存在
boolean exists = cacheManager.containsSession(sessionId);

// 获取会话数量
int count = cacheManager.getSessionCount();

// 使会话失效
cacheManager.invalidateSession(sessionId);

// 清理过期会话
cacheManager.cleanupExpiredSessions();

// 清空所有缓存
cacheManager.clear();
```

### 5.2 缓存统计

```java
import net.ooder.server.session.CacheStats;

CacheStats stats = cacheManager.getStats();

System.out.println("会话数: " + stats.getSessionCount());
System.out.println("连接信息数: " + stats.getConnectInfoCount());
System.out.println("系统代码数: " + stats.getSystemCodeCount());
System.out.println("连接时间数: " + stats.getConnectTimeCount());
System.out.println("缓存一致性: " + stats.isConsistent());
```

## 六、管理服务 API

### 6.1 会话查询

```java
import net.ooder.server.session.admin.SessionAdminService;
import net.ooder.server.session.admin.SessionInfo;
import java.util.List;

SessionAdminService adminService = server.getAdminService();

// 获取单个会话信息
SessionInfo info = adminService.getSessionInfo(sessionId);

// 获取所有会话
List<SessionInfo> allSessions = adminService.getAllSessions();

// 按用户ID查询
List<SessionInfo> userSessions = adminService.getSessionsByUserId("userId");

// 按账号查询
List<SessionInfo> accountSessions = adminService.getSessionsByAccount("account");

// 按系统代码查询
List<SessionInfo> systemSessions = adminService.getSessionsBySystemCode("systemCode");

// 获取过期会话
List<SessionInfo> expiredSessions = adminService.getExpiredSessions();

// 分页查询
List<SessionInfo> pageSessions = adminService.getSessions(0, 10); // 第一页，每页10条
```

### 6.2 SessionInfo 对象

```java
public class SessionInfo {
    private String sessionId;       // 会话ID
    private String userId;          // 用户ID
    private String account;         // 登录账号
    private String userName;        // 用户名
    private String mobile;          // 手机号
    private String email;           // 邮箱
    private String systemCode;      // 系统代码
    private Date connectTime;       // 连接时间
    private Date lastActiveTime;    // 最后活跃时间
    private long duration;          // 持续时间(ms)
    private String status;          // 状态: ACTIVE/EXPIRED
    private String clientIp;        // 客户端IP
    private String clientInfo;      // 客户端信息
    
    // 格式化持续时间
    public String getDurationFormatted(); // 返回 "HH:MM:SS" 格式
    
    // 检查是否过期
    public boolean isExpired(long expireTimeMs);
}
```

### 6.3 会话操作

```java
import net.ooder.server.session.admin.SessionOperationResult;

// 使单个会话失效
SessionOperationResult result = adminService.invalidateSession(sessionId);

// 按用户ID使会话失效
result = adminService.invalidateSessionsByUserId("userId");

// 按账号使会话失效
result = adminService.invalidateSessionsByAccount("account");

// 使所有会话失效
result = adminService.invalidateAllSessions();

// 使过期会话失效
result = adminService.invalidateExpiredSessions();

// 保持会话活跃
result = adminService.keepAlive(sessionId);

// 强制下线
result = adminService.forceLogout(sessionId, "管理员强制下线");

// 检查操作结果
if (result.isSuccess()) {
    System.out.println("操作成功: " + result.getMessage());
} else {
    System.out.println("操作失败: " + result.getErrorCode() + " - " + result.getMessage());
}
```

### 6.4 健康检查

```java
import net.ooder.server.session.admin.SessionHealthCheck;

// 基础健康检查
SessionHealthCheck health = adminService.healthCheck();

// 详细健康检查
SessionHealthCheck detailedHealth = adminService.detailedHealthCheck();

System.out.println("状态: " + health.getStatus()); // UP/DOWN/DEGRADED
System.out.println("活跃会话数: " + health.getActiveSessionCount());
System.out.println("缓存健康: " + health.isCacheHealthy());
System.out.println("会话管理器健康: " + health.isSessionManagerHealthy());

// 详细信息
System.out.println("内存使用: " + detailedHealth.getMemoryUsedFormatted());
System.out.println("最大内存: " + detailedHealth.getMemoryMaxFormatted());
System.out.println("内存使用率: " + detailedHealth.getMemoryUsagePercent() + "%");
System.out.println("运行时间: " + detailedHealth.getUptimeFormatted());
```

### 6.5 统计信息

```java
import java.util.Map;

// 获取统计信息
Map<String, Object> stats = adminService.getStatistics();
System.out.println("活跃会话: " + stats.get("activeSessions"));
System.out.println("总登录数: " + stats.get("totalLogins"));
System.out.println("总登出数: " + stats.get("totalLogouts"));
System.out.println("过期会话: " + stats.get("expiredSessions"));
System.out.println("平均登录时间: " + stats.get("avgLoginTime") + "ms");
System.out.println("登出率: " + stats.get("logoutRate") + "%");
System.out.println("过期率: " + stats.get("expirationRate") + "%");

// 获取缓存统计
Map<String, Object> cacheStats = adminService.getCacheStatistics();

// 快捷方法
int activeCount = adminService.getActiveSessionCount();
int totalCount = adminService.getTotalSessionCount();
long avgDuration = adminService.getAverageSessionDuration();
```

### 6.6 会话属性

```java
// 设置会话属性
adminService.setSessionAttribute(sessionId, "theme", "dark");
adminService.setSessionAttribute(sessionId, "language", "zh-CN");

// 获取会话属性
Object theme = adminService.getSessionAttribute(sessionId, "theme");

// 获取所有属性
Map<String, Object> attrs = adminService.getSessionAttributes(sessionId);
```

### 6.7 分析功能

```java
// 获取活跃用户ID列表
List<String> activeUserIds = adminService.getActiveUserIds();

// 按系统代码统计会话数
Map<String, Integer> bySystem = adminService.getSessionCountBySystemCode();
// {"systemA": 10, "systemB": 5, "unknown": 2}

// 按小时统计会话数
Map<String, Integer> byHour = adminService.getSessionCountByHour();
// {"08:00": 5, "09:00": 12, "10:00": 8}
```

## 七、JDSServerSupport 快捷方法

```java
// 获取会话信息
SessionInfo info = server.getSessionInfo(sessionId);

// 获取所有会话
List<SessionInfo> sessions = server.getAllSessions();

// 使会话失效
SessionOperationResult result = server.invalidateSession(sessionId);

// 按用户ID使会话失效
result = server.invalidateSessionsByUserId("userId");

// 使所有会话失效
result = server.invalidateAllSessions();

// 健康检查
SessionHealthCheck health = server.healthCheck();
SessionHealthCheck detailedHealth = server.detailedHealthCheck();

// 获取统计信息
Map<String, Object> stats = server.getStatistics();

// 获取活跃会话数
int count = server.getActiveSessionCount();

// 获取运行时间
long uptime = server.getUptime();
```

## 八、场景配置集成

### 8.1 配置文件

```yaml
# scene-dev.yaml
scene:
  id: dev-scene
  name: 开发环境场景
  
  jds:
    sceneId: dev-jds
    configName: jds
    adminPort: 9090
    adminKey: jds-admin
    singleLogin: true
    sessionEnabled: true
    sessionExpireTime: 1800000    # 30分钟
    sessionCheckInterval: 300000  # 5分钟
    cacheMaxSize: 10485760        # 10MB
    cacheExpireTime: 86400000     # 24小时
    clusterEnabled: false
    udpPort: 8087
    
  org:
    sceneId: dev-org
    configName: org
    cacheEnabled: true
    cacheExpireTime: 604800000    # 7天
```

### 8.2 获取场景配置

```java
import net.ooder.config.scene.SceneConfig;
import net.ooder.config.scene.JdsSceneConfig;
import net.ooder.config.scene.OrgSceneConfig;

SceneConfig sceneConfig = server.getSceneConfig();

// 获取 JDS 配置
JdsSceneConfig jdsConfig = sceneConfig.getJds();
if (jdsConfig != null) {
    System.out.println("管理端口: " + jdsConfig.getAdminPort());
    System.out.println("会话过期时间: " + jdsConfig.getSessionExpireTime());
    System.out.println("单点登录: " + jdsConfig.isSingleLogin());
}

// 获取 Org 配置
OrgSceneConfig orgConfig = sceneConfig.getOrg();
if (orgConfig != null) {
    System.out.println("缓存启用: " + orgConfig.isCacheEnabled());
    System.out.println("缓存过期时间: " + orgConfig.getCacheExpireTime());
}
```

## 九、错误处理

### 9.1 异常类型

```java
import net.ooder.common.JDSException;

try {
    JDSServerSupport server = JDSServerSupport.getInstance();
} catch (JDSException e) {
    // 初始化失败
    System.err.println("JDSServer 初始化失败: " + e.getMessage());
    Throwable cause = e.getCause();
}
```

### 9.2 操作结果检查

```java
SessionOperationResult result = adminService.invalidateSession(sessionId);

if (result.isSuccess()) {
    System.out.println("操作成功");
} else {
    String errorCode = result.getErrorCode();
    String message = result.getMessage();
    
    switch (errorCode) {
        case "NOT_FOUND":
            System.out.println("会话不存在");
            break;
        case "INVALID_PARAM":
            System.out.println("参数无效");
            break;
        case "ERROR":
            System.out.println("服务器错误: " + message);
            break;
        default:
            System.out.println("未知错误: " + errorCode);
    }
}
```

## 十、最佳实践

### 10.1 会话管理

```java
// 推荐：使用 try-with-resources 风格
public class SessionHelper {
    private final JDSServerSupport server;
    
    public SessionHelper() {
        this.server = JDSServerSupport.getInstanceOrNull();
    }
    
    public boolean isAvailable() {
        return server != null && JDSServerSupport.isAvailable();
    }
    
    public SessionInfo getSessionSafely(String sessionId) {
        if (!isAvailable()) return null;
        return server.getSessionInfo(sessionId);
    }
}
```

### 10.2 定时清理

```java
import java.util.Timer;
import java.util.TimerTask;

// 定时清理过期会话
Timer cleanupTimer = new Timer("SessionCleanup", true);
cleanupTimer.schedule(new TimerTask() {
    @Override
    public void run() {
        JDSServerSupport server = JDSServerSupport.getInstanceOrNull();
        if (server != null) {
            server.cleanupExpiredSessions();
        }
    }
}, 0, 5 * 60 * 1000); // 每5分钟执行一次
```

### 10.3 健康检查端点

```java
// 用于 HTTP 健康检查端点
public class HealthCheckEndpoint {
    
    public Map<String, Object> check() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        JDSServerSupport server = JDSServerSupport.getInstanceOrNull();
        if (server == null) {
            result.put("status", "DOWN");
            result.put("reason", "JDSServer not initialized");
            return result;
        }
        
        SessionHealthCheck health = server.healthCheck();
        result.put("status", health.getStatus());
        result.put("activeSessions", health.getActiveSessionCount());
        result.put("cacheHealthy", health.isCacheHealthy());
        result.put("sessionManagerHealthy", health.isSessionManagerHealthy());
        
        return result;
    }
}
```

---

**文档版本**: v1.0  
**适用版本**: ooder-common-client 2.2  
**更新日期**: 2026-02-19
