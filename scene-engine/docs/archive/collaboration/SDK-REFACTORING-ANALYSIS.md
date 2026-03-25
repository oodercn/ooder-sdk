# SDK 重构分析报告

## 一、SDK 重构概览

### 1.1 包结构变化

SDK 进行了重大重构，引入了新的包结构：

```
net.ooder.sdk.api.scene
├── SceneManager          # 场景管理器（已增强）
├── SceneGroup            # 场景组（高可用集群）
├── SceneMember           # 场景成员
└── SceneGroupKey         # 场景组密钥

net.ooder.skills.sync
├── UserSceneGroup        # 用户场景组接口（已标准化）
├── Participant           # 参与者（已标准化）
├── SkillBinding          # 技能绑定
├── CapabilityBinding     # 能力绑定
├── KnowledgeBinding      # 知识库绑定
└── CollaborationSession  # 协作会话

net.ooder.sdk.common.enums
├── MemberRole            # 成员角色
└── SceneType             # 场景类型
```

### 1.2 SceneManager 新增方法

```java
// 获取场景的运行时实例 (SceneGroup)
CompletableFuture<SceneGroup> getSceneGroup(String sceneId);

// 获取用户场景组
CompletableFuture<UserSceneGroup> getUserSceneGroup(String sceneGroupId, String userId);

// 获取用户参与的所有场景组
CompletableFuture<List<UserSceneGroup>> getUserSceneGroups(String userId);
```

---

## 二、UserSceneGroup 接口分析

### 2.1 完整接口定义

```java
public interface UserSceneGroup {
    
    // ========== 基础信息 ==========
    String getSceneGroupId();
    String getSceneId();
    String getUserId();
    Participant.Role getRole();
    void setRole(Participant.Role role);
    
    // ========== 协作者管理 ==========
    Participant addCollaborator(String userId, Participant.Role role);
    void removeCollaborator(String userId);
    void changeCollaboratorRole(String userId, Participant.Role newRole);
    List<Participant> getCollaborators();
    
    // ========== 技能绑定 ==========
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    void removeSkill(String skillId);
    void updateSkillConfig(String skillId, Map<String, Object> config);
    List<SkillBinding> getSkills();
    SkillBinding getSkill(String skillId);
    
    // ========== 协作会话 ==========
    CollaborationSession startCollaboration(String collaborationType, List<String> participants);
    void endCollaboration(String sessionId);
    CollaborationSession getCollaborationSession(String sessionId);
    List<CollaborationSession> getActiveCollaborations();
    
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
    void removePersonalContext(String key);
    
    // ========== 同步 ==========
    void syncToSceneGroup();
    void syncFromSceneGroup();
    
    // ========== 状态 ==========
    void activate();
    void deactivate();
    String getStatus();
}
```

### 2.2 新增功能

| 功能 | 说明 | SE 需要适配 |
|------|------|-------------|
| **协作会话** | `startCollaboration`, `endCollaboration` | ✅ 需要新增 |
| **协作者管理** | `addCollaborator`, `removeCollaborator`, `changeCollaboratorRole` | ✅ 需要适配 |
| **技能绑定** | `addSkill`, `removeSkill`, `updateSkillConfig` | ✅ 已有基础 |
| **能力绑定** | `bindCapability`, `unbindCapability` | ✅ 已有基础 |
| **知识库绑定** | `bindKnowledgeBase`, `unbindKnowledgeBase` | ✅ 已有基础 |
| **个人上下文** | `getPersonalContext`, `setPersonalContext` | ✅ 已有基础 |

---

## 三、Participant 类分析

### 3.1 类型枚举

```java
public enum Type {
    USER("user", "Human user"),
    AGENT("agent", "AI Agent"),
    SUPER_AGENT("super_agent", "Super Agent");
}
```

### 3.2 角色枚举

```java
public enum Role {
    OWNER("owner", "Scene group owner with full control"),
    MANAGER("manager", "Manager with administrative privileges"),
    COORDINATOR("coordinator", "Coordinator for collaboration"),
    EMPLOYEE("employee", "Regular participant"),
    OBSERVER("observer", "Read-only observer"),
    LLM_ASSISTANT("llm_assistant", "LLM-based assistant");
}
```

### 3.3 与 SE Participant 对比

| SDK Participant.Role | SE Participant.Role | 映射关系 |
|---------------------|---------------------|----------|
| OWNER | OWNER | ✅ 一致 |
| MANAGER | ADMIN | ⚠️ 需要映射 |
| COORDINATOR | - | ⚠️ SE 缺失 |
| EMPLOYEE | EMPLOYEE | ✅ 一致 |
| OBSERVER | OBSERVER | ✅ 一致 |
| LLM_ASSISTANT | - | ⚠️ SE 缺失 |

---

## 四、绑定类分析

### 4.1 SkillBinding

```java
public class SkillBinding {
    private String bindingId;
    private String skillId;
    private String sceneGroupId;
    private Map<String, Object> config;
    private String status;
    private long createTime;
    private long lastUpdateTime;
}
```

### 4.2 CapabilityBinding

```java
public class CapabilityBinding {
    private String bindingId;
    private String capabilityId;
    private String sceneGroupId;
    private Map<String, Object> config;
    private String status;
    private long createTime;
    private long lastUpdateTime;
}
```

### 4.3 KnowledgeBinding

```java
public class KnowledgeBinding {
    private String bindingId;
    private String knowledgeBaseId;
    private String sceneGroupId;
    private String layer;
    private String status;
    private long createTime;
    private long lastUpdateTime;
}
```

---

## 五、CollaborationSession 分析

### 5.1 协作会话

```java
public class CollaborationSession {
    private String sessionId;
    private String sceneGroupId;
    private String collaborationType;
    private List<String> participantIds;
    private String status;
    private Map<String, Object> context;
    private long startTime;
    private long endTime;
}
```

### 5.2 关键方法

- `startCollaboration(type, participants)` - 开始协作会话
- `endCollaboration(sessionId)` - 结束协作会话
- `getActiveCollaborations()` - 获取活跃协作会话

---

## 六、SE 层影响分析

### 6.1 需要适配的类

| SE 类 | 需要适配 | 说明 |
|-------|----------|------|
| `SceneGroup` | ✅ 高 | 简化为 SeSceneGroup，引用 SDK SceneGroup |
| `SceneGroupManager` | ✅ 高 | 添加 SeSceneGroup 管理 |
| `SceneGroupBridge` | ✅ 高 | 适配新的 SDK 接口 |
| `Participant` | ✅ 中 | 添加 COORDINATOR, LLM_ASSISTANT 角色 |
| `CapabilityBinding` | ⚠️ 低 | 字段名不同，需要映射 |
| `KnowledgeBinding` | ⚠️ 低 | 字段名不同，需要映射 |

### 6.2 需要新增的功能

| 功能 | 优先级 | 说明 |
|------|--------|------|
| **协作会话管理** | P0 | SE 需要支持 CollaborationSession |
| **协作者角色变更** | P1 | 支持 changeCollaboratorRole |
| **LLM_ASSISTANT 角色** | P1 | 支持 LLM 助手参与 |
| **COORDINATOR 角色** | P2 | 支持协调员角色 |

### 6.3 架构调整建议

```
┌─────────────────────────────────────────────────────────────┐
│ SDK 层 (已完成重构)                                          │
│                                                             │
│  SceneManager                                               │
│  ├── getSceneGroup(sceneId)                                │
│  ├── getUserSceneGroup(sceneGroupId, userId)               │
│  └── getUserSceneGroups(userId)                            │
│                                                             │
│  SceneGroup (高可用集群)                                     │
│  ├── members: List<SceneMember>                            │
│  ├── key: SceneGroupKey                                    │
│  └── sharedState: Map                                      │
│                                                             │
│  UserSceneGroup (用户场景组)                                 │
│  ├── collaborators: List<Participant>                      │
│  ├── skillBindings: List<SkillBinding>                     │
│  ├── capabilityBindings: List<CapabilityBinding>           │
│  ├── knowledgeBindings: List<KnowledgeBinding>             │
│  ├── collaborationSessions: List<CollaborationSession>     │
│  └── personalContext: Map                                  │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│ SE 层 (需要适配)                                             │
│                                                             │
│  SeSceneGroup (简化版)                                       │
│  ├── sdkSceneGroupId: String (引用 SDK SceneGroup)         │
│  ├── sceneId: String                                       │
│  ├── businessContext: Map (SE 特有)                        │
│  ├── workflowState: Map (SE 特有)                          │
│  └── auditLog: List (SE 特有)                              │
│                                                             │
│  SceneGroupBridge (桥接层)                                   │
│  ├── syncFromSdkToSe()                                     │
│  ├── syncFromSeToSdk()                                     │
│  └── healthCheck()                                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 七、行动计划

### 7.1 Phase 1: 适配 SDK 新接口 (P0)

- [ ] 更新 SE `Participant.Role` 添加 COORDINATOR, LLM_ASSISTANT
- [ ] 更新 `SceneGroupBridge` 适配新的 SDK 接口
- [ ] 实现 `CollaborationSession` 支持

### 7.2 Phase 2: 简化 SE SceneGroup (P1)

- [ ] 完成 `SeSceneGroup` 实现
- [ ] 更新 `SceneGroupManager` 支持 SeSceneGroup
- [ ] 数据迁移

### 7.3 Phase 3: MVP 适配 (P1)

- [ ] 更新 MVP 使用新的 SDK 接口
- [ ] 验证功能正确性

---

## 八、状态

- [x] SDK 重构分析完成
- [ ] SE 层适配方案确认
- [ ] 开始实施

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**最后更新**: 2026-03-20
