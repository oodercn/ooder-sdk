# 场景激活 → 场景组创建闭环检查报告

## 1. 问题发现

### 1.1 核心问题

**场景激活时没有正确创建和存储场景组信息！**

### 1.2 问题详情

| 组件 | 使用的 SceneGroupManager | 创建的 SceneGroup | 用途 |
|------|--------------------------|------------------|------|
| `SceneGroupInitializer` | **SDK 的 SceneGroupManager** | **SDK 的 SceneGroup** | 集群高可用 |
| `SE SceneGroupManager` | SE 自己的 SceneGroupManager | **SE 的 SceneGroup** | 业务场景组 |

**问题**: 两者是**完全不同的概念**，没有关联！

---

## 2. 代码分析

### 2.1 SceneGroupInitializer 的实现

**文件**: `core/init/SceneGroupInitializer.java`

```java
// 导入的是 SDK 的 SceneGroupManager
import net.ooder.sdk.api.scene.SceneGroupManager;

public class SceneGroupInitializer {
    private final SceneGroupManager sceneGroupManager; // SDK 的
    
    private void activate(InitContext context) {
        // ...
        // 创建的是 SDK 的 SceneGroup（集群版）
        SceneGroup group = sceneGroupManager.create(config);
        // ...
    }
}
```

### 2.2 SE SceneGroupManager 的实现

**文件**: `group/SceneGroupManager.java`

```java
@Component
public class SceneGroupManager {
    // 这是 SE 自己的业务场景组管理器
    private final Map<String, SceneGroup> sceneGroups = new ConcurrentHashMap<>();
    
    public SceneGroup createSceneGroup(...) {
        // 创建的是 SE 的 SceneGroup（业务版）
    }
}
```

### 2.3 两个 SceneGroup 的区别

| 特性 | SDK SceneGroup | SE SceneGroup |
|------|----------------|---------------|
| **用途** | 场景集群高可用 | 业务场景组管理 |
| **成员** | SceneMember（集群节点） | Participant（业务参与者） |
| **功能** | 故障转移、主备管理 | 参与者管理、能力绑定、知识库绑定 |
| **快照** | 不支持 | 支持 |
| **知识库** | 不支持 | 支持 |

---

## 3. 闭环断裂分析

### 3.1 当前流程（有问题）

```
用户激活场景
    │
    ▼
SceneGroupInitializer.initialize()
    │
    ▼
sceneGroupManager.create()  ← SDK 的 SceneGroupManager
    │
    ▼
创建 SDK SceneGroup（集群版）
    │
    ▼
❌ 没有创建 SE SceneGroup（业务版）
    │
    ▼
❌ SE SceneGroupManager 中没有数据
    │
    ▼
❌ 用户故事中的业务功能无法使用
```

### 3.2 正确流程（应该这样）

```
用户激活场景
    │
    ▼
SceneGroupInitializer.initialize()
    │
    ├─────────────────────────────────────┐
    │                               │
    ▼                               ▼
SDK SceneGroup（集群版）         SE SceneGroup（业务版）
    │                               │
    │                               ▼
    │                        SE SceneGroupManager 存储
    │                               │
    └─────────────────────────────────────┘
                                    │
                                    ▼
                            用户故事中的业务功能可用
```

---

## 4. 解决方案

### 4.1 方案一：修改 SceneGroupInitializer（推荐）

**优点**: 最小改动，保持兼容性

**修改内容**:
1. 在 `SceneGroupInitializer` 中注入 SE 的 `SceneGroupManager`
2. 在 `activate()` 方法中同时创建 SE 的 SceneGroup

```java
public class SceneGroupInitializer {
    private final net.ooder.sdk.api.scene.SceneGroupManager sdkSceneGroupManager;
    private final net.ooder.scene.group.SceneGroupManager seSceneGroupManager; // 新增
    
    private void activate(InitContext context) {
        // ... 原有逻辑 ...
        
        // 创建 SDK SceneGroup（集群版）
        SceneGroup sdkGroup = sdkSceneGroupManager.create(config);
        
        // 新增：创建 SE SceneGroup（业务版）
        net.ooder.scene.group.SceneGroup seGroup = seSceneGroupManager.createSceneGroup(
            sdkGroup.getSceneGroupId(),
            request.getSceneId(),
            request.getUserId(),
            SceneGroup.CreatorType.USER
        );
        
        // 同步参与者
        for (AgentConfig agent : request.getAgentConfigs()) {
            Participant participant = new Participant(
                UUID.randomUUID().toString(),
                agent.getUserId(),
                agent.getUserId(),
                Participant.Type.USER
            );
            participant.setRole(convertRole(agent.getRole()));
            seGroup.addParticipant(participant);
        }
        
        // 激活 SE SceneGroup
        seGroup.activate();
        
        // ...
    }
}
```

### 4.2 方案二：统一 SceneGroup 模型

**优点**: 架构更清晰

**缺点**: 改动较大，需要重构

**修改内容**:
1. 扩展 SDK 的 SceneGroup 支持业务功能
2. 废弃 SE 自己的 SceneGroup

---

## 5. 接口可用性检查

### 5.1 SE SceneGroupManager 接口

| 接口 | 状态 | 说明 |
|------|------|------|
| `createSceneGroup()` | ✅ 可用 | 创建业务场景组 |
| `getSceneGroup()` | ✅ 可用 | 获取场景组 |
| `activateSceneGroup()` | ✅ 可用 | 激活场景组 |
| `suspendSceneGroup()` | ✅ 可用 | 暂停场景组 |
| `destroySceneGroup()` | ✅ 可用 | 销毁场景组 |
| `addParticipant()` | ✅ 可用 | 添加参与者 |
| `removeParticipant()` | ✅ 可用 | 移除参与者 |

### 5.2 问题

**SE SceneGroupManager 没有被正确调用！**

因为 `SceneGroupInitializer` 使用的是 SDK 的 `SceneGroupManager`，而不是 SE 的。

---

## 6. 总结

### 6.1 问题

1. ❌ 场景激活时没有创建 SE 的业务场景组
2. ❌ SE SceneGroupManager 中没有数据
3. ❌ 用户故事中的业务功能（推送、提醒、日志）无法使用

### 6.2 建议

**优先级 P0**: 修改 `SceneGroupInitializer`，在激活时同时创建 SE 的业务场景组

**工作量**: 2 人天

---

**报告日期**: 2026-03-19  
**报告版本**: SceneEngine 2.3.1
