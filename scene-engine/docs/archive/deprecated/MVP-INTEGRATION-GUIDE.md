# SE 2.3.1 MVP 集成文档

## 概述

SceneEngine 2.3.1 版本新增了业务场景组管理功能，为 MVP 项目提供完整的场景组生命周期管理能力。

## 新增功能

### 1. SceneGroup（业务场景组）

**位置**: `net.ooder.scene.group.SceneGroup`

**功能**:

- 场景组生命周期管理（创建、激活、暂停、销毁）
- 参与者管理
- 能力绑定管理
- 知识库绑定管理
- 快照管理

**状态流转**:

```
CREATING → ACTIVE ⇄ SUSPENDED → DESTROYING → DESTROYED
```

### 2. Participant（参与者）

**位置**: `net.ooder.scene.participant.Participant`

**功能**:

- 参与者类型（USER、AGENT、SUPER\_AGENT）
- 角色管理（OWNER、MANAGER、EMPLOYEE、LLM\_ASSISTANT、COORDINATOR、OBSERVER）
- 状态管理（INVITED、JOINED、ACTIVE、LEFT、SUSPENDED、REMOVED）
- 心跳检测

### 3. CapabilityBinding（能力绑定）

**位置**: `net.ooder.scene.capability.CapabilityBinding`

**功能**:

- 能力绑定生命周期
- 提供者类型（AGENT、PLATFORM、EXTERNAL、HYBRID）
- 连接器类型（INTERNAL、EXTERNAL、HYBRID）
- 优先级和降级策略

### 4. KnowledgeBinding（知识库绑定）

**位置**: `net.ooder.scene.knowledge.KnowledgeBinding`

**功能**:

- 知识库绑定管理
- 知识库类型（DOCUMENT、DATABASE、GRAPH、VECTOR、HYBRID）
- 同步状态管理

### 5. SceneSnapshot（场景快照）

**位置**: `net.ooder.scene.snapshot.SceneSnapshot`

**功能**:

- 完整状态保存
- 快照恢复
- 快照类型（MANUAL、AUTO、SCHEDULED、BEFORE\_ACTION、SYSTEM）

### 6. SceneGroupManager（场景组管理器）

**位置**: `net.ooder.scene.group.SceneGroupManager`

**功能**:

- 场景组集中管理
- 心跳检测
- 状态维护

## MVP 集成方式

### 1. 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 2. 使用示例

```java
@Autowired
private SceneGroupManager sceneGroupManager;

// 创建场景组
SceneGroup sceneGroup = sceneGroupManager.createSceneGroup(
    "scene-group-001",
    "template-001",
    "user-001",
    SceneGroup.CreatorType.USER
);

// 设置基本信息
sceneGroup.setName("测试场景组");
sceneGroup.setDescription("这是一个测试场景组");

// 激活场景组
sceneGroupManager.activateSceneGroup("scene-group-001");

// 添加参与者
Participant participant = new Participant(
    "participant-001",
    "user-001",
    "张三",
    Participant.Type.USER
);
participant.setRole(Participant.Role.EMPLOYEE);
sceneGroupManager.addParticipant("scene-group-001", participant);

// 参与者加入
participant.join();
participant.activate();

// 添加能力绑定
CapabilityBinding binding = new CapabilityBinding(
    "binding-001",
    "scene-group-001",
    "cap-001"
);
binding.setCapName("测试能力");
binding.setProviderType(CapabilityBinding.ProviderType.PLATFORM);
binding.activate();
sceneGroup.addCapabilityBinding(binding);

// 添加知识库绑定
KnowledgeBinding kbBinding = new KnowledgeBinding(
    "kb-001",
    "scene-group-001"
);
kbBinding.setName("产品文档库");
kbBinding.setType(KnowledgeBinding.Type.DOCUMENT);
kbBinding.activate();
sceneGroup.addKnowledgeBinding(kbBinding);

// 创建快照
SceneSnapshot snapshot = new SceneSnapshot(
    "snapshot-001",
    "scene-group-001",
    SceneSnapshot.Type.MANUAL
);
snapshot.setName("初始状态快照");
snapshot.setParticipants(sceneGroup.getAllParticipants());
snapshot.setCapabilityBindings(sceneGroup.getAllCapabilityBindings());
snapshot.setKnowledgeBindings(sceneGroup.getAllKnowledgeBindings());
sceneGroup.addSnapshot(snapshot);
```

### 3. 查询场景组

```java
// 获取单个场景组
SceneGroup sceneGroup = sceneGroupManager.getSceneGroup("scene-group-001");

// 获取所有场景组
List<SceneGroup> allGroups = sceneGroupManager.getAllSceneGroups();

// 按模板查询
List<SceneGroup> templateGroups = sceneGroupManager.getSceneGroupsByTemplate("template-001");

// 获取参与者
Participant participant = sceneGroupManager.getParticipant("scene-group-001", "participant-001");

// 获取场景组内的参与者列表
List<Participant> participants = sceneGroup.getAllParticipants();

// 获取能力绑定列表
List<CapabilityBinding> bindings = sceneGroup.getAllCapabilityBindings();

// 获取知识库绑定列表
List<KnowledgeBinding> kbBindings = sceneGroup.getAllKnowledgeBindings();

// 获取快照列表
List<SceneSnapshot> snapshots = sceneGroup.getAllSnapshots();
```

### 4. 状态管理

```java
// 激活场景组
sceneGroupManager.activateSceneGroup("scene-group-001");

// 暂停场景组
sceneGroupManager.suspendSceneGroup("scene-group-001");

// 销毁场景组
sceneGroupManager.destroySceneGroup("scene-group-001");
```

### 5. 参与者管理

```java
// 移除参与者
sceneGroupManager.removeParticipant("scene-group-001", "participant-001");

// 参与者心跳
participant.heartbeat();

// 检查参与者是否在线
boolean isOnline = participant.isOnline();
```

## 与 SDK SceneGroup 的区别

| 特性  | SDK SceneGroup    | SE SceneGroup      |
| --- | ----------------- | ------------------ |
| 用途  | 场景集群高可用           | 业务场景组管理            |
| 成员  | SceneMember（集群节点） | Participant（业务参与者） |
| 功能  | 故障转移、主备管理         | 参与者管理、能力绑定、知识库绑定   |
| 快照  | 不支持               | 支持                 |
| 知识库 | 不支持               | 支持                 |

## 注意事项

1. **SE SceneGroup 与 SDK SceneGroup 是完全不同的概念**，不要混淆使用
2. **不需要实现任何 MVP 接口**，直接使用 SE 原生类即可
3. **不需要 DTO 转换**，SE 使用原生对象模型
4. **心跳检测**：参与者需要定期调用 `heartbeat()` 方法
5. **状态管理**：所有状态变更都是原子操作，线程安全

## 版本信息

- **版本**: 2.3.1
- **发布日期**: 2026-03-19
- **Maven 坐标**: `net.ooder:scene-engine:2.3.1`

