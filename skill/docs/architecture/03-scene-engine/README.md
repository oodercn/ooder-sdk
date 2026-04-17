# 第三册：SceneEngine 场景引擎

## 目录

1. [SceneEngine 架构定位](#1-sceneengine-架构定位)
2. [场景生命周期管理](#2-场景生命周期管理)
3. [场景编排与协调](#3-场景编排与协调)
4. [事件驱动架构](#4-事件驱动架构)
5. [与 Agent SDK 的集成](#5-与-agent-sdk-的集成)
6. [状态机与数据流](#6-状态机与数据流)

---

## 1. SceneEngine 架构定位

### 1.1 在整体架构中的位置

```
┌─────────────────────────────────────────┐
│           用户交互层                      │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │   CLI   │ │llm-chat │ │ Web UI  │   │
│  └────┬────┘ └────┬────┘ └────┬────┘   │
└───────┼───────────┼───────────┼────────┘
        │           │           │
        └───────────┴───────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Agent SDK 协议层               │
│  ┌─────────────────────────────────┐   │
│  │  - 无状态原子操作                 │   │
│  │  - 命令通道                       │   │
│  │  - 异步任务                       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│  ┌─────────────────────────────────┐   │
│  │      SceneEngine 场景层          │   │
│  │  ┌─────────────────────────┐   │   │
│  │  │  - 有状态场景管理         │   │   │
│  │  │  - 场景级编排             │   │   │
│  │  │  - 参与者协调             │   │   │
│  │  │  - 事件驱动               │   │   │
│  │  └─────────────────────────┘   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Skill 运行时层                 │
│  ┌─────────────────────────────────┐   │
│  │  - 插件管理                       │   │
│  │  - 服务注册                       │   │
│  │  - 路由管理                       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 1.2 核心职责

| 职责 | 说明 | 与 Agent SDK 的区别 |
|------|------|---------------------|
| 场景管理 | 创建、激活、暂停、关闭场景 | Agent SDK 无场景概念 |
| 状态维护 | 维护场景状态、参与者状态 | Agent SDK 无状态 |
| 编排协调 | 协调多个 Skill 协作 | Agent SDK 原子操作 |
| 事件驱动 | 场景内事件发布订阅 | Agent SDK 消息队列 |

---

## 2. 场景生命周期管理

### 2.1 场景状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           场景状态机                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌─────────┐    create     ┌─────────┐    activate    ┌─────────┐        │
│    │  INIT   │──────────────▶│ CREATED │───────────────▶│ ACTIVE  │        │
│    └─────────┘               └────┬────┘                └────┬────┘        │
│         ▲                         │                         │              │
│         │                         │ deactivate              │              │
│         │                         ▼                         │              │
│         │                    ┌─────────┐                   │              │
│         │                    │INACTIVE │◀──────────────────┘              │
│         │                    └────┬────┘                                  │
│         │                         │ activate                               │
│         │                         ▼                                        │
│         │                    ┌─────────┐                                   │
│         └────────────────────│  CLOSED │◀──────────────────┐              │
│              close           └─────────┘                   │              │
│                                                            │              │
│                                                            │ close        │
│                                                            │              │
│         ┌──────────────────────────────────────────────────┘              │
│         │                                                                 │
│         ▼                                                                 │
│    ┌─────────┐                                                            │
│    │ARCHIVED │                                                            │
│    └─────────┘                                                            │
│                                                                             │
│  状态说明：                                                                 │
│  - INIT: 初始状态                                                           │
│  - CREATED: 已创建，等待激活                                                 │
│  - ACTIVE: 激活状态，可执行业务                                               │
│  - INACTIVE: 暂停状态，可恢复                                                │
│  - CLOSED: 已关闭，不可恢复                                                  │
│  - ARCHIVED: 已归档，仅保留历史                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心接口

```java
/**
 * 场景上下文 API
 * 基于代码分析的实际接口
 */
public interface SceneContextApi {
    
    /**
     * 获取场景组 ID
     */
    String getSceneGroupId();
    
    /**
     * 获取场景类型
     */
    String getSceneType();
    
    /**
     * 获取当前 Skill ID
     */
    String getSkillId();
    
    /**
     * 获取配置
     */
    Object getConfig(String key);
    
    /**
     * 设置配置
     */
    void setConfig(String key, Object value);
    
    /**
     * 调用能力
     */
    Object invokeCapability(String capabilityId, Map<String, Object> params);
    
    /**
     * 获取参与者列表
     */
    List<ParticipantInfo> getParticipants();
    
    /**
     * 发布事件
     */
    void publishEvent(String eventType, Map<String, Object> data);
    
    // ===== 建议扩展的方法 =====
    
    /**
     * 获取场景状态
     */
    SceneStatus getStatus();
    
    /**
     * 获取当前激活的 Skills
     */
    List<String> getActiveSkills();
    
    /**
     * 检查 Skill 是否激活
     */
    boolean isSkillActive(String skillId);
    
    /**
     * 在场景内执行命令（与 Agent SDK 集成）
     */
    CommandResult executeCommand(String commandId, Map<String, Object> params);
    
    /**
     * 获取可用的 NLP 能力
     */
    List<NlpCapability> getAvailableNlpCapabilities();
}

/**
 * 场景状态
 */
public enum SceneStatus {
    INIT,       // 初始
    CREATED,    // 已创建
    ACTIVE,     // 激活
    INACTIVE,   // 暂停
    CLOSED,     // 已关闭
    ARCHIVED    // 已归档
}
```

### 2.3 生命周期回调

```java
/**
 * 场景生命周期监听器
 */
public interface SceneLifecycleListener {
    
    /**
     * 场景创建时
     */
    void onSceneCreated(SceneContext context);
    
    /**
     * 场景激活时
     */
    void onSceneActivated(SceneContext context);
    
    /**
     * 场景暂停时
     */
    void onSceneDeactivated(SceneContext context);
    
    /**
     * 场景关闭时
     */
    void onSceneClosed(SceneContext context);
    
    /**
     * Skill 绑定时
     */
    void onSkillBound(SceneContext context, String skillId);
    
    /**
     * Skill 解绑时
     */
    void onSkillUnbound(SceneContext context, String skillId);
}
```

---

## 3. 场景编排与协调

### 3.1 编排模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          场景编排模型                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                           场景 (Scene)                               │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │ Participant │  │ Participant │  │ Participant │  │ Participant │ │   │
│  │  │   (Leader)  │  │(Collaborator)│  │(Collaborator)│  │(Collaborator)│ │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘ │   │
│  │         │                │                │                │        │   │
│  │         └────────────────┴────────────────┴────────────────┘        │   │
│  │                                    │                                 │   │
│  │                                    ▼                                 │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                    能力编排层 (Orchestration)                 │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │   │   │
│  │  │  │ Capability  │  │ Capability  │  │ Capability  │          │   │   │
│  │  │  │  (rag-skill)│  │(chart-skill)│  │ (db-skill)  │          │   │   │
│  │  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘          │   │   │
│  │  │         │                │                │                 │   │   │
│  │  │         └────────────────┴────────────────┘                 │   │   │
│  │  │                           │                                 │   │   │
│  │  │                           ▼                                 │   │   │
│  │  │                    ┌─────────────┐                          │   │   │
│  │  │                    │  编排引擎    │                          │   │   │
│  │  │                    │(Saga/Workflow)│                         │   │   │
│  │  │                    └─────────────┘                          │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 能力绑定

```java
/**
 * 能力绑定服务
 * 基于代码分析的实际接口
 */
public interface CapabilityBindingService {
    
    /**
     * 绑定能力到场景
     */
    BindResult bindCapability(String sceneGroupId, String capabilityId, 
                              BindConfiguration config);
    
    /**
     * 解绑能力
     */
    void unbindCapability(String sceneGroupId, String capabilityId);
    
    /**
     * 获取场景绑定的能力
     */
    List<BoundCapability> getBoundCapabilities(String sceneGroupId);
    
    /**
     * 激活能力
     */
    void activateCapability(String sceneGroupId, String capabilityId);
    
    /**
     * 停用能力
     */
    void deactivateCapability(String sceneGroupId, String capabilityId);
}

/**
 * 绑定配置
 */
public interface BindConfiguration {
    
    /**
     * 绑定角色
     */
    BindRole getBindRole();
    
    /**
     * 配置参数
     */
    Map<String, Object> getParameters();
    
    /**
     * 依赖的能力
     */
    List<String> getDependencies();
}

/**
 * 绑定角色
 */
public enum BindRole {
    PRIMARY,      // 主能力
    SECONDARY,    // 辅助能力
    OPTIONAL      // 可选能力
}
```

### 3.3 编排流程示例

```java
/**
 * 场景编排示例：数据分析场景
 */
public class DataAnalysisSceneOrchestration {
    
    /**
     * 编排流程：
     * 1. 从 rag-skill 查询知识
     * 2. 从 db-skill 查询数据
     * 3. 使用 chart-skill 生成图表
     * 4. 使用 llm-skill 生成分析报告
     */
    public AnalysisReport executeDataAnalysis(SceneContext context, 
                                               AnalysisRequest request) {
        
        // 1. 并行查询知识和数据
        CompletableFuture<KnowledgeResult> knowledgeFuture = 
            invokeAsync(context, "rag-skill:search", request.getQuery());
        
        CompletableFuture<DataResult> dataFuture = 
            invokeAsync(context, "db-skill:query", request.getSql());
        
        // 2. 等待结果
        CompletableFuture.allOf(knowledgeFuture, dataFuture).join();
        
        KnowledgeResult knowledge = knowledgeFuture.join();
        DataResult data = dataFuture.join();
        
        // 3. 生成图表
        ChartResult chart = invoke(context, "chart-skill:generate", 
            Map.of("data", data, "type", request.getChartType()));
        
        // 4. 生成分析报告
        AnalysisReport report = invoke(context, "llm-skill:analyze",
            Map.of("knowledge", knowledge, "data", data, "chart", chart));
        
        return report;
    }
}
```

---

## 4. 事件驱动架构

### 4.1 事件总线设计

```java
/**
 * 场景事件总线
 */
public interface SceneEventBus {
    
    /**
     * 发布事件
     */
    void publish(SceneEvent event);
    
    /**
     * 订阅事件
     */
    Subscription subscribe(String eventType, EventHandler handler);
    
    /**
     * 订阅事件（带过滤）
     */
    Subscription subscribe(String eventType, EventFilter filter, EventHandler handler);
    
    /**
     * 取消订阅
     */
    void unsubscribe(Subscription subscription);
}

/**
 * 场景事件
 */
public interface SceneEvent {
    
    String getEventId();
    
    String getEventType();
    
    String getSceneGroupId();
    
    String getSourceSkillId();
    
    Map<String, Object> getData();
    
    long getTimestamp();
}

/**
 * 事件处理器
 */
public interface EventHandler {
    void handle(SceneEvent event);
}
```

### 4.2 事件类型

```java
/**
 * 标准场景事件类型
 */
public final class SceneEventTypes {
    
    // 生命周期事件
    public static final String SCENE_CREATED = "scene.created";
    public static final String SCENE_ACTIVATED = "scene.activated";
    public static final String SCENE_DEACTIVATED = "scene.deactivated";
    public static final String SCENE_CLOSED = "scene.closed";
    
    // 参与者事件
    public static final String PARTICIPANT_JOINED = "participant.joined";
    public static final String PARTICIPANT_LEFT = "participant.left";
    public static final String PARTICIPANT_ROLE_CHANGED = "participant.role_changed";
    
    // 能力事件
    public static final String CAPABILITY_BOUND = "capability.bound";
    public static final String CAPABILITY_UNBOUND = "capability.unbound";
    public static final String CAPABILITY_ACTIVATED = "capability.activated";
    public static final String CAPABILITY_DEACTIVATED = "capability.deactivated";
    
    // 任务事件
    public static final String TASK_CREATED = "task.created";
    public static final String TASK_STARTED = "task.started";
    public static final String TASK_COMPLETED = "task.completed";
    public static final String TASK_FAILED = "task.failed";
    
    // 消息事件
    public static final String MESSAGE_RECEIVED = "message.received";
    public static final String MESSAGE_SENT = "message.sent";
}
```

### 4.3 事件处理流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          事件处理流程                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐                                                            │
│  │  事件源      │                                                            │
│  │ (Skill/场景) │                                                            │
│  └──────┬──────┘                                                            │
│         │ publishEvent                                                       │
│         ▼                                                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      SceneEventBus                                   │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │   │
│  │  │   Event Queue   │  │  Event Router   │  │  Event Handlers │      │   │
│  │  │   (消息队列)     │  │   (路由分发)     │  │   (处理器集合)   │      │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│         │                                                                   │
│         ├──────────────────┬──────────────────┐                            │
│         ▼                  ▼                  ▼                            │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                    │
│  │ Handler 1   │    │ Handler 2   │    │ Handler 3   │                    │
│  │ (日志记录)   │    │ (状态更新)   │    │ (通知推送)   │                    │
│  └─────────────┘    └─────────────┘    └─────────────┘                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. 与 Agent SDK 的集成

### 5.1 集成架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      SceneEngine 与 Agent SDK 集成                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      SceneEngine 层                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  场景编排                                                     │   │   │
│  │  │  - 场景状态管理                                                │   │   │
│  │  │  - 能力绑定/解绑                                               │   │   │
│  │  │  - 事件发布/订阅                                               │   │   │
│  │  └──────────────────────────┬──────────────────────────────────┘   │   │
│  └─────────────────────────────┼──────────────────────────────────────┘   │
│                                │                                           │
│                                ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      集成适配层 (Integration Adapter)                │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  - SceneCommandAdapter: 场景命令转 Agent SDK Command         │   │   │
│  │  │  - TaskSyncManager: 任务状态同步                            │   │   │
│  │  │  - EventBridge: 事件桥接                                    │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                │                                           │
│                                ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Agent SDK 层                                    │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  - Command 执行                                               │   │   │
│  │  │  - 异步任务队列                                               │   │   │
│  │  │  - 消息通信                                                   │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 集成适配器

```java
/**
 * 场景命令适配器
 * 将 SceneEngine 的场景操作转换为 Agent SDK Command
 */
public class SceneCommandAdapter {
    
    @Autowired
    private CommandExecutor commandExecutor;
    
    /**
     * 在场景内执行命令
     */
    public CommandResult executeInScene(String sceneGroupId, String commandId, 
                                        Map<String, Object> params) {
        // 1. 获取场景上下文
        SceneContext sceneContext = sceneContextManager.get(sceneGroupId);
        
        // 2. 构建 CommandContext
        CommandContext commandContext = CommandContext.builder()
            .sceneContext(sceneContext)
            .parameters(params)
            .build();
        
        // 3. 获取 Command
        Command<?> command = commandRegistry.get(commandId);
        
        // 4. 执行
        return commandExecutor.execute(command, commandContext);
    }
    
    /**
     * 异步执行场景命令
     */
    public TaskId executeInSceneAsync(String sceneGroupId, String commandId,
                                       Map<String, Object> params,
                                       TaskStatusListener listener) {
        // 1. 创建任务
        Task task = Task.builder()
            .commandId(commandId)
            .context(buildContext(sceneGroupId, params))
            .statusListener(listener)
            .build();
        
        // 2. 提交到任务队列
        return taskQueue.submit(task);
    }
}

/**
 * 事件桥接器
 * 将 Agent SDK 消息转换为 SceneEngine 事件
 */
public class EventBridge {
    
    @Autowired
    private SceneEventBus eventBus;
    
    /**
     * 桥接消息到事件
     */
    public void bridge(UnifiedMessage message) {
        if (message.getType() == MessageType.COMMAND_RESULT) {
            // 命令结果消息 -> 任务完成事件
            SceneEvent event = SceneEvent.builder()
                .eventType(SceneEventTypes.TASK_COMPLETED)
                .sceneGroupId(extractSceneId(message))
                .sourceSkillId(extractSkillId(message))
                .data(message.getContent())
                .build();
            
            eventBus.publish(event);
        }
    }
}
```

---

## 6. 状态机与数据流

### 6.1 完整数据流

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      完整数据流图                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  用户请求                                                                    │
│     │                                                                       │
│     ▼                                                                       │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │   CLI/      │───▶│   Agent     │───▶│   Scene     │───▶│   Skill     │  │
│  │   Chat      │    │   SDK       │    │   Engine    │    │   Runtime   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│       │                  │                  │                  │           │
│       │                  │                  │                  │           │
│       ▼                  ▼                  ▼                  ▼           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  输入解析    │    │  命令转换    │    │  场景编排    │    │  原子执行    │  │
│  │  - 命令解析  │    │  - 协议封装  │    │  - 状态管理  │    │  - 服务调用  │  │
│  │  - 参数校验  │    │  - 异步转换  │    │  - 事件驱动  │    │  - 资源操作  │  │
│  │  - 权限检查  │    │  - 任务创建  │    │  - 能力协调  │    │  - 结果返回  │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│       │                  │                  │                  │           │
│       │                  │                  │                  │           │
│       └──────────────────┴──────────────────┴──────────────────┘           │
│                                    │                                       │
│                                    ▼                                       │
│                              ┌─────────────┐                               │
│                              │   结果聚合    │                               │
│                              │   响应返回    │                               │
│                              └─────────────┘                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 状态同步机制

```java
/**
 * 场景状态同步器
 * 确保 SceneEngine 状态与 Agent SDK 任务状态一致
 */
public class SceneStateSynchronizer {
    
    /**
     * 同步任务状态到场景
     */
    public void syncTaskState(String sceneGroupId, TaskId taskId, TaskStatus status) {
        SceneContext scene = sceneContextManager.get(sceneGroupId);
        
        switch (status) {
            case RUNNING:
                scene.publishEvent(SceneEventTypes.TASK_STARTED, 
                    Map.of("taskId", taskId));
                break;
            case COMPLETED:
                scene.publishEvent(SceneEventTypes.TASK_COMPLETED,
                    Map.of("taskId", taskId));
                break;
            case FAILED:
                scene.publishEvent(SceneEventTypes.TASK_FAILED,
                    Map.of("taskId", taskId));
                break;
        }
    }
}
```

---

## 下一册预告

**第四册：CLI 设计实现**

将深入探讨：
- CLI 命令体系设计
- 与 Agent SDK 的集成
- 与 SceneEngine 的集成
- 命令透传安全机制

请继续阅读第四册了解 CLI 的设计实现。
