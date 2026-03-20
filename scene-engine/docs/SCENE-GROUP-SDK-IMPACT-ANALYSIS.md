# SceneGroup 重构方案 - SDK 层影响分析与推荐建议

## 一、SDK 层现状分析

### 1.1 SceneManager 接口职责

SDK 层的 `SceneManager` 已经提供了完整的场景生命周期管理：

```
SceneManager (SDK)
├── 生命周期管理
│   ├── create(definition)      # 创建场景
│   ├── delete(sceneId)         # 删除场景
│   ├── activate(sceneId)       # 激活场景
│   ├── deactivate(sceneId)     # 停用场景
│   ├── startScene(sceneId)     # 启动场景
│   ├── stopScene(sceneId)      # 停止场景
│   ├── pauseScene(sceneId)     # 暂停场景
│   ├── resumeScene(sceneId)    # 恢复场景
│   └── destroyScene(sceneId)   # 销毁场景
│
├── 能力管理
│   ├── addCapability(sceneId, capability)
│   ├── removeCapability(sceneId, capId)
│   └── listCapabilities(sceneId)
│
├── 协作场景
│   ├── addCollaborativeScene(sceneId, collaborativeSceneId)
│   └── listCollaborativeScenes(sceneId)
│
├── 快照管理
│   ├── createSnapshot(sceneId)
│   └── restoreSnapshot(sceneId, snapshot)
│
├── 工作流管理
│   ├── startWorkflow(sceneId, workflowId)
│   └── stopWorkflow(sceneId)
│
└── 状态查询
    ├── getState(sceneId)
    ├── isSceneActive(sceneId)
    └── getStats(sceneId)
```

### 1.2 SceneGroup 与 Scene 的关系

```
Scene (场景定义)
   │
   ├── sceneId: String
   ├── name: String
   ├── capabilities: List<Capability>
   ├── collaborativeScenes: List<String>
   └── config: SceneConfig
        │
        ▼ 激活后创建
SceneGroup (场景组实例)
   │
   ├── sceneGroupId: String
   ├── sceneId: String (关联到 Scene)
   ├── members: List<SceneMember> (Agent 集群)
   ├── key: SceneGroupKey (密钥管理)
   └── sharedState: Map (运行时状态)
```

**关键理解**: `SceneGroup` 是 `Scene` 激活后创建的运行时实例。

## 二、重构方案对 SDK 层的影响

### 2.1 需要修改的接口

| 接口 | 当前状态 | 修改内容 | 影响范围 |
|------|----------|----------|----------|
| `SceneManager` | 完整 | 添加 `getSceneGroup(sceneId)` | 低 |
| `SceneGroupManager` | 独立 | 合并到 `SceneManager` 或保持独立 | 中 |
| `UserSceneGroup` | SDK 层 | 扩展接口，添加用户状态管理 | 中 |
| `SceneGroupBridge` | SE 层 | 更新桥接逻辑 | 高 |

### 2.2 推荐方案：增强 SDK 层

**不建议移除 SE 层 SceneGroup，而是增强 SDK 层的职责边界。**

```
┌─────────────────────────────────────────────────────────────┐
│ 推荐架构                                                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  SDK 层 (基础设施)                                           │
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
│  │ ├── failoverConfig ← 故障转移配置                   │   │
│  │ └── sharedState: Map ← 运行时共享状态               │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ UserSceneGroup (用户场景组 - 有状态)                 │   │
│  │ ├── sceneGroupId: String                            │   │
│  │ ├── userId: String                                  │   │
│  │ ├── role: Participant.Role                          │   │
│  │ ├── skillBindings: List<SkillBinding>               │   │
│  │ ├── capabilityBindings: List<CapabilityBinding>     │   │
│  │ ├── knowledgeBindings: List<KnowledgeBinding>       │   │
│  │ └── personalContext: Map                            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  SE 层 (业务引擎)                                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ SceneGroupBridge (桥接层)                           │   │
│  │ ├── syncFromSdkToSe()                               │   │
│  │ ├── syncFromSeToSdk()                               │   │
│  │ └── healthCheck()                                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ SE SceneGroup (业务场景组 - 简化版)                  │   │
│  │ ├── sceneGroupId: String (引用 SDK SceneGroup)      │   │
│  │ ├── businessContext: Map ← 业务上下文               │   │
│  │ ├── workflowState: Map ← 工作流状态                 │   │
│  │ └── auditLog: List ← 审计日志                       │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 2.3 SDK 层需要的修改

#### 2.3.1 SceneManager 增强

```java
public interface SceneManager {
    
    // ========== 现有方法保持不变 ==========
    CompletableFuture<SceneDefinition> create(SceneDefinition definition);
    CompletableFuture<Void> activate(String sceneId);
    // ... 其他现有方法 ...
    
    // ========== 新增方法 ==========
    
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

#### 2.3.2 SceneGroup 增强

```java
/**
 * 场景组 - Scene 的运行时实例
 * 
 * <p>管理 Agent 集群，提供高可用、故障转移、密钥管理等功能。</p>
 */
public class SceneGroup {
    
    // ========== 基础信息 ==========
    private final String sceneGroupId;
    private final String sceneId;
    private String status;
    
    // ========== Agent 集群 ==========
    private List<SceneMember> members;
    private SceneGroupKey key;
    
    // ========== 高可用配置 ==========
    private int maxMembers = 10;
    private int heartbeatInterval = 5000;
    private int heartbeatTimeout = 30000;
    private boolean autoFailover = true;
    
    // ========== 运行时状态 (无状态设计) ==========
    private Map<String, Object> sharedState;
    
    // ========== 新增：用户场景组管理 ==========
    private final Map<String, UserSceneGroup> userSceneGroups = new ConcurrentHashMap<>();
    
    // ========== 新增方法 ==========
    
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
}
```

#### 2.3.3 UserSceneGroup 增强

```java
/**
 * 用户场景组 - 用户参与的场景组
 * 
 * <p>包含用户相关的状态：技能绑定、能力绑定、知识库绑定。</p>
 */
public interface UserSceneGroup {
    
    // ========== 基础信息 ==========
    String getSceneGroupId();
    String getSceneId();
    String getUserId();
    Participant.Role getRole();
    void setRole(Participant.Role role);
    
    // ========== 技能绑定 ==========
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    void removeSkill(String skillId);
    List<SkillBinding> getSkills();
    SkillBinding getSkill(String skillId);
    
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
    
    // ========== 事件 ==========
    void addEventListener(UserSceneGroupEventListener listener);
    void removeEventListener(UserSceneGroupEventListener listener);
}
```

## 三、推荐建议

### 3.1 推荐：增强 SDK 层，简化 SE 层

**理由**：

| 维度 | 说明 |
|------|------|
| **最小改动** | 不需要大规模重构，只需增强现有接口 |
| **向后兼容** | 现有 MVP 代码可以继续工作 |
| **职责清晰** | SDK 管理 Agent 集群和用户状态，SE 管理业务逻辑 |
| **渐进迁移** | 可以分阶段迁移，降低风险 |

### 3.2 不推荐：完全移除 SE 层 SceneGroup

**理由**：

| 风险 | 说明 |
|------|------|
| **重构成本高** | 需要修改大量代码 |
| **数据迁移风险** | 现有数据可能丢失 |
| **API 不兼容** | MVP 需要大量适配工作 |
| **测试成本高** | 需要全面回归测试 |

### 3.3 推荐的实施步骤

#### Phase 1: SDK 层增强 (低风险)

- [ ] `SceneManager` 添加 `getSceneGroup()` 方法
- [ ] `SceneManager` 添加 `getUserSceneGroup()` 方法
- [ ] `SceneGroup` 添加用户场景组管理
- [ ] `UserSceneGroup` 接口标准化

#### Phase 2: SE 层简化 (中风险)

- [ ] SE `SceneGroup` 改为引用 SDK `SceneGroup`
- [ ] SE `SceneGroup` 只保留业务相关字段
- [ ] 更新 `SceneGroupBridge` 桥接逻辑

#### Phase 3: 迁移和清理 (中风险)

- [ ] MVP 适配新 API
- [ ] 移除冗余代码
- [ ] 更新文档

## 四、SDK 层影响评估

### 4.1 接口变更

| 接口 | 变更类型 | 影响范围 |
|------|----------|----------|
| `SceneManager` | 新增方法 | 低 - 向后兼容 |
| `SceneGroup` | 新增方法 | 低 - 向后兼容 |
| `UserSceneGroup` | 标准化接口 | 中 - 需要适配 |
| `SceneGroupManager` | 可能合并 | 中 - 需要评估 |

### 4.2 实现变更

| 实现 | 变更内容 | 工作量 |
|------|----------|--------|
| `SceneManagerImpl` | 添加 SceneGroup 管理 | 2-3 天 |
| `SceneGroupImpl` | 添加用户场景组管理 | 2-3 天 |
| `UserSceneGroupImpl` | 实现标准化接口 | 3-5 天 |
| `SceneGroupBridgeImpl` | 更新桥接逻辑 | 2-3 天 |

### 4.3 测试变更

| 测试类型 | 工作量 |
|----------|--------|
| 单元测试 | 2-3 天 |
| 集成测试 | 3-5 天 |
| 回归测试 | 5-7 天 |

## 五、总结

### 推荐方案

**增强 SDK 层，简化 SE 层，保留桥接层**

### 关键决策

| 决策点 | 推荐 | 理由 |
|--------|------|------|
| SceneGroup 位置 | SDK 层 | Scene 运行时实例，管理 Agent 集群 |
| UserSceneGroup 位置 | SDK 层 | 用户状态管理，与 SceneGroup 关联 |
| SE SceneGroup | 保留简化版 | 业务上下文、工作流状态、审计日志 |
| SceneGroupBridge | 保留 | SDK-SE 数据同步 |

### 预期收益

- ✅ 概念清晰：SceneGroup = Scene 运行时实例
- ✅ 职责分离：SDK 管理 Agent，SE 管理业务
- ✅ 最小改动：向后兼容，渐进迁移
- ✅ 降低风险：分阶段实施，可控风险

## 六、状态

- [x] SDK 层影响分析完成
- [x] 推荐建议确定
- [ ] 确认实施方案
- [ ] 开始实施

## 七、联系人

- SDK 团队
- SE 团队
- MVP 团队
