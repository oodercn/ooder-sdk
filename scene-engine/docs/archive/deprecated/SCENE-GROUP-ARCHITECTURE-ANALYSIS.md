# SceneGroup 架构深度分析

## 一、类层次结构

### 1.1 SDK 层

```
net.ooder.sdk.api.scene
├── SceneGroup                    # 高可用集群实体
│   ├── sceneGroupId: String
│   ├── sceneId: String
│   ├── members: List<SceneMember>    # 主/备成员
│   ├── key: SceneGroupKey            # 密钥管理
│   └── status: String
│
├── SceneGroupManager              # 高可用集群管理器接口
│   ├── create(sceneId, config)       # 创建集群
│   ├── destroy(sceneGroupId)         # 销毁集群
│   ├── join(sceneGroupId, agentId, role)  # 加入集群
│   ├── leave(sceneGroupId, agentId)  # 离开集群
│   ├── handleFailover(...)           # 故障转移
│   ├── generateKey(...)              # 生成密钥
│   └── distributeKeyShares(...)      # 分发密钥分片
│
├── SceneMember                     # 集群成员
│   ├── agentId: String
│   ├── role: MemberRole (PRIMARY/BACKUP)
│   ├── status: String
│   └── endpoint: String
│
├── SceneGroupKey                   # 集群密钥
└── KeyShare                        # 密钥分片

net.ooder.skills.api
├── SceneGroupManager              # Skills 框架场景组管理器
│   ├── createGroup(request)          # 创建场景组
│   ├── disbandGroup(groupId)         # 解散场景组
│   ├── addCollaborativeCapability(...)  # 添加协作能力
│   └── getGroupInfo(groupId)         # 获取场景组信息
│
└── SceneGroupInfo                  # 场景组信息
    ├── groupId: String
    ├── mainCapabilityId: String      # 主能力
    └── collaborativeCapabilities     # 协作能力列表

net.ooder.skills.sync
├── UserSceneGroup                  # 用户场景组接口
│   ├── addCollaborator(userId, role) # 添加协作者
│   ├── addSkill(skillId, config)     # 添加技能
│   ├── bindCapability(capId, config) # 绑定能力
│   ├── bindKnowledgeBase(kbId, layer)# 绑定知识库
│   ├── activate() / deactivate()     # 激活/停用
│   └── syncStateToSdk() / syncStateFromSdk()  # 双向同步
│
├── UserSceneGroupManager           # 用户场景组管理器
│   ├── createSceneGroup(...)         # 创建场景组
│   ├── getUserSceneGroup(groupId)    # 获取场景组
│   └── getSceneGroupsByUser(userId)  # 用户场景组列表
│
└── BidirectionalSyncCoordinator   # 双向同步协调器
```

### 1.2 SE 层

```
net.ooder.scene.group
├── SceneGroup                      # SE 场景组实体
│   ├── sceneGroupId: String
│   ├── templateId: String
│   ├── name: String
│   ├── description: String
│   ├── status: Status (CREATING/ACTIVE/SUSPENDED/ARCHIVED/DESTROYED)
│   ├── participants: List<Participant>    # 业务参与者
│   ├── capabilityBindings: List<CapabilityBinding>  # 能力绑定
│   ├── knowledgeBindings: List<KnowledgeBinding>    # 知识库绑定
│   ├── snapshots: List<SceneSnapshot>    # 快照
│   └── eventLog: List<SceneGroupEvent>   # 事件日志
│
├── SceneGroupManager               # SE 场景组管理器
│   ├── createSceneGroup(...)          # 创建场景组
│   ├── destroySceneGroup(...)         # 销毁场景组
│   ├── getSceneGroup(groupId)         # 获取场景组
│   ├── addParticipant(...)            # 添加参与者
│   ├── getEventLog(groupId, limit)    # 获取事件日志
│   └── getUserSceneGroups(userId)     # 用户场景组列表
│
├── SceneGroupEvent                 # 场景组事件
│   └── Type: CREATED/ACTIVATED/PARTICIPANT_JOINED/...
│
└── Participant                     # 参与者
    ├── participantId: String
    ├── userId: String
    ├── type: Type (USER/AGENT/SYSTEM)
    └── role: Role (OWNER/ADMIN/MEMBER/OBSERVER)

net.ooder.scene.bridge
├── SceneGroupBridge                # SDK-SE 桥接接口
│   ├── syncFromSdkToSe(groupId)       # SDK -> SE 同步
│   ├── syncFromSeToSdk(groupId)       # SE -> SDK 同步
│   └── healthCheck()                  # 健康检查
│
└── UserSceneGroup                  # 用户场景组关联
    ├── getSceneGroupId()
    ├── getParticipants()
    ├── addParticipant(...)
    ├── getCapabilityBindings()
    └── archive() / restore()
```

## 二、职责分层

```
┌─────────────────────────────────────────────────────────────────┐
│                      应用层 (Application)                        │
│  MVP / 业务系统                                                  │
│  └── 使用 UserSceneGroup 接口                                    │
├─────────────────────────────────────────────────────────────────┤
│                      SDK 层 (SDK)                                │
│  ┌──────────────────────┐  ┌──────────────────────┐            │
│  │ SceneGroupManager    │  │ UserSceneGroup       │            │
│  │ (高可用集群管理)      │  │ (用户场景组)          │            │
│  │ - 故障转移           │  │ - 协作者管理          │            │
│  │ - 密钥管理           │  │ - 技能/能力绑定       │            │
│  │ - 心跳检测           │  │ - 双向同步            │            │
│  └──────────────────────┘  └──────────────────────┘            │
│              │                        │                         │
│              └────────────┬───────────┘                         │
│                           ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              BidirectionalSyncCoordinator                │  │
│  │                     (双向同步协调器)                       │  │
│  └──────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                      桥接层 (Bridge)                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   SceneGroupBridge                       │  │
│  │              (SDK-SE 数据桥接)                            │  │
│  └──────────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                      SE 层 (Scene Engine)                        │
│  ┌──────────────────────┐  ┌──────────────────────┐            │
│  │ SceneGroup           │  │ SceneGroupManager    │            │
│  │ (业务场景组)          │  │ (场景组管理)          │            │
│  │ - 参与者管理          │  │ - 生命周期管理        │            │
│  │ - 能力绑定            │  │ - 持久化             │            │
│  │ - 知识库绑定          │  │ - 归档               │            │
│  │ - 快照/事件日志       │  │ - 事件日志           │            │
│  └──────────────────────┘  └──────────────────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

## 三、概念映射

| SDK 概念 | SE 概念 | 说明 |
|----------|---------|------|
| `SceneGroup` (高可用集群) | `SceneGroup` (业务场景组) | **不同概念！** |
| `SceneMember` (主/备成员) | `Participant` (参与者) | 角色映射不同 |
| `MemberRole.PRIMARY/BACKUP` | `Role.OWNER/ADMIN/MEMBER` | 角色体系不同 |
| `SceneGroupManager` (SDK) | `SceneGroupManager` (SE) | 职责不同 |
| `UserSceneGroup` (SDK) | `UserSceneGroup` (SE) | **职责相似，位置不同** |

## 四、UserSceneGroup 分析

### 4.1 SDK UserSceneGroup (`net.ooder.skills.sync.UserSceneGroup`)

**职责**: 用户视角的场景组管理，包含双向同步

```java
public interface UserSceneGroup {
    // 协作者管理
    Participant addCollaborator(String userId, Participant.Role role);
    void removeCollaborator(String userId);
    
    // 技能/能力绑定
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    CapabilityBinding bindCapability(String capId, Map<String, Object> config);
    KnowledgeBinding bindKnowledgeBase(String kbId, String layer);
    
    // 协作会话
    CollaborationSession startCollaboration(String type, List<String> participants);
    
    // 双向同步
    void syncStateToSdk();
    void syncStateFromSdk();
}
```

**特点**:
- 包含 `BidirectionalSyncCoordinator` 双向同步协调器
- 与 SDK `SceneGroupManager` 交互
- 支持协作会话 (CollaborationSession)

### 4.2 SE UserSceneGroup (`net.ooder.scene.bridge.UserSceneGroup`)

**职责**: 用户场景组关联，桥接 SDK 和 SE

```java
public class UserSceneGroup implements AutoCloseable {
    // 基础信息
    String getSceneGroupId();
    String getName();
    
    // 参与者管理
    List<Participant> getParticipants();
    Participant addParticipant(String userId, String name, Type type, Role role);
    
    // 能力/知识库绑定
    List<CapabilityBinding> getCapabilityBindings();
    CapabilityBinding addCapabilityBinding(String capId, String name, String type);
    List<KnowledgeBinding> getKnowledgeBindings();
    
    // 状态管理
    boolean activate();
    boolean suspend();
    
    // 归档
    ArchiveResult archive(String description);
    ArchiveResult restore(String archiveId);
    
    // 同步
    void syncFromSdk();
    void syncToSdk();
}
```

**特点**:
- 持有 SE `SceneGroup` 引用
- 通过 `SceneGroupBridge` 与 SDK 同步
- 支持归档/恢复

## 五、冲突分析

### 5.1 命名冲突

| 类名 | SDK | SE | 冲突程度 |
|------|-----|-----|----------|
| `SceneGroup` | 高可用集群 | 业务场景组 | 🔴 高 |
| `SceneGroupManager` | 集群管理 | 场景组管理 | 🔴 高 |
| `UserSceneGroup` | 用户场景组 | 用户场景组关联 | 🟡 中 |
| `Participant` | 协作者 | 参与者 | 🟢 低 (包名不同) |

### 5.2 职责重叠

| 职责 | SDK UserSceneGroup | SE UserSceneGroup | 重叠程度 |
|------|-------------------|-------------------|----------|
| 参与者管理 | ✅ | ✅ | 🔴 高 |
| 能力绑定 | ✅ | ✅ | 🔴 高 |
| 知识库绑定 | ✅ | ✅ | 🔴 高 |
| 双向同步 | ✅ (BidirectionalSyncCoordinator) | ✅ (SceneGroupBridge) | 🔴 高 |

## 六、建议方案

### 方案: 明确职责边界

**SDK 层**:
- `SceneGroup` → 重命名为 `HaCluster` (高可用集群)
- `SceneGroupManager` → 重命名为 `HaClusterManager`
- `UserSceneGroup` → 保留，作为用户视角的场景组接口

**SE 层**:
- `SceneGroup` → 保留，作为业务场景组
- `SceneGroupManager` → 保留，作为场景组管理器
- `UserSceneGroup` → 重命名为 `UserSceneGroupBinding` 或移除，改用 SDK 的 `UserSceneGroup`

**桥接层**:
- `SceneGroupBridge` → 保留，负责 SDK-SE 数据转换

### 映射关系

```
SDK 层                           SE 层
─────────────────────────────────────────────────
HaCluster (高可用集群)    ←→    SceneGroup (业务场景组)
  ├── members (主/备)            ├── participants (用户/Agent)
  ├── key (密钥)                 ├── capabilityBindings
  └── failover                   ├── knowledgeBindings
                                  └── snapshots

UserSceneGroup             ←→    UserSceneGroupBinding
  ├── collaborators               ├── sceneGroupId
  ├── skillBindings               ├── participants (引用)
  ├── capabilityBindings          └── bridge
  └── knowledgeBindings
```

## 七、状态

- [x] 架构分析完成
- [ ] 确定重命名方案
- [ ] 实施重构
- [ ] 更新文档

## 八、联系人

- SDK 团队
- SE 团队
