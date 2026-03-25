# SceneGroup 类命名冲突分析

## 问题发现

在 `ooder-sdk` 项目中存在多个 `SceneGroup` 类，命名冲突且职责重叠。

## 类清单

### SDK 侧

| 类 | 包路径 | 职责 |
|----|--------|------|
| `SceneGroup` | `net.ooder.sdk.api.scene.SceneGroup` | SDK 场景组 API，高可用集群管理 |
| `SceneGroupDef` | `net.ooder.sdk.capability.model.SceneGroupDef` | 能力模型中的场景组定义 |
| `SceneGroupManager` | `net.ooder.sdk.api.scene.SceneGroupManager` | SDK 场景组管理器接口 |
| `SceneGroupManager` | `net.ooder.skills.api.SceneGroupManager` | Skills 框架场景组管理器 |

### SE 侧

| 类 | 包路径 | 职责 |
|----|--------|------|
| `SceneGroup` | `net.ooder.scene.group.SceneGroup` | SE 原生场景组模型 |
| `SceneGroupManager` | `net.ooder.scene.group.SceneGroupManager` | SE 场景组管理器 |

## 职责分析

### SDK SceneGroup (`net.ooder.sdk.api.scene.SceneGroup`)

```java
/**
 * 场景组类
 * 
 * 场景组是场景内的高可用集群，包含多个成员（主成员和备份成员）。
 * 场景组提供故障转移、状态共享和密钥管理等功能。
 */
public class SceneGroup {
    private String sceneGroupId;
    private String sceneId;
    private List<SceneMember> members;  // 高可用成员
    private SceneGroupKey key;          // 密钥管理
    // ...
}
```

**职责**: SDK 层面的高可用集群管理

### SE SceneGroup (`net.ooder.scene.group.SceneGroup`)

```java
/**
 * 场景组
 * 
 * SE原生的场景组模型，用于管理一个完整的业务场景，
 * 包含参与者、能力绑定、知识库等。
 */
public class SceneGroup {
    private String sceneGroupId;
    private String templateId;
    private List<Participant> participants;    // 业务参与者
    private List<CapabilityBinding> capabilityBindings;  // 能力绑定
    private List<KnowledgeBinding> knowledgeBindings;    // 知识库绑定
    // ...
}
```

**职责**: SE 层面的业务场景管理

## 冲突分析

| 冲突点 | SDK SceneGroup | SE SceneGroup |
|--------|----------------|---------------|
| **概念** | 高可用集群 | 业务场景组 |
| **成员** | `SceneMember` (主/备) | `Participant` (用户/Agent) |
| **关注点** | 故障转移、密钥管理 | 业务流程、能力绑定 |
| **层级** | SDK 基础设施层 | SE 业务层 |

## 解决方案

### 方案1: 重命名 SDK 类

将 SDK 的 `SceneGroup` 重命名为 `HaCluster` 或 `FailoverGroup`:

```java
// SDK 侧
net.ooder.sdk.api.cluster.HaCluster          // 高可用集群
net.ooder.sdk.api.cluster.HaClusterManager   // 集群管理器

// SE 侧保持不变
net.ooder.scene.group.SceneGroup             // 场景组
net.ooder.scene.group.SceneGroupManager      // 场景组管理器
```

### 方案2: 使用不同包名前缀

```java
// SDK 侧
net.ooder.sdk.scene.SceneGroup               // SDK 场景组

// SE 侧
net.ooder.se.scene.SceneGroup                // SE 场景组
```

### 方案3: 明确职责边界

保持现有命名，但明确职责边界：

| 层级 | 类 | 职责 |
|------|-----|------|
| **SDK 层** | `SdkSceneGroup` | 高可用、故障转移 |
| **SE 层** | `SceneGroup` | 业务场景、能力绑定 |
| **桥接层** | `SceneGroupBridge` | SDK-SE 数据同步 |

## 建议

**推荐方案1**: 重命名 SDK 类为 `HaCluster`

理由：
1. 更准确反映 SDK 层的职责（高可用集群）
2. 避免与 SE 层概念混淆
3. 符合分层架构设计原则

## 状态

- [x] 问题分析完成
- [ ] 确定解决方案
- [ ] 实施重命名
- [ ] 更新文档

## 联系人

- SDK 团队
- SE 团队
