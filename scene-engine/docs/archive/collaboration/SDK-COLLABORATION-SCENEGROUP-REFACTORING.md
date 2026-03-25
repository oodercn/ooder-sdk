# SDK 协作需求: SceneGroup 架构重构

## 1. 协作概述

**发起方**: SE 团队  
**接收方**: SDK 团队  
**主题**: SceneGroup 架构重构 - SDK 层增强  
**优先级**: P0  
**日期**: 2026-03-20  
**状态**: 🔴 待确认

---

## 2. 背景说明

### 2.1 当前问题

在 `ooder-sdk` 项目中存在多个 `SceneGroup` 类，命名冲突且职责重叠：

| 类 | 包路径 | 职责 |
|----|--------|------|
| `SceneGroup` (SDK) | `net.ooder.sdk.api.scene.SceneGroup` | 高可用集群管理 |
| `SceneGroup` (SE) | `net.ooder.scene.group.SceneGroup` | 业务场景组管理 |
| `UserSceneGroup` (SDK) | `net.ooder.skills.sync.UserSceneGroup` | 用户场景组 |
| `UserSceneGroup` (SE) | `net.ooder.scene.bridge.UserSceneGroup` | 用户场景组关联 |

### 2.2 核心理解

**SceneGroup 是 Scene 场景技能的运行时实例**，而不是简单的"普通节点"。

```
Scene (场景技能定义)
   │
   ├── sceneId: String
   ├── capabilities: List<Capability>
   └── config: SceneConfig
        │
        ▼ 激活后创建
SceneGroup (场景组实例)
   │
   ├── sceneGroupId: String
   ├── sceneId: String (关联到 Scene)
   ├── members: List<SceneMember> (Agent 集群)
   └── sharedState: Map (运行时状态)
```

### 2.3 重构目标

**增强 SDK 层，简化 SE 层，保留桥接层**

---

## 3. SDK 层需求

### 3.1 需求清单

| 需求ID | 需求描述 | 优先级 | 工作量 |
|--------|----------|--------|--------|
| SDK-REQ-001 | SceneManager 接口增强 | P0 | 2-3 天 |
| SDK-REQ-002 | SceneGroup 类增强 | P0 | 2-3 天 |
| SDK-REQ-003 | UserSceneGroup 接口标准化 | P0 | 3-5 天 |
| SDK-REQ-004 | UserSceneGroupImpl 实现 | P0 | 3-5 天 |
| SDK-REQ-005 | SceneManagerImpl 实现 | P0 | 2-3 天 |

---

### 3.2 SDK-REQ-001: SceneManager 接口增强

**需求描述**:

在 `SceneManager` 接口中添加以下方法：

```java
public interface SceneManager {
    
    // 现有方法保持不变...
    
    /**
     * 获取场景的运行时实例 (SceneGroup)
     * 
     * <p>场景激活后，返回 SceneGroup 实例。</p>
     * 
     * @param sceneId 场景ID
     * @return SceneGroup 实例，未激活返回 null
     */
    CompletableFuture<SceneGroup> getSceneGroup(String sceneId);
    
    /**
     * 获取用户场景组
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @return UserSceneGroup 实例
     */
    CompletableFuture<UserSceneGroup> getUserSceneGroup(String sceneGroupId, String userId);
    
    /**
     * 获取用户参与的所有场景组
     * 
     * @param userId 用户ID
     * @return UserSceneGroup 列表
     */
    CompletableFuture<List<UserSceneGroup>> getUserSceneGroups(String userId);
}
```

**验收标准**:
- [ ] 接口定义完成
- [ ] JavaDoc 文档完整
- [ ] 编译通过

---

### 3.3 SDK-REQ-002: SceneGroup 类增强

**需求描述**:

在 `SceneGroup` 类中添加用户场景组管理功能：

```java
public class SceneGroup {
    
    // 新增字段
    private final Map<String, UserSceneGroup> userSceneGroups = new ConcurrentHashMap<>();
    
    /**
     * 获取或创建用户场景组
     */
    public UserSceneGroup getOrCreateUserSceneGroup(String userId, Participant.Role role) {
        return userSceneGroups.computeIfAbsent(userId, 
            uid -> new UserSceneGroupImpl(this, uid, role));
    }
    
    /**
     * 获取所有用户场景组
     */
    public List<UserSceneGroup> getAllUserSceneGroups() {
        return new ArrayList<>(userSceneGroups.values());
    }
    
    /**
     * 移除用户场景组
     */
    public void removeUserSceneGroup(String userId) {
        userSceneGroups.remove(userId);
    }
    
    /**
     * 获取用户场景组
     */
    public UserSceneGroup getUserSceneGroup(String userId) {
        return userSceneGroups.get(userId);
    }
}
```

**验收标准**:
- [ ] 方法实现完成
- [ ] 单元测试通过
- [ ] 线程安全验证

---

### 3.4 SDK-REQ-003: UserSceneGroup 接口标准化

**需求描述**:

标准化 `UserSceneGroup` 接口定义：

```java
package net.ooder.skills.sync;

/**
 * 用户场景组 - 用户参与的场景组
 * 
 * <p>包含用户相关的状态：技能绑定、能力绑定、知识库绑定。</p>
 */
public interface UserSceneGroup {
    
    // ========== 基础信息 ==========
    
    /**
     * 获取场景组ID
     */
    String getSceneGroupId();
    
    /**
     * 获取场景ID
     */
    String getSceneId();
    
    /**
     * 获取用户ID
     */
    String getUserId();
    
    /**
     * 获取用户角色
     */
    Participant.Role getRole();
    
    /**
     * 设置用户角色
     */
    void setRole(Participant.Role role);
    
    // ========== 技能绑定 ==========
    
    /**
     * 添加技能
     */
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    
    /**
     * 移除技能
     */
    void removeSkill(String skillId);
    
    /**
     * 获取所有技能
     */
    List<SkillBinding> getSkills();
    
    /**
     * 获取指定技能
     */
    SkillBinding getSkill(String skillId);
    
    // ========== 能力绑定 ==========
    
    /**
     * 绑定能力
     */
    CapabilityBinding bindCapability(String capId, Map<String, Object> config);
    
    /**
     * 解绑能力
     */
    void unbindCapability(String bindingId);
    
    /**
     * 获取所有能力绑定
     */
    List<CapabilityBinding> getCapabilityBindings();
    
    // ========== 知识库绑定 ==========
    
    /**
     * 绑定知识库
     */
    KnowledgeBinding bindKnowledgeBase(String kbId, String layer);
    
    /**
     * 解绑知识库
     */
    void unbindKnowledgeBase(String kbId);
    
    /**
     * 获取所有知识库绑定
     */
    List<KnowledgeBinding> getKnowledgeBaseBindings();
    
    // ========== 个人上下文 ==========
    
    /**
     * 获取个人上下文
     */
    Map<String, Object> getPersonalContext();
    
    /**
     * 设置个人上下文
     */
    void setPersonalContext(String key, Object value);
    
    /**
     * 移除个人上下文
     */
    void removePersonalContext(String key);
    
    // ========== 同步 ==========
    
    /**
     * 同步到 SceneGroup
     */
    void syncToSceneGroup();
    
    /**
     * 从 SceneGroup 同步
     */
    void syncFromSceneGroup();
}
```

**验收标准**:
- [ ] 接口定义完成
- [ ] JavaDoc 文档完整
- [ ] 与现有 `UserSceneGroup` 接口兼容

---

### 3.5 SDK-REQ-004: UserSceneGroupImpl 实现

**需求描述**:

实现 `UserSceneGroup` 接口：

```java
package net.ooder.skills.sync.impl;

public class UserSceneGroupImpl implements UserSceneGroup {
    
    private final SceneGroup sceneGroup;
    private final String userId;
    private Participant.Role role;
    
    private final List<SkillBinding> skillBindings = new CopyOnWriteArrayList<>();
    private final List<CapabilityBinding> capabilityBindings = new CopyOnWriteArrayList<>();
    private final List<KnowledgeBinding> knowledgeBindings = new CopyOnWriteArrayList<>();
    private final Map<String, Object> personalContext = new ConcurrentHashMap<>();
    
    public UserSceneGroupImpl(SceneGroup sceneGroup, String userId, Participant.Role role) {
        this.sceneGroup = sceneGroup;
        this.userId = userId;
        this.role = role;
    }
    
    // 实现所有接口方法...
}
```

**验收标准**:
- [ ] 所有接口方法实现
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试通过

---

### 3.6 SDK-REQ-005: SceneManagerImpl 实现

**需求描述**:

实现 `SceneManager` 新增方法：

```java
public class SceneManagerImpl implements SceneManager {
    
    // 新增字段
    private final Map<String, SceneGroup> activeSceneGroups = new ConcurrentHashMap<>();
    
    @Override
    public CompletableFuture<SceneGroup> getSceneGroup(String sceneId) {
        return CompletableFuture.completedFuture(activeSceneGroups.get(sceneId));
    }
    
    @Override
    public CompletableFuture<UserSceneGroup> getUserSceneGroup(String sceneGroupId, String userId) {
        SceneGroup group = activeSceneGroups.get(sceneGroupId);
        if (group == null) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(group.getUserSceneGroup(userId));
    }
    
    @Override
    public CompletableFuture<List<UserSceneGroup>> getUserSceneGroups(String userId) {
        List<UserSceneGroup> result = new ArrayList<>();
        for (SceneGroup group : activeSceneGroups.values()) {
            UserSceneGroup usg = group.getUserSceneGroup(userId);
            if (usg != null) {
                result.add(usg);
            }
        }
        return CompletableFuture.completedFuture(result);
    }
}
```

**验收标准**:
- [ ] 所有新增方法实现
- [ ] 单元测试通过
- [ ] 集成测试通过

---

## 4. 架构设计

### 4.1 重构后架构

```
┌─────────────────────────────────────────────────────────────┐
│ SDK 层 (基础设施)                                            │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ SceneManager (场景管理器)                            │   │
│  │ ├── create(definition) ← 创建场景定义               │   │
│  │ ├── activate(sceneId) ← 激活场景，创建 SceneGroup   │   │
│  │ └── getSceneGroup(sceneId) ← 获取运行时实例         │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ SceneGroup (场景组 - Scene 运行时实例)               │   │
│  │ ├── sceneGroupId: String                            │   │
│  │ ├── sceneId: String                                 │   │
│  │ ├── members: List<SceneMember> ← Agent 集群         │   │
│  │ ├── key: SceneGroupKey ← 密钥管理                   │   │
│  │ └── userSceneGroups: Map ← 用户场景组               │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ UserSceneGroup (用户场景组 - 有状态)                 │   │
│  │ ├── userId: String                                  │   │
│  │ ├── role: Participant.Role                          │   │
│  │ ├── skillBindings: List<SkillBinding>               │   │
│  │ ├── capabilityBindings: List<CapabilityBinding>     │   │
│  │ ├── knowledgeBindings: List<KnowledgeBinding>       │   │
│  │ └── personalContext: Map                            │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 职责划分

| 层级 | 组件 | 职责 | 状态 |
|------|------|------|------|
| **SDK 层** | `SceneGroup` | Scene 运行时实例、Agent 集群管理 | 无状态 |
| **SDK 层** | `UserSceneGroup` | 用户参与、技能绑定、能力绑定 | 有状态 |
| **SE 层** | `SceneGroupBridge` | SDK-SE 数据桥接 | 无状态 |

---

## 5. 时间计划

| 阶段 | 任务 | 工作量 | 完成时间 |
|------|------|--------|----------|
| Phase 1 | SDK 层增强 | 12-19 天 | 2-3 周 |
| Phase 2 | SE 层简化 | 7-11 天 | 1-2 周 |
| Phase 3 | MVP 适配 | 6-10 天 | 1-2 周 |
| **总计** | - | **25-40 天** | **4-7 周** |

---

## 6. 风险评估

| 风险 | 级别 | 缓解措施 |
|------|------|----------|
| API 不兼容 | 🔴 高 | 提供适配器模式，渐进迁移 |
| 数据丢失 | 🔴 高 | 完整备份，提供回滚机制 |
| 性能下降 | 🟡 中 | 性能基准测试，优化同步机制 |

---

## 7. 验收标准

### 7.1 功能验收

- [ ] `SceneManager.getSceneGroup()` 方法可用
- [ ] `SceneManager.getUserSceneGroup()` 方法可用
- [ ] `SceneManager.getUserSceneGroups()` 方法可用
- [ ] `SceneGroup` 用户场景组管理功能正常
- [ ] `UserSceneGroup` 接口实现完整

### 7.2 质量验收

- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试通过
- [ ] 性能测试通过
- [ ] 文档完整

---

## 8. 联系方式

**SE 团队**: SceneEngine Team  
**SDK 团队**: SDK Team  
**MVP 团队**: MVP Team

---

## 9. 状态

- [x] 需求文档完成
- [ ] SDK 团队确认
- [ ] 开始实施

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**最后更新**: 2026-03-20
