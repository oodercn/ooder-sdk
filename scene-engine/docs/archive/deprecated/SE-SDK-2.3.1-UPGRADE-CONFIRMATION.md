# SE SDK 2.3.1 升级计划 - 待确认事项回复

## 一、待确认事项回复

### 1.1 SceneGroupBridge 如何注入？

**回复**：通过 Spring Boot Auto-Configuration 自动注入

```java
// 方式1: 通过 @Autowired 注入
@Autowired
private SceneGroupBridge sceneGroupBridge;

// 方式2: 通过构造函数注入
@Service
public class SceneGroupService {
    private final SceneGroupBridge bridge;
    
    public SceneGroupService(SceneGroupBridge bridge) {
        this.bridge = bridge;
    }
}

// 方式3: 通过 SceneGroupManager 获取
@Autowired
private SceneGroupManager sceneGroupManager;

// SceneGroupManager 内部持有 SceneGroupBridge
```

**配置类**：`SdkSceneGroupAutoConfiguration`

```java
@Configuration
@ConditionalOnClass(SceneGroupBridge.class)
public class SdkSceneGroupAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SceneGroupBridge sceneGroupBridge(SceneGroupManager manager, 
                                              SdkSceneGroupProvider provider) {
        return new SceneGroupBridgeImpl(manager, provider);
    }
}
```

---

### 1.2 UserSceneGroup 查询接口？

**回复**：通过 SDK SceneManager 和 SceneGroupBridge 查询

```java
// 方式1: 通过 SDK SceneManager 查询
@Autowired
private SceneManager sceneManager;

public UserSceneGroup getUserSceneGroup(String sceneGroupId, String userId) {
    return sceneManager.getUserSceneGroup(sceneGroupId, userId).join();
}

public List<UserSceneGroup> getUserSceneGroups(String userId) {
    return sceneManager.getUserSceneGroups(userId).join();
}

// 方式2: 通过 SceneGroupBridge 查询
@Autowired
private SceneGroupBridge bridge;

public UserSceneGroup getUserSceneGroup(String sceneGroupId, String userId) {
    // 获取 SDK SceneGroup
    Object sdkGroup = bridge.getSdkSceneGroup(sceneGroupId);
    // 通过 SDK UserSceneGroup 接口查询
    // ...
}
```

**SE SDK 提供的接口**：

| 方法 | 说明 |
|------|------|
| `SceneManager.getUserSceneGroup(sceneGroupId, userId)` | 获取用户场景组 |
| `SceneManager.getUserSceneGroups(userId)` | 获取用户所有场景组 |
| `SceneGroup.getOrCreateUserSceneGroup(userId, role)` | 获取或创建用户场景组 |

---

### 1.3 CollaborativeGroupManager 与 SceneGroupManager 关系？

**回复**：职责分离，协作互补

```
┌─────────────────────────────────────────────────────────────┐
│                     应用层                                   │
│                                                             │
│  CollaborativeGroupManager (协作组管理器)                    │
│  ├── 职责：管理用户协作关系、协作会话                        │
│  ├── 功能：创建协作组、邀请成员、管理协作会话                │
│  └── 依赖：SceneGroupManager                                │
│                                                             │
│  SceneGroupManager (场景组管理器)                            │
│  ├── 职责：管理场景组生命周期、参与者、能力绑定              │
│  ├── 功能：创建/销毁场景组、管理参与者、管理 SeSceneGroup    │
│  └── 依赖：SDK SceneManager                                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**关系说明**：

| 维度 | CollaborativeGroupManager | SceneGroupManager |
|------|---------------------------|-------------------|
| **层级** | 应用层 | 引擎层 |
| **职责** | 用户协作关系 | 场景组生命周期 |
| **数据** | 协作组、协作会话 | 场景组、参与者、能力绑定 |
| **依赖** | 依赖 SceneGroupManager | 被 CollaborativeGroupManager 依赖 |

**使用示例**：

```java
// 创建协作组
CollaborativeGroup group = collaborativeGroupManager.createGroup(request);

// 关联到场景组
SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(group.getSceneGroupId());

// 开始协作会话
CollaborationSession session = userSceneGroup.startCollaboration(
    "brainstorm", 
    participantIds
);
```

---

### 1.4 CapabilityBindingService 事件监听机制？

**回复**：通过 Spring Event 和 SDK 事件监听器

```java
// 方式1: Spring Event 监听
@Component
public class CapabilityBindingEventListener {
    
    @EventListener
    public void onBindingCreated(CapabilityBindingCreatedEvent event) {
        // 处理绑定创建事件
        log.info("Capability bound: {}", event.getBindingId());
    }
    
    @EventListener
    public void onBindingRemoved(CapabilityBindingRemovedEvent event) {
        // 处理绑定移除事件
        log.info("Capability unbound: {}", event.getBindingId());
    }
}

// 方式2: SDK SceneGroupEventListener
@Component
public class SdkEventListener implements SceneGroupBridge.SceneGroupEventListener {
    
    @Override
    public void onMemberJoined(SceneMemberEvent event) {
        // 成员加入
    }
    
    @Override
    public void onMemberLeft(SceneMemberEvent event) {
        // 成员离开
    }
    
    @Override
    public void onRoleChanged(SceneMemberEvent event) {
        // 角色变更
    }
    
    @Override
    public void onStatusChanged(SceneGroupStatusEvent event) {
        // 状态变更
    }
    
    @Override
    public void onFailover(FailoverEvent event) {
        // 故障转移
    }
}

// 注册监听器
@Autowired
private SceneGroupBridge bridge;

@PostConstruct
public void init() {
    bridge.registerEventListener(new SdkEventListener());
}
```

**事件类型**：

| 事件 | 说明 | 触发时机 |
|------|------|----------|
| `CapabilityBindingCreatedEvent` | 能力绑定创建 | 调用 `bindCapability()` |
| `CapabilityBindingRemovedEvent` | 能力绑定移除 | 调用 `unbindCapability()` |
| `SceneMemberEvent` | 成员事件 | 加入/离开/角色变更 |
| `SceneGroupStatusEvent` | 状态事件 | 场景组状态变更 |
| `FailoverEvent` | 故障转移 | 主节点切换 |

---

## 二、MVP 升级建议

### 2.1 Phase 1 验证清单

| 任务 | 验证方法 | 状态 |
|------|----------|------|
| `SdkConfiguration` | 启动应用，检查 Bean 注入 | ✅ |
| `DiscoveryController` | 调用 `/discovery/local` API | ✅ |
| `SceneServiceImpl` | 调用场景相关 API | ✅ |
| `SceneGroupServiceSEImpl` | 调用场景组 API | 待验证 |

### 2.2 Phase 2 开发建议

| 任务 | 实现建议 |
|------|----------|
| 场景组桥接 | 创建 `SceneGroupBridgeController`，暴露桥接状态 API |
| 用户场景组 | 创建 `UserSceneGroupService`，封装 SDK 查询 |
| 协作组管理 | 创建 `CollaborativeGroupController`，管理协作组 |
| 能力绑定服务 | 更新绑定逻辑，使用 SDK `CapabilityBindingService` |

---

## 三、状态更新

- [x] SceneGroupBridge 注入方式确认
- [x] UserSceneGroup 查询接口确认
- [x] CollaborativeGroupManager 与 SceneGroupManager 关系确认
- [x] CapabilityBindingService 事件监听机制确认

**协作状态**: 🟢 SE SDK 接口已确认，可以继续开发

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**SE 团队**: SceneEngine Team
