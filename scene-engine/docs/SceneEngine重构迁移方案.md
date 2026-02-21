# SceneEngine 重构迁移方案

## 一、重构目标

将 ooder-Nexus-Enterprise 和 ooder-Nexus 项目中关于场景、能力、消息等 API 调用进行抽取，拆分为用户接口层和 Engine 实现层，为工程全面迁移到 SceneEngine 做准备。

### 1.1 重构原则

| 原则 | 说明 |
|------|------|
| 用户层完整性 | 用户层逻辑完整，减少外部依赖 |
| DTO 统一抽象 | 合并现有 DTO 和实体 Bean |
| 接口与实现分离 | 用户接口与 Engine 实现解耦 |
| 向后兼容 | 保持现有 API 兼容性 |

### 1.2 重构范围

```
重构范围：
├── 场景相关 API
│   ├── 场景管理
│   ├── 场景组管理
│   └── 场景协作
├── 能力相关 API
│   ├── 能力注册
│   ├── 能力调用
│   └── 能力监控
├── 消息相关 API
│   ├── 消息发送
│   ├── 消息订阅
│   └── 消息推送
├── 组织相关 API
│   ├── 用户管理
│   ├── 部门管理
│   └── 角色权限
└── 文件相关 API
    ├── 文件上传下载
    ├── 文件夹管理
    └── 文件共享
```

---

## 二、DTO 统一梳理

### 2.1 现有 DTO 分析

#### 企业端 DTO

| 模块 | DTO 类 | 说明 |
|------|--------|------|
| 异常处理 | AnomalyDTO | 异常数据传输对象 |
| 异常处理 | CorrectionDTO | 纠正结果数据传输对象 |
| 异常处理 | AnomalyQueryDTO | 查询参数数据传输对象 |
| 异常处理 | InterventionDTO | 手动干预请求数据传输对象 |
| 异常处理 | AnomalyStatsDTO | 统计数据传输对象 |
| 立体观测 | TopologyNodeDTO | 拓扑节点数据传输对象 |
| 立体观测 | RouteDTO | 路由数据传输对象 |
| 立体观测 | LogDTO | 日志数据传输对象 |
| 协作协调 | CollabRequestDTO | 协作请求数据传输对象 |
| 协作协调 | TaskDTO | 任务数据传输对象 |

#### 北向服务数据模型

| 模块 | 类名 | 说明 |
|------|------|------|
| 能力管理 | CapabilityConfig | 能力配置 |
| 能力管理 | CapabilityDetail | 能力详情 |
| 能力管理 | CapabilityQuery | 能力查询 |
| 能力管理 | CapabilityStatistics | 能力统计 |
| 能力管理 | AuthorizationInfo | 授权信息 |
| 能力管理 | InvocationLog | 调用日志 |
| 场景管理 | SceneDefinition | 场景定义 |
| 场景管理 | CapabilityRequirement | 能力需求定义 |
| 场景管理 | SceneRule | 场景规则 |
| 场景管理 | SceneLifecycle | 场景生命周期 |

### 2.2 统一 DTO 设计

#### 用户层 DTO（scene-user-api）

```java
package net.ooder.northbound.scene.user;

/**
 * 用户信息 DTO
 */
public class UserInfoDTO implements Serializable {
    
    private String userId;
    private String account;
    private String name;
    private String email;
    private String phone;
    private String orgId;
    private String orgName;
    private String deptId;
    private String deptName;
    private List<String> roleIds;
    private String status;
    private Long createTime;
    private Long updateTime;
    
    // getters and setters
}

/**
 * 部门信息 DTO
 */
public class DepartmentDTO implements Serializable {
    
    private String deptId;
    private String deptName;
    private String parentId;
    private String orgId;
    private Integer memberCount;
    private Long createTime;
    
    // getters and setters
}

/**
 * 角色信息 DTO
 */
public class RoleDTO implements Serializable {
    
    private String roleId;
    private String roleName;
    private String orgId;
    private List<String> permissions;
    private Long createTime;
    
    // getters and setters
}

/**
 * 消息 DTO
 */
public class MessageDTO implements Serializable {
    
    private String messageId;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toUserName;
    private String content;
    private String messageType;
    private Integer status;
    private Long createTime;
    
    // getters and setters
}

/**
 * 文件信息 DTO
 */
public class FileInfoDTO implements Serializable {
    
    private String fileId;
    private String fileName;
    private String folderId;
    private String ownerId;
    private Long size;
    private String contentType;
    private String downloadUrl;
    private Integer version;
    private Long createTime;
    
    // getters and setters
}

/**
 * 场景信息 DTO
 */
public class SceneInfoDTO implements Serializable {
    
    private String sceneId;
    private String sceneName;
    private String sceneType;
    private String description;
    private String status;
    private Integer memberCount;
    private Integer capabilityCount;
    private Long createTime;
    
    // getters and setters
}

/**
 * 能力信息 DTO
 */
public class CapabilityDTO implements Serializable {
    
    private String capabilityId;
    private String capabilityName;
    private String capabilityType;
    private String description;
    private String providerId;
    private String sceneId;
    private String status;
    private List<OperationDTO> operations;
    
    // getters and setters
}

/**
 * 操作信息 DTO
 */
public class OperationDTO implements Serializable {
    
    private String operationName;
    private String description;
    private List<ParamDTO> inputParams;
    private List<ParamDTO> outputParams;
    
    // getters and setters
}

/**
 * 参数信息 DTO
 */
public class ParamDTO implements Serializable {
    
    private String paramName;
    private String paramType;
    private String description;
    private Boolean required;
    private Object defaultValue;
    
    // getters and setters
}

/**
 * 统一响应 DTO
 */
public class ResultDTO<T> implements Serializable {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> result = new ResultDTO<T>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    public static <T> ResultDTO<T> error(Integer code, String message) {
        ResultDTO<T> result = new ResultDTO<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    // getters and setters
}
```

---

## 三、用户层接口抽象

### 3.1 用户接口包结构

```
net.ooder.northbound.scene.user/
├── dto/                        # 数据传输对象
│   ├── UserInfoDTO.java
│   ├── DepartmentDTO.java
│   ├── RoleDTO.java
│   ├── MessageDTO.java
│   ├── FileInfoDTO.java
│   ├── SceneInfoDTO.java
│   ├── CapabilityDTO.java
│   ├── OperationDTO.java
│   ├── ParamDTO.java
│   └── ResultDTO.java
│
├── api/                        # 用户接口定义
│   ├── UserApi.java            # 用户接口
│   ├── OrgApi.java             # 组织接口
│   ├── MsgApi.java             # 消息接口
│   ├── VfsApi.java             # 文件接口
│   ├── SceneApi.java           # 场景接口
│   └── CapabilityApi.java      # 能力接口
│
├── callback/                   # 回调接口
│   ├── MessageCallback.java
│   ├── FileUploadCallback.java
│   └── CapabilityCallback.java
│
└── exception/                  # 异常定义
    ├── SceneException.java
    ├── CapabilityException.java
    └── AuthException.java
```

### 3.2 用户接口定义

```java
package net.ooder.northbound.scene.user.api;

/**
 * 用户接口 - 用户层统一入口
 */
public interface UserApi {
    
    String getUserId();
    String getUserName();
    boolean isAuthenticated();
    
    OrgApi org();
    MsgApi msg();
    VfsApi vfs();
    SceneApi scene();
    CapabilityApi capability();
    
    void logout();
}

/**
 * 组织接口
 */
public interface OrgApi {
    
    UserInfoDTO getCurrentUser();
    UserInfoDTO getUser(String userId);
    List<UserInfoDTO> listUsers(String deptId);
    List<UserInfoDTO> searchUsers(String keyword);
    
    DepartmentDTO getDepartment(String deptId);
    List<DepartmentDTO> listDepartments(String parentId);
    List<DepartmentDTO> getDepartmentTree();
    
    RoleDTO getRole(String roleId);
    List<RoleDTO> listRoles();
    
    boolean hasPermission(String permission);
    List<String> getPermissions();
}

/**
 * 消息接口
 */
public interface MsgApi {
    
    String sendMessage(String toUserId, String content, String type);
    String sendGroupMessage(String groupId, String content, String type);
    List<MessageDTO> getMessages(Long startTime, Long endTime, Integer page, Integer size);
    List<MessageDTO> getUnreadMessages();
    int getUnreadCount();
    boolean markAsRead(String messageId);
    boolean markAllAsRead();
    
    String subscribe(String topicId, MessageCallback callback);
    boolean unsubscribe(String subscriptionId);
    
    boolean setOnline();
    boolean setOffline();
    boolean isOnline(String userId);
}

/**
 * 文件接口
 */
public interface VfsApi {
    
    FileInfoDTO uploadFile(String fileName, byte[] content, String folderId);
    FileInfoDTO uploadFile(String fileName, InputStream content, String folderId, FileUploadCallback callback);
    byte[] downloadFile(String fileId);
    InputStream downloadFileStream(String fileId);
    boolean deleteFile(String fileId);
    
    FileInfoDTO getFileInfo(String fileId);
    List<FileInfoDTO> listFiles(String folderId);
    
    String createFolder(String name, String parentId);
    boolean deleteFolder(String folderId);
    List<FileInfoDTO> listFolders(String parentId);
    
    String shareFile(String fileId, List<String> userIds, String permission, Long expireTime);
    boolean revokeShare(String shareId);
    
    int createVersion(String fileId);
    List<FileInfoDTO> listVersions(String fileId);
}

/**
 * 场景接口
 */
public interface SceneApi {
    
    SceneInfoDTO getCurrentScene();
    boolean switchScene(String sceneId);
    List<SceneInfoDTO> getAvailableScenes();
    
    boolean joinScene(String sceneId, String accessToken);
    boolean leaveScene(String sceneId);
    
    List<UserInfoDTO> getSceneMembers(String sceneId);
    boolean inviteMember(String sceneId, String userId);
    boolean removeMember(String sceneId, String userId);
    
    List<CapabilityDTO> getSceneCapabilities(String sceneId);
}

/**
 * 能力接口
 */
public interface CapabilityApi {
    
    CapabilityDTO getCapability(String capabilityId);
    List<CapabilityDTO> listCapabilities(String sceneId);
    List<CapabilityDTO> searchCapabilities(String keyword);
    
    <T> T invoke(String capabilityId, String operation, Map<String, Object> params);
    <T> String invokeAsync(String capabilityId, String operation, 
        Map<String, Object> params, CapabilityCallback<T> callback);
    
    boolean subscribe(String capabilityId, String eventType, CapabilityCallback<Object> callback);
    boolean unsubscribe(String subscriptionId);
    
    String shareCapability(String capabilityId, List<String> userIds, 
        String permission, Long expireTime);
}
```

---

## 四、Engine 实现层重构

### 4.1 Engine 层包结构

```
net.ooder.northbound.scene.engine/
├── Engine.java                 # 引擎基础接口
├── EngineType.java             # 引擎类型枚举
├── EngineStatus.java           # 引擎状态枚举
├── EngineManager.java          # 引擎管理器
├── EngineConfig.java           # 引擎配置
├── EngineMetrics.java          # 引擎指标
├── HealthStatus.java           # 健康状态
│
├── impl/                       # 引擎实现
│   ├── AbstractEngine.java     # 抽象引擎基类
│   ├── OrgEngineImpl.java      # 组织引擎
│   ├── MsgEngineImpl.java      # 消息引擎
│   ├── VfsEngineImpl.java      # 文件引擎
│   ├── CapabilityEngineImpl.java # 能力引擎
│   ├── SessionEngineImpl.java  # 会话引擎
│   ├── StateEngineImpl.java    # 状态引擎
│   └── ResourceEngineImpl.java # 资源引擎
│
├── adapter/                    # 适配器层
│   ├── AgentSDKAdapter.java    # AgentSDK 适配器
│   ├── DatabaseAdapter.java    # 数据库适配器
│   ├── DingTalkAdapter.java    # 钉钉适配器
│   ├── FeishuAdapter.java      # 飞书适配器
│   └── WeComAdapter.java       # 企业微信适配器
│
└── spi/                        # SPI 扩展点
    ├── EngineProvider.java     # 引擎提供者
    └── EngineFactory.java      # 引擎工厂
```

### 4.2 适配器设计

```java
package net.ooder.northbound.scene.engine.adapter;

/**
 * AgentSDK 适配器
 * 
 * <p>封装 agent-sdk 0.7.3 的调用，为 Engine 层提供统一接口</p>
 */
public class AgentSDKAdapter {
    
    private AgentClient agentClient;
    
    /**
     * 初始化 AgentSDK
     */
    public void initialize(String agentId, String agentType, String serverUrl) {
        // 初始化 agent-sdk
    }
    
    /**
     * 注册 Agent
     */
    public String registerAgent(String agentName, String agentType) {
        // 调用 agent-sdk 注册
        return null;
    }
    
    /**
     * 发送心跳
     */
    public boolean heartbeat(String agentId) {
        // 调用 agent-sdk 心跳
        return true;
    }
    
    /**
     * 上报能力
     */
    public boolean reportCapability(String agentId, List<String> capabilities) {
        // 调用 agent-sdk 能力上报
        return true;
    }
    
    /**
     * 发送消息
     */
    public String sendMessage(String from, String to, String content, String type) {
        // 调用 agent-sdk 消息发送
        return null;
    }
    
    /**
     * 订阅主题
     */
    public boolean subscribe(String topic, MessageHandler handler) {
        // 调用 agent-sdk 订阅
        return true;
    }
}

/**
 * 数据源适配器接口
 */
public interface DataSourceAdapter {
    
    void initialize(Map<String, Object> config);
    
    UserInfoDTO getUser(String userId);
    List<UserInfoDTO> listUsers(String deptId);
    DepartmentDTO getDepartment(String deptId);
    List<DepartmentDTO> listDepartments(String parentId);
    
    boolean isAvailable();
    void close();
}

/**
 * 钉钉适配器
 */
public class DingTalkAdapter implements DataSourceAdapter {
    
    private DingTalkClient client;
    
    @Override
    public void initialize(Map<String, Object> config) {
        String appKey = (String) config.get("appKey");
        String appSecret = (String) config.get("appSecret");
        // 初始化钉钉客户端
    }
    
    @Override
    public UserInfoDTO getUser(String userId) {
        // 调用钉钉 API 获取用户
        return null;
    }
    
    // 其他方法实现...
}

/**
 * 飞书适配器
 */
public class FeishuAdapter implements DataSourceAdapter {
    
    private FeishuClient client;
    
    @Override
    public void initialize(Map<String, Object> config) {
        String appId = (String) config.get("appId");
        String appSecret = (String) config.get("appSecret");
        // 初始化飞书客户端
    }
    
    // 其他方法实现...
}
```

### 4.3 Engine 与用户层连接

```java
package net.ooder.northbound.scene.engine.impl;

/**
 * 组织引擎实现 - 连接用户层和适配器层
 */
public class OrgEngineImpl extends AbstractEngine {
    
    private DataSourceAdapter dataSourceAdapter;
    private AgentSDKAdapter agentSDKAdapter;
    
    public OrgEngineImpl() {
        super(EngineType.ORG, "Organization Engine");
    }
    
    @Override
    protected void doInitialize() {
        // 初始化数据源适配器
        String dataSourceType = getConfig().getProperty("dataSourceType");
        if ("dingtalk".equals(dataSourceType)) {
            dataSourceAdapter = new DingTalkAdapter();
        } else if ("feishu".equals(dataSourceType)) {
            dataSourceAdapter = new FeishuAdapter();
        } else {
            dataSourceAdapter = new DatabaseAdapter();
        }
        dataSourceAdapter.initialize(getConfig().getProperties());
        
        // 初始化 AgentSDK 适配器
        agentSDKAdapter = new AgentSDKAdapter();
        agentSDKAdapter.initialize(
            getConfig().getProperty("agentId"),
            getConfig().getProperty("agentType"),
            getConfig().getProperty("serverUrl")
        );
    }
    
    // 用户层接口实现
    
    public UserInfoDTO getUser(String userId) {
        return dataSourceAdapter.getUser(userId);
    }
    
    public List<UserInfoDTO> listUsers(String deptId) {
        return dataSourceAdapter.listUsers(deptId);
    }
    
    public DepartmentDTO getDepartment(String deptId) {
        return dataSourceAdapter.getDepartment(deptId);
    }
    
    // ... 其他方法
}
```

---

## 五、迁移指南

### 5.1 迁移步骤

```
迁移步骤：
├── 第一阶段：准备工作
│   ├── 1.1 添加 scene-server-sdk 依赖
│   ├── 1.2 创建适配器配置
│   └── 1.3 数据源迁移评估
│
├── 第二阶段：用户层迁移
│   ├── 2.1 替换现有 DTO 为统一 DTO
│   ├── 2.2 替换 Controller 调用为 UserApi
│   └── 2.3 更新前端 API 调用
│
├── 第三阶段：Engine 层集成
│   ├── 3.1 配置 Engine 实例
│   ├── 3.2 配置数据源适配器
│   └── 3.3 配置 AgentSDK 适配器
│
├── 第四阶段：测试验证
│   ├── 4.1 单元测试
│   ├── 4.2 集成测试
│   └── 4.3 性能测试
│
└── 第五阶段：上线部署
    ├── 5.1 灰度发布
    ├── 5.2 监控告警
    └── 5.3 文档更新
```

### 5.2 Maven 依赖迁移

```xml
<!-- 原有依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>0.7.3</version>
</dependency>

<!-- 新增依赖 -->
<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-server-sdk</artifactId>
    <version>0.7.3</version>
</dependency>

<!-- 可选：数据源适配器 -->
<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-adapter-dingtalk</artifactId>
    <version>0.7.3</version>
</dependency>
```

### 5.3 代码迁移示例

#### 原有代码

```java
// 原有 Controller
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{userId}")
    public ResultModel getUser(@PathVariable String userId) {
        User user = userService.getUser(userId);
        return ResultModel.success(user);
    }
}

// 原有 Service
@Service
public class UserService {
    
    @Autowired
    private OrgClient orgClient;
    
    public User getUser(String userId) {
        // 调用 orgClient 或 agent-sdk
        return orgClient.getUser(userId);
    }
}
```

#### 迁移后代码

```java
// 迁移后 Controller
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private SceneServer sceneServer;
    
    @GetMapping("/{userId}")
    public ResultDTO<UserInfoDTO> getUser(@PathVariable String userId,
            @RequestHeader("sessionId") String sessionId) {
        SceneClient client = sceneServer.getClient(sessionId);
        UserInfoDTO user = client.org().getUser(userId);
        return ResultDTO.success(user);
    }
}

// 迁移后配置
@Configuration
public class SceneConfig {
    
    @Bean
    public SceneServer sceneServer() {
        return SceneServerFactory.builder()
            .withEngine(EngineType.ORG, createOrgEngine())
            .withEngine(EngineType.MSG, createMsgEngine())
            .withEngine(EngineType.VFS, createVfsEngine())
            .build();
    }
    
    private OrgEngineImpl createOrgEngine() {
        EngineConfig config = new EngineConfig();
        config.setProperty("dataSourceType", "dingtalk");
        config.setProperty("appKey", "${dingtalk.appKey}");
        config.setProperty("appSecret", "${dingtalk.appSecret}");
        
        OrgEngineImpl engine = new OrgEngineImpl();
        engine.install(config);
        return engine;
    }
}
```

### 5.4 API 映射表

| 原 API | 新 API | 说明 |
|--------|--------|------|
| `/api/auth/login` | `SceneServer.login()` | 用户登录 |
| `/api/auth/logout` | `SceneClient.logout()` | 用户登出 |
| `/api/org/getUser` | `UserApi.org().getUser()` | 获取用户 |
| `/api/org/listUsers` | `UserApi.org().listUsers()` | 用户列表 |
| `/api/msg/send` | `UserApi.msg().sendMessage()` | 发送消息 |
| `/api/vfs/upload` | `UserApi.vfs().uploadFile()` | 上传文件 |
| `/api/vfs/download` | `UserApi.vfs().downloadFile()` | 下载文件 |

---

## 六、兼容性保证

### 6.1 向后兼容策略

```java
/**
 * 兼容层 - 保持原有 API 兼容
 */
@RestController
@RequestMapping("/api/org")
public class OrgControllerCompat {
    
    @Autowired
    private SceneServer sceneServer;
    
    /**
     * 兼容原有 API
     */
    @GetMapping("/getUser")
    public ResultModel getUser(@RequestParam String userId,
            @RequestHeader("X-Session-Id") String sessionId) {
        try {
            SceneClient client = sceneServer.getClient(sessionId);
            UserInfoDTO user = client.org().getUser(userId);
            
            // 转换为原有响应格式
            ResultModel result = new ResultModel();
            result.setRequestStatus(200);
            result.setData(user);
            return result;
        } catch (Exception e) {
            ResultModel result = new ResultModel();
            result.setRequestStatus(500);
            result.setMessage(e.getMessage());
            return result;
        }
    }
}
```

### 6.2 渐进式迁移

```
渐进式迁移路径：
├── 阶段1：并行运行
│   ├── 原有 API 保持不变
│   ├── 新增 SceneServer API
│   └── 两套 API 共存
│
├── 阶段2：流量切换
│   ├── 新功能使用新 API
│   ├── 逐步迁移旧功能
│   └── 监控对比
│
└── 阶段3：完全迁移
    ├── 下线旧 API
    ├── 清理旧代码
    └── 统一使用 SceneServer
```

---

## 七、总结

### 7.1 重构收益

| 收益 | 说明 |
|------|------|
| 代码复用 | 通过 AbstractEngine 避免重复代码 |
| 接口统一 | 用户层接口统一，减少学习成本 |
| 灵活扩展 | 适配器模式支持多数据源 |
| 易于测试 | 接口与实现分离，便于 Mock 测试 |
| 向后兼容 | 兼容层保证平滑迁移 |

### 7.2 后续工作

1. **完善适配器**：实现钉钉、飞书、企业微信等适配器
2. **性能优化**：Engine 层缓存、连接池优化
3. **监控告警**：集成监控系统，完善告警机制
4. **文档完善**：API 文档、迁移指南、最佳实践

---

**文档版本**: 1.0.0  
**创建日期**: 2024-01-20  
**适用版本**: SceneServer SDK 0.7.3
