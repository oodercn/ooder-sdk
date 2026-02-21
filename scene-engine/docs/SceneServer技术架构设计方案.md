# SceneServer 技术架构设计方案

## 一、概述

### 1.1 设计目标

SceneServer 是企业级场景服务的核心协调层，旨在：

1. **对外**：以场景方式提供统一服务入口，支持点对点通讯、垂直业务场景（HR、CRM、财务、审批等）
2. **对内**：将各个 Skills 能力根据场景协作关系建立连接和管理

### 1.2 核心概念

| 概念 | 说明 |
|------|------|
| **Scene（场景）** | 服务组织单元，定义一组相关能力和协作关系 |
| **SceneServer** | 场景服务总协调器，管理场景生命周期和能力编排 |
| **Engine（引擎）** | 技术能力分类管理器，负责特定领域能力的安装、初始化、运行监控 |
| **Skill（技能）** | 原子化服务能力，由 Engine 管理和提供 |

### 1.3 命名变更

| 原名称 | 新名称 | 说明 |
|--------|--------|------|
| Northbound（北向服务） | Scene（场景服务） | 统一为企业级场景服务概念 |

---

## 二、整体架构

### 2.1 分层架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           用户接入层 (User Layer)                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  Web Client │  │ Mobile App  │  │  Desktop    │  │  Third-party│        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景服务层 (Scene Layer)                              │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                         SceneServer (总协调器)                          │  │
│  │  ┌─────────────────────────────────────────────────────────────────┐  │  │
│  │  │                    SceneRegistry (场景注册中心)                   │  │  │
│  │  └─────────────────────────────────────────────────────────────────┘  │  │
│  │                                                                        │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐  │  │
│  │  │ P2PScene     │ │ HRScene      │ │ CRMScene     │ │ FinanceScene │  │  │
│  │  │ (点对点通讯)  │ │ (人力资源)    │ │ (客户管理)   │ │ (财务管理)   │  │  │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘  │  │
│  │                                                                        │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐                   │  │
│  │  │ ApprovalScene│ │ CustomScene  │ │    ...       │                   │  │
│  │  │ (审批流程)    │ │ (自定义场景)  │ │              │                   │  │
│  │  └──────────────┘ └──────────────┘ └──────────────┘                   │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        引擎能力层 (Engine Layer)                             │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                      EngineManager (引擎管理器)                         │  │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐         │  │
│  │  │ OrgEngine  │ │ MsgEngine  │ │ AgentEngine│ │ VfsEngine  │         │  │
│  │  │ (组织引擎)  │ │ (消息引擎)  │ │ (代理引擎) │ │ (文件引擎) │         │  │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘         │  │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐         │  │
│  │  │WorkflowEng │ │ MonitorEng │ │ NetworkEng │ │  ...       │         │  │
│  │  │(流程引擎)   │ │(监控引擎)   │ │(网络引擎)  │ │            │         │  │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘         │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技能服务层 (Skill Layer)                              │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐              │
│  │ skill-org  │ │ skill-msg  │ │ skill-vfs  │ │ skill-agent│              │
│  │ (组织服务)  │ │ (消息服务)  │ │ (文件服务) │ │ (代理服务) │              │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘              │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐              │
│  │skill-workflow│skill-monitor│skill-network│    ...      │              │
│  │ (流程服务)   │ │ (监控服务) │ │ (网络服务) │             │              │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘              │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        基础设施层 (Infrastructure Layer)                     │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐              │
│  │   MQTT     │ │   Redis    │ │  Database  │ │   Storage  │              │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件职责

| 组件 | 层级 | 职责 |
|------|------|------|
| **SceneServer** | 场景服务层 | 总协调器，管理场景生命周期、路由请求、编排能力 |
| **SceneRegistry** | 场景服务层 | 场景注册中心，维护场景定义和元数据 |
| **EngineManager** | 引擎能力层 | 引擎管理器，协调各 Engine 的安装、启动、监控 |
| **Engine** | 引擎能力层 | 特定领域能力管理，负责 Skill 的安装、初始化、运行监控 |
| **Skill** | 技能服务层 | 原子化服务能力，提供具体业务功能 |

---

## 三、SceneServer 核心设计

### 3.1 SceneServer 接口定义

```java
/**
 * 场景服务器 - 企业级场景服务总协调器
 * 
 * <p>职责：</p>
 * <ul>
 *   <li>场景生命周期管理</li>
 *   <li>场景请求路由</li>
 *   <li>能力编排协调</li>
 *   <li>引擎集成管理</li>
 * </ul>
 */
public interface SceneServer {
    
    // ========== 生命周期管理 ==========
    
    /**
     * 启动场景服务器
     */
    void start();
    
    /**
     * 停止场景服务器
     */
    void stop();
    
    /**
     * 获取服务器状态
     */
    SceneServerStatus getStatus();
    
    // ========== 场景管理 ==========
    
    /**
     * 注册场景
     */
    String registerScene(SceneDefinition definition);
    
    /**
     * 注销场景
     */
    boolean unregisterScene(String sceneId);
    
    /**
     * 获取场景
     */
    Scene getScene(String sceneId);
    
    /**
     * 获取所有场景
     */
    List<Scene> listScenes(SceneQuery query);
    
    // ========== 用户接入 ==========
    
    /**
     * 用户登录场景
     */
    SceneClient login(String account, String password);
    
    /**
     * Token登录场景
     */
    SceneClient loginByToken(String token);
    
    /**
     * 管理员登录
     */
    SceneAdminClient adminLogin(String account, String password);
    
    // ========== 引擎管理 ==========
    
    /**
     * 获取引擎管理器
     */
    EngineManager getEngineManager();
    
    /**
     * 获取指定类型的引擎
     */
    <T extends Engine> T getEngine(EngineType type);
    
    // ========== 场景路由 ==========
    
    /**
     * 路由请求到场景
     */
    <T> T route(String sceneId, SceneRequest<T> request);
    
    /**
     * 广播请求到多个场景
     */
    Map<String, Object> broadcast(List<String> sceneIds, SceneRequest<?> request);
}
```

### 3.2 Scene 场景定义

```java
/**
 * 场景定义
 */
public class SceneDefinition {
    
    private String sceneId;
    private String sceneName;
    private String sceneType;
    private String description;
    
    private List<String> requiredEngines;
    private List<CapabilityRequirement> requiredCapabilities;
    private List<SceneRule> rules;
    private Map<String, Object> config;
    
    private SceneLifecycle lifecycle;
    private SceneSecurity security;
}

/**
 * 场景类型枚举
 * 
 * <p>基于 Scene 需求规格规范分册定义的18种场景类型</p>
 */
public enum SceneType {
    
    // ========== 通讯类场景 ==========
    P2P("点对点通讯场景", SceneCategory.COMMUNICATION),
    GROUP("群组通讯场景", SceneCategory.COMMUNICATION),
    BROADCAST("广播通讯场景", SceneCategory.COMMUNICATION),
    
    // ========== 业务类场景 ==========
    HR("人力资源场景", SceneCategory.BUSINESS),
    CRM("客户管理场景", SceneCategory.BUSINESS),
    FINANCE("财务管理场景", SceneCategory.BUSINESS),
    APPROVAL("审批流程场景", SceneCategory.BUSINESS),
    PROJECT("项目管理场景", SceneCategory.BUSINESS),
    KNOWLEDGE("知识管理场景", SceneCategory.BUSINESS),
    
    // ========== IoT类场景 ==========
    DEVICE("设备管理场景", SceneCategory.IOT),
    COLLECTION("数据采集场景", SceneCategory.IOT),
    EDGE("边缘计算场景", SceneCategory.IOT),
    
    // ========== 协作类场景 ==========
    MEETING("会议协作场景", SceneCategory.COLLABORATION),
    DOCUMENT("文档协作场景", SceneCategory.COLLABORATION),
    TASK("任务协作场景", SceneCategory.COLLABORATION),
    
    // ========== 系统类场景 ==========
    SYS("系统管理场景", SceneCategory.SYSTEM),
    MONITOR("监控运维场景", SceneCategory.SYSTEM),
    SECURITY("安全审计场景", SceneCategory.SYSTEM),
    
    // ========== 自定义场景 ==========
    CUSTOM("自定义场景", SceneCategory.CUSTOM);
    
    private String description;
    private SceneCategory category;
}

/**
 * 场景分类枚举
 */
public enum SceneCategory {
    COMMUNICATION("通讯类"),
    BUSINESS("业务类"),
    IOT("IoT类"),
    COLLABORATION("协作类"),
    SYSTEM("系统类"),
    CUSTOM("自定义");
    
    private String description;
}

/**
 * 场景接口
 */
public interface Scene {
    
    String getSceneId();
    String getSceneName();
    SceneType getType();
    SceneStatus getStatus();
    
    List<String> getEngines();
    List<CapabilityInfo> getCapabilities();
    List<SceneMember> getMembers();
    
    void start();
    void stop();
    void restart();
    
    Object invoke(String capabilityId, String operation, Map<String, Object> params);
}
```

### 3.3 场景客户端

```java
/**
 * 场景客户端 - 用户统一入口
 */
public interface SceneClient {
    
    String getClientId();
    String getUserId();
    String getSessionId();
    String getToken();
    boolean isAuthenticated();
    
    // ========== 当前场景 ==========
    
    /**
     * 获取当前场景
     */
    Scene getCurrentScene();
    
    /**
     * 切换场景
     */
    boolean switchScene(String sceneId);
    
    /**
     * 获取可用场景列表
     */
    List<Scene> getAvailableScenes();
    
    // ========== 场景操作 ==========
    
    /**
     * 加入场景
     */
    boolean joinScene(String sceneId, String accessToken);
    
    /**
     * 离开场景
     */
    boolean leaveScene(String sceneId);
    
    // ========== 能力调用 ==========
    
    /**
     * 调用场景能力
     */
    <T> T invoke(String capabilityId, String operation, Map<String, Object> params);
    
    /**
     * 异步调用
     */
    <T> String invokeAsync(String capabilityId, String operation, 
        Map<String, Object> params, SceneCallback<T> callback);
    
    // ========== 专项客户端 ==========
    
    /**
     * 获取消息客户端
     */
    MsgClient msgClient();
    
    /**
     * 获取文件客户端
     */
    VfsClient vfsClient();
    
    /**
     * 获取组织客户端
     */
    OrgClient orgClient();
    
    // ========== 生命周期 ==========
    
    void refreshSession();
    void logout();
}
```

---

## 四、Engine 引擎设计

### 4.1 Engine 接口定义

```java
/**
 * 引擎接口 - 技术能力分类管理器
 * 
 * <p>职责：</p>
 * <ul>
 *   <li>Skill 安装管理</li>
 *   <li>能力初始化</li>
 *   <li>运行监控</li>
 *   <li>状态报告</li>
 * </ul>
 */
public interface Engine {
    
    // ========== 引擎信息 ==========
    
    /**
     * 获取引擎类型
     */
    EngineType getType();
    
    /**
     * 获取引擎名称
     */
    String getName();
    
    /**
     * 获取引擎版本
     */
    String getVersion();
    
    // ========== 生命周期 ==========
    
    /**
     * 安装引擎
     */
    void install(EngineConfig config);
    
    /**
     * 初始化引擎
     */
    void initialize();
    
    /**
     * 启动引擎
     */
    void start();
    
    /**
     * 停止引擎
     */
    void stop();
    
    /**
     * 卸载引擎
     */
    void uninstall();
    
    /**
     * 获取引擎状态
     */
    EngineStatus getStatus();
    
    // ========== Skill 管理 ==========
    
    /**
     * 安装 Skill
     */
    String installSkill(SkillDefinition definition);
    
    /**
     * 卸载 Skill
     */
    boolean uninstallSkill(String skillId);
    
    /**
     * 获取 Skill
     */
    Skill getSkill(String skillId);
    
    /**
     * 获取所有 Skill
     */
    List<Skill> listSkills();
    
    // ========== 能力管理 ==========
    
    /**
     * 获取能力列表
     */
    List<CapabilityInfo> getCapabilities();
    
    /**
     * 获取能力
     */
    CapabilityInfo getCapability(String capabilityId);
    
    /**
     * 检查能力可用性
     */
    boolean isCapabilityAvailable(String capabilityId);
    
    // ========== 监控 ==========
    
    /**
     * 获取引擎指标
     */
    EngineMetrics getMetrics();
    
    /**
     * 获取健康状态
     */
    HealthStatus getHealth();
}
```

### 4.2 Engine 类型定义

```java
/**
 * 引擎类型枚举
 */
public enum EngineType {
    
    ORG("组织引擎", "skill-org"),
    MSG("消息引擎", "skill-msg"),
    VFS("文件引擎", "skill-vfs"),
    AGENT("代理引擎", "skill-agent"),
    WORKFLOW("流程引擎", "skill-workflow"),
    MONITOR("监控引擎", "skill-monitor"),
    NETWORK("网络引擎", "skill-network"),
    CAPABILITY("能力引擎", "skill-capability"),
    RESOURCE("资源引擎", "skill-resource"),
    SESSION("会话引擎", "skill-session"),
    STATE("状态引擎", "skill-state");
    
    private String description;
    private String defaultSkillType;
}
```

### 4.3 EngineManager 引擎管理器

```java
/**
 * 引擎管理器
 */
public interface EngineManager {
    
    /**
     * 注册引擎
     */
    void registerEngine(Engine engine);
    
    /**
     * 注销引擎
     */
    boolean unregisterEngine(EngineType type);
    
    /**
     * 获取引擎
     */
    <T extends Engine> T getEngine(EngineType type);
    
    /**
     * 获取所有引擎
     */
    List<Engine> listEngines();
    
    /**
     * 获取引擎状态
     */
    Map<EngineType, EngineStatus> getEngineStatuses();
    
    /**
     * 启动所有引擎
     */
    void startAll();
    
    /**
     * 停止所有引擎
     */
    void stopAll();
    
    /**
     * 健康检查
     */
    Map<EngineType, HealthStatus> healthCheck();
}
```

---

## 五、场景类型设计

### 5.1 P2P场景（点对点通讯）

```java
/**
 * P2P场景定义
 * 
 * <p>提供点对点通讯能力：</p>
 * <ul>
 *   <li>消息收发</li>
 *   <li>文件传输</li>
 *   <li>能力共享</li>
 * </ul>
 */
public class P2PSceneDefinition extends SceneDefinition {
    
    public P2PSceneDefinition() {
        setSceneType(SceneType.P2P.name());
        setRequiredEngines(Arrays.asList(
            EngineType.MSG.name(),
            EngineType.VFS.name(),
            EngineType.AGENT.name()
        ));
        setRequiredCapabilities(Arrays.asList(
            new CapabilityRequirement("message", "send", "receive"),
            new CapabilityRequirement("file", "upload", "download", "share"),
            new CapabilityRequirement("presence", "online", "offline")
        ));
    }
}
```

### 5.2 垂直业务场景

```java
/**
 * HR场景定义
 */
public class HRSceneDefinition extends SceneDefinition {
    
    public HRSceneDefinition() {
        setSceneType(SceneType.HR.name());
        setRequiredEngines(Arrays.asList(
            EngineType.ORG.name(),
            EngineType.WORKFLOW.name(),
            EngineType.VFS.name()
        ));
        setRequiredCapabilities(Arrays.asList(
            new CapabilityRequirement("employee", "create", "update", "query"),
            new CapabilityRequirement("department", "create", "update", "query"),
            new CapabilityRequirement("attendance", "checkin", "report"),
            new CapabilityRequirement("leave", "apply", "approve")
        ));
    }
}

/**
 * CRM场景定义
 */
public class CRMSceneDefinition extends SceneDefinition {
    
    public CRMSceneDefinition() {
        setSceneType(SceneType.CRM.name());
        setRequiredEngines(Arrays.asList(
            EngineType.ORG.name(),
            EngineType.MSG.name(),
            EngineType.VFS.name()
        ));
        setRequiredCapabilities(Arrays.asList(
            new CapabilityRequirement("customer", "create", "update", "query"),
            new CapabilityRequirement("contact", "create", "update", "query"),
            new CapabilityRequirement("opportunity", "create", "track"),
            new CapabilityRequirement("contract", "create", "sign")
        ));
    }
}

/**
 * 审批场景定义
 */
public class ApprovalSceneDefinition extends SceneDefinition {
    
    public ApprovalSceneDefinition() {
        setSceneType(SceneType.APPROVAL.name());
        setRequiredEngines(Arrays.asList(
            EngineType.WORKFLOW.name(),
            EngineType.ORG.name(),
            EngineType.MSG.name()
        ));
        setRequiredCapabilities(Arrays.asList(
            new CapabilityRequirement("approval", "submit", "approve", "reject"),
            new CapabilityRequirement("flow", "start", "query", "cancel"),
            new CapabilityRequirement("notify", "send", "subscribe")
        ));
    }
}
```

### 5.3 自定义场景

```java
/**
 * 自定义场景构建器
 */
public class CustomSceneBuilder {
    
    private String sceneId;
    private String sceneName;
    private List<EngineType> engines = new ArrayList<>();
    private List<CapabilityRequirement> capabilities = new ArrayList<>();
    private Map<String, Object> config = new HashMap<>();
    
    public CustomSceneBuilder sceneId(String sceneId) {
        this.sceneId = sceneId;
        return this;
    }
    
    public CustomSceneBuilder sceneName(String sceneName) {
        this.sceneName = sceneName;
        return this;
    }
    
    public CustomSceneBuilder addEngine(EngineType engine) {
        this.engines.add(engine);
        return this;
    }
    
    public CustomSceneBuilder addCapability(String name, String... operations) {
        this.capabilities.add(new CapabilityRequirement(name, operations));
        return this;
    }
    
    public CustomSceneBuilder config(String key, Object value) {
        this.config.put(key, value);
        return this;
    }
    
    public SceneDefinition build() {
        SceneDefinition definition = new SceneDefinition();
        definition.setSceneId(sceneId);
        definition.setSceneName(sceneName);
        definition.setSceneType(SceneType.CUSTOM.name());
        definition.setRequiredEngines(engines.stream()
            .map(EngineType::name).collect(Collectors.toList()));
        definition.setRequiredCapabilities(capabilities);
        definition.setConfig(config);
        return definition;
    }
}

// 使用示例
SceneDefinition customScene = new CustomSceneBuilder()
    .sceneId("project-management")
    .sceneName("项目管理场景")
    .addEngine(EngineType.ORG)
    .addEngine(EngineType.MSG)
    .addEngine(EngineType.VFS)
    .addEngine(EngineType.WORKFLOW)
    .addCapability("project", "create", "update", "query", "delete")
    .addCapability("task", "create", "assign", "complete")
    .addCapability("document", "upload", "download", "share")
    .config("notification.enabled", true)
    .build();
```

---

## 六、Engine 实现设计

### 6.1 OrgEngine 组织引擎

```java
/**
 * 组织引擎实现
 */
public class OrgEngineImpl implements Engine {
    
    private EngineStatus status = EngineStatus.CREATED;
    private Map<String, Skill> skills = new ConcurrentHashMap<>();
    private OrgConfig config;
    
    @Override
    public EngineType getType() {
        return EngineType.ORG;
    }
    
    @Override
    public void install(EngineConfig config) {
        this.config = (OrgConfig) config;
        status = EngineStatus.INSTALLED;
    }
    
    @Override
    public void initialize() {
        status = EngineStatus.INITIALIZING;
        
        // 初始化组织服务 Skill
        Skill orgSkill = installSkill(new SkillDefinition(
            "skill-org", "组织服务", SkillType.ORG
        ));
        
        // 初始化用户服务
        // 初始化部门服务
        // 初始化角色服务
        
        status = EngineStatus.INITIALIZED;
    }
    
    @Override
    public void start() {
        status = EngineStatus.STARTING;
        
        for (Skill skill : skills.values()) {
            skill.start();
        }
        
        status = EngineStatus.RUNNING;
    }
    
    @Override
    public List<CapabilityInfo> getCapabilities() {
        List<CapabilityInfo> capabilities = new ArrayList<>();
        capabilities.add(new CapabilityInfo("user", "用户管理"));
        capabilities.add(new CapabilityInfo("department", "部门管理"));
        capabilities.add(new CapabilityInfo("role", "角色管理"));
        capabilities.add(new CapabilityInfo("permission", "权限管理"));
        return capabilities;
    }
}
```

### 6.2 MsgEngine 消息引擎

```java
/**
 * 消息引擎实现
 */
public class MsgEngineImpl implements Engine {
    
    private EngineStatus status = EngineStatus.CREATED;
    private Map<String, Skill> skills = new ConcurrentHashMap<>();
    private MqttClusterService mqttCluster;
    
    @Override
    public EngineType getType() {
        return EngineType.MSG;
    }
    
    @Override
    public void initialize() {
        status = EngineStatus.INITIALIZING;
        
        // 初始化 MQTT 集群
        mqttCluster = new MqttClusterService();
        mqttCluster.initialize();
        
        // 安装消息服务 Skill
        installSkill(new SkillDefinition("skill-msg", "消息服务", SkillType.MSG));
        
        status = EngineStatus.INITIALIZED;
    }
    
    @Override
    public List<CapabilityInfo> getCapabilities() {
        List<CapabilityInfo> capabilities = new ArrayList<>();
        capabilities.add(new CapabilityInfo("message", "消息收发"));
        capabilities.add(new CapabilityInfo("topic", "主题订阅"));
        capabilities.add(new CapabilityInfo("presence", "在线状态"));
        capabilities.add(new CapabilityInfo("push", "消息推送"));
        return capabilities;
    }
}
```

### 6.3 VfsEngine 文件引擎

```java
/**
 * 文件引擎实现
 */
public class VfsEngineImpl implements Engine {
    
    @Override
    public EngineType getType() {
        return EngineType.VFS;
    }
    
    @Override
    public List<CapabilityInfo> getCapabilities() {
        List<CapabilityInfo> capabilities = new ArrayList<>();
        capabilities.add(new CapabilityInfo("file", "文件管理"));
        capabilities.add(new CapabilityInfo("folder", "文件夹管理"));
        capabilities.add(new CapabilityInfo("share", "文件共享"));
        capabilities.add(new CapabilityInfo("version", "版本管理"));
        capabilities.add(new CapabilityInfo("trash", "回收站"));
        return capabilities;
    }
}
```

### 6.4 WorkflowEngine 流程引擎

```java
/**
 * 流程引擎实现
 */
public class WorkflowEngineImpl implements Engine {
    
    @Override
    public EngineType getType() {
        return EngineType.WORKFLOW;
    }
    
    @Override
    public List<CapabilityInfo> getCapabilities() {
        List<CapabilityInfo> capabilities = new ArrayList<>();
        capabilities.add(new CapabilityInfo("flow", "流程定义"));
        capabilities.add(new CapabilityInfo("instance", "流程实例"));
        capabilities.add(new CapabilityInfo("task", "任务管理"));
        capabilities.add(new CapabilityInfo("approval", "审批管理"));
        return capabilities;
    }
}
```

---

## 七、SceneServer 实现设计

### 7.1 SceneServerImpl 核心实现

```java
/**
 * 场景服务器实现
 */
public class SceneServerImpl implements SceneServer {
    
    private String serverId;
    private SceneServerStatus status;
    
    private final EngineManager engineManager;
    private final SceneRegistry sceneRegistry;
    private final SessionManager sessionManager;
    
    private final Map<String, Scene> scenes = new ConcurrentHashMap<>();
    private final Map<String, SceneClient> clients = new ConcurrentHashMap<>();
    
    public SceneServerImpl() {
        this.engineManager = new EngineManagerImpl();
        this.sceneRegistry = new SceneRegistryImpl();
        this.sessionManager = new SessionManagerImpl();
        this.status = SceneServerStatus.CREATED;
    }
    
    @Override
    public void start() {
        status = SceneServerStatus.STARTING;
        
        // 1. 启动引擎管理器
        engineManager.startAll();
        
        // 2. 加载预定义场景
        loadPredefinedScenes();
        
        // 3. 启动会话管理器
        sessionManager.start();
        
        status = SceneServerStatus.RUNNING;
    }
    
    @Override
    public String registerScene(SceneDefinition definition) {
        String sceneId = generateSceneId(definition);
        
        // 1. 验证场景定义
        validateSceneDefinition(definition);
        
        // 2. 检查引擎依赖
        checkEngineDependencies(definition);
        
        // 3. 创建场景实例
        Scene scene = createScene(definition);
        
        // 4. 注册场景
        scenes.put(sceneId, scene);
        sceneRegistry.register(sceneId, definition);
        
        // 5. 启动场景
        scene.start();
        
        return sceneId;
    }
    
    @Override
    public SceneClient login(String account, String password) {
        // 1. 认证
        AuthResult authResult = authenticate(account, password);
        if (!authResult.isSuccess()) {
            throw new AuthenticationException(authResult.getMessage());
        }
        
        // 2. 创建会话
        String sessionId = sessionManager.createSession(authResult.getUserId());
        
        // 3. 创建客户端
        SceneClient client = new SceneClientImpl(
            authResult.getUserId(),
            sessionId,
            authResult.getToken(),
            this
        );
        
        clients.put(sessionId, client);
        return client;
    }
    
    @Override
    public <T> T route(String sceneId, SceneRequest<T> request) {
        Scene scene = scenes.get(sceneId);
        if (scene == null) {
            throw new SceneNotFoundException(sceneId);
        }
        
        // 路由到场景处理
        return scene.invoke(
            request.getCapabilityId(),
            request.getOperation(),
            request.getParams()
        );
    }
    
    private void loadPredefinedScenes() {
        // 加载 P2P 场景
        registerScene(new P2PSceneDefinition());
        
        // 加载其他预定义场景
        // ...
    }
    
    private void checkEngineDependencies(SceneDefinition definition) {
        for (String engineName : definition.getRequiredEngines()) {
            EngineType engineType = EngineType.valueOf(engineName);
            Engine engine = engineManager.getEngine(engineType);
            if (engine == null || engine.getStatus() != EngineStatus.RUNNING) {
                throw new EngineNotAvailableException(engineType);
            }
        }
    }
}
```

### 7.2 SceneRegistry 场景注册中心

```java
/**
 * 场景注册中心实现
 */
public class SceneRegistryImpl implements SceneRegistry {
    
    private final Map<String, SceneDefinition> definitions = new ConcurrentHashMap<>();
    private final Map<String, SceneMetadata> metadata = new ConcurrentHashMap<>();
    
    @Override
    public void register(String sceneId, SceneDefinition definition) {
        definitions.put(sceneId, definition);
        
        SceneMetadata meta = new SceneMetadata();
        meta.setSceneId(sceneId);
        meta.setCreateTime(System.currentTimeMillis());
        meta.setStatus(SceneStatus.REGISTERED);
        metadata.put(sceneId, meta);
    }
    
    @Override
    public boolean unregister(String sceneId) {
        definitions.remove(sceneId);
        metadata.remove(sceneId);
        return true;
    }
    
    @Override
    public SceneDefinition getDefinition(String sceneId) {
        return definitions.get(sceneId);
    }
    
    @Override
    public List<SceneDefinition> listByType(SceneType type) {
        return definitions.values().stream()
            .filter(d -> type.name().equals(d.getSceneType()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SceneDefinition> listByEngine(EngineType engineType) {
        return definitions.values().stream()
            .filter(d -> d.getRequiredEngines().contains(engineType.name()))
            .collect(Collectors.toList());
    }
}
```

---

## 八、包结构设计

```
net.ooder.scene/
├── SceneServer.java                 # 场景服务器接口
├── Scene.java                       # 场景接口
├── SceneClient.java                 # 场景客户端接口
├── SceneAdminClient.java            # 场景管理客户端接口
│
├── definition/                      # 场景定义
│   ├── SceneDefinition.java
│   ├── SceneType.java
│   ├── P2PSceneDefinition.java
│   ├── HRSceneDefinition.java
│   ├── CRMSceneDefinition.java
│   ├── FinanceSceneDefinition.java
│   ├── ApprovalSceneDefinition.java
│   └── CustomSceneBuilder.java
│
├── engine/                          # 引擎层
│   ├── Engine.java                  # 引擎接口
│   ├── EngineType.java              # 引擎类型枚举
│   ├── EngineManager.java           # 引擎管理器接口
│   ├── EngineStatus.java            # 引擎状态枚举
│   │
│   └── impl/                        # 引擎实现
│       ├── EngineManagerImpl.java
│       ├── OrgEngineImpl.java       # 组织引擎
│       ├── MsgEngineImpl.java       # 消息引擎
│       ├── VfsEngineImpl.java       # 文件引擎
│       ├── AgentEngineImpl.java     # 代理引擎
│       ├── WorkflowEngineImpl.java  # 流程引擎
│       ├── MonitorEngineImpl.java   # 监控引擎
│       └── NetworkEngineImpl.java   # 网络引擎
│
├── impl/                            # 场景服务实现
│   ├── SceneServerImpl.java
│   ├── SceneImpl.java
│   ├── SceneClientImpl.java
│   └── SceneAdminClientImpl.java
│
├── registry/                        # 场景注册中心
│   ├── SceneRegistry.java
│   └── SceneRegistryImpl.java
│
├── skill/                           # 技能服务
│   ├── Skill.java
│   ├── SkillDefinition.java
│   ├── SkillType.java
│   └── impl/
│       ├── OrgSkillImpl.java
│       ├── MsgSkillImpl.java
│       ├── VfsSkillImpl.java
│       └── AgentSkillImpl.java
│
├── capability/                      # 能力管理
│   ├── CapabilityInfo.java
│   ├── CapabilityRequirement.java
│   └── CapabilityRegistry.java
│
├── session/                         # 会话管理
│   ├── SessionManager.java
│   └── SessionManagerImpl.java
│
└── security/                        # 安全管理
    ├── SceneSecurity.java
    └── SceneSecurityImpl.java
```

---

## 九、使用示例

### 9.1 启动场景服务器

```java
public class SceneServerExample {
    
    public static void main(String[] args) {
        // 1. 创建场景服务器
        SceneServer server = new SceneServerImpl();
        
        // 2. 启动服务器
        server.start();
        
        // 3. 注册自定义场景
        SceneDefinition projectScene = new CustomSceneBuilder()
            .sceneId("project-mgmt")
            .sceneName("项目管理场景")
            .addEngine(EngineType.ORG)
            .addEngine(EngineType.MSG)
            .addEngine(EngineType.VFS)
            .addEngine(EngineType.WORKFLOW)
            .addCapability("project", "create", "update", "query")
            .addCapability("task", "create", "assign", "complete")
            .build();
        
        server.registerScene(projectScene);
        
        // 4. 用户登录
        SceneClient client = server.login("user@example.com", "password");
        
        // 5. 切换到项目场景
        client.switchScene("project-mgmt");
        
        // 6. 调用场景能力
        Map<String, Object> project = client.invoke(
            "project", "create",
            Map.of("name", "新项目", "owner", "user1")
        );
    }
}
```

### 9.2 场景客户端使用

```java
// 用户登录
SceneClient client = sceneServer.login("user@example.com", "password");

// 获取可用场景
List<Scene> scenes = client.getAvailableScenes();

// 加入场景
client.joinScene("hr-scene", "access-token");

// 获取消息客户端发送消息
client.msgClient().send("recipient-id", "Hello!");

// 获取文件客户端上传文件
client.vfsClient().upload("/path/to/file", "document.pdf");

// 调用场景能力
Object result = client.invoke("approval", "submit", params);

// 登出
client.logout();
```

### 9.3 管理员操作

```java
// 管理员登录
SceneAdminClient admin = sceneServer.adminLogin("admin@example.com", "password");

// 获取引擎状态
Map<EngineType, EngineStatus> engineStatuses = 
    sceneServer.getEngineManager().getEngineStatuses();

// 健康检查
Map<EngineType, HealthStatus> health = 
    sceneServer.getEngineManager().healthCheck();

// 注册新场景
admin.registerScene(new CustomSceneBuilder()
    .sceneId("finance-scene")
    .sceneName("财务场景")
    .addEngine(EngineType.ORG)
    .addEngine(EngineType.WORKFLOW)
    .build());

// 启停场景
admin.stopScene("hr-scene");
admin.startScene("hr-scene");
```

---

## 十、迁移计划

### 10.1 命名迁移

| 原包名 | 新包名 | 说明 |
|--------|--------|------|
| net.ooder.northbound | net.ooder.scene | 包名统一 |
| ServiceEngine | SceneServer | 核心服务重命名 |
| UserClient | SceneClient | 客户端重命名 |
| AdminClient | SceneAdminClient | 管理客户端重命名 |

### 10.2 架构迁移

| 原组件 | 新组件 | 变更说明 |
|--------|--------|----------|
| ServiceEngineImpl | SceneServerImpl | 重构为场景服务总协调器 |
| CapabilityEngine | 归入 Engine 层 | 作为 CapabilityEngineImpl |
| ResourceEngine | 归入 Engine 层 | 作为 ResourceEngineImpl |
| SessionEngine | 归入 Engine 层 | 作为 SessionEngineImpl |
| StateEngine | 归入 Engine 层 | 作为 StateEngineImpl |
| McpAgentService | 归入 AgentEngine | 作为 AgentEngine 的一部分 |
| SceneGroupManager | 归入 SceneServer | 场景管理功能整合 |

### 10.3 迁移步骤

1. **第一阶段**：创建新包结构和接口定义
2. **第二阶段**：实现 Engine 层各引擎
3. **第三阶段**：实现 SceneServer 核心逻辑
4. **第四阶段**：迁移现有功能到新架构
5. **第五阶段**：兼容性测试和文档更新

---

## 十一、总结

SceneServer 技术架构通过以下设计实现企业级场景服务：

1. **场景驱动**：以场景为核心组织服务，支持预定义和自定义场景
2. **引擎分层**：技术能力按领域分类管理，便于扩展和维护
3. **统一入口**：SceneClient 提供统一的用户接入和场景操作接口
4. **能力编排**：场景定义能力需求，引擎提供能力实现
5. **灵活扩展**：支持新增场景类型和引擎类型

此架构将北向服务统一为企业级场景服务，使用 Scene 替代 Northbound 概念，更符合业务语义和用户认知。

---

## 十二、SceneServer SDK 集成规范

### 12.1 SDK 定位

SceneServer SDK 是所有北向工程节点的支撑 SDK，以 JAR 包形式提供集成。

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SceneServer SDK 定位                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    应用层 (Application Layer)                        │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │   │
│  │  │  HR App     │  │  CRM App    │  │  IoT App    │  ...             │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    SceneServer SDK v0.7.3                            │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │  scene-server-sdk-0.7.3.jar                                  │    │   │
│  │  │  ├── SceneServer (场景服务总协调器)                           │    │   │
│  │  │  ├── EngineManager (引擎管理器)                              │    │   │
│  │  │  ├── SceneRegistry (场景注册中心)                            │    │   │
│  │  │  ├── SceneClient (场景客户端)                                │    │   │
│  │  │  └── Engines (各领域引擎)                                    │    │   │
│  │  └─────────────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    基础设施层 (Infrastructure)                       │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐    │   │
│  │  │   MQTT     │  │   Redis    │  │  Database  │  │   Storage  │    │   │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 12.2 Maven 依赖配置

```xml
<!-- SceneServer SDK 核心依赖 -->
<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-server-sdk</artifactId>
    <version>0.7.3</version>
</dependency>

<!-- 可选：特定引擎依赖 -->
<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-engine-org</artifactId>
    <version>0.7.3</version>
</dependency>

<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-engine-msg</artifactId>
    <version>0.7.3</version>
</dependency>

<dependency>
    <groupId>net.ooder.scene</groupId>
    <artifactId>scene-engine-vfs</artifactId>
    <version>0.7.3</version>
</dependency>
```

### 12.3 SDK 模块结构

```
scene-server-sdk (0.7.3)
├── scene-server-core              # 核心模块（必需）
│   ├── SceneServer.java
│   ├── Scene.java
│   ├── SceneClient.java
│   ├── SceneAdminClient.java
│   ├── Engine.java
│   ├── EngineManager.java
│   ├── EngineType.java
│   ├── SceneRegistry.java
│   └── definition/
│       ├── SceneDefinition.java
│       ├── SceneType.java
│       └── CustomSceneBuilder.java
│
├── scene-engine-org               # 组织引擎（可选）
│   └── OrgEngineImpl.java
│
├── scene-engine-msg               # 消息引擎（可选）
│   └── MsgEngineImpl.java
│
├── scene-engine-vfs               # 文件引擎（可选）
│   └── VfsEngineImpl.java
│
├── scene-engine-agent             # 代理引擎（可选）
│   └── AgentEngineImpl.java
│
├── scene-engine-workflow          # 流程引擎（可选）
│   └── WorkflowEngineImpl.java
│
├── scene-engine-monitor           # 监控引擎（可选）
│   └── MonitorEngineImpl.java
│
└── scene-engine-network           # 网络引擎（可选）
    └── NetworkEngineImpl.java
```

### 12.4 快速集成示例

#### 12.4.1 最小集成（零配置）

```java
import net.ooder.scene.SceneServer;
import net.ooder.scene.SceneClient;
import net.ooder.scene.SceneServerFactory;

public class QuickStart {
    
    public static void main(String[] args) {
        // 1. 获取 SceneServer 实例（零配置自动初始化）
        SceneServer server = SceneServerFactory.getDefault();
        
        // 2. 用户登录
        SceneClient client = server.login("user@example.com", "password");
        
        // 3. 调用能力
        Object result = client.invoke("message", "send", Map.of(
            "to", "recipient@example.com",
            "content", "Hello!"
        ));
        
        // 4. 登出
        client.logout();
    }
}
```

#### 12.4.2 自定义配置集成

```java
import net.ooder.scene.*;
import net.ooder.scene.engine.*;
import net.ooder.scene.definition.*;

public class CustomIntegration {
    
    public static void main(String[] args) {
        // 1. 创建引擎配置
        EngineConfig orgConfig = new OrgEngineConfig()
            .datasource("mysql")
            .url("jdbc:mysql://localhost:3306/org_db");
        
        EngineConfig msgConfig = new MsgEngineConfig()
            .brokerType("emqx")
            .brokerUrl("tcp://localhost:1883");
        
        // 2. 创建 SceneServer
        SceneServer server = SceneServerFactory.create()
            .withEngine(EngineType.ORG, new OrgEngineImpl(orgConfig))
            .withEngine(EngineType.MSG, new MsgEngineImpl(msgConfig))
            .build();
        
        // 3. 启动服务
        server.start();
        
        // 4. 注册自定义场景
        SceneDefinition hrScene = new CustomSceneBuilder()
            .sceneId("hr-scene")
            .sceneName("人力资源场景")
            .addEngine(EngineType.ORG)
            .addEngine(EngineType.WORKFLOW)
            .addCapability("employee", "create", "update", "query")
            .build();
        
        server.registerScene(hrScene);
    }
}
```

#### 12.4.3 Spring Boot 集成

```yaml
# application.yml
scene:
  server:
    version: 0.7.3
    autoStart: true
    engines:
      org:
        enabled: true
        datasource: mysql
      msg:
        enabled: true
        brokerType: lightweight
        port: 1883
      vfs:
        enabled: true
        storageType: local
        basePath: ./data/vfs
```

```java
// Spring Boot 配置类
@Configuration
@EnableSceneServer
public class SceneServerConfig {
    
    @Bean
    public SceneServer sceneServer() {
        return SceneServerFactory.getDefault();
    }
}

// Controller 使用
@RestController
@RequestMapping("/api/scene")
public class SceneController {
    
    @Autowired
    private SceneServer sceneServer;
    
    @PostMapping("/login")
    public SceneClient login(@RequestBody LoginRequest request) {
        return sceneServer.login(request.getAccount(), request.getPassword());
    }
    
    @PostMapping("/invoke")
    public Object invoke(@RequestHeader("sessionId") String sessionId,
                         @RequestBody InvokeRequest request) {
        SceneClient client = sceneServer.getClient(sessionId);
        return client.invoke(request.getCapability(), 
                            request.getOperation(), 
                            request.getParams());
    }
}
```

### 12.5 重构计划

#### 第一阶段：核心重构（0.7.3）

基于 northbound-core 重构：

| 原组件 | 新组件 | 说明 |
|--------|--------|------|
| ServiceEngine | SceneServer | 核心服务重命名 |
| UserClient | SceneClient | 客户端重命名 |
| AdminClient | SceneAdminClient | 管理客户端重命名 |
| CapabilityEngine | CapabilityEngineImpl | 归入 Engine 层 |
| ResourceEngine | ResourceEngineImpl | 归入 Engine 层 |
| SessionEngine | SessionEngineImpl | 归入 Engine 层 |
| StateEngine | StateEngineImpl | 归入 Engine 层 |
| McpAgentService | AgentEngineImpl | 归入 AgentEngine |

#### 第二阶段：场景类型补充（0.7.4）

补充规范定义的场景类型：

```
补充场景类型：
├── GroupSceneDefinition（群组通讯）
├── BroadcastSceneDefinition（广播通讯）
├── ProjectSceneDefinition（项目管理）
├── KnowledgeSceneDefinition（知识管理）
├── DeviceSceneDefinition（设备管理）
├── CollectionSceneDefinition（数据采集）
├── EdgeSceneDefinition（边缘计算）
├── MeetingSceneDefinition（会议协作）
├── DocumentSceneDefinition（文档协作）
├── TaskSceneDefinition（任务协作）
├── SysSceneDefinition（系统管理）
├── MonitorSceneDefinition（监控运维）
└── SecuritySceneDefinition（安全审计）
```

#### 第三阶段：接口完善（0.8.0）

补充缺失接口：

```
补充接口：
├── Group（组接口）
├── Role（角色接口）
├── Link（链路接口）
├── SceneGroupManager（场景组管理器）
└── ZeroConfigSceneCreator（零配置创建器）
```

### 12.6 版本兼容性

| 版本 | 说明 | 兼容性 |
|------|------|--------|
| 0.7.3 | 初始版本，基于 northbound-core 重构 | - |
| 0.7.4 | 补充场景类型定义 | 兼容 0.7.3 |
| 0.8.0 | 引擎接口升级 | 不兼容 0.7.x |
| 1.0.0 | 正式版本 | 兼容 0.8.x |

---

## 十三、附录

### 13.1 场景类型速查表

| 场景类型 | 代码 | 分类 | 核心能力 | 默认引擎 |
|----------|------|------|----------|----------|
| 点对点通讯 | P2P | COMMUNICATION | message, file | Msg, Vfs, Agent |
| 群组通讯 | GROUP | COMMUNICATION | broadcast, group | Msg, Org |
| 广播通讯 | BROADCAST | COMMUNICATION | broadcast, subscribe | Msg, Agent |
| 人力资源 | HR | BUSINESS | employee, attendance | Org, Workflow, Vfs |
| 客户管理 | CRM | BUSINESS | customer, contact | Org, Msg, Vfs |
| 财务管理 | FINANCE | BUSINESS | account, expense | Org, Workflow, Vfs |
| 审批流程 | APPROVAL | BUSINESS | flow, approval | Workflow, Org, Msg |
| 项目管理 | PROJECT | BUSINESS | project, task | Org, Vfs, Workflow |
| 知识管理 | KNOWLEDGE | BUSINESS | knowledge, search | Vfs, Org, Msg |
| 设备管理 | DEVICE | IOT | device, command | Agent, Msg, Monitor |
| 数据采集 | COLLECTION | IOT | collect, store | Msg, Vfs, Monitor |
| 边缘计算 | EDGE | IOT | edge, compute | Agent, Msg, Network |
| 会议协作 | MEETING | COLLABORATION | meeting, share | Msg, Vfs, Agent |
| 文档协作 | DOCUMENT | COLLABORATION | document, edit | Vfs, Org, Msg |
| 任务协作 | TASK | COLLABORATION | task, assign | Org, Workflow, Msg |
| 系统管理 | SYS | SYSTEM | user, permission | Org, Session, State |
| 监控运维 | MONITOR | SYSTEM | monitor, alert | Monitor, Msg, Agent |
| 安全审计 | SECURITY | SYSTEM | audit, log | Monitor, State, Org |
| 自定义 | CUSTOM | CUSTOM | custom | 根据需求配置 |

### 13.2 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2024-01-20 | 初始版本 |
| 1.1.0 | 2024-01-20 | 补充18种场景类型定义 |
| 1.2.0 | 2024-01-20 | 补充 SDK 集成规范 |

---

**文档结束**
