# Scene 需求规格规范分册

> SDK 版本: 0.7.3  
> 规范版本: 1.0.0  
> 适用范围: 企业级场景服务

---

## 目录

1. [概述](#一概述)
2. [场景术语字典](#二场景术语字典)
3. [场景分类枚举](#三场景分类枚举)
4. [场景全生命周期管理](#四场景全生命周期管理)
5. [场景组管理规范](#五场景组管理规范)
6. [场景协作规范](#六场景协作规范)
7. [Agent 管理规范](#七agent-管理规范)
8. [Capability 能力规范](#八capability-能力规范)
9. [Skills 技能规范](#九skills-技能规范)
10. [用户故事与实用场景](#十用户故事与实用场景)
11. [零配置规范](#十一零配置规范)
12. [附录](#十二附录)

---

## 一、概述

### 1.1 文档目的

本规范分册定义 SceneServer 场景服务的完整需求规格，包括：

- 场景全生命周期管理规范
- 场景组管理和协作规范
- Agent、Capability、Skills 规范
- 用户故事和实用场景归纳
- 零配置自组网规范

### 1.2 适用范围

| 范围 | 说明 |
|------|------|
| SDK版本 | 0.7.3 及以上 |
| Java版本 | 1.8 (支持 JAVA8) |
| 框架 | Spring Boot 2.7.0 |
| 协议版本 | skill.ooder.net/v1, scene.ooder.net/v1 |

### 1.3 规范层级

```
┌─────────────────────────────────────────────────────────────┐
│                    Scene 规范体系                            │
├─────────────────────────────────────────────────────────────┤
│  第一层：场景分类与术语（顶层抽象）                           │
│  第二层：场景生命周期管理（核心流程）                         │
│  第三层：场景组管理与协作（组织结构）                         │
│  第四层：Agent/Capability/Skills（能力支撑）                 │
│  第五层：用户故事与实用场景（业务落地）                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、场景术语字典

### 2.1 核心术语

| 术语 | 英文 | 定义 | 示例 |
|------|------|------|------|
| **场景** | Scene | 服务组织单元，定义一组相关能力和协作关系 | HR场景、CRM场景、P2P场景 |
| **场景服务** | SceneServer | 场景服务总协调器，管理场景生命周期和能力编排 | SceneServerImpl |
| **场景客户端** | SceneClient | 用户接入场景的统一入口，提供场景操作接口 | SceneClientImpl |
| **场景定义** | SceneDefinition | 场景的元数据描述，包含能力需求、引擎依赖等 | SceneDefinition.yaml |
| **场景注册中心** | SceneRegistry | 维护场景定义和元数据的注册中心 | SceneRegistryImpl |
| **场景实例** | SceneInstance | 运行时的场景实例，包含状态和成员信息 | SceneImpl |

### 2.2 组织术语

| 术语 | 英文 | 定义 | 示例 |
|------|------|------|------|
| **组** | Group | 场景内的服务节点集合，按功能或角色分组 | default组、admin组 |
| **成员** | Member | 场景或组的参与者，可以是用户或Agent | 用户成员、Agent成员 |
| **角色** | Role | 场景内的角色定义，关联一组能力 | mqtt-broker、mqtt-p2p-agent |
| **链路** | Link | 场景内成员之间的连接关系 | Agent链路、服务链路 |

### 2.3 能力术语

| 术语 | 英文 | 定义 | 示例 |
|------|------|------|------|
| **引擎** | Engine | 技术能力分类管理器，负责特定领域能力管理 | OrgEngine、MsgEngine |
| **技能** | Skill | 原子化服务能力，由Engine管理和提供 | skill-org、skill-msg |
| **能力** | Capability | Skill提供的具体功能点 | mqtt-publish、mqtt-subscribe |
| **操作** | Operation | 能力的具体操作方法 | send、receive、subscribe |

### 2.4 实体术语

| 术语 | 英文 | 定义 | 示例 |
|------|------|------|------|
| **代理** | Agent | 场景内的智能代理，可以执行任务和协同工作 | MCP Agent、IoT Agent |
| **用户** | User | 场景的人类参与者 | 普通用户、管理员 |
| **组织** | Organization | 用户的组织归属 | 企业、部门 |
| **会话** | Session | 用户与场景的连接状态 | 用户会话、Agent会话 |

### 2.5 状态术语

| 术语 | 英文 | 定义 | 取值范围 |
|------|------|------|----------|
| **场景状态** | SceneStatus | 场景实例的运行状态 | CREATED, STARTING, RUNNING, STOPPING, STOPPED, ERROR |
| **引擎状态** | EngineStatus | 引擎的运行状态 | CREATED, INSTALLED, INITIALIZING, INITIALIZED, STARTING, RUNNING, STOPPING, STOPPED, ERROR |
| **能力状态** | CapabilityStatus | 能力的可用状态 | AVAILABLE, UNAVAILABLE, DEGRADED, MAINTENANCE |
| **Agent状态** | AgentState | Agent的生命周期状态 | INITIALIZING, SCANNING, REGISTERING, ACTIVATING, CONNECTING, LOGGING_IN, REPORTING, ACTIVE, INACTIVE, ERROR |

### 2.6 操作术语

| 术语 | 英文 | 定义 | 使用场景 |
|------|------|------|----------|
| **注册** | Register | 将实体加入系统 | 场景注册、能力注册、Agent注册 |
| **注销** | Unregister | 将实体从系统移除 | 场景注销、能力注销、Agent注销 |
| **激活** | Activate | 使实体进入工作状态 | Agent激活、场景激活 |
| **停用** | Deactivate | 使实体进入非工作状态 | Agent停用、场景停用 |
| **授权** | Authorize | 授予访问权限 | 能力授权、场景授权 |
| **撤销** | Revoke | 收回访问权限 | 能力撤销、场景撤销 |
| **订阅** | Subscribe | 注册事件监听 | 消息订阅、能力订阅 |
| **取消订阅** | Unsubscribe | 取消事件监听 | 消息取消订阅、能力取消订阅 |

---

## 三、场景分类枚举

### 3.1 场景顶级分类

```java
/**
 * 场景类型枚举 - 顶级分类
 */
public enum SceneType {
    
    // ========== 通讯类场景 ==========
    
    /**
     * 点对点通讯场景
     * 提供消息、文件、能力共享等点对点通讯能力
     */
    P2P("点对点通讯", "p2p", SceneCategory.COMMUNICATION),
    
    /**
     * 群组通讯场景
     * 提供群组消息、群组文件、群组协作能力
     */
    GROUP("群组通讯", "group", SceneCategory.COMMUNICATION),
    
    /**
     * 广播通讯场景
     * 提供一对多消息广播能力
     */
    BROADCAST("广播通讯", "broadcast", SceneCategory.COMMUNICATION),
    
    // ========== 业务类场景 ==========
    
    /**
     * 人力资源场景
     * 提供员工管理、考勤、薪酬、招聘等HR能力
     */
    HR("人力资源", "hr", SceneCategory.BUSINESS),
    
    /**
     * 客户管理场景
     * 提供客户、联系人、商机、合同等CRM能力
     */
    CRM("客户管理", "crm", SceneCategory.BUSINESS),
    
    /**
     * 财务管理场景
     * 提供账务、报销、预算、结算等财务能力
     */
    FINANCE("财务管理", "finance", SceneCategory.BUSINESS),
    
    /**
     * 审批流程场景
     * 提供流程定义、审批、流转等工作流能力
     */
    APPROVAL("审批流程", "approval", SceneCategory.BUSINESS),
    
    /**
     * 项目管理场景
     * 提供项目、任务、文档、进度等项目管理能力
     */
    PROJECT("项目管理", "project", SceneCategory.BUSINESS),
    
    /**
     * 知识管理场景
     * 提供知识库、文档、搜索等知识管理能力
     */
    KNOWLEDGE("知识管理", "knowledge", SceneCategory.BUSINESS),
    
    // ========== IoT类场景 ==========
    
    /**
     * 设备管理场景
     * 提供设备注册、监控、控制等IoT能力
     */
    DEVICE("设备管理", "device", SceneCategory.IOT),
    
    /**
     * 数据采集场景
     * 提供数据采集、存储、分析等IoT能力
     */
    COLLECTION("数据采集", "collection", SceneCategory.IOT),
    
    /**
     * 边缘计算场景
     * 提供边缘节点、边缘应用等边缘计算能力
     */
    EDGE("边缘计算", "edge", SceneCategory.IOT),
    
    // ========== 协作类场景 ==========
    
    /**
     * 会议协作场景
     * 提供会议、白板、共享等协作能力
     */
    MEETING("会议协作", "meeting", SceneCategory.COLLABORATION),
    
    /**
     * 文档协作场景
     * 提供文档编辑、评论、版本等协作能力
     */
    DOCUMENT("文档协作", "document", SceneCategory.COLLABORATION),
    
    /**
     * 任务协作场景
     * 提供任务分配、跟踪、报告等协作能力
     */
    TASK("任务协作", "task", SceneCategory.COLLABORATION),
    
    // ========== 系统类场景 ==========
    
    /**
     * 系统管理场景
     * 提供用户、权限、配置等系统管理能力
     */
    SYS("系统管理", "sys", SceneCategory.SYSTEM),
    
    /**
     * 监控运维场景
     * 提供监控、告警、日志等运维能力
     */
    MONITOR("监控运维", "monitor", SceneCategory.SYSTEM),
    
    /**
     * 安全审计场景
     * 提供安全、审计、合规等安全能力
     */
    SECURITY("安全审计", "security", SceneCategory.SYSTEM),
    
    // ========== 自定义场景 ==========
    
    /**
     * 自定义场景
     * 用户自定义的业务场景
     */
    CUSTOM("自定义", "custom", SceneCategory.CUSTOM);
    
    private String displayName;
    private String code;
    private SceneCategory category;
}

/**
 * 场景分类
 */
public enum SceneCategory {
    COMMUNICATION("通讯类"),
    BUSINESS("业务类"),
    IOT("物联网类"),
    COLLABORATION("协作类"),
    SYSTEM("系统类"),
    CUSTOM("自定义类");
    
    private String displayName;
}
```

### 3.2 场景分类矩阵

| 分类 | 场景类型 | 核心能力 | 依赖驱动 |
|------|----------|----------|----------|
| **通讯类** | P2P | 消息、文件、共享 | driver-msg, driver-vfs, driver-mqtt |
| **通讯类** | GROUP | 群组消息、群组文件 | driver-msg, driver-vfs, driver-org |
| **通讯类** | BROADCAST | 广播消息、订阅推送 | driver-msg, driver-mqtt |
| **业务类** | HR | 员工、考勤、薪酬 | driver-org, driver-vfs |
| **业务类** | CRM | 客户、商机、合同 | driver-org, driver-msg, driver-vfs |
| **业务类** | FINANCE | 账务、报销、预算 | driver-org, driver-vfs |
| **业务类** | APPROVAL | 流程、审批、流转 | driver-org, driver-msg |
| **业务类** | PROJECT | 项目、任务、进度 | driver-org, driver-vfs, driver-msg |
| **业务类** | KNOWLEDGE | 知识库、文档、搜索 | driver-vfs, driver-org, driver-msg |
| **IoT类** | DEVICE | 设备注册、监控、控制 | driver-mqtt, driver-msg |
| **IoT类** | COLLECTION | 数据采集、存储、分析 | driver-msg, driver-vfs |
| **IoT类** | EDGE | 边缘节点、边缘应用 | driver-mqtt, driver-msg |
| **协作类** | MEETING | 会议、白板、共享 | driver-msg, driver-vfs |
| **协作类** | DOCUMENT | 文档编辑、评论、版本 | driver-vfs, driver-org, driver-msg |
| **协作类** | TASK | 任务分配、跟踪、报告 | driver-org, driver-msg |
| **系统类** | SYS | 用户、权限、配置 | driver-org |
| **系统类** | MONITOR | 监控、告警、日志 | driver-msg, driver-mqtt |
| **系统类** | SECURITY | 安全、审计、合规 | driver-org, driver-msg |

### 3.3 场景能力映射

```yaml
# 场景能力映射表
sceneCapabilityMapping:
  
  # P2P场景能力
  P2P:
    required:
      - message.send
      - message.receive
      - file.upload
      - file.download
    optional:
      - presence.online
      - presence.offline
      - file.share
      - capability.share
  
  # HR场景能力
  HR:
    required:
      - employee.create
      - employee.update
      - employee.query
      - department.query
    optional:
      - attendance.checkin
      - leave.apply
      - salary.query
  
  # 审批场景能力
  APPROVAL:
    required:
      - flow.start
      - flow.query
      - approval.submit
      - approval.approve
    optional:
      - approval.reject
      - approval.delegate
      - notify.send
```

---

## 四、场景全生命周期管理

### 4.1 生命周期状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Scene 生命周期状态机                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌──────────┐                                                             │
│    │ CREATED  │ ◄─── registerScene()                                        │
│    └────┬─────┘                                                             │
│         │ start()                                                           │
│         ▼                                                                   │
│    ┌──────────┐                                                             │
│    │ STARTING │ ────(检查引擎依赖)───► [失败] ──► ┌──────────┐               │
│    └────┬─────┘                                │  ERROR   │               │
│         │ 成功                                 └──────────┘               │
│         ▼                                                                   │
│    ┌──────────┐                                                             │
│    │ RUNNING  │ ◄───────────────────────┐                                   │
│    └────┬─────┘                         │                                   │
│         │ stop()                        │ restart()                         │
│         ▼                               │                                   │
│    ┌──────────┐                         │                                   │
│    │ STOPPING │ ────(清理资源)───►       │                                   │
│    └────┬─────┘                         │                                   │
│         │ 成功                          │                                   │
│         ▼                               │                                   │
│    ┌──────────┐ ──── start() ──────────┘                                   │
│    │ STOPPED  │                                                             │
│    └────┬─────┘                                                             │
│         │ unregisterScene()                                                 │
│         ▼                                                                   │
│    ┌──────────┐                                                             │
│    │ REMOVED  │                                                             │
│    └──────────┘                                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 生命周期阶段详解

#### 4.2.1 创建阶段 (CREATED)

```java
/**
 * 场景创建阶段
 * 
 * 触发条件: registerScene(SceneDefinition definition)
 * 
 * 执行步骤:
 * 1. 验证场景定义合法性
 * 2. 生成场景ID
 * 3. 创建场景实例
 * 4. 注册到场景注册中心
 * 
 * 输出: 场景ID (sceneId)
 */
public interface SceneCreationPhase {
    
    /**
     * 验证场景定义
     */
    ValidationResult validateDefinition(SceneDefinition definition);
    
    /**
     * 生成场景ID
     */
    String generateSceneId(SceneDefinition definition);
    
    /**
     * 创建场景实例
     */
    Scene createSceneInstance(String sceneId, SceneDefinition definition);
    
    /**
     * 注册场景
     */
    void registerToRegistry(String sceneId, Scene scene);
}
```

#### 4.2.2 启动阶段 (STARTING → RUNNING)

```java
/**
 * 场景启动阶段
 * 
 * 触发条件: scene.start() 或 sceneServer.start()
 * 
 * 执行步骤:
 * 1. 检查引擎依赖
 * 2. 初始化场景配置
 * 3. 创建默认组
 * 4. 加载场景Skills
 * 5. 启动场景服务
 * 
 * 失败处理: 进入ERROR状态，记录错误信息
 */
public interface SceneStartPhase {
    
    /**
     * 检查引擎依赖
     * @throws EngineNotAvailableException 引擎不可用异常
     */
    void checkEngineDependencies(SceneDefinition definition);
    
    /**
     * 初始化场景配置
     */
    void initializeConfig(Scene scene, Map<String, Object> config);
    
    /**
     * 创建默认组
     */
    String createDefaultGroup(Scene scene);
    
    /**
     * 加载场景Skills
     */
    void loadSkills(Scene scene, List<String> skillIds);
    
    /**
     * 启动场景服务
     */
    void startServices(Scene scene);
}
```

#### 4.2.3 运行阶段 (RUNNING)

```java
/**
 * 场景运行阶段
 * 
 * 状态: RUNNING
 * 
 * 可执行操作:
 * 1. 用户加入/离开场景
 * 2. 能力调用
 * 3. 成员管理
 * 4. 组管理
 * 5. 配置更新
 * 
 * 监控指标:
 * - 成员数量
 * - 能力调用次数
 * - 消息吞吐量
 * - 资源使用率
 */
public interface SceneRunningPhase {
    
    /**
     * 用户加入场景
     */
    boolean joinScene(String sceneId, String userId, String accessToken);
    
    /**
     * 用户离开场景
     */
    boolean leaveScene(String sceneId, String userId);
    
    /**
     * 调用能力
     */
    <T> T invokeCapability(String sceneId, String capabilityId, 
        String operation, Map<String, Object> params);
    
    /**
     * 创建组
     */
    String createGroup(String sceneId, String groupId, Map<String, Object> config);
    
    /**
     * 获取场景指标
     */
    SceneMetrics getMetrics(String sceneId);
}
```

#### 4.2.4 停止阶段 (STOPPING → STOPPED)

```java
/**
 * 场景停止阶段
 * 
 * 触发条件: scene.stop() 或 sceneServer.stop()
 * 
 * 执行步骤:
 * 1. 停止接受新请求
 * 2. 等待进行中的请求完成
 * 3. 通知所有成员
 * 4. 停止场景Skills
 * 5. 清理资源
 * 
 * 超时处理: 强制停止，记录警告日志
 */
public interface SceneStopPhase {
    
    /**
     * 停止接受新请求
     */
    void stopAcceptingRequests(Scene scene);
    
    /**
     * 等待请求完成
     * @param timeout 超时时间(毫秒)
     */
    void waitForPendingRequests(Scene scene, long timeout);
    
    /**
     * 通知所有成员
     */
    void notifyMembers(Scene scene, String message);
    
    /**
     * 停止场景Skills
     */
    void stopSkills(Scene scene);
    
    /**
     * 清理资源
     */
    void cleanupResources(Scene scene);
}
```

#### 4.2.5 注销阶段 (REMOVED)

```java
/**
 * 场景注销阶段
 * 
 * 触发条件: unregisterScene(String sceneId)
 * 
 * 前置条件: 场景状态为 STOPPED
 * 
 * 执行步骤:
 * 1. 验证场景已停止
 * 2. 从注册中心移除
 * 3. 删除场景配置
 * 4. 释放场景ID
 * 
 * 不可逆操作: 注销后场景ID可被重新分配
 */
public interface SceneRemovalPhase {
    
    /**
     * 验证场景已停止
     */
    boolean isSceneStopped(String sceneId);
    
    /**
     * 从注册中心移除
     */
    void removeFromRegistry(String sceneId);
    
    /**
     * 删除场景配置
     */
    void deleteConfig(String sceneId);
    
    /**
     * 释放场景ID
     */
    void releaseSceneId(String sceneId);
}
```

### 4.3 生命周期事件

```java
/**
 * 场景生命周期事件
 */
public class SceneLifecycleEvent {
    
    private String eventId;
    private String sceneId;
    private SceneStatus fromStatus;
    private SceneStatus toStatus;
    private long timestamp;
    private String operator;
    private Map<String, Object> details;
}

/**
 * 场景生命周期监听器
 */
public interface SceneLifecycleListener {
    
    /**
     * 场景创建事件
     */
    void onSceneCreated(SceneLifecycleEvent event);
    
    /**
     * 场景启动事件
     */
    void onSceneStarted(SceneLifecycleEvent event);
    
    /**
     * 场景停止事件
     */
    void onSceneStopped(SceneLifecycleEvent event);
    
    /**
     * 场景注销事件
     */
    void onSceneRemoved(SceneLifecycleEvent event);
    
    /**
     * 场景错误事件
     */
    void onSceneError(SceneLifecycleEvent event);
}
```

### 4.4 生命周期配置

```yaml
# 场景生命周期配置
sceneLifecycle:
  
  # 创建配置
  creation:
    idPrefix: "scene-"           # 场景ID前缀
    idLength: 12                  # 场景ID长度
    validateOnCreate: true        # 创建时验证
    
  # 启动配置
  startup:
    checkDependencies: true       # 检查依赖
    createDefaultGroup: true      # 创建默认组
    defaultGroupId: "default"     # 默认组ID
    loadSkills: true              # 加载Skills
    timeout: 30000                # 启动超时(毫秒)
    
  # 运行配置
  running:
    maxMembers: 10000             # 最大成员数
    maxGroups: 100                # 最大组数
    heartbeatInterval: 30000      # 心跳间隔(毫秒)
    sessionTimeout: 1800000       # 会话超时(毫秒)
    
  # 停止配置
  shutdown:
    gracefulShutdown: true        # 优雅停止
    waitForPending: true          # 等待进行中请求
    pendingTimeout: 60000         # 等待超时(毫秒)
    notifyMembers: true           # 通知成员
    
  # 注销配置
  removal:
    requireStopped: true          # 需要先停止
    cleanupConfig: true           # 清理配置
    archiveData: false            # 归档数据
```

---

## 五、场景组管理规范

### 5.1 组的概念模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Scene (场景)                                    │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                         GroupRegistry (组注册表)                       │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Group     │  │   Group     │  │   Group     │  │   Group     │        │
│  │  (default)  │  │   (admin)   │  │  (devops)   │  │  (custom)   │        │
│  │             │  │             │  │             │  │             │        │
│  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │        │
│  │ │ Skill   │ │  │ │ Skill   │ │  │ │ Skill   │ │  │ │ Skill   │ │        │
│  │ │ Org     │ │  │ │ Agent   │ │  │ │ Monitor │ │  │ │ Custom  │ │        │
│  │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │        │
│  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │  │             │        │
│  │ │ Skill   │ │  │ │ Skill   │ │  │ │ Skill   │ │  │             │        │
│  │ │ VFS     │ │  │ │ Msg     │ │  │ │ Network │ │  │             │        │
│  │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │  │             │        │
│  │ ┌─────────┐ │  │             │  │             │  │             │        │
│  │ │ Skill   │ │  │             │  │             │  │             │        │
│  │ │ Msg     │ │  │             │  │             │  │             │        │
│  │ └─────────┘ │  │             │  │             │  │             │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 组管理接口

```java
/**
 * 场景组管理器接口
 * 
 * SDK 0.7.3 规范
 */
public interface SceneGroupManager {
    
    // ========== 场景管理 ==========
    
    /**
     * 创建场景
     * @param sceneId 场景ID
     * @param config 场景配置
     * @return 场景ID
     */
    String createScene(String sceneId, Map<String, Object> config);
    
    /**
     * 移除场景
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean removeScene(String sceneId);
    
    /**
     * 检查场景是否存在
     * @param sceneId 场景ID
     * @return 是否存在
     */
    boolean sceneExists(String sceneId);
    
    /**
     * 获取场景配置
     * @param sceneId 场景ID
     * @return 场景配置
     */
    Map<String, Object> getSceneConfig(String sceneId);
    
    // ========== 组管理 ==========
    
    /**
     * 创建组
     * @param sceneId 场景ID
     * @param groupId 组ID
     * @param config 组配置
     * @return 组ID
     */
    String createGroup(String sceneId, String groupId, Map<String, Object> config);
    
    /**
     * 移除组
     * @param sceneId 场景ID
     * @param groupId 组ID
     * @return 是否成功
     */
    boolean removeGroup(String sceneId, String groupId);
    
    /**
     * 检查组是否存在
     * @param sceneId 场景ID
     * @param groupId 组ID
     * @return 是否存在
     */
    boolean groupExists(String sceneId, String groupId);
    
    /**
     * 获取场景下所有组
     * @param sceneId 场景ID
     * @return 组ID列表
     */
    List<String> getGroups(String sceneId);
    
    /**
     * 获取组配置
     * @param sceneId 场景ID
     * @param groupId 组ID
     * @return 组配置
     */
    Map<String, Object> getGroupConfig(String sceneId, String groupId);
    
    // ========== Skill管理 ==========
    
    /**
     * 注册Skill到组
     * @param sceneId 场景ID
     * @param groupId 组ID
     * @param skill Skill服务
     * @return Skill ID
     */
    String registerSkill(String sceneId, String groupId, SkillService skill);
    
    /**
     * 注销Skill
     * @param skillId Skill ID
     * @return 是否成功
     */
    boolean unregisterSkill(String skillId);
    
    /**
     * 获取Skill
     * @param skillId Skill ID
     * @return Skill服务
     */
    SkillService getSkill(String skillId);
    
    /**
     * 获取组内所有Skills
     * @param sceneId 场景ID
     * @param groupId 组ID (null表示场景下所有组)
     * @return Skill列表
     */
    List<SkillService> getSkills(String sceneId, String groupId);
    
    // ========== 能力发现 ==========
    
    /**
     * 发现场景能力
     * @param sceneId 场景ID
     * @return 能力列表
     */
    List<Map<String, Object>> discoverCapabilities(String sceneId);
    
    /**
     * 发现组能力
     * @param sceneId 场景ID
     * @param groupId 组ID
     * @return 能力列表
     */
    List<Map<String, Object>> discoverGroupCapabilities(String sceneId, String groupId);
}
```

### 5.3 组类型定义

```java
/**
 * 组类型枚举
 */
public enum GroupType {
    
    /**
     * 默认组
     * 场景创建时自动创建，包含基础服务
     */
    DEFAULT("default", "默认组"),
    
    /**
     * 管理组
     * 包含管理类服务，如用户管理、权限管理
     */
    ADMIN("admin", "管理组"),
    
    /**
     * 服务组
     * 包含业务服务，如消息服务、文件服务
     */
    SERVICE("service", "服务组"),
    
    /**
     * 监控组
     * 包含监控类服务，如日志、指标、告警
     */
    MONITOR("monitor", "监控组"),
    
    /**
     * 协作组
     * 包含协作类服务，如会议、文档、任务
     */
    COLLABORATION("collaboration", "协作组"),
    
    /**
     * 自定义组
     * 用户自定义的服务组
     */
    CUSTOM("custom", "自定义组");
    
    private String code;
    private String displayName;
}
```

### 5.4 组配置规范

```yaml
# 组配置规范
apiVersion: scene.ooder.net/v1
kind: Group

metadata:
  id: "default"                    # 组ID
  sceneId: "SYS"                   # 所属场景ID
  name: "默认服务组"                # 组名称
  description: "系统默认服务组"      # 组描述
  
spec:
  type: default                    # 组类型
  
  # Skill配置
  skills:
    - skillId: skill-org
      required: true
      config:
        datasource: database
        
    - skillId: skill-vfs
      required: true
      config:
        storageType: local
        
    - skillId: skill-msg
      required: true
      config:
        brokerType: lightweight
        
    - skillId: skill-agent
      required: false
      config:
        heartbeatInterval: 30000
  
  # 成员配置
  members:
    maxCount: 1000                 # 最大成员数
    joinPolicy: auto               # 加入策略: auto, approval, invite
    
  # 能力配置
  capabilities:
    expose: true                   # 是否暴露能力
    requireAuth: true              # 是否需要认证
    
  # 资源配置
  resources:
    cpu: "500m"
    memory: "512Mi"
    storage: "1Gi"
```

### 5.5 组生命周期

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Group 生命周期                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  createGroup()                                                              │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────┐                                                                │
│  │ CREATED │                                                                │
│  └────┬────┘                                                                │
│       │ registerSkill()                                                     │
│       ▼                                                                     │
│  ┌─────────┐     ┌─────────┐                                                │
│  │ READY   │ ──► │ RUNNING │ ◄─── skill.start()                             │
│  └─────────┘     └────┬────┘                                                │
│                       │ skill.stop()                                        │
│                       ▼                                                     │
│                  ┌─────────┐                                                │
│                  │ STOPPED │                                                │
│                  └────┬────┘                                                │
│                       │ removeGroup()                                       │
│                       ▼                                                     │
│                  ┌─────────┐                                                │
│                  │ REMOVED │                                                │
│                  └─────────┘                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、场景协作规范

### 6.1 协作模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景协作模型                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         协作上下文 (Context)                         │   │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐           │   │
│  │  │  Scene    │ │  Group    │ │  Role     │ │  Session  │           │   │
│  │  │  Context  │ │  Context  │ │  Context  │ │  Context  │           │   │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         协作通道 (Channel)                           │   │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐           │   │
│  │  │   P2P     │ │  Group    │ │ Broadcast │ │  Command  │           │   │
│  │  │  Channel  │ │  Channel  │ │  Channel  │ │  Channel  │           │   │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         协作动作 (Action)                            │   │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐           │   │
│  │  │  Invoke   │ │  Publish  │ │ Subscribe │ │  Notify   │           │   │
│  │  │  Action   │ │  Action   │ │  Action   │ │  Action   │           │   │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 协作通道类型

```java
/**
 * 协作通道类型
 */
public enum ChannelType {
    
    /**
     * 点对点通道
     * 用于两个成员之间的直接通信
     * Topic模式: ooder/p2p/{userId}/inbox
     */
    P2P("p2p", "点对点通道"),
    
    /**
     * 组通道
     * 用于组内成员之间的通信
     * Topic模式: ooder/group/{groupId}/broadcast
     */
    GROUP("group", "组通道"),
    
    /**
     * 广播通道
     * 用于场景级别的广播消息
     * Topic模式: ooder/scene/{sceneId}/broadcast
     */
    BROADCAST("broadcast", "广播通道"),
    
    /**
     * 命令通道
     * 用于设备或Agent的命令控制
     * Topic模式: ooder/command/{deviceType}/{deviceId}/request
     */
    COMMAND("command", "命令通道"),
    
    /**
     * 事件通道
     * 用于事件通知和订阅
     * Topic模式: ooder/event/{sceneId}/{eventType}
     */
    EVENT("event", "事件通道"),
    
    /**
     * 数据通道
     * 用于数据同步和共享
     * Topic模式: ooder/data/{sceneId}/{dataType}
     */
    DATA("data", "数据通道");
    
    private String code;
    private String displayName;
}
```

### 6.3 协作动作规范

```java
/**
 * 协作动作接口
 */
public interface CollaborationAction {
    
    /**
     * 获取动作类型
     */
    ActionType getType();
    
    /**
     * 获取动作ID
     */
    String getActionId();
    
    /**
     * 获取发起者
     */
    String getInitiator();
    
    /**
     * 获取目标
     */
    String getTarget();
    
    /**
     * 获取动作数据
     */
    Map<String, Object> getData();
    
    /**
     * 获取动作时间戳
     */
    long getTimestamp();
}

/**
 * 动作类型枚举
 */
public enum ActionType {
    
    /**
     * 调用动作
     * 调用场景能力
     */
    INVOKE("invoke", "调用"),
    
    /**
     * 发布动作
     * 发布消息到通道
     */
    PUBLISH("publish", "发布"),
    
    /**
     * 订阅动作
     * 订阅通道消息
     */
    SUBSCRIBE("subscribe", "订阅"),
    
    /**
     * 取消订阅动作
     */
    UNSUBSCRIBE("unsubscribe", "取消订阅"),
    
    /**
     * 通知动作
     * 发送通知消息
     */
    NOTIFY("notify", "通知"),
    
    /**
     * 邀请动作
     * 邀请成员加入场景或组
     */
    INVITE("invite", "邀请"),
    
    /**
     * 加入动作
     * 加入场景或组
     */
    JOIN("join", "加入"),
    
    /**
     * 离开动作
     * 离开场景或组
     */
    LEAVE("leave", "离开"),
    
    /**
     * 共享动作
     * 共享资源或能力
     */
    SHARE("share", "共享");
    
    private String code;
    private String displayName;
}
```

### 6.4 协作流程示例

#### 6.4.1 点对点消息协作

```java
/**
 * 点对点消息协作流程
 */
public class P2PMessageCollaboration {
    
    /**
     * 发送点对点消息
     * 
     * 流程:
     * 1. 验证发送者身份
     * 2. 验证接收者存在
     * 3. 构建消息
     * 4. 发布到P2P通道
     * 5. 确认消息送达
     */
    public void sendP2PMessage(SceneClient sender, String recipientId, String content) {
        // 1. 验证发送者身份
        if (!sender.isAuthenticated()) {
            throw new AuthenticationException("Sender not authenticated");
        }
        
        // 2. 验证接收者存在
        if (!isUserOnline(recipientId)) {
            // 存储离线消息
            storeOfflineMessage(recipientId, content);
            return;
        }
        
        // 3. 构建消息
        Message message = Message.builder()
            .messageId(UUID.randomUUID().toString())
            .senderId(sender.getUserId())
            .recipientId(recipientId)
            .content(content)
            .timestamp(System.currentTimeMillis())
            .build();
        
        // 4. 发布到P2P通道
        String topic = String.format("ooder/p2p/%s/inbox", recipientId);
        msgClient.publish(topic, message);
        
        // 5. 确认消息送达
        notifyMessageDelivered(message.getMessageId());
    }
}
```

#### 6.4.2 群组协作流程

```java
/**
 * 群组协作流程
 */
public class GroupCollaboration {
    
    /**
     * 创建协作组
     */
    public String createCollaborationGroup(String sceneId, String groupName, 
            List<String> memberIds) {
        
        // 1. 创建组
        String groupId = sceneGroupManager.createGroup(sceneId, 
            "group-" + UUID.randomUUID().toString().substring(0, 8), 
            Map.of("name", groupName));
        
        // 2. 添加成员
        for (String memberId : memberIds) {
            addGroupMember(sceneId, groupId, memberId);
        }
        
        // 3. 创建组通道
        createGroupChannel(sceneId, groupId);
        
        // 4. 通知成员
        notifyGroupCreated(sceneId, groupId, memberIds);
        
        return groupId;
    }
    
    /**
     * 群组广播消息
     */
    public void broadcastToGroup(String sceneId, String groupId, String message) {
        String topic = String.format("ooder/group/%s/broadcast", groupId);
        msgClient.publish(topic, message);
    }
}
```

### 6.5 协作事件规范

```java
/**
 * 协作事件类型
 */
public enum CollaborationEventType {
    
    // 成员事件
    MEMBER_JOINED("member.joined", "成员加入"),
    MEMBER_LEFT("member.left", "成员离开"),
    MEMBER_KICKED("member.kicked", "成员被踢出"),
    
    // 消息事件
    MESSAGE_SENT("message.sent", "消息发送"),
    MESSAGE_DELIVERED("message.delivered", "消息送达"),
    MESSAGE_READ("message.read", "消息已读"),
    
    // 能力事件
    CAPABILITY_INVOKED("capability.invoked", "能力调用"),
    CAPABILITY_COMPLETED("capability.completed", "能力完成"),
    CAPABILITY_FAILED("capability.failed", "能力失败"),
    
    // 组事件
    GROUP_CREATED("group.created", "组创建"),
    GROUP_DISBANDED("group.disbanded", "组解散"),
    GROUP_UPDATED("group.updated", "组更新"),
    
    // 共享事件
    RESOURCE_SHARED("resource.shared", "资源共享"),
    RESOURCE_REVOKED("resource.revoked", "资源撤销共享"),
    RESOURCE_ACCESSED("resource.accessed", "资源访问");
    
    private String code;
    private String displayName;
}

/**
 * 协作事件
 */
public class CollaborationEvent {
    
    private String eventId;
    private CollaborationEventType eventType;
    private String sceneId;
    private String groupId;
    private String initiatorId;
    private String targetId;
    private long timestamp;
    private Map<String, Object> payload;
}
```

---

## 七、Agent 管理规范

### 7.1 Agent 概念模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Agent 概念模型                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Agent (代理)                                 │   │
│  │                                                                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │   │
│  │  │ Identity    │  │ Capability  │  │ Connection  │                  │   │
│  │  │ (身份信息)   │  │ (能力集)     │  │ (连接状态)   │                  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                  │   │
│  │                                                                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │   │
│  │  │ Component   │  │ Link        │  │ Scene       │                  │   │
│  │  │ (组件)       │  │ (链路)       │  │ (场景归属)   │                  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                  │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                       Agent 类型                                     │   │
│  │                                                                      │   │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐           │   │
│  │  │   MCP     │ │   IoT     │ │  Service  │ │  Gateway  │           │   │
│  │  │  Agent    │ │  Agent    │ │  Agent    │ │  Agent    │           │   │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘           │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 Agent 类型定义

```java
/**
 * Agent 类型枚举
 */
public enum AgentType {
    
    /**
     * MCP Agent
     * 北向服务主代理，负责场景协调和服务发现
     */
    MCP("mcp", "MCP代理"),
    
    /**
     * IoT Agent
     * 物联网设备代理，负责设备接入和控制
     */
    IOT("iot", "IoT代理"),
    
    /**
     * Service Agent
     * 服务代理，负责特定服务的运行和管理
     */
    SERVICE("service", "服务代理"),
    
    /**
     * Gateway Agent
     * 网关代理，负责协议转换和路由
     */
    GATEWAY("gateway", "网关代理"),
    
    /**
     * Edge Agent
     * 边缘代理，负责边缘计算和本地处理
     */
    EDGE("edge", "边缘代理"),
    
    /**
     * Bot Agent
     * 机器人代理，负责自动化任务和智能交互
     */
    BOT("bot", "机器人代理");
    
    private String code;
    private String displayName;
}
```

### 7.3 Agent 状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Agent 状态机                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐                                                           │
│  │ INITIALIZING │ ◄─── Agent启动                                            │
│  └──────┬───────┘                                                           │
│         │ 扫描雷达                                                           │
│         ▼                                                                   │
│  ┌──────────────┐                                                           │
│  │   SCANNING   │ ────(发现服务)───► [失败] ──► ┌──────────┐                │
│  └──────┬───────┘                             │  ERROR   │                │
│         │ 发现目标                            └──────────┘                │
│         ▼                                                                   │
│  ┌──────────────┐                                                           │
│  │ REGISTERING  │ ────(注册到集群)───► [失败]                                │
│  └──────┬───────┘                                                           │
│         │ 注册成功                                                           │
│         ▼                                                                   │
│  ┌──────────────┐                                                           │
│  │  ACTIVATING  │ ────(激活服务)───► [失败]                                  │
│  └──────┬───────┘                                                           │
│         │ 激活成功                                                           │
│         ▼                                                                   │
│  ┌──────────────┐                                                           │
│  │  CONNECTING  │ ────(建立连接)───► [失败]                                  │
│  └──────┬───────┘                                                           │
│         │ 连接成功                                                           │
│         ▼                                                                   │
│  ┌──────────────┐                                                           │
│  │  LOGGING_IN  │ ────(登录认证)───► [失败]                                  │
│  └──────┬───────┘                                                           │
│         │ 登录成功                                                           │
│         ▼                                                                   │
│  ┌──────────────┐                                                           │
│  │  REPORTING   │ ────(上报组成)───► [失败]                                  │
│  └──────┬───────┘                                                           │
│         │ 上报成功                                                           │
│         ▼                                                                   │
│  ┌──────────────┐     deactivate()     ┌──────────────┐                    │
│  │    ACTIVE    │ ◄──────────────────► │   INACTIVE   │                    │
│  └──────────────┘     activate()       └──────────────┘                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.4 Agent 管理接口

```java
/**
 * Agent 管理接口 (SDK 0.7.3)
 */
public interface AgentAdmin {
    
    // ========== Agent 生命周期 ==========
    
    /**
     * 创建Agent
     * @param config Agent配置
     * @return Agent ID
     */
    String createAgent(AgentConfig config);
    
    /**
     * 更新Agent配置
     * @param agentId Agent ID
     * @param config 新配置
     * @return 是否成功
     */
    boolean updateAgent(String agentId, AgentConfig config);
    
    /**
     * 删除Agent
     * @param agentId Agent ID
     * @return 是否成功
     */
    boolean deleteAgent(String agentId);
    
    /**
     * 获取Agent详情
     * @param agentId Agent ID
     * @return Agent详情
     */
    AgentDetail getAgent(String agentId);
    
    /**
     * 查询Agent列表
     * @param query 查询条件
     * @return Agent列表
     */
    List<AgentDetail> listAgents(AgentQuery query);
    
    // ========== Agent 状态管理 ==========
    
    /**
     * 启用Agent
     * @param agentId Agent ID
     * @return 是否成功
     */
    boolean enableAgent(String agentId);
    
    /**
     * 禁用Agent
     * @param agentId Agent ID
     * @return 是否成功
     */
    boolean disableAgent(String agentId);
    
    /**
     * 强制下线
     * @param agentId Agent ID
     * @return 是否成功
     */
    boolean forceOffline(String agentId);
    
    /**
     * 重启Agent
     * @param agentId Agent ID
     * @return 是否成功
     */
    boolean restartAgent(String agentId);
    
    // ========== Agent 权限管理 ==========
    
    /**
     * 分配场景权限
     * @param agentId Agent ID
     * @param sceneId 场景ID
     * @param permission 权限
     * @return 是否成功
     */
    boolean assignScenePermission(String agentId, String sceneId, String permission);
    
    /**
     * 移除场景权限
     * @param agentId Agent ID
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean removeScenePermission(String agentId, String sceneId);
    
    /**
     * 分配能力权限
     * @param agentId Agent ID
     * @param capabilityId 能力ID
     * @param permission 权限
     * @return 是否成功
     */
    boolean assignCapabilityPermission(String agentId, String capabilityId, String permission);
    
    /**
     * 移除能力权限
     * @param agentId Agent ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    boolean removeCapabilityPermission(String agentId, String capabilityId);
    
    /**
     * 获取Agent权限列表
     * @param agentId Agent ID
     * @return 权限列表
     */
    List<PermissionInfo> getAgentPermissions(String agentId);
    
    // ========== Agent 监控 ==========
    
    /**
     * 获取Agent统计信息
     * @return 统计信息
     */
    AgentStatistics getAgentStatistics();
    
    /**
     * 获取Agent日志
     * @param agentId Agent ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<LogEntry> getAgentLogs(String agentId, long startTime, long endTime);
    
    /**
     * 获取Agent事件
     * @param agentId Agent ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件列表
     */
    List<EventEntry> getAgentEvents(String agentId, long startTime, long endTime);
    
    /**
     * 获取Agent指标
     * @param agentId Agent ID
     * @return 指标数据
     */
    Map<String, Object> getAgentMetrics(String agentId);
}
```

### 7.5 Agent 配置规范

```yaml
# Agent 配置规范
apiVersion: agent.ooder.net/v1
kind: Agent

metadata:
  id: "mcp-agent-main"            # Agent ID
  name: "MCP Main Agent"          # Agent 名称
  version: "0.7.3"                # Agent 版本
  type: mcp                       # Agent 类型
  
spec:
  # 身份配置
  identity:
    ownerId: "system"             # 所属者ID
    orgId: "default"              # 组织ID
    roles:                        # 角色列表
      - coordinator
      - manager
    
  # 连接配置
  connection:
    endpoint: "tcp://localhost:1883"
    protocol: mqtt
    timeout: 30000
    reconnectInterval: 5000
    maxReconnectAttempts: 10
    
  # 能力配置
  capabilities:
    - id: scene-management
      operations: [create, remove, start, stop]
    - id: agent-management
      operations: [register, unregister, monitor]
    - id: capability-discovery
      operations: [discover, invoke]
      
  # 场景配置
  scenes:
    - sceneId: SYS
      role: coordinator
      groups:
        - default
        - admin
        
  # 心跳配置
  heartbeat:
    interval: 30000               # 心跳间隔(毫秒)
    timeout: 60000                # 心跳超时(毫秒)
    maxMissed: 3                  # 最大丢失心跳数
    
  # 组件配置
  components:
    - componentId: org-service
      componentType: skill
      version: "0.7.3"
    - componentId: msg-service
      componentType: skill
      version: "0.7.3"
    - componentId: vfs-service
      componentType: skill
      version: "0.7.3"
      
  # 链路配置
  links:
    - linkId: org-msg-link
      fromComponent: org-service
      toComponent: msg-service
      linkType: notification
```

### 7.6 Agent Gateway 规范

```java
/**
 * Agent Gateway - 个人 Nexus 网关
 * 
 * SDK 0.7.3 规范
 * 
 * 实现从雷达扫描到完成注册、激活、获取场景组入口的完整流程
 */
public class AgentGateway {
    
    private String agentId;
    private AgentState state;
    private RadarService radarService;
    private McpAgentService mcpService;
    
    /**
     * 启动网关
     * 
     * 流程:
     * 1. INITIALIZING -> 初始化
     * 2. SCANNING -> 扫描雷达发现服务
     * 3. REGISTERING -> 注册到集群
     * 4. ACTIVATING -> 激活服务
     * 5. CONNECTING -> 建立连接
     * 6. LOGGING_IN -> 登录认证
     * 7. REPORTING -> 上报组成
     * 8. ACTIVE -> 激活完成
     */
    public void start() {
        setState(AgentState.INITIALIZING);
        initialize();
        
        setState(AgentState.SCANNING);
        scanRadar();
        
        setState(AgentState.REGISTERING);
        register();
        
        setState(AgentState.ACTIVATING);
        activate();
        
        setState(AgentState.CONNECTING);
        connect();
        
        setState(AgentState.LOGGING_IN);
        login();
        
        setState(AgentState.REPORTING);
        reportComposition();
        
        setState(AgentState.ACTIVE);
    }
    
    /**
     * 获取场景组入口
     */
    public SceneGroupManager getSceneGroupManager() {
        return mcpService.getSceneGroupManager();
    }
    
    /**
     * 上报Agent组成
     */
    private void reportComposition() {
        AgentComposition composition = AgentComposition.builder()
            .agentId(agentId)
            .components(getComponents())
            .links(getLinks())
            .capabilities(getCapabilities())
            .build();
        
        mcpService.reportComposition(composition);
    }
    
    // Agent组件
    public static class AgentComponent {
        String componentId;
        String componentType;
        String componentName;
        String version;
        Map<String, Object> config;
    }
    
    // Agent链路
    public static class AgentLink {
        String linkId;
        String fromComponent;
        String toComponent;
        String linkType;
        String status;
    }
}
```

---

## 八、Capability 能力规范

### 8.1 能力概念模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Capability 能力模型                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Capability (能力)                               │   │
│  │                                                                      │   │
│  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐           │   │
│  │  │   ID      │ │   Name    │ │   Type    │ │  Status   │           │   │
│  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘           │   │
│  │                                                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Operations (操作)                           │ │   │
│  │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐              │ │   │
│  │  │  │ Op1     │ │ Op2     │ │ Op3     │ │  ...    │              │ │   │
│  │  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘              │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Parameters (参数)                           │ │   │
│  │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐              │ │   │
│  │  │  │ Input   │ │ Output  │ │ Config  │ │ Context │              │ │   │
│  │  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘              │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 8.2 能力类型定义

```java
/**
 * 能力类型枚举
 */
public enum CapabilityType {
    
    /**
     * 服务能力
     * 提供业务服务功能
     */
    SERVICE("service", "服务能力"),
    
    /**
     * 存储能力
     * 提供数据存储功能
     */
    STORAGE("storage", "存储能力"),
    
    /**
     * 通讯能力
     * 提供消息通讯功能
     */
    COMMUNICATION("communication", "通讯能力"),
    
    /**
     * 计算能力
     * 提供计算处理功能
     */
    COMPUTATION("computation", "计算能力"),
    
    /**
     * 集成能力
     * 提供系统集成功能
     */
    INTEGRATION("integration", "集成能力"),
    
    /**
     * 管理能力
     * 提供系统管理功能
     */
    MANAGEMENT("management", "管理能力"),
    
    /**
     * 安全能力
     * 提供安全认证功能
     */
    SECURITY("security", "安全能力"),
    
    /**
     * 监控能力
     * 提供监控告警功能
     */
    MONITORING("monitoring", "监控能力");
    
    private String code;
    private String displayName;
}
```

### 8.3 能力状态定义

```java
/**
 * 能力状态枚举
 */
public enum CapabilityStatus {
    
    /**
     * 可用
     * 能力正常运行，可以调用
     */
    AVAILABLE("available", "可用"),
    
    /**
     * 不可用
     * 能力不可用，无法调用
     */
    UNAVAILABLE("unavailable", "不可用"),
    
    /**
     * 降级
     * 能力降级运行，部分功能受限
     */
    DEGRADED("degraded", "降级"),
    
    /**
     * 维护中
     * 能力正在维护，暂时不可用
     */
    MAINTENANCE("maintenance", "维护中"),
    
    /**
     * 初始化中
     * 能力正在初始化
     */
    INITIALIZING("initializing", "初始化中"),
    
    /**
     * 错误
     * 能力发生错误
     */
    ERROR("error", "错误");
    
    private String code;
    private String displayName;
}
```

### 8.4 能力管理接口

```java
/**
 * 能力管理接口 (SDK 0.7.3)
 */
public interface CapabilityAdmin {
    
    // ========== 能力注册 ==========
    
    /**
     * 注册能力
     * @param config 能力配置
     * @return 能力ID
     */
    String registerCapability(CapabilityConfig config);
    
    /**
     * 更新能力配置
     * @param capabilityId 能力ID
     * @param config 新配置
     * @return 是否成功
     */
    boolean updateCapability(String capabilityId, CapabilityConfig config);
    
    /**
     * 注销能力
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    boolean unregisterCapability(String capabilityId);
    
    /**
     * 获取能力详情
     * @param capabilityId 能力ID
     * @return 能力详情
     */
    CapabilityDetail getCapability(String capabilityId);
    
    /**
     * 查询能力列表
     * @param query 查询条件
     * @return 能力列表
     */
    List<CapabilityDetail> listCapabilities(CapabilityQuery query);
    
    // ========== 能力状态管理 ==========
    
    /**
     * 启用能力
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    boolean enableCapability(String capabilityId);
    
    /**
     * 禁用能力
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    boolean disableCapability(String capabilityId);
    
    /**
     * 设置维护状态
     * @param capabilityId 能力ID
     * @param reason 维护原因
     * @return 是否成功
     */
    boolean setMaintenance(String capabilityId, String reason);
    
    /**
     * 获取能力统计信息
     * @return 统计信息
     */
    CapabilityStatistics getCapabilityStatistics();
    
    // ========== 能力授权 ==========
    
    /**
     * 授权给场景
     * @param capabilityId 能力ID
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean authorizeToScene(String capabilityId, String sceneId);
    
    /**
     * 从场景撤销
     * @param capabilityId 能力ID
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean revokeFromScene(String capabilityId, String sceneId);
    
    /**
     * 授权给用户
     * @param capabilityId 能力ID
     * @param userId 用户ID
     * @param permission 权限
     * @return 是否成功
     */
    boolean authorizeToUser(String capabilityId, String userId, String permission);
    
    /**
     * 从用户撤销
     * @param capabilityId 能力ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean revokeFromUser(String capabilityId, String userId);
    
    /**
     * 获取能力授权列表
     * @param capabilityId 能力ID
     * @return 授权列表
     */
    List<AuthorizationInfo> getCapabilityAuthorizations(String capabilityId);
    
    // ========== 能力监控 ==========
    
    /**
     * 获取能力统计数据
     * @param capabilityId 能力ID
     * @return 统计数据
     */
    Map<String, Object> getCapabilityStatistics(String capabilityId);
    
    /**
     * 获取调用日志
     * @param capabilityId 能力ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<InvocationLog> getInvocationLogs(String capabilityId, long startTime, long endTime);
    
    /**
     * 获取能力指标
     * @param capabilityId 能力ID
     * @return 指标数据
     */
    Map<String, Object> getCapabilityMetrics(String capabilityId);
}
```

### 8.5 能力配置规范

```yaml
# 能力配置规范
apiVersion: capability.ooder.net/v1
kind: Capability

metadata:
  id: "mqtt-publish"              # 能力ID
  name: "MQTT Publish"            # 能力名称
  version: "1.0.0"                # 能力版本
  type: communication             # 能力类型
  
spec:
  description: "MQTT消息发布能力"
  
  # 提供者信息
  provider:
    providerId: "skill-mqtt"
    providerName: "MQTT Service"
    providerType: skill
    endpoint: "mqtt://localhost:1883"
    
  # 协议信息
  protocol:
    name: mqtt
    version: "3.1.1"
    
  # 操作定义
  operations:
    - operationId: publish
      description: "发布消息"
      inputParams:
        - name: topic
          type: string
          required: true
          description: "消息主题"
        - name: message
          type: object
          required: true
          description: "消息内容"
        - name: qos
          type: integer
          required: false
          defaultValue: 0
          description: "服务质量等级"
      outputParams:
        - name: messageId
          type: string
          description: "消息ID"
        - name: success
          type: boolean
          description: "是否成功"
          
    - operationId: publishBatch
      description: "批量发布消息"
      inputParams:
        - name: messages
          type: array
          required: true
          description: "消息列表"
      outputParams:
        - name: results
          type: array
          description: "发布结果列表"
          
  # 能力配置
  config:
    maxMessageSize: 1048576       # 最大消息大小(字节)
    defaultMessageTTL: 86400      # 默认消息TTL(秒)
    enableRetain: true            # 是否启用保留消息
    
  # 能力限制
  limits:
    maxInvokePerSecond: 1000      # 每秒最大调用次数
    maxConcurrentInvoke: 100      # 最大并发调用数
    timeout: 30000                # 调用超时(毫秒)
    
  # 能力依赖
  dependencies:
    - capabilityId: mqtt-broker
      required: true
    - capabilityId: mqtt-subscribe
      required: false
```

### 8.6 能力调用规范

```java
/**
 * 能力调用接口
 */
public interface CapabilityOperations {
    
    /**
     * 同步调用能力
     * @param capabilityId 能力ID
     * @param operation 操作名称
     * @param params 参数
     * @return 调用结果
     */
    Object invoke(String capabilityId, String operation, Map<String, Object> params);
    
    /**
     * 异步调用能力
     * @param capabilityId 能力ID
     * @param operation 操作名称
     * @param params 参数
     * @param callback 回调函数
     * @return 请求ID
     */
    String invokeAsync(String capabilityId, String operation, 
        Map<String, Object> params, CapabilityCallback callback);
    
    /**
     * 批量调用能力
     * @param requests 请求列表
     * @return 结果映射
     */
    Map<String, Object> batchInvoke(List<CapabilityRequest> requests);
    
    /**
     * 获取异步调用结果
     * @param requestId 请求ID
     * @return 调用结果
     */
    CapabilityResult getResult(String requestId);
    
    /**
     * 订阅能力事件
     * @param capabilityId 能力ID
     * @param eventType 事件类型
     * @param listener 监听器
     * @return 是否成功
     */
    boolean subscribe(String capabilityId, String eventType, CapabilityListener listener);
    
    /**
     * 取消订阅
     * @param capabilityId 能力ID
     * @param eventType 事件类型
     * @return 是否成功
     */
    boolean unsubscribe(String capabilityId, String eventType);
}
```

---

## 九、Skills 技能规范

### 9.1 Skill 概念模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Skill 技能模型                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Skill (技能)                                 │   │
│  │                                                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Metadata (元数据)                           │ │   │
│  │  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐     │ │   │
│  │  │  │    ID     │ │   Name    │ │  Version  │ │   Type    │     │ │   │
│  │  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘     │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Runtime (运行时)                            │ │   │
│  │  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐     │ │   │
│  │  │  │ Language  │ │ Framework │ │ MainClass │ │  Config   │     │ │   │
│  │  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘     │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                 Capabilities (能力集)                          │ │   │
│  │  │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐     │ │   │
│  │  │  │  Cap 1    │ │  Cap 2    │ │  Cap 3    │ │   ...     │     │ │   │
│  │  │  └───────────┘ └───────────┘ └───────────┘ └───────────┘     │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                    Scenes (场景集)                             │ │   │
│  │  │  ┌───────────┐ ┌───────────┐ ┌───────────┐                    │ │   │
│  │  │  │  Scene 1  │ │  Scene 2  │ │  Scene 3  │                    │ │   │
│  │  │  └───────────┘ └───────────┘ └───────────┘                    │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9.2 Skill 类型定义

```java
/**
 * Skill 类型常量 (SDK 0.7.3)
 */
public interface SkillType {
    
    /**
     * 组织机构服务
     */
    String SKILL_TYPE_ORG = "skill-org";
    
    /**
     * 虚拟文件系统服务
     */
    String SKILL_TYPE_VFS = "skill-vfs";
    
    /**
     * 消息服务
     */
    String SKILL_TYPE_MSG = "skill-msg";
    
    /**
     * Agent服务
     */
    String SKILL_TYPE_AGENT = "skill-agent";
    
    /**
     * MQTT服务
     */
    String SKILL_TYPE_MQTT = "skill-mqtt";
    
    /**
     * 工作流服务
     */
    String SKILL_TYPE_WORKFLOW = "skill-workflow";
    
    /**
     * 监控服务
     */
    String SKILL_TYPE_MONITOR = "skill-monitor";
    
    /**
     * 网络服务
     */
    String SKILL_TYPE_NETWORK = "skill-network";
}
```

### 9.3 Skill 接口规范

```java
/**
 * Skill 服务接口 (SDK 0.7.3)
 */
public interface SkillService {
    
    // ========== 常量定义 ==========
    
    String SKILL_TYPE_ORG = "skill-org";
    String SKILL_TYPE_VFS = "skill-vfs";
    String SKILL_TYPE_MSG = "skill-msg";
    String SKILL_TYPE_AGENT = "skill-agent";
    String SCENE_SYS = "SYS";
    
    // ========== 基本信息 ==========
    
    /**
     * 获取Skill ID
     */
    String getSkillId();
    
    /**
     * 获取Skill类型
     */
    String getSkillType();
    
    /**
     * 获取所属场景ID
     */
    String getSceneId();
    
    /**
     * 获取所属组ID
     */
    String getGroupId();
    
    // ========== 生命周期 ==========
    
    /**
     * 初始化Skill
     * @param context Skill上下文
     */
    void initialize(SkillContext context);
    
    /**
     * 启动Skill
     */
    void start();
    
    /**
     * 停止Skill
     */
    void stop();
    
    /**
     * 检查是否运行中
     */
    boolean isRunning();
    
    /**
     * 获取Skill状态
     */
    String getStatus();
    
    // ========== 能力信息 ==========
    
    /**
     * 获取Skill信息
     */
    Map<String, Object> getSkillInfo();
    
    /**
     * 获取能力列表
     */
    Map<String, Object> getCapabilities();
    
    // ========== 执行接口 ==========
    
    /**
     * 同步执行
     * @param request 请求
     * @return 响应
     */
    Object execute(SkillRequest request);
    
    /**
     * 异步执行
     * @param request 请求
     * @param callback 回调
     */
    void executeAsync(SkillRequest request, SkillCallback callback);
}
```

### 9.4 Skill YAML 规范

```yaml
# Skill YAML 规范 (SDK 0.7.3)
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: "skill-mqtt"                # Skill ID
  name: "MQTT Service Skill"      # Skill 名称
  version: "0.7.3"                # Skill 版本
  description: "MQTT服务技能"      # Skill 描述
  
spec:
  type: service-skill             # Skill 类型: service-skill, integration-skill, agent-skill
  
  # 运行时配置
  runtime:
    language: java                # 语言: java, python, nodejs, go
    javaVersion: "8"              # Java版本
    framework: spring-boot        # 框架
    mainClass: net.ooder.northbound.skills.mqtt.SkillMqttService
    
  # 能力定义
  capabilities:
    - id: mqtt-broker
      name: "MQTT Broker"
      description: "MQTT代理服务"
      operations:
        - start
        - stop
        - status
        
    - id: mqtt-publish
      name: "MQTT Publish"
      description: "MQTT消息发布"
      operations:
        - publish
        - publishBatch
        
    - id: mqtt-subscribe
      name: "MQTT Subscribe"
      description: "MQTT消息订阅"
      operations:
        - subscribe
        - unsubscribe
        
    - id: mqtt-p2p
      name: "MQTT P2P"
      description: "点对点消息"
      operations:
        - send
        - receive
        
    - id: mqtt-topic
      name: "MQTT Topic"
      description: "主题消息"
      operations:
        - create
        - delete
        - list
        
    - id: mqtt-command
      name: "MQTT Command"
      description: "命令消息"
      operations:
        - sendCommand
        - receiveCommand
        
  # 场景定义
  scenes:
    - name: mqtt-messaging
      description: "MQTT消息场景"
      required: true
      
    - name: iot-device
      description: "IoT设备场景"
      required: false
      
  # 提供者配置
  providers:
    - id: lightweight-mqtt
      name: "Lightweight MQTT"
      description: "轻量级MQTT Broker"
      default: true
      
    - id: emqx-enterprise
      name: "EMQX Enterprise"
      description: "EMQX企业版"
      
    - id: mosquitto-enterprise
      name: "Mosquitto Enterprise"
      description: "Mosquitto企业版"
      
    - id: aliyun-iot
      name: "阿里云IoT"
      description: "阿里云IoT平台"
      
    - id: tencent-iot
      name: "腾讯云IoT"
      description: "腾讯云IoT平台"
      
  # 配置参数
  config:
    broker:
      type: lightweight
      host: localhost
      port: 1883
    qos:
      default: 0
      max: 2
    message:
      maxSize: 1048576
      defaultTTL: 86400
      
  # 资源限制
  resources:
    cpu: "500m"
    memory: "256Mi"
    
  # 健康检查
  healthCheck:
    interval: 30
    timeout: 5
    threshold: 3
```

### 9.5 Skill 实现规范

```java
/**
 * Skill 实现示例 - MQTT Skill
 */
public class SkillMqttService implements SkillService {
    
    private String skillId;
    private String sceneId;
    private String groupId;
    private SkillContext context;
    private MqttClusterService mqttCluster;
    private volatile boolean running;
    
    @Override
    public String getSkillId() {
        return skillId;
    }
    
    @Override
    public String getSkillType() {
        return SKILL_TYPE_MQTT;
    }
    
    @Override
    public String getSceneId() {
        return sceneId;
    }
    
    @Override
    public String getGroupId() {
        return groupId;
    }
    
    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.sceneId = context.getSceneId();
        this.groupId = context.getGroupId();
        this.skillId = context.getSkillId();
        
        // 初始化MQTT集群服务
        Map<String, Object> config = context.getConfig();
        this.mqttCluster = new MqttClusterService();
        this.mqttCluster.initialize(config);
    }
    
    @Override
    public void start() {
        mqttCluster.start();
        running = true;
    }
    
    @Override
    public void stop() {
        mqttCluster.stop();
        running = false;
    }
    
    @Override
    public boolean isRunning() {
        return running && mqttCluster.isRunning();
    }
    
    @Override
    public String getStatus() {
        return running ? "RUNNING" : "STOPPED";
    }
    
    @Override
    public Map<String, Object> getSkillInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("skillId", skillId);
        info.put("skillType", getSkillType());
        info.put("sceneId", sceneId);
        info.put("groupId", groupId);
        info.put("status", getStatus());
        info.put("running", running);
        return info;
    }
    
    @Override
    public Map<String, Object> getCapabilities() {
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("mqtt-broker", Map.of(
            "operations", Arrays.asList("start", "stop", "status")
        ));
        capabilities.put("mqtt-publish", Map.of(
            "operations", Arrays.asList("publish", "publishBatch")
        ));
        capabilities.put("mqtt-subscribe", Map.of(
            "operations", Arrays.asList("subscribe", "unsubscribe")
        ));
        return capabilities;
    }
    
    @Override
    public Object execute(SkillRequest request) {
        String operation = request.getOperation();
        Map<String, Object> params = request.getParams();
        
        switch (operation) {
            case "publish":
                return publish(params);
            case "subscribe":
                return subscribe(params);
            case "unsubscribe":
                return unsubscribe(params);
            default:
                throw new UnsupportedOperationException("Unknown operation: " + operation);
        }
    }
    
    @Override
    public void executeAsync(SkillRequest request, SkillCallback callback) {
        try {
            Object result = execute(request);
            callback.onSuccess(result);
        } catch (Exception e) {
            callback.onError(e);
        }
    }
    
    private Object publish(Map<String, Object> params) {
        String topic = (String) params.get("topic");
        Object message = params.get("message");
        int qos = (Integer) params.getOrDefault("qos", 0);
        
        mqttCluster.publish(topic, message, qos);
        return Map.of("success", true, "topic", topic);
    }
    
    private Object subscribe(Map<String, Object> params) {
        String topic = (String) params.get("topic");
        int qos = (Integer) params.getOrDefault("qos", 0);
        
        mqttCluster.subscribe(topic, qos);
        return Map.of("success", true, "topic", topic);
    }
    
    private Object unsubscribe(Map<String, Object> params) {
        String topic = (String) params.get("topic");
        
        mqttCluster.unsubscribe(topic);
        return Map.of("success", true, "topic", topic);
    }
}
```

---

## 十、用户故事与实用场景

### 10.1 用户故事分类

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          用户故事分类                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    通讯类用户故事                                    │   │
│  │  US-COM-001: 点对点消息发送                                          │   │
│  │  US-COM-002: 群组消息广播                                            │   │
│  │  US-COM-003: 文件传输                                                │   │
│  │  US-COM-004: 离线消息                                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    业务类用户故事                                    │   │
│  │  US-BIZ-001: 员工信息管理                                            │   │
│  │  US-BIZ-002: 审批流程发起                                            │   │
│  │  US-BIZ-003: 客户信息维护                                            │   │
│  │  US-BIZ-004: 项目任务分配                                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    IoT类用户故事                                     │   │
│  │  US-IOT-001: 设备注册接入                                            │   │
│  │  US-IOT-002: 设备远程控制                                            │   │
│  │  US-IOT-003: 数据实时采集                                            │   │
│  │  US-IOT-004: 告警通知                                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    协作类用户故事                                    │   │
│  │  US-COL-001: 在线会议                                                │   │
│  │  US-COL-002: 文档协同编辑                                            │   │
│  │  US-COL-003: 任务协作                                                │   │
│  │  US-COL-004: 知识分享                                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    系统类用户故事                                    │   │
│  │  US-SYS-001: 用户登录认证                                            │   │
│  │  US-SYS-002: 权限管理                                                │   │
│  │  US-SYS-003: 系统监控                                                │   │
│  │  US-SYS-004: 日志审计                                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 10.2 通讯类用户故事

#### US-COM-001: 点对点消息发送

```yaml
# 用户故事: 点对点消息发送
id: US-COM-001
title: 点对点消息发送
category: COMMUNICATION

# 用户故事描述
story: |
  作为用户，我希望能够向指定用户发送消息，以便进行私密通信。

# 验收标准
acceptanceCriteria:
  - 给定团队成员已登录，当创建协作任务时，则应通知相关成员
  - 给定任务已分配，当成员接受任务时，则应更新任务状态
  - 给定任务有更新，当更新发生时，则应通知所有相关人员
  - 给定任务完成，当标记完成时，则应更新项目进度

sceneMapping:
  sceneType: TASK
  requiredDrivers:
    - driver-org
    - driver-msg
  requiredCapabilities:
    - task.create
    - task.assign
    - task.update
    - task.complete
    - notify.send

implementation:
  operations:
    - operation: createTask
      capability: task.create
      params:
        title: string (required)
        description: string (optional)
        assignees: array (required)
        dueDate: long (optional)
```

#### US-COL-004: 知识分享

```yaml
# 用户故事: 知识分享
id: US-COL-004
title: 知识分享
category: COLLABORATION

story: |
  作为团队成员，我希望能够分享知识文档，以便团队知识沉淀和传承。

acceptanceCriteria:
  - 给定用户已登录，当创建知识文档时，则应保存到知识库
  - 给定知识文档已创建，当分享给团队时，则团队成员应能访问
  - 给定知识文档存在，当搜索关键词时，则应返回相关文档列表
  - 给定知识文档被访问，当记录访问日志时，则应统计访问次数

sceneMapping:
  sceneType: KNOWLEDGE
  requiredDrivers:
    - driver-vfs
    - driver-org
    - driver-msg
  requiredCapabilities:
    - knowledge.create
    - knowledge.share
    - knowledge.search
    - knowledge.access

implementation:
  operations:
    - operation: createKnowledge
      capability: knowledge.create
      params:
        title: string (required)
        content: object (required)
        category: string (optional)
        tags: array (optional)
    - operation: searchKnowledge
      capability: knowledge.search
      params:
        keyword: string (required)
        category: string (optional)
        limit: integer (optional, default: 20)
```

### 10.6 系统类用户故事

#### US-SYS-001: 用户登录认证

```yaml
# 用户故事: 用户登录认证
id: US-SYS-001
title: 用户登录认证
category: SYSTEM

story: |
  作为用户，我希望能够安全登录系统，以便访问系统功能。

acceptanceCriteria:
  - 给定用户已注册，当输入正确账号密码时，则应成功登录并返回Token
  - 给定用户登录成功，当访问受保护资源时，则应验证Token并允许访问
  - 给定Token过期，当访问资源时，则应提示重新登录
  - 给定用户退出登录，当退出时，则应销毁会话

sceneMapping:
  sceneType: SYS
  requiredDrivers:
    - driver-org
  requiredCapabilities:
    - auth.login
    - auth.logout
    - auth.validate
    - auth.refresh

implementation:
  operations:
    - operation: login
      capability: auth.login
      params:
        account: string (required)
        password: string (required)
        deviceId: string (optional)
      returns:
        userId: string
        token: string
        expireTime: long
    - operation: logout
      capability: auth.logout
      params:
        token: string (required)

# 零配置要求
zeroConfig:
  autoCreateScene: true
  sceneId: "SYS"
  autoCreateDefaultGroup: true
  defaultSkills:
    - skill-org
    - skill-msg
    - skill-vfs
```

#### US-SYS-002: 权限管理

```yaml
# 用户故事: 权限管理
id: US-SYS-002
title: 权限管理
category: SYSTEM

story: |
  作为系统管理员，我希望能够管理用户权限，以便控制系统访问。

acceptanceCriteria:
  - 给定管理员已登录，当创建角色时，则应保存角色并关联权限
  - 给定角色已创建，当分配给用户时，则用户应获得相应权限
  - 给定用户有权限，当访问资源时，则应验证权限并决定是否允许
  - 给定权限变更，当权限修改时，则应立即生效

sceneMapping:
  sceneType: SYS
  requiredDrivers:
    - driver-org
  requiredCapabilities:
    - role.create
    - role.update
    - role.delete
    - permission.assign
    - permission.check

implementation:
  operations:
    - operation: createRole
      capability: role.create
      params:
        roleName: string (required)
        permissions: array (required)
        description: string (optional)
    - operation: checkPermission
      capability: permission.check
      params:
        userId: string (required)
        resource: string (required)
        action: string (required)
```

#### US-SYS-003: 系统监控

```yaml
# 用户故事: 系统监控
id: US-SYS-003
title: 系统监控
category: SYSTEM

story: |
  作为运维人员，我希望能够监控系统运行状态，以便及时发现和处理问题。

acceptanceCriteria:
  - 给定运维人员已登录，当查看监控面板时，则应显示系统运行指标
  - 给定系统异常，当指标超过阈值时，则应触发告警
  - 给定告警触发，当发送通知时，则运维人员应收到告警信息
  - 给定历史数据，当查询历史指标时，则应返回指定时间范围的数据

sceneMapping:
  sceneType: MONITOR
  requiredDrivers:
    - driver-msg
    - driver-mqtt
  requiredCapabilities:
    - monitor.metrics
    - monitor.alert
    - monitor.history
    - notify.send

implementation:
  operations:
    - operation: getMetrics
      capability: monitor.metrics
      params:
        metricType: string (optional)
        timeRange: object (optional)
      returns:
        cpu: number
        memory: number
        disk: number
        network: object
    - operation: createAlertRule
      capability: monitor.alert
      params:
        metricName: string (required)
        threshold: number (required)
        operator: string (required)
        notifyTargets: array (required)
```

#### US-SYS-004: 日志审计

```yaml
# 用户故事: 日志审计
id: US-SYS-004
title: 日志审计
category: SYSTEM

story: |
  作为安全审计员，我希望能够查看系统操作日志，以便进行安全审计。

acceptanceCriteria:
  - 给定用户操作，当操作发生时，则应记录操作日志
  - 给定审计员已登录，当查询日志时，则应返回符合条件的日志列表
  - 给定日志存在，当导出日志时，则应生成日志文件
  - 给定敏感操作，当发生时，则应标记为重点审计

sceneMapping:
  sceneType: SECURITY
  requiredDrivers:
    - driver-org
    - driver-msg
  requiredCapabilities:
    - log.record
    - log.query
    - log.export
    - audit.mark

implementation:
  operations:
    - operation: recordLog
      capability: log.record
      params:
        userId: string (required)
        action: string (required)
        resource: string (required)
        result: string (required)
        details: object (optional)
    - operation: queryLogs
      capability: log.query
      params:
        userId: string (optional)
        action: string (optional)
        startTime: long (optional)
        endTime: long (optional)
        page: integer (optional)
        size: integer (optional)
```

### 10.7 用户故事到场景映射表

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     用户故事 → 场景类型映射（基于Driver架构）                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  用户故事ID      │ 场景类型   │ 必需驱动              │ 零配置场景          │
│  ─────────────────────────────────────────────────────────────────────────  │
│  US-COM-001     │ P2P       │ driver-msg, driver-mqtt │ 自动创建P2P场景     │
│  US-COM-002     │ GROUP     │ driver-msg, driver-org  │ 自动创建群组场景     │
│  US-COM-003     │ P2P       │ driver-vfs, driver-msg  │ 复用P2P场景         │
│  US-COM-004     │ P2P       │ driver-msg, driver-vfs  │ 复用P2P场景         │
│  ─────────────────────────────────────────────────────────────────────────  │
│  US-BIZ-001     │ HR        │ driver-org, driver-vfs  │ 自动创建HR场景       │
│  US-BIZ-002     │ APPROVAL  │ driver-org, driver-msg   │ 自动创建审批场景   │
│  US-BIZ-003     │ CRM       │ driver-org, driver-msg   │ 自动创建CRM场景    │
│  US-BIZ-004     │ PROJECT   │ driver-org, driver-msg   │ 自动创建项目场景   │
│  ─────────────────────────────────────────────────────────────────────────  │
│  US-IOT-001     │ DEVICE    │ driver-mqtt, driver-msg  │ 自动创建设备场景   │
│  US-IOT-002     │ DEVICE    │ driver-mqtt, driver-msg  │ 复用设备场景       │
│  US-IOT-003     │ COLLECTION│ driver-msg, driver-vfs   │ 自动创建采集场景   │
│  US-IOT-004     │ MONITOR   │ driver-msg, driver-mqtt  │ 自动创建监控场景   │
│  ─────────────────────────────────────────────────────────────────────────  │
│  US-COL-001     │ MEETING   │ driver-msg, driver-vfs   │ 自动创建会议场景   │
│  US-COL-002     │ DOCUMENT  │ driver-vfs, driver-org   │ 自动创建文档场景   │
│  US-COL-003     │ TASK      │ driver-org, driver-msg   │ 自动创建任务场景   │
│  US-COL-004     │ KNOWLEDGE │ driver-vfs, driver-org   │ 自动创建知识场景   │
│  ─────────────────────────────────────────────────────────────────────────  │
│  US-SYS-001     │ SYS       │ driver-org               │ 系统场景(默认)     │
│  US-SYS-002     │ SYS       │ driver-org               │ 复用系统场景       │
│  US-SYS-003     │ MONITOR   │ driver-msg, driver-mqtt  │ 自动创建监控场景   │
│  US-SYS-004     │ SECURITY  │ driver-org, driver-msg   │ 自动创建安全场景   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 10.8 Driver架构说明

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Driver架构（场景驱动）                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    内置场景驱动 (SEC 仓库)                            │   │
│  │  driver-org: 组织管理场景驱动                                         │   │
│  │  driver-vfs: 文件系统场景驱动                                         │   │
│  │  driver-msg: 消息通讯场景驱动                                         │   │
│  │  driver-mqtt: MQTT通讯场景驱动                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    扩展场景驱动 (Skills 仓库)                         │   │
│  │  driver-org-dingtalk: 钉钉组织驱动                                    │   │
│  │  driver-org-feishu: 飞书组织驱动                                      │   │
│  │  driver-org-wecom: 企业微信组织驱动                                   │   │
│  │  driver-openwrt: OpenWrt设备驱动                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Provider接口 (SEC定义)                             │   │
│  │  AgentProvider: Agent管理接口                                         │   │
│  │  HealthProvider: 健康检查接口                                         │   │
│  │  ProtocolProvider: 协议管理接口                                        │   │
│  │  SkillShareProvider: 技能共享接口                                     │   │
│  │  NetworkConfigProvider: 网络配置接口                                   │   │
│  │  DeviceManagementProvider: 设备管理接口                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 十一、零配置规范

### 11.1 零配置原则

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          零配置设计原则                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 约定优于配置 (Convention over Configuration)                             │
│     - 使用合理的默认值                                                       │
│     - 遵循命名约定                                                           │
│     - 自动推断配置                                                           │
│                                                                             │
│  2. 自动发现 (Auto Discovery)                                                │
│     - 自动发现服务                                                           │
│     - 自动发现能力                                                           │
│     - 自动发现依赖                                                           │
│                                                                             │
│  3. 自动配置 (Auto Configuration)                                            │
│     - 自动创建场景                                                           │
│     - 自动创建组                                                             │
│     - 自动注册Skill                                                          │
│                                                                             │
│  4. 自愈能力 (Self Healing)                                                  │
│     - 自动恢复连接                                                           │
│     - 自动重试操作                                                           │
│     - 自动清理资源                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 11.2 零配置启动流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        零配置启动流程                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ 1. 系统启动                                                            │   │
│  │    SceneServer.start()                                                 │   │
│  │    ├── 自动创建 SYS 场景                                               │   │
│  │    ├── 自动创建 default 组                                             │   │
│  │    └── 自动加载系统 Skills                                             │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ 2. 驱动初始化                                                          │   │
│  │    DriverRegistry.initializeAll()                                      │   │
│  │    ├── OrgDriver.initialize() → 自动配置数据源                         │   │
│  │    ├── MsgDriver.initialize() → 自动启动消息服务                       │   │
│  │    ├── VfsDriver.initialize() → 自动配置存储                           │   │
│  │    └── MqttDriver.initialize() → 自动启动MQTT Broker                   │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ 3. Skill注册                                                           │   │
│  │    SceneGroupManager.registerSkill()                                   │   │
│  │    ├── skill-org → 自动发现组织能力                                    │   │
│  │    ├── skill-msg → 自动发现消息能力                                    │   │
│  │    ├── skill-vfs → 自动发现文件能力                                    │   │
│  │    └── skill-agent → 自动发现Agent能力                                 │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ 4. 能力发现                                                            │   │
│  │    CapabilityEngine.discoverCapabilities()                             │   │
│  │    ├── 自动注册能力到能力库                                            │   │
│  │    ├── 自动建立能力依赖关系                                            │   │
│  │    └── 自动发布能力到场景                                              │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ 5. 服务就绪                                                            │   │
│  │    SceneServerStatus.RUNNING                                           │   │
│  │    ├── 接受用户登录                                                    │   │
│  │    ├── 接受场景创建                                                    │   │
│  │    └── 接受能力调用                                                    │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 11.3 零配置场景创建

```java
/**
 * 零配置场景创建器
 * 
 * 根据用户故事自动推导场景配置
 */
public class ZeroConfigSceneCreator {
    
    /**
     * 根据用户故事自动创建场景
     * 
     * @param userStory 用户故事
     * @return 场景实例
     */
    public Scene createSceneFromUserStory(UserStory userStory) {
        
        // 1. 推导场景类型
        SceneType sceneType = inferSceneType(userStory);
        
        // 2. 推导必需驱动
        List<String> requiredDrivers = inferRequiredDrivers(userStory);
        
        // 3. 推导必需能力
        List<String> requiredCapabilities = inferRequiredCapabilities(userStory);
        
        // 4. 生成场景定义
        SceneDefinition definition = SceneDefinition.builder()
            .sceneId(generateSceneId(sceneType))
            .sceneName(userStory.getTitle())
            .sceneType(sceneType.name())
            .requiredDrivers(requiredDrivers)
            .requiredCapabilities(requiredCapabilities.stream()
                .map(cap -> new CapabilityRequirement(cap))
                .collect(Collectors.toList()))
            .build();
        
        // 5. 注册场景
        return sceneServer.registerScene(definition);
    }
    
    /**
     * 推导场景类型
     */
    private SceneType inferSceneType(UserStory userStory) {
        String category = userStory.getCategory();
        
        switch (category) {
            case "COMMUNICATION":
                if (userStory.getTitle().contains("点对点")) {
                    return SceneType.P2P;
                } else if (userStory.getTitle().contains("群组")) {
                    return SceneType.GROUP;
                } else {
                    return SceneType.BROADCAST;
                }
                
            case "BUSINESS":
                if (userStory.getTitle().contains("员工")) {
                    return SceneType.HR;
                } else if (userStory.getTitle().contains("客户")) {
                    return SceneType.CRM;
                } else if (userStory.getTitle().contains("审批")) {
                    return SceneType.APPROVAL;
                } else if (userStory.getTitle().contains("项目")) {
                    return SceneType.PROJECT;
                } else {
                    return SceneType.CUSTOM;
                }
                
            case "IOT":
                if (userStory.getTitle().contains("设备")) {
                    return SceneType.DEVICE;
                } else if (userStory.getTitle().contains("数据")) {
                    return SceneType.COLLECTION;
                } else {
                    return SceneType.EDGE;
                }
                
            case "COLLABORATION":
                if (userStory.getTitle().contains("会议")) {
                    return SceneType.MEETING;
                } else if (userStory.getTitle().contains("文档")) {
                    return SceneType.DOCUMENT;
                } else if (userStory.getTitle().contains("任务")) {
                    return SceneType.TASK;
                } else {
                    return SceneType.KNOWLEDGE;
                }
                
            case "SYSTEM":
                if (userStory.getTitle().contains("监控")) {
                    return SceneType.MONITOR;
                } else if (userStory.getTitle().contains("审计")) {
                    return SceneType.SECURITY;
                } else {
                    return SceneType.SYS;
                }
                
            default:
                return SceneType.CUSTOM;
        }
    }
    
    /**
     * 推导必需驱动
     */
    private List<String> inferRequiredDrivers(UserStory userStory) {
        List<String> drivers = new ArrayList<String>();
        
        // 根据能力需求推导驱动
        for (String capability : userStory.getRequiredCapabilities()) {
            if (capability.startsWith("message.") || capability.startsWith("notify.")) {
                if (!drivers.contains("driver-msg")) {
                    drivers.add("driver-msg");
                }
            }
            if (capability.startsWith("file.") || capability.startsWith("document.")) {
                if (!drivers.contains("driver-vfs")) {
                    drivers.add("driver-vfs");
                }
            }
            if (capability.startsWith("employee.") || capability.startsWith("user.") 
                || capability.startsWith("customer.") || capability.startsWith("group.")) {
                if (!drivers.contains("driver-org")) {
                    drivers.add("driver-org");
                }
            }
            if (capability.startsWith("mqtt.") || capability.startsWith("device.") 
                || capability.startsWith("agent.")) {
                if (!drivers.contains("driver-mqtt")) {
                    drivers.add("driver-mqtt");
                }
            }
        }
        
        return drivers;
    }
}
```

### 11.4 零配置默认值

```yaml
# 零配置默认值规范
zeroConfig:
  
  # 系统场景默认配置
  systemScene:
    sceneId: "SYS"
    sceneName: "系统场景"
    autoCreate: true
    defaultGroup: "default"
    defaultSkills:
      - skill-org
      - skill-msg
      - skill-vfs
      - skill-agent
      
  # 场景默认配置
  sceneDefaults:
    
    # P2P场景
    P2P:
      sceneIdPrefix: "p2p-"
      defaultGroupName: "default"
      maxMembers: 2
      capabilities:
        - message.send
        - message.receive
        - file.upload
        - file.download
        
    # 群组场景
    GROUP:
      sceneIdPrefix: "group-"
      defaultGroupName: "members"
      maxMembers: 500
      capabilities:
        - message.broadcast
        - group.members
        
    # HR场景
    HR:
      sceneIdPrefix: "hr-"
      defaultGroups:
        - name: "employees"
          type: "service"
        - name: "admin"
          type: "admin"
      capabilities:
        - employee.create
        - employee.update
        - employee.query
        
    # 审批场景
    APPROVAL:
      sceneIdPrefix: "approval-"
      defaultGroups:
        - name: "applicants"
          type: "service"
        - name: "approvers"
          type: "service"
      capabilities:
        - flow.start
        - approval.submit
        - approval.approve
        
    # 设备场景
    DEVICE:
      sceneIdPrefix: "device-"
      defaultGroups:
        - name: "devices"
          type: "service"
        - name: "controllers"
          type: "admin"
      capabilities:
        - device.register
        - device.command
        - device.status
        
  # 引擎默认配置
  engineDefaults:
    
    OrgEngine:
      datasource: "json"
      jsonPath: "./data/org.json"
      
    MsgEngine:
      brokerType: "lightweight"
      port: 1883
      
    VfsEngine:
      storageType: "local"
      basePath: "./data/vfs"
      
    AgentEngine:
      heartbeatInterval: 30000
      maxMissedHeartbeats: 3
      
  # 能力默认配置
  capabilityDefaults:
    
    message:
      maxSize: 1048576
      defaultTTL: 86400
      
    file:
      maxSize: 104857600
      allowedTypes: "*"
      
    device:
      commandTimeout: 30000
      maxRetryAttempts: 3
```

### 11.5 零配置检查清单

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        零配置检查清单                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  □ 系统启动                                                                 │
│    □ SYS场景自动创建                                                        │
│    □ default组自动创建                                                       │
│    □ 系统Skills自动加载                                                     │
│    □ 引擎自动初始化                                                         │
│                                                                             │
│  □ 场景创建                                                                 │
│    □ 场景ID自动生成                                                         │
│    □ 默认组自动创建                                                         │
│    □ 必需引擎自动检查                                                       │
│    □ 必需能力自动配置                                                       │
│                                                                             │
│  □ 用户登录                                                                 │
│    □ 会话自动创建                                                           │
│    □ 可用场景自动发现                                                       │
│    □ 用户权限自动加载                                                       │
│    □ 客户端自动创建                                                         │
│                                                                             │
│  □ 能力调用                                                                 │
│    □ 能力自动发现                                                           │
│    □ 参数自动验证                                                           │
│    □ 结果自动处理                                                           │
│    □ 日志自动记录                                                           │
│                                                                             │
│  □ 错误处理                                                                 │
│    □ 连接自动重试                                                           │
│    □ 错误自动恢复                                                           │
│    □ 资源自动清理                                                           │
│    □ 日志自动记录                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 十二、附录

### 12.1 场景类型速查表

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

### 12.2 能力类型速查表

| 能力类型 | 代码 | 说明 | 典型能力 |
|----------|------|------|----------|
| 服务能力 | SERVICE | 业务服务功能 | employee, customer, project |
| 存储能力 | STORAGE | 数据存储功能 | file, document, knowledge |
| 通讯能力 | COMMUNICATION | 消息通讯功能 | message, notify, broadcast |
| 计算能力 | COMPUTATION | 计算处理功能 | analytics, transform |
| 集成能力 | INTEGRATION | 系统集成功能 | sync, import, export |
| 管理能力 | MANAGEMENT | 系统管理功能 | user, role, permission |
| 安全能力 | SECURITY | 安全认证功能 | auth, encrypt, audit |
| 监控能力 | MONITORING | 监控告警功能 | metrics, alert, log |

### 12.3 引擎类型速查表

| 引擎类型 | 代码 | 默认Skill | 核心能力 |
|----------|------|-----------|----------|
| 组织引擎 | ORG | skill-org | user, department, role, permission |
| 消息引擎 | MSG | skill-msg | message, topic, presence, push |
| 文件引擎 | VFS | skill-vfs | file, folder, share, version |
| 代理引擎 | AGENT | skill-agent | agent, scene, capability |
| 流程引擎 | WORKFLOW | skill-workflow | flow, instance, task, approval |
| 监控引擎 | MONITOR | skill-monitor | metrics, alert, log |
| 网络引擎 | NETWORK | skill-network | connection, route, proxy |
| 会话引擎 | SESSION | - | session, token, auth |
| 状态引擎 | STATE | - | state, cache, transaction |

### 12.4 状态枚举速查表

#### 场景状态 (SceneStatus)

| 状态 | 代码 | 说明 |
|------|------|------|
| 已创建 | CREATED | 场景已注册，未启动 |
| 启动中 | STARTING | 场景正在启动 |
| 运行中 | RUNNING | 场景正常运行 |
| 停止中 | STOPPING | 场景正在停止 |
| 已停止 | STOPPED | 场景已停止 |
| 错误 | ERROR | 场景发生错误 |

#### 引擎状态 (EngineStatus)

| 状态 | 代码 | 说明 |
|------|------|------|
| 已创建 | CREATED | 引擎已实例化 |
| 已安装 | INSTALLED | 引擎已安装配置 |
| 初始化中 | INITIALIZING | 引擎正在初始化 |
| 已初始化 | INITIALIZED | 引擎初始化完成 |
| 启动中 | STARTING | 引擎正在启动 |
| 运行中 | RUNNING | 引擎正常运行 |
| 停止中 | STOPPING | 引擎正在停止 |
| 已停止 | STOPPED | 引擎已停止 |
| 错误 | ERROR | 引擎发生错误 |

#### 能力状态 (CapabilityStatus)

| 状态 | 代码 | 说明 |
|------|------|------|
| 可用 | AVAILABLE | 能力正常可用 |
| 不可用 | UNAVAILABLE | 能力不可用 |
| 降级 | DEGRADED | 能力降级运行 |
| 维护中 | MAINTENANCE | 能力维护中 |
| 初始化中 | INITIALIZING | 能力初始化中 |
| 错误 | ERROR | 能力发生错误 |

#### Agent状态 (AgentState)

| 状态 | 代码 | 说明 |
|------|------|------|
| 初始化中 | INITIALIZING | Agent初始化 |
| 扫描中 | SCANNING | Agent扫描雷达 |
| 注册中 | REGISTERING | Agent注册集群 |
| 激活中 | ACTIVATING | Agent激活服务 |
| 连接中 | CONNECTING | Agent建立连接 |
| 登录中 | LOGGING_IN | Agent登录认证 |
| 上报中 | REPORTING | Agent上报组成 |
| 活跃 | ACTIVE | Agent正常运行 |
| 非活跃 | INACTIVE | Agent已停用 |
| 错误 | ERROR | Agent发生错误 |

### 12.5 术语索引

| 术语 | 英文 | 章节 | 说明 |
|------|------|------|------|
| 场景 | Scene | 2.1 | 服务组织单元 |
| 场景服务 | SceneServer | 2.1 | 场景服务总协调器 |
| 场景客户端 | SceneClient | 2.1 | 用户接入场景的统一入口 |
| 场景定义 | SceneDefinition | 2.1 | 场景的元数据描述 |
| 场景注册中心 | SceneRegistry | 2.1 | 维护场景定义和元数据 |
| 组 | Group | 2.2 | 场景内的服务节点集合 |
| 成员 | Member | 2.2 | 场景或组的参与者 |
| 角色 | Role | 2.2 | 场景内的角色定义 |
| 链路 | Link | 2.2 | 成员之间的连接关系 |
| 引擎 | Engine | 2.3 | 技术能力分类管理器 |
| 技能 | Skill | 2.3 | 原子化服务能力 |
| 能力 | Capability | 2.3 | Skill提供的功能点 |
| 操作 | Operation | 2.3 | 能力的具体操作方法 |
| 代理 | Agent | 2.4 | 场景内的智能代理 |
| 用户 | User | 2.4 | 场景的人类参与者 |
| 组织 | Organization | 2.4 | 用户的组织归属 |
| 会话 | Session | 2.4 | 用户与场景的连接状态 |

### 12.6 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2024-01-20 | 初始版本，基于SDK 0.7.3规范 |

---

**文档结束**
  - 给定用户已登录，当用户选择联系人并发送消息时，则消息应成功发送给目标用户
  - 给定目标用户在线，当消息发送时，则目标用户应实时收到消息
  - 给定目标用户离线，当消息发送时，则消息应存储为离线消息
  - 给定用户发送消息，当发送成功时，则应返回消息ID和时间戳

# 场景映射
sceneMapping:
  sceneType: P2P
  requiredEngines:
    - MsgEngine
    - AgentEngine
  requiredCapabilities:
    - message.send
    - message.receive
    - presence.online
    - presence.offline

# 技术实现
implementation:
  channels:
    - type: P2P
      topic: "ooder/p2p/{recipientId}/inbox"
  operations:
    - operation: send
      capability: message.send
      params:
        recipientId: string (required)
        content: object (required)
        messageType: string (optional, default: text)
        
# 零配置要求
zeroConfig:
  autoCreateScene: true
  autoCreateGroup: true
  defaultGroupId: "p2p-default"
```

#### US-COM-002: 群组消息广播

```yaml
# 用户故事: 群组消息广播
id: US-COM-002
title: 群组消息广播
category: COMMUNICATION

story: |
  作为管理员，我希望能够向多个用户群发消息，以便进行通知公告。

acceptanceCriteria:
  - 给定管理员已登录，当管理员发送群组消息时，则所有群组成员应收到消息
  - 给定群组消息发送，当发送成功时，则应返回发送统计（成功数、失败数）
  - 给定用户不在群组中，当群组消息发送时，则该用户不应收到消息

sceneMapping:
  sceneType: GROUP
  requiredEngines:
    - MsgEngine
    - OrgEngine
  requiredCapabilities:
    - message.broadcast
    - group.members

implementation:
  channels:
    - type: GROUP
      topic: "ooder/group/{groupId}/broadcast"
  operations:
    - operation: broadcast
      capability: message.broadcast
      params:
        groupId: string (required)
        content: object (required)
```

#### US-COM-003: 文件传输

```yaml
# 用户故事: 文件传输
id: US-COM-003
title: 文件传输
category: COMMUNICATION

story: |
  作为用户，我希望能够向其他用户发送文件，以便共享文档和资料。

acceptanceCriteria:
  - 给定用户已登录，当用户选择文件并发送时，则文件应成功上传并发送给目标用户
  - 给定文件大小超过限制，当用户发送文件时，则应提示文件过大
  - 给定目标用户接收文件，当用户下载文件时，则应成功下载完整文件

sceneMapping:
  sceneType: P2P
  requiredEngines:
    - VfsEngine
    - MsgEngine
  requiredCapabilities:
    - file.upload
    - file.download
    - file.share

implementation:
  operations:
    - operation: upload
      capability: file.upload
      params:
        file: binary (required)
        fileName: string (required)
        recipientId: string (optional)
    - operation: share
      capability: file.share
      params:
        fileId: string (required)
        recipientIds: array (required)
        expireTime: long (optional)
```

#### US-COM-004: 离线消息

```yaml
# 用户故事: 离线消息
id: US-COM-004
title: 离线消息
category: COMMUNICATION

story: |
  作为用户，希望离线时也能接收消息，上线后能够查看未读消息。

acceptanceCriteria:
  - 给定用户离线，当其他用户发送消息时，则消息应存储为离线消息
  - 给定用户上线，当用户登录时，则应收到所有未读离线消息
  - 给定用户查看离线消息，当用户阅读后，则消息应标记为已读

sceneMapping:
  sceneType: P2P
  requiredEngines:
    - MsgEngine
    - VfsEngine
  requiredCapabilities:
    - message.store
    - message.retrieve
    - message.read

implementation:
  operations:
    - operation: getOfflineMessages
      capability: message.retrieve
      params:
        userId: string (required)
        limit: integer (optional, default: 50)
    - operation: markAsRead
      capability: message.read
      params:
        messageIds: array (required)
```

### 10.3 业务类用户故事

#### US-BIZ-001: 员工信息管理

```yaml
# 用户故事: 员工信息管理
id: US-BIZ-001
title: 员工信息管理
category: BUSINESS

story: |
  作为HR管理员，我希望能够管理员工信息，以便维护企业人力资源数据。

acceptanceCriteria:
  - 给定HR管理员已登录，当创建员工信息时，则员工信息应成功保存
  - 给定员工信息存在，当更新员工信息时，则应成功更新并记录变更历史
  - 给定员工信息存在，当查询员工信息时，则应返回完整员工信息
  - 给定员工离职，当删除员工信息时，则应标记为离职状态而非物理删除

sceneMapping:
  sceneType: HR
  requiredEngines:
    - OrgEngine
    - VfsEngine
  requiredCapabilities:
    - employee.create
    - employee.update
    - employee.query
    - employee.delete

implementation:
  operations:
    - operation: createEmployee
      capability: employee.create
      params:
        name: string (required)
        departmentId: string (required)
        position: string (required)
        email: string (required)
        phone: string (optional)
    - operation: updateEmployee
      capability: employee.update
      params:
        employeeId: string (required)
        updates: object (required)
```

#### US-BIZ-002: 审批流程发起

```yaml
# 用户故事: 审批流程发起
id: US-BIZ-002
title: 审批流程发起
category: BUSINESS

story: |
  作为员工，我希望能够发起审批流程，以便申请请假、报销等事项。

acceptanceCriteria:
  - 给定员工已登录，当发起审批申请时，则应创建审批流程实例
  - 给定审批流程创建，当流程启动时，则应通知相关审批人
  - 给定审批人收到审批，当审批人处理时，则应记录审批意见并流转到下一节点
  - 给定审批完成，当流程结束时，则应通知申请人审批结果

sceneMapping:
  sceneType: APPROVAL
  requiredEngines:
    - WorkflowEngine
    - OrgEngine
    - MsgEngine
  requiredCapabilities:
    - flow.start
    - flow.query
    - approval.submit
    - approval.approve
    - approval.reject
    - notify.send

implementation:
  operations:
    - operation: startFlow
      capability: flow.start
      params:
        flowType: string (required)
        applicantId: string (required)
        formData: object (required)
    - operation: approve
      capability: approval.approve
      params:
        instanceId: string (required)
        approverId: string (required)
        comment: string (optional)
```

#### US-BIZ-003: 客户信息维护

```yaml
# 用户故事: 客户信息维护
id: US-BIZ-003
title: 客户信息维护
category: BUSINESS

story: |
  作为销售人员，我希望能够维护客户信息，以便跟进客户关系。

acceptanceCriteria:
  - 给定销售人员已登录，当创建客户信息时，则客户信息应成功保存
  - 给定客户信息存在，当更新客户状态时，则应记录状态变更历史
  - 给定客户有联系记录，当添加联系记录时，则应关联到客户信息
  - 给定查询客户，当按条件搜索时，则应返回匹配的客户列表

sceneMapping:
  sceneType: CRM
  requiredEngines:
    - OrgEngine
    - MsgEngine
    - VfsEngine
  requiredCapabilities:
    - customer.create
    - customer.update
    - customer.query
    - contact.create
    - contact.query

implementation:
  operations:
    - operation: createCustomer
      capability: customer.create
      params:
        name: string (required)
        industry: string (optional)
        address: string (optional)
        contacts: array (optional)
```

#### US-BIZ-004: 项目任务分配

```yaml
# 用户故事: 项目任务分配
id: US-BIZ-004
title: 项目任务分配
category: BUSINESS

story: |
  作为项目经理，我希望能够分配项目任务，以便推进项目进度。

acceptanceCriteria:
  - 给定项目经理已登录，当创建任务时，则任务应成功创建并通知负责人
  - 给定任务已分配，当负责人接受任务时，则应更新任务状态
  - 给定任务进行中，当更新进度时，则应记录进度变更
  - 给定任务完成，当标记完成时，则应通知项目经理

sceneMapping:
  sceneType: PROJECT
  requiredEngines:
    - OrgEngine
    - VfsEngine
    - WorkflowEngine
    - MsgEngine
  requiredCapabilities:
    - project.create
    - project.update
    - task.create
    - task.assign
    - task.complete
    - notify.send

implementation:
  operations:
    - operation: createTask
      capability: task.create
      params:
        projectId: string (required)
        taskName: string (required)
        assigneeId: string (required)
        dueDate: long (optional)
        priority: string (optional)
```

### 10.4 IoT类用户故事

#### US-IOT-001: 设备注册接入

```yaml
# 用户故事: 设备注册接入
id: US-IOT-001
title: 设备注册接入
category: IOT

story: |
  作为设备管理员，我希望能够注册新设备，以便将设备接入系统。

acceptanceCriteria:
  - 给定设备管理员已登录，当注册新设备时，则应生成设备ID和访问凭证
  - 给定设备已注册，当设备首次连接时，则应验证凭证并激活设备
  - 给定设备激活，当设备上线时，则应更新设备状态为在线
  - 给定设备离线，当设备断开连接时，则应更新设备状态为离线

sceneMapping:
  sceneType: DEVICE
  requiredEngines:
    - AgentEngine
    - MsgEngine
    - MonitorEngine
  requiredCapabilities:
    - device.register
    - device.activate
    - device.status
    - device.credential

implementation:
  operations:
    - operation: registerDevice
      capability: device.register
      params:
        deviceName: string (required)
        deviceType: string (required)
        manufacturer: string (optional)
        model: string (optional)
    - operation: activateDevice
      capability: device.activate
      params:
        deviceId: string (required)
        credential: object (required)
```

#### US-IOT-002: 设备远程控制

```yaml
# 用户故事: 设备远程控制
id: US-IOT-002
title: 设备远程控制
category: IOT

story: |
  作为设备管理者，我希望能够向设备发送控制命令，以便远程控制设备。

acceptanceCriteria:
  - 给定设备在线，当发送控制命令时，则设备应执行命令并返回结果
  - 给定设备离线，当发送控制命令时，则应存储命令等待设备上线后执行
  - 给定命令执行，当命令完成时，则应返回执行结果

sceneMapping:
  sceneType: DEVICE
  requiredEngines:
    - AgentEngine
    - MsgEngine
  requiredCapabilities:
    - device.command
    - device.status

implementation:
  channels:
    - type: COMMAND
      topic: "ooder/command/{deviceType}/{deviceId}/request"
  operations:
    - operation: sendCommand
      capability: device.command
      params:
        deviceId: string (required)
        command: string (required)
        params: object (optional)
        timeout: long (optional)
```

#### US-IOT-003: 数据实时采集

```yaml
# 用户故事: 数据实时采集
id: US-IOT-003
title: 数据实时采集
category: IOT

story: |
  作为数据分析师，我希望能够实时采集设备数据，以便进行数据分析。

acceptanceCriteria:
  - 给定设备在线，当设备上报数据时，则应实时接收并存储数据
  - 给定数据采集，当订阅设备数据时，则应实时推送数据更新
  - 给定数据存储，当查询历史数据时，则应返回指定时间范围的数据

sceneMapping:
  sceneType: COLLECTION
  requiredEngines:
    - MsgEngine
    - VfsEngine
    - MonitorEngine
  requiredCapabilities:
    - data.collect
    - data.store
    - data.query
    - data.subscribe

implementation:
  channels:
    - type: DATA
      topic: "ooder/data/{sceneId}/{dataType}"
  operations:
    - operation: subscribeData
      capability: data.subscribe
      params:
        deviceId: string (required)
        dataType: string (required)
        callback: function (required)
```

#### US-IOT-004: 告警通知

```yaml
# 用户故事: 告警通知
id: US-IOT-004
title: 告警通知
category: IOT

story: |
  作为运维人员，我希望能够收到设备告警通知，以便及时处理异常。

acceptanceCriteria:
  - 给定设备异常，当触发告警规则时，则应发送告警通知
  - 给定告警通知，当运维人员收到时，则应包含告警详情和处理建议
  - 给定告警处理，当告警解决时，则应更新告警状态为已处理

sceneMapping:
  sceneType: MONITOR
  requiredEngines:
    - MonitorEngine
    - MsgEngine
    - AgentEngine
  requiredCapabilities:
    - alert.create
    - alert.notify
    - alert.resolve
    - notify.send

implementation:
  operations:
    - operation: createAlert
      capability: alert.create
      params:
        deviceId: string (required)
        alertType: string (required)
        severity: string (required)
        message: string (required)
    - operation: resolveAlert
      capability: alert.resolve
      params:
        alertId: string (required)
        resolution: string (required)
```

### 10.5 协作类用户故事

#### US-COL-001: 在线会议

```yaml
# 用户故事: 在线会议
id: US-COL-001
title: 在线会议
category: COLLABORATION

story: |
  作为用户，我希望能够发起和参与在线会议，以便进行远程协作。

acceptanceCriteria:
  - 给定用户已登录，当发起会议时，则应创建会议并生成会议链接
  - 给定会议已创建，当邀请参会者时，则应发送会议邀请通知
  - 给定参会者加入会议，当进入会议室时，则应能够看到其他参会者
  - 给定会议进行中，当共享屏幕时，则其他参会者应能看到共享内容

sceneMapping:
  sceneType: MEETING
  requiredEngines:
    - MsgEngine
    - VfsEngine
    - AgentEngine
  requiredCapabilities:
    - meeting.create
    - meeting.join
    - meeting.invite
    - meeting.share
    - meeting.record

implementation:
  operations:
    - operation: createMeeting
      capability: meeting.create
      params:
        title: string (required)
        startTime: long (optional)
        duration: integer (optional)
        participants: array (optional)
```

#### US-COL-002: 文档协同编辑

```yaml
# 用户故事: 文档协同编辑
id: US-COL-002
title: 文档协同编辑
category: COLLABORATION

story: |
  作为团队成员，我希望能够协同编辑文档，以便团队共同完成文档编写。

acceptanceCriteria:
  - 给定用户已登录，当创建文档时，则应创建空白文档并设置权限
  - 给定文档已创建，当邀请协作者时，则应发送邀请通知
  - 给定多人编辑，当同时编辑时，则应实时同步编辑内容
  - 给定编辑完成，当保存文档时，则应创建新版本

sceneMapping:
  sceneType: DOCUMENT
  requiredEngines:
    - VfsEngine
    - OrgEngine
    - MsgEngine
  requiredCapabilities:
    - document.create
    - document.edit
    - document.share
    - document.version

implementation:
  operations:
    - operation: createDocument
      capability: document.create
      params:
        title: string (required)
        type: string (required)
        content: object (optional)
    - operation: editDocument
      capability: document.edit
      params:
        documentId: string (required)
        changes: object (required)
```

#### US-COL-003: 任务协作

```yaml
# 用户故事: 任务协作
id: US-COL-003
title: 任务协作
category: COLLABORATION

story: |
  作为团队成员，我希望能够协作完成任务，以便高效完成工作。

acceptanceCriteria:
