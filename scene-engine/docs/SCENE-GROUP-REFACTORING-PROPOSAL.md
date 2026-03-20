# SceneGroup 架构重构方案分析

## 一、核心理解

### 1.1 SceneGroup 的本质

**SceneGroup 是 Scene 场景技能的运行时实例**，而不是简单的"普通节点"。

```
Scene (场景技能定义)
   │
   ├── sceneId: String
   ├── capabilities: List<Capability>
   ├── skills: List<Skill>
   └── config: SceneConfig
        │
        ▼
SceneGroup (场景组实例) ← Scene 的运行时实例化
   │
   ├── sceneGroupId: String
   ├── sceneId: String (关联到 Scene)
   ├── members: List<SceneMember> (Agent 集群)
   ├── key: SceneGroupKey (密钥管理)
   └── sharedState: Map (运行时状态)
```

### 1.2 当前架构问题

```
┌─────────────────────────────────────────────────────────────┐
│ 当前架构                                                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  SDK 层                                                      │
│  ┌─────────────────┐                                        │
│  │ SceneGroup      │ ← 高可用集群（Agent 视角）              │
│  │ - members       │                                        │
│  │ - failover      │                                        │
│  │ - key           │                                        │
│  └─────────────────┘                                        │
│         │                                                   │
│         ▼                                                   │
│  ┌─────────────────┐                                        │
│  │ UserSceneGroup  │ ← 用户视角（有状态）                    │
│  │ - collaborators │                                        │
│  │ - skillBindings │                                        │
│  │ - syncToSdk()   │                                        │
│  └─────────────────┘                                        │
│                                                             │
│  SE 层                                                       │
│  ┌─────────────────┐                                        │
│  │ SceneGroup      │ ← 业务场景组（重复定义！）              │
│  │ - participants  │                                        │
│  │ - capabilities  │                                        │
│  │ - knowledge     │                                        │
│  └─────────────────┘                                        │
│         │                                                   │
│         ▼                                                   │
│  ┌─────────────────┐                                        │
│  │ UserSceneGroup  │ ← 用户关联（重复定义！）                │
│  │ - participants  │                                        │
│  │ - capabilities  │                                        │
│  └─────────────────┘                                        │
│                                                             │
│  问题：两层 SceneGroup 职责重叠，概念混淆                     │
└─────────────────────────────────────────────────────────────┘
```

## 二、重构方案

### 2.1 方案概述

**核心思路**：
1. **SceneGroup 下放到 SDK 层** - 作为 Scene 的运行时实例，管理 Agent 集群
2. **SDK 层无状态设计** - SceneGroup 只管理 Agent 相关的高可用、故障转移
3. **UserSceneGroup 有状态设计** - 包含用户参与、业务状态、能力绑定等

```
┌─────────────────────────────────────────────────────────────┐
│ 重构后架构                                                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  SDK 层 (无状态)                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ SceneGroup (Scene 运行时实例)                        │   │
│  │ ├── sceneGroupId: String                            │   │
│  │ ├── sceneId: String ← 关联到 Scene                  │   │
│  │ ├── members: List<SceneMember> ← Agent 集群         │   │
│  │ ├── key: SceneGroupKey ← 密钥管理                   │   │
│  │ ├── failover: FailoverConfig ← 故障转移             │   │
│  │ └── sharedState: Map ← 运行时共享状态               │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  SDK 层 (有状态 - 用户参与)                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ UserSceneGroup (用户场景组)                          │   │
│  │ ├── sceneGroupId: String                            │   │
│  │ ├── userId: String ← 用户标识                       │   │
│  │ ├── role: Participant.Role ← 用户角色               │   │
│  │ ├── skillBindings: List<SkillBinding> ← 技能绑定    │   │
│  │ ├── capabilityBindings: List<CapabilityBinding>     │   │
│  │ ├── knowledgeBindings: List<KnowledgeBinding>       │   │
│  │ ├── personalContext: Map ← 个人上下文               │   │
│  │ └── notifications: List<Notification> ← 通知        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  SE 层 (业务引擎)                                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ SceneGroupBridge (桥接层)                           │   │
│  │ ├── syncFromSdkToSe() ← SDK -> SE 同步              │   │
│  │ ├── syncFromSeToSdk() ← SE -> SDK 同步              │   │
│  │ └── healthCheck() ← 健康检查                        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  SE 层不再有 SceneGroup 实体，只保留桥接层                    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 职责划分

| 层级 | 组件 | 职责 | 状态 |
|------|------|------|------|
| **SDK 层** | `SceneGroup` | Scene 运行时实例、Agent 集群管理、高可用、故障转移 | 无状态 |
| **SDK 层** | `UserSceneGroup` | 用户参与、技能绑定、能力绑定、知识库绑定、个人上下文 | 有状态 |
| **SE 层** | `SceneGroupBridge` | SDK-SE 数据桥接、事件转发 | 无状态 |

### 2.3 类设计

#### SDK SceneGroup (无状态)

```java
package net.ooder.sdk.api.scene;

/**
 * 场景组 - Scene 的运行时实例
 * 
 * <p>管理 Agent 集群，提供高可用、故障转移、密钥管理等功能。</p>
 * <p>无状态设计：不包含用户相关状态。</p>
 */
public class SceneGroup {
    
    // ========== 基础信息 ==========
    private final String sceneGroupId;
    private final String sceneId;           // 关联到 Scene
    private String status;
    
    // ========== Agent 集群 ==========
    private List<SceneMember> members;      // Agent 成员列表
    private SceneGroupKey key;              // 集群密钥
    
    // ========== 高可用配置 ==========
    private int maxMembers;
    private int heartbeatInterval;
    private int heartbeatTimeout;
    private boolean autoFailover;
    
    // ========== 运行时状态 ==========
    private Map<String, Object> sharedState; // Agent 间共享状态
    
    // ========== 故障转移 ==========
    private String previousPrimaryId;
    private int failoverCount;
    
    // ========== 方法 ==========
    public SceneMember getPrimary();
    public List<SceneMember> getBackups();
    public void join(String agentId, MemberRole role);
    public void leave(String agentId);
    public void handleFailover(String failedAgentId);
    public SceneGroupKey generateKey();
    public void distributeKeyShares(SceneGroupKey key);
}
```

#### SDK UserSceneGroup (有状态)

```java
package net.ooder.skills.sync;

/**
 * 用户场景组 - 用户参与的场景组
 * 
 * <p>包含用户相关的状态：技能绑定、能力绑定、知识库绑定、个人上下文。</p>
 * <p>有状态设计：每个用户-场景组组合一个实例。</p>
 */
public interface UserSceneGroup {
    
    // ========== 基础信息 ==========
    String getSceneGroupId();
    String getSceneId();
    String getUserId();
    Participant.Role getRole();
    
    // ========== 协作者管理 ==========
    Participant addCollaborator(String userId, Participant.Role role);
    void removeCollaborator(String userId);
    List<Participant> getCollaborators();
    
    // ========== 技能绑定 ==========
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    void removeSkill(String skillId);
    List<SkillBinding> getSkills();
    
    // ========== 能力绑定 ==========
    CapabilityBinding bindCapability(String capId, Map<String, Object> config);
    void unbindCapability(String bindingId);
    List<CapabilityBinding> getCapabilityBindings();
    
    // ========== 知识库绑定 ==========
    KnowledgeBinding bindKnowledgeBase(String kbId, String layer);
    void unbindKnowledgeBase(String kbId);
    List<KnowledgeBinding> getKnowledgeBaseBindings();
    
    // ========== 个人上下文 ==========
    Map<String, Object> getPersonalContext();
    void setPersonalContext(String key, Object value);
    
    // ========== 通知 ==========
    List<Notification> getNotifications();
    void markNotificationRead(String notificationId);
    
    // ========== 同步 ==========
    void syncToSceneGroup();    // 同步到 SceneGroup
    void syncFromSceneGroup();  // 从 SceneGroup 同步
}
```

#### SE SceneGroupBridge (桥接层)

```java
package net.ooder.scene.bridge;

/**
 * SDK-SE 场景组桥接
 * 
 * <p>负责 SDK SceneGroup 与 SE 业务层的数据同步。</p>
 */
public interface SceneGroupBridge {
    
    // ========== 数据转换 ==========
    Participant createParticipantFromMember(SceneMemberInfo member);
    SceneMemberInfo createMemberInfoFromParticipant(Participant participant);
    
    // ========== 双向同步 ==========
    void syncFromSdkToSe(String sceneGroupId);
    void syncFromSeToSdk(String sceneGroupId);
    
    // ========== 健康检查 ==========
    BridgeHealthStatus healthCheck();
    
    // ========== 事件监听 ==========
    void registerEventListener(SceneGroupEventListener listener);
    void unregisterEventListener(SceneGroupEventListener listener);
}
```

## 三、优缺点分析

### 3.1 优点

| 优点 | 说明 |
|------|------|
| ✅ **概念清晰** | SceneGroup = Scene 运行时实例，UserSceneGroup = 用户参与 |
| ✅ **职责分离** | SDK 层管理 Agent 集群，UserSceneGroup 管理用户状态 |
| ✅ **无状态设计** | SceneGroup 可水平扩展，支持多 Agent 部署 |
| ✅ **有状态隔离** | 用户状态独立管理，不影响 Agent 集群稳定性 |
| ✅ **减少重复** | 移除 SE 层 SceneGroup，避免概念混淆 |
| ✅ **符合分层** | SDK 层专注基础设施，SE 层专注业务逻辑 |

### 3.2 缺点

| 缺点 | 说明 | 缓解措施 |
|------|------|----------|
| ❌ **重构成本** | 需要移除 SE 层 SceneGroup，修改大量代码 | 分阶段迁移，保留桥接层 |
| ❌ **API 变更** | MVP 需要适配新的 API | 提供适配器模式兼容旧 API |
| ❌ **数据迁移** | 现有数据需要迁移 | 提供迁移脚本和工具 |
| ❌ **学习曲线** | 团队需要理解新架构 | 提供详细文档和培训 |

### 3.3 风险评估

| 风险 | 级别 | 缓解措施 |
|------|------|----------|
| 现有功能中断 | 🔴 高 | 分阶段迁移，保留桥接层兼容 |
| 数据丢失 | 🔴 高 | 完整备份，提供回滚机制 |
| 性能下降 | 🟡 中 | 性能测试，优化同步机制 |
| 学习成本 | 🟡 中 | 详细文档，培训支持 |

## 四、迁移计划

### 4.1 Phase 1: 准备阶段

- [ ] 创建 SDK `SceneGroup` 新接口
- [ ] 创建 SDK `UserSceneGroup` 新接口
- [ ] 更新 `SceneGroupBridge` 桥接层

### 4.2 Phase 2: 迁移阶段

- [ ] SE `SceneGroup` 数据迁移到 SDK
- [ ] SE `UserSceneGroup` 数据迁移到 SDK
- [ ] 更新 MVP API 适配

### 4.3 Phase 3: 清理阶段

- [ ] 移除 SE 层 `SceneGroup`
- [ ] 移除 SE 层 `UserSceneGroup`
- [ ] 更新文档

## 五、状态

- [x] 方案分析完成
- [ ] 确认迁移方案
- [ ] 实施迁移
- [ ] 验证测试

## 六、联系人

- SDK 团队
- SE 团队
- MVP 团队
